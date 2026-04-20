package dev.dsddr.aeronauticsaddon.mixin.honeyglue;

import dev.dsddr.aeronauticsaddon.service.AddonHoneyGlueRange;
import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueMaxSizing;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HoneyGlueMaxSizing.class)
public abstract class HoneyGlueMaxSizingMixin {
    @Inject(method = "checkBBMax", at = @At("HEAD"), cancellable = true)
    private static void aeronauticsAddon$checkBBMax(final AABB bb, final CallbackInfoReturnable<Boolean> cir) {
        final int max = AddonHoneyGlueRange.getConfiguredMinimum();
        cir.setReturnValue(bb.getXsize() > max || bb.getYsize() > max || bb.getZsize() > max);
    }
}
