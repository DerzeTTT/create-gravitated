package dev.dsddr.aeronauticsaddon.mixin.client;

import dev.dsddr.aeronauticsaddon.service.AddonHoneyGlueRange;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueClientHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(HoneyGlueClientHandler.class)
public abstract class HoneyGlueClientHandlerMixin {
    @ModifyArg(
            method = "getHoneyGlue",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"),
            index = 0
    )
    private static double aeronauticsAddon$increaseClientSearchRange(final double original) {
        return AddonHoneyGlueRange.getRangeAsDouble();
    }
}
