package dev.dsddr.aeronauticsaddon.mixin;

import dev.dsddr.aeronauticsaddon.events.AddonCommonEvents;
import dev.eriksonn.aeronautics.events.AeronauticsCommonEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AeronauticsCommonEvents.class)
public class AeronauticsCommonEventsMixin {
    @Inject(method = "onBlockModifiedEvent", at = @At("TAIL"))
    private static void aeronauticsAddon$onBlockModifiedEvent(final LevelAccessor level, final BlockPos blockPos,
                                                              final BlockState oldState, final BlockState newState,
                                                              final CallbackInfo ci) {
        AddonCommonEvents.onBlockModifiedEvent(level, blockPos, oldState, newState);
    }

    @Inject(method = "onServerStopped", at = @At("TAIL"))
    private static void aeronauticsAddon$onServerStopped(final MinecraftServer server, final CallbackInfo ci) {
        AddonCommonEvents.onServerStopped(server);
    }
}
