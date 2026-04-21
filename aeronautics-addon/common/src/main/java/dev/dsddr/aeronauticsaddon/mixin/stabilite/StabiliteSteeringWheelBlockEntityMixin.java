package dev.dsddr.aeronauticsaddon.mixin.stabilite;

import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteWheelHelper;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SteeringWheelBlockEntity.class)
public abstract class StabiliteSteeringWheelBlockEntityMixin {
    @Shadow private int inUse;
    @Shadow public float targetAngle;
    @Shadow private float clientAngle;
    @Shadow float generatedSpeed;
    @Shadow float logicalSpeed;

    @Unique
    private SteeringWheelBlockEntity creategravitated$self() {
        return (SteeringWheelBlockEntity) (Object) this;
    }

    @Unique
    private boolean creategravitated$isMounted() {
        final SteeringWheelBlockEntity self = this.creategravitated$self();
        return AddonStabiliteWheelHelper.isStabiliteMounted(self.getLevel(), self.getBlockPos());
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void creategravitated$lockStabiliteWheelSettings(final CallbackInfo ci) {
        AddonStabiliteWheelHelper.lockWheelSettings(this.creategravitated$self());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void creategravitated$retargetMountedWheelEveryTick(final CallbackInfo ci) {
        final SteeringWheelBlockEntity self = this.creategravitated$self();
        if (self.getLevel() == null || !this.creategravitated$isMounted()) {
            return;
        }

        if (self.getLevel().isClientSide()) {
            if (!SimClickInteractions.STEERING_WHEEL_MANAGER.isBlockActive(self.getBlockPos())) {
                this.clientAngle = this.clientAngle + (self.targetAngleToUpdate - this.clientAngle) / 3.0F;
            }
            return;
        }

        if (self.getGeneratedSpeed() != 0 || Math.abs(self.targetAngleToUpdate - this.targetAngle) > 0.001F) {
            this.inUse = 0;
            this.targetAngle = Float.NaN;
            self.updateTargetAngle(self.targetAngleToUpdate);
        }
    }

    @Inject(
        method = "updateTargetAngle",
        at = @At(
            value = "INVOKE",
            target = "Ldev/simulated_team/simulated/content/blocks/steering_wheel/SteeringWheelBlockEntity;updateGeneratedRotation()V",
            ordinal = 2,
            shift = At.Shift.BEFORE
        )
    )
    private void creategravitated$accelerateMountedWheelTarget(final float absoluteTarget, final CallbackInfo ci) {
        if (!this.creategravitated$isMounted() || this.inUse <= 0 || this.logicalSpeed == 0 || this.generatedSpeed == 0) {
            return;
        }

        this.inUse = Math.max(1, (this.inUse + 1) / 2);
        this.logicalSpeed *= 2.0F;
        this.generatedSpeed *= 2.0F;
    }
}
