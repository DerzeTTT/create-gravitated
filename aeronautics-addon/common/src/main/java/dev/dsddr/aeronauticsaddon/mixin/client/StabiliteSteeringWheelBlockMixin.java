package dev.dsddr.aeronauticsaddon.mixin.client;

import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteWheelHelper;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SteeringWheelBlock.class)
public class StabiliteSteeringWheelBlockMixin {
    @Inject(method = "quietUse", at = @At("HEAD"), cancellable = true)
    private void creategravitated$disableMouseHelmControl(final Player player, final InteractionHand hand, final BlockPos pos,
                                                          final BlockState state, final CallbackInfoReturnable<InteractionResult> cir) {
        if (!AddonStabiliteWheelHelper.isStabiliteMounted(player.level(), pos)) {
            return;
        }

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
