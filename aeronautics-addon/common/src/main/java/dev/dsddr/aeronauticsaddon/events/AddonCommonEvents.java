package dev.dsddr.aeronauticsaddon.events;

import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonGravititeGravityManager;
import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteManager;
import dev.ryanhcode.sable.api.sublevel.ServerSubLevelContainer;
import dev.ryanhcode.sable.api.sublevel.SubLevelContainer;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class AddonCommonEvents {
    public static void onBlockModifiedEvent(final LevelAccessor level, final BlockPos blockPos, final BlockState oldState, final BlockState newState) {
        AddonGravititeGravityManager.onBlockModifiedEvent(level, blockPos, oldState, newState);
        AddonStabiliteManager.onBlockModifiedEvent(level, blockPos, oldState, newState);
    }

    public static void onSubLevelContainerReady(final Level level, final SubLevelContainer subLevelContainer) {
        if (!(level instanceof final ServerLevel serverLevel) || !(subLevelContainer instanceof final ServerSubLevelContainer serverContainer)) {
            return;
        }

        serverContainer.addObserver(new AddonGravititeGravityManager.GravititeSubLevelObserver(serverLevel));
        serverContainer.addObserver(new AddonStabiliteManager.StabiliteSubLevelObserver(serverLevel));
    }

    public static void onServerStopped(final MinecraftServer server) {
        for (final ServerLevel level : server.getAllLevels()) {
            AddonGravititeGravityManager.clearLevel(level);
            AddonStabiliteManager.clearLevel(level);
        }
    }

    public static void physicsTick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        AddonGravititeGravityManager.physicsTick(physicsSystem, timeStep);
        AddonStabiliteManager.physicsTick(physicsSystem, timeStep);
    }
}
