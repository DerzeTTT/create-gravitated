package dev.dsddr.aeronauticsaddon.content.blocks.levitite;

import dev.dsddr.aeronauticsaddon.index.AddonBlocks;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public final class AddonStabiliteWheelHelper {
    public static final int MAX_STEERING_ANGLE = 45;

    private AddonStabiliteWheelHelper() {
    }

    public static boolean isStabiliteMounted(final LevelAccessor level, final BlockPos wheelPos) {
        if (level == null) {
            return false;
        }

        final BlockState wheelState = level.getBlockState(wheelPos);
        return wheelState.getBlock() instanceof SteeringWheelBlock
                && wheelState.hasProperty(SteeringWheelBlock.ON_FLOOR)
                && wheelState.getValue(SteeringWheelBlock.ON_FLOOR)
                && level.getBlockState(wheelPos.below()).is(AddonBlocks.STABILITE.get());
    }

    public static float clampTargetAngle(final float targetAngle) {
        return Mth.clamp(targetAngle, -MAX_STEERING_ANGLE, MAX_STEERING_ANGLE);
    }

    public static void lockWheelSettings(final SteeringWheelBlockEntity steeringWheel) {
        if (steeringWheel == null || steeringWheel.getLevel() == null || steeringWheel.angleInput == null) {
            return;
        }

        if (!isStabiliteMounted(steeringWheel.getLevel(), steeringWheel.getBlockPos())) {
            return;
        }

        if (steeringWheel.angleInput.getValue() != MAX_STEERING_ANGLE) {
            steeringWheel.angleInput.setValue(MAX_STEERING_ANGLE);
        }

        steeringWheel.targetAngleToUpdate = clampTargetAngle(steeringWheel.targetAngleToUpdate);
        steeringWheel.targetAngle = clampTargetAngle(steeringWheel.targetAngle);
    }
}
