package dev.dsddr.aeronauticsaddon.mixin.honeyglue;

import dev.dsddr.aeronauticsaddon.service.AddonHoneyGlueRange;
import dev.simulated_team.simulated.util.assembly.SimAssemblyContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(SimAssemblyContraption.class)
public abstract class SimAssemblyContraptionMixin {
    @ModifyArg(
            method = "checkAndCacheGlue",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;", ordinal = 0),
            index = 0
    )
    private double aeronauticsAddon$increaseCheckAndCacheRange(final double original) {
        return AddonHoneyGlueRange.getRangeAsDouble();
    }

    @ModifyArg(
            method = "addInitialHoneyGlue",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/AABB;inflate(D)Lnet/minecraft/world/phys/AABB;"),
            index = 0
    )
    private static double aeronauticsAddon$increaseInitialRange(final double original) {
        return AddonHoneyGlueRange.getRangeAsDouble();
    }
}
