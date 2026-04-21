package dev.eriksonn.aeronautics.events;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeCrystallizerManager;
import dev.eriksonn.aeronautics.content.blocks.hot_air.balloon.map.BalloonMap;
import dev.eriksonn.aeronautics.content.blocks.levitite.GravititeGravityManager;
import dev.eriksonn.aeronautics.content.blocks.levitite.StabiliteManager;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AeronauticsCommonEvents {
    /**
     * Called at the end of the given ServerLevel's tick
     *
     * @param level The level ticking.
     */
    public static void onServerTickEnd(final ServerLevel level) {
        LevititeCrystallizerManager.tick(level);
        BalloonMap.tick(level);
    }

    /**
     * Called whenever a block is modified in the given level
     */
    public static void onBlockModifiedEvent(final LevelAccessor level, final BlockPos blockPos, final BlockState oldState, final BlockState newState) {
        BalloonMap.MAP.get(level).updateNearbyBalloons(blockPos, oldState, newState);
        GravititeGravityManager.onBlockModifiedEvent(level, blockPos, oldState, newState);
        StabiliteManager.onBlockModifiedEvent(level, blockPos, oldState, newState);
    }

    /**
     * Called whenever a sub-level container is ready
     */
    public static void onSubLevelContainerReady(final Level level, final SubLevelContainer subLevelContainer) {
        if (!(level instanceof final ServerLevel serverLevel) || !(subLevelContainer instanceof final ServerSubLevelContainer serverContainer)) {
            return;
        }

        serverContainer.addObserver(new BalloonMap.BalloonSubLevelObserver(level));
        serverContainer.addObserver(new GravititeGravityManager.GravititeSubLevelObserver(serverLevel));
        serverContainer.addObserver(new StabiliteManager.StabiliteSubLevelObserver(serverLevel));
    }

    public static void onServerStopped(final MinecraftServer server) {
        for (final ServerLevel level : server.getAllLevels()) {
            LevititeCrystallizerManager.clearLevel(level);
            GravititeGravityManager.clearLevel(level);
            StabiliteManager.clearLevel(level);
        }
    }

    public static void physicsTick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        final ServerLevel level = physicsSystem.getLevel();
        BalloonMap.physicsTick(level, timeStep);
        GravititeGravityManager.physicsTick(physicsSystem, timeStep);
        StabiliteManager.physicsTick(physicsSystem, timeStep);
    }
}
