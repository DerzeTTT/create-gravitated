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
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

public class AddonStabiliteManager {
    private static final double MIN_SIGNAL_FACTOR = 0.15;
    private static final double MIN_STABILIZATION_STRENGTH = 0.08;
    private static final double MAX_STABILIZATION_STRENGTH = 48.0;
    private static final double MIN_STABILIZATION_DAMPING = 0.22;
    private static final double MAX_STABILIZATION_DAMPING = 84.0;
    private static final double MIN_MAX_TORQUE_IMPULSE = 0.35;
    private static final double MAX_MAX_TORQUE_IMPULSE = 420.0;
    private static final double YAW_RATE_GAIN = 16.0;
    private static final double MAX_YAW_TORQUE_IMPULSE = 80.0;
    private static final double MAX_TARGET_YAW_RATE = Math.toRadians(90.0);
    private static final Map<ServerLevel, AddonStabiliteManager> MANAGERS = new WeakHashMap<>();

    private final ServerLevel level;
    private final Map<UUID, Integer> stabiliteCounts = new HashMap<>();
    private final Map<UUID, Set<BlockPos>> stabiliteBlocks = new HashMap<>();
    private final Set<UUID> dirtySubLevels = new HashSet<>();
    private boolean allDirty = true;

    private AddonStabiliteManager(final ServerLevel level) {
        this.level = level;
    }

    public static AddonStabiliteManager get(final ServerLevel level) {
        return MANAGERS.computeIfAbsent(level, AddonStabiliteManager::new);
    }

    public static void clearLevel(final ServerLevel level) {
        MANAGERS.remove(level);
    }

    public static void onBlockModifiedEvent(final LevelAccessor level, final BlockPos blockPos, final BlockState oldState, final BlockState newState) {
        if (!(level instanceof final ServerLevel serverLevel)) {
            return;
        }

        if (!oldState.is(AddonBlocks.STABILITE.get()) && !newState.is(AddonBlocks.STABILITE.get())) {
            return;
        }

        get(serverLevel).markAllDirty();
    }

    public static void physicsTick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        get(physicsSystem.getLevel()).tick(physicsSystem, timeStep);
    }

    private void tick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        final SubLevelContainer container = SubLevelContainer.getContainer(this.level);
        final boolean rescanAll = this.allDirty;

        for (final SubLevel subLevel : container.getAllSubLevels()) {
            if (!(subLevel instanceof final ServerSubLevel serverSubLevel)) {
                continue;
            }

            final UUID subLevelId = serverSubLevel.getUniqueId();
            if (rescanAll || this.dirtySubLevels.remove(subLevelId) || !this.stabiliteCounts.containsKey(subLevelId)) {
                final Set<BlockPos> stabilitePositions = this.findStabilite(serverSubLevel);
                this.stabiliteBlocks.put(subLevelId, stabilitePositions);
                this.stabiliteCounts.put(subLevelId, stabilitePositions.size());
            }

            if (this.stabiliteCounts.getOrDefault(subLevelId, 0) > 0) {
                this.applyStabilization(physicsSystem, serverSubLevel, subLevelId, timeStep, this.getSignalFactor(subLevelId));
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
        this.stabiliteCounts.remove(subLevelId);
        this.stabiliteBlocks.remove(subLevelId);
        this.dirtySubLevels.remove(subLevelId);
    }

    private Set<BlockPos> findStabilite(final ServerSubLevel subLevel) {
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
                        if (this.level.getBlockState(blockPos).is(AddonBlocks.STABILITE.get())) {
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

        for (final BlockPos blockPos : this.stabiliteBlocks.getOrDefault(subLevelId, Set.of())) {
            maxSignal = Math.max(maxSignal, this.level.getBestNeighborSignal(blockPos));
            if (maxSignal >= 15) {
                break;
            }
        }

        return Mth.lerp(maxSignal / 15.0, MIN_SIGNAL_FACTOR, 1.0);
    }

    private void applyStabilization(final SubLevelPhysicsSystem physicsSystem, final ServerSubLevel subLevel, final UUID subLevelId,
                                    final double timeStep, final double signalFactor) {
        final MassData massData = subLevel.getMassTracker();
        if (massData.getMass() <= 0.0) {
            return;
        }

        final Vector3d gravity = new Vector3d(DimensionPhysicsData.getGravity(physicsSystem.getLevel()));
        if (gravity.lengthSquared() == 0.0) {
            return;
        }

        final Vector3d targetUp = gravity.negate().normalize();
        final Vector3d currentUp = subLevel.logicalPose().orientation().transform(new Vector3d(0.0, 1.0, 0.0));
        final double stabilizationStrength = Mth.lerp(signalFactor, MIN_STABILIZATION_STRENGTH, MAX_STABILIZATION_STRENGTH);
        final double stabilizationDamping = Mth.lerp(signalFactor, MIN_STABILIZATION_DAMPING, MAX_STABILIZATION_DAMPING);
        final double maxTorqueImpulse = Mth.lerp(signalFactor, MIN_MAX_TORQUE_IMPULSE, MAX_MAX_TORQUE_IMPULSE);
        final Vector3d correctionTorque = currentUp.cross(targetUp, new Vector3d())
                .mul(stabilizationStrength * massData.getMass() * timeStep);

        final RigidBodyHandle handle = physicsSystem.getPhysicsHandle(subLevel);
        final Vector3d angularVelocity = handle.getAngularVelocity(new Vector3d());
        final Vector3d yawSpin = new Vector3d(targetUp).mul(angularVelocity.dot(targetUp));
        final Vector3d dampingTorque = angularVelocity.sub(yawSpin, new Vector3d())
                .mul(-stabilizationDamping * massData.getMass() * timeStep);

        final Vector3d totalWorldTorque = correctionTorque.add(dampingTorque);
        final double steeringInput = this.getSteeringInput(subLevelId);
        if (!Double.isNaN(steeringInput)) {
            totalWorldTorque.add(this.getYawControlTorque(targetUp, angularVelocity, massData.getMass(), timeStep, steeringInput));
        }

        if (totalWorldTorque.lengthSquared() < 1.0E-6) {
            return;
        }

        totalWorldTorque.x = Mth.clamp(totalWorldTorque.x, -maxTorqueImpulse, maxTorqueImpulse);
        totalWorldTorque.y = Mth.clamp(totalWorldTorque.y, -maxTorqueImpulse, maxTorqueImpulse);
        totalWorldTorque.z = Mth.clamp(totalWorldTorque.z, -maxTorqueImpulse, maxTorqueImpulse);

        final QueuedForceGroup forceGroup = subLevel.getOrCreateQueuedForceGroup(ForceGroups.LEVITATION.get());
        forceGroup.getForceTotal().applyLinearAndAngularImpulse(new Vector3d(), subLevel.logicalPose().transformNormalInverse(totalWorldTorque));
    }

    private double getSteeringInput(final UUID subLevelId) {
        double steeringSum = 0.0;
        int steeringWheelCount = 0;

        for (final BlockPos blockPos : this.stabiliteBlocks.getOrDefault(subLevelId, Set.of())) {
            final BlockEntity blockEntity = this.level.getBlockEntity(blockPos.above());
            if (!(blockEntity instanceof final SteeringWheelBlockEntity steeringWheel)) {
                continue;
            }

            if (steeringWheel.angleInput == null || !AddonStabiliteWheelHelper.isStabiliteMounted(this.level, steeringWheel.getBlockPos())) {
                continue;
            }

            AddonStabiliteWheelHelper.lockWheelSettings(steeringWheel);

            final double maxAngle = AddonStabiliteWheelHelper.MAX_STEERING_ANGLE;
            final double steeringInput = Mth.clamp(-steeringWheel.directionConvert(steeringWheel.targetAngleToUpdate) / maxAngle, -1.0F, 1.0F);
            steeringSum += steeringInput;
            steeringWheelCount++;
        }

        if (steeringWheelCount == 0) {
            return Double.NaN;
        }

        return Mth.clamp(steeringSum / steeringWheelCount, -1.0, 1.0);
    }

    private Vector3d getYawControlTorque(final Vector3d targetUp, final Vector3d angularVelocity, final double mass,
                                         final double timeStep, final double steeringInput) {
        final double desiredYawRate = steeringInput * MAX_TARGET_YAW_RATE;
        final double currentYawRate = angularVelocity.dot(targetUp);
        final double yawImpulse = Mth.clamp((desiredYawRate - currentYawRate) * YAW_RATE_GAIN * mass * timeStep,
                -MAX_YAW_TORQUE_IMPULSE, MAX_YAW_TORQUE_IMPULSE);
        return new Vector3d(targetUp).mul(yawImpulse);
    }

    public static class StabiliteSubLevelObserver implements SubLevelObserver {
        private final ServerLevel level;

        public StabiliteSubLevelObserver(final ServerLevel level) {
            this.level = level;
        }

        @Override
        public void onSubLevelAdded(final SubLevel subLevel) {
            AddonStabiliteManager.get(this.level).markDirty(subLevel.getUniqueId());
        }

        @Override
        public void onSubLevelRemoved(final SubLevel subLevel, final SubLevelRemovalReason reason) {
            AddonStabiliteManager.get(this.level).remove(subLevel.getUniqueId());
        }
    }
}
