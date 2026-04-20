package dev.dsddr.aeronauticsaddon.content.blocks.levitite;

import dev.dsddr.aeronauticsaddon.index.AddonBlocks;
import dev.ryanhcode.sable.api.physics.force.ForceGroups;
import dev.ryanhcode.sable.api.physics.force.QueuedForceGroup;
import dev.ryanhcode.sable.api.physics.handle.RigidBodyHandle;
import dev.ryanhcode.sable.api.physics.mass.MassData;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelObserver;
import dev.ryanhcode.sable.companion.math.BoundingBox3i;
import dev.ryanhcode.sable.companion.math.BoundingBox3ic;
import dev.ryanhcode.sable.physics.config.dimension_physics.DimensionPhysicsData;
import dev.ryanhcode.sable.sublevel.ServerSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import dev.ryanhcode.sable.sublevel.plot.LevelPlot;
import dev.ryanhcode.sable.sublevel.plot.PlotChunkHolder;
import dev.ryanhcode.sable.sublevel.storage.SubLevelRemovalReason;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class AddonGravititeGravityManager {
    private static final double VELOCITY_DAMPING = 1.6;
    private static final double MAX_AXIS_CORRECTION = 0.35;
    private static final Map<ServerLevel, AddonGravititeGravityManager> MANAGERS = new WeakHashMap<>();

    private final ServerLevel level;
    private final Map<UUID, Integer> gravititeCounts = new HashMap<>();
    private final Map<UUID, Set<BlockPos>> gravititeBlocks = new HashMap<>();
    private final Set<UUID> dirtySubLevels = new HashSet<>();
    private boolean allDirty = true;

    private AddonGravititeGravityManager(final ServerLevel level) {
        this.level = level;
    }

    public static AddonGravititeGravityManager get(final ServerLevel level) {
        return MANAGERS.computeIfAbsent(level, AddonGravititeGravityManager::new);
    }

    public static void clearLevel(final ServerLevel level) {
        MANAGERS.remove(level);
    }

    public static void onBlockModifiedEvent(final LevelAccessor level, final BlockPos blockPos, final BlockState oldState, final BlockState newState) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return;
        }

        if (!isGravitite(oldState) && !isGravitite(newState)) {
            return;
        }

        get(serverLevel).markAllDirty();
    }

    public static void physicsTick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        get(physicsSystem.getLevel()).tick(physicsSystem, timeStep);
    }

    private static boolean isGravitite(final BlockState state) {
        return state.is(AddonBlocks.GRAVITITE.get());
    }

    private void tick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        final SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        final boolean rescanAll = this.allDirty;

        for (final SubLevel subLevel : container.getAllSubLevels()) {
            if (!(subLevel instanceof final ServerSubLevel serverSubLevel)) {
                continue;
            }

            final UUID subLevelId = serverSubLevel.getUniqueId();
            if (rescanAll || this.dirtySubLevels.remove(subLevelId) || !this.gravititeCounts.containsKey(subLevelId)) {
                final Set<BlockPos> gravititePositions = this.findGravitite(serverSubLevel);
                this.gravititeBlocks.put(subLevelId, gravititePositions);
                this.gravititeCounts.put(subLevelId, gravititePositions.size());
            }

            if (this.gravititeCounts.getOrDefault(subLevelId, 0) > 0) {
                final double strengthFactor = this.getSignalFactor(subLevelId);
                if (strengthFactor > 0.0) {
                    this.applyZeroGravity(physicsSystem, serverSubLevel, timeStep, strengthFactor);
                }
            }
        }

        if (rescanAll) {
            this.dirtySubLevels.clear();
            this.allDirty = false;
        }
    }

    private void markAllDirty() {
        this.allDirty = true;
    }

    private void markDirty(final UUID subLevelId) {
        this.dirtySubLevels.add(subLevelId);
    }

    private void remove(final UUID subLevelId) {
        this.gravititeCounts.remove(subLevelId);
        this.gravititeBlocks.remove(subLevelId);
        this.dirtySubLevels.remove(subLevelId);
    }

    private Set<BlockPos> findGravitite(final ServerSubLevel subLevel) {
        final Set<BlockPos> positions = new HashSet<>();
        final LevelPlot plot = subLevel.getPlot();
        final BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();

        for (final PlotChunkHolder chunk : plot.getLoadedChunks()) {
            final BoundingBox3ic localChunkBounds = chunk.getBoundingBox();
            if (localChunkBounds == null || localChunkBounds == BoundingBox3i.EMPTY) {
                continue;
            }

            final int minBlockX = chunk.getPos().getMinBlockX();
            final int minBlockZ = chunk.getPos().getMinBlockZ();

            for (int x = localChunkBounds.minX(); x <= localChunkBounds.maxX(); x++) {
                for (int y = localChunkBounds.minY(); y <= localChunkBounds.maxY(); y++) {
                    for (int z = localChunkBounds.minZ(); z <= localChunkBounds.maxZ(); z++) {
                        blockPos.set(minBlockX + x, y, minBlockZ + z);
                        if (this.level.getBlockState(blockPos).is(AddonBlocks.GRAVITITE.get())) {
                            positions.add(blockPos.immutable());
                        }
                    }
                }
            }
        }

        return positions;
    }

    private double getSignalFactor(final UUID subLevelId) {
        int maxSignal = 0;

        for (final BlockPos blockPos : this.gravititeBlocks.getOrDefault(subLevelId, Set.of())) {
            maxSignal = Math.max(maxSignal, this.level.getBestNeighborSignal(blockPos));
            if (maxSignal >= 15) {
                break;
            }
        }

        return maxSignal / 15.0;
    }

    private void applyZeroGravity(final SubLevelPhysicsSystem physicsSystem, final ServerSubLevel subLevel,
                                  final double timeStep, final double strengthFactor) {
        final MassData massData = subLevel.getMassTracker();
        final Vector3dc centerOfMass = massData.getCenterOfMass();
        if (centerOfMass == null || massData.getMass() <= 0.0) {
            return;
        }

        final Vector3d gravity = new Vector3d(DimensionPhysicsData.getGravity(physicsSystem.getLevel()));
        if (gravity.lengthSquared() == 0.0) {
            return;
        }

        final Vector3d cancelImpulse = gravity.negate().mul(timeStep * massData.getMass() * strengthFactor);
        cancelImpulse.add(this.computeGravityAxisDamping(physicsSystem.getPhysicsHandle(subLevel), massData.getMass(), gravity, strengthFactor));

        subLevel.logicalPose().transformNormalInverse(cancelImpulse);

        final QueuedForceGroup levitationGroup = subLevel.getOrCreateQueuedForceGroup(ForceGroups.LEVITATION.get());
        levitationGroup.applyAndRecordPointForce(centerOfMass, cancelImpulse);
    }

    private Vector3d computeGravityAxisDamping(final RigidBodyHandle handle, final double mass, final Vector3dc gravity,
                                               final double strengthFactor) {
        final Vector3d gravityDirection = new Vector3d(gravity).normalize();
        final double velocityAlongGravity = handle.getLinearVelocity(new Vector3d()).dot(gravityDirection);
        final double correction = Mth.clamp(-velocityAlongGravity * mass * VELOCITY_DAMPING * strengthFactor,
                -MAX_AXIS_CORRECTION * mass,
                MAX_AXIS_CORRECTION * mass);

        if (Math.abs(correction) < 1.0E-4) {
            return new Vector3d();
        }

        return gravityDirection.mul(correction);
    }

    public static class GravititeSubLevelObserver implements SubLevelObserver {
        private final ServerLevel level;

        public GravititeSubLevelObserver(final ServerLevel level) {
            this.level = level;
        }

        @Override
        public void onSubLevelAdded(final SubLevel subLevel) {
            AddonGravititeGravityManager.get(this.level).markDirty(subLevel.getUniqueId());
        }

        @Override
        public void onSubLevelRemoved(final SubLevel subLevel, final SubLevelRemovalReason reason) {
            AddonGravititeGravityManager.get(this.level).remove(subLevel.getUniqueId());
        }
    }
}
