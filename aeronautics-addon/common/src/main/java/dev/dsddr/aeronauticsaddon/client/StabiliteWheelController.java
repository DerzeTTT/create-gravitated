package dev.dsddr.aeronauticsaddon.client;

import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteWheelHelper;
import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.JOMLConversion;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlock;
import dev.simulated_team.simulated.content.blocks.steering_wheel.SteeringWheelBlockEntity;
import dev.simulated_team.simulated.network.packets.SteeringWheelPacket;
import dev.simulated_team.simulated.util.hold_interaction.BlockHoldInteraction;
import foundry.veil.api.network.VeilPacketManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3dc;

public final class StabiliteWheelController {
    private static final double CONTROL_STANDOFF = 1.0;
    private static BlockPos controlledWheelPos;
    private static float lastTargetAngle;
    private static boolean lastHolding;
    private static boolean engaged;
    private static boolean wasUsePressed;

    private StabiliteWheelController() {
    }

    public static void tick(final Minecraft minecraft) {
        final boolean usePressed = minecraft.options.keyUse.isDown();

        try {
            if (minecraft.player == null || minecraft.level == null) {
                releaseControlledWheel(null);
                return;
            }

            if (minecraft.screen != null || minecraft.player.isSpectator() || minecraft.player.isDeadOrDying()) {
                releaseControlledWheel(minecraft.level);
                return;
            }

            if (!engaged) {
                tryStartControl(minecraft, usePressed);
                return;
            }

            if (minecraft.options.keyShift.isDown()) {
                releaseControlledWheel(minecraft.level);
                return;
            }

            if (!isControllableWheel(minecraft.level, minecraft.player, controlledWheelPos)) {
                releaseControlledWheel(minecraft.level);
                return;
            }

            final BlockPos wheelPos = controlledWheelPos;
            lockPlayerToWheel(minecraft.player, minecraft.level, wheelPos);
            syncWheelTarget(minecraft.level, wheelPos, getSteeringTarget(minecraft), true);
        } finally {
            wasUsePressed = usePressed;
        }
    }

    private static void syncWheelTarget(final ClientLevel level, final BlockPos wheelPos, final float targetAngle, final boolean holding) {
        final BlockPos immutableWheelPos = wheelPos.immutable();
        if (!immutableWheelPos.equals(controlledWheelPos)
                || Float.compare(lastTargetAngle, targetAngle) != 0
                || lastHolding != holding) {
            sendWheelUpdate(level, immutableWheelPos, targetAngle, holding);
            controlledWheelPos = immutableWheelPos;
            lastTargetAngle = targetAngle;
            lastHolding = holding;
            return;
        }

        controlledWheelPos = immutableWheelPos;
        updateLocalWheel(level, immutableWheelPos, targetAngle, holding);
    }

    private static void releaseControlledWheel(@Nullable final ClientLevel level) {
        if (controlledWheelPos != null && level != null) {
            sendWheelUpdate(level, controlledWheelPos, 0.0F, false);
        }

        engaged = false;
        controlledWheelPos = null;
        lastTargetAngle = 0.0F;
        lastHolding = false;
    }

    private static void tryStartControl(final Minecraft minecraft, final boolean usePressed) {
        if (!usePressed || wasUsePressed || minecraft.options.keyShift.isDown()) {
            return;
        }

        final BlockPos wheelPos = getLookedAtWheel(minecraft);
        if (!isControllableWheel(minecraft.level, minecraft.player, wheelPos)) {
            return;
        }

        engaged = true;
        controlledWheelPos = wheelPos.immutable();
        lastTargetAngle = Float.NaN;
        lastHolding = false;
        syncWheelTarget(minecraft.level, controlledWheelPos, 0.0F, true);
    }

    private static float getSteeringTarget(final Minecraft minecraft) {
        final boolean leftPressed = minecraft.options.keyLeft.isDown();
        final boolean rightPressed = minecraft.options.keyRight.isDown();

        if (leftPressed == rightPressed) {
            return 0.0F;
        }

        return rightPressed ? AddonStabiliteWheelHelper.MAX_STEERING_ANGLE : -AddonStabiliteWheelHelper.MAX_STEERING_ANGLE;
    }

    private static void lockPlayerToWheel(final LocalPlayer player, final ClientLevel level, final BlockPos wheelPos) {
        final BlockState wheelState = level.getBlockState(wheelPos);
        if (!wheelState.hasProperty(SteeringWheelBlock.FACING)) {
            return;
        }

        final Direction facing = wheelState.getValue(SteeringWheelBlock.FACING);
        final Vec3 localAnchor = Vec3.atBottomCenterOf(wheelPos)
                .add(facing.getStepX() * CONTROL_STANDOFF, 0.0, facing.getStepZ() * CONTROL_STANDOFF);
        final Vector3dc globalAnchor = Sable.HELPER.projectOutOfSubLevel(level, JOMLConversion.toJOML(localAnchor));

        player.setDeltaMovement(Vec3.ZERO);
        player.moveTo(globalAnchor.x(), globalAnchor.y(), globalAnchor.z(), player.getYRot(), player.getXRot());
        player.resetFallDistance();
    }

    private static void sendWheelUpdate(final ClientLevel level, final BlockPos wheelPos, final float targetAngle, final boolean holding) {
        updateLocalWheel(level, wheelPos, targetAngle, holding);
        VeilPacketManager.server().sendPacket(new SteeringWheelPacket(!holding, targetAngle, wheelPos));
    }

    private static void updateLocalWheel(final ClientLevel level, final BlockPos wheelPos, final float targetAngle, final boolean holding) {
        if (level.getBlockEntity(wheelPos) instanceof final SteeringWheelBlockEntity steeringWheel) {
            steeringWheel.targetAngleToUpdate = targetAngle;
            steeringWheel.held = holding;
            AddonStabiliteWheelHelper.lockWheelSettings(steeringWheel);
        }
    }

    @Nullable
    private static BlockPos getLookedAtWheel(final Minecraft minecraft) {
        if (!(minecraft.hitResult instanceof final BlockHitResult blockHitResult) || minecraft.hitResult.getType() != HitResult.Type.BLOCK) {
            return null;
        }

        final BlockPos hitPos = blockHitResult.getBlockPos();
        return minecraft.level.getBlockEntity(hitPos) instanceof SteeringWheelBlockEntity ? hitPos : null;
    }

    private static boolean isControllableWheel(final ClientLevel level, final LocalPlayer player, @Nullable final BlockPos wheelPos) {
        if (wheelPos == null) {
            return false;
        }

        if (!(level.getBlockEntity(wheelPos) instanceof final SteeringWheelBlockEntity steeringWheel)) {
            return false;
        }

        if (!AddonStabiliteWheelHelper.isStabiliteMounted(level, wheelPos)) {
            return false;
        }

        if (!BlockHoldInteraction.inInteractionRange(player, wheelPos.getCenter(), 0.75)) {
            return false;
        }

        AddonStabiliteWheelHelper.lockWheelSettings(steeringWheel);
        return true;
    }
}
