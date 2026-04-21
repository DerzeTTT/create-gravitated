package dev.dsddr.aeronauticsaddon.content.ponder.scenes;

import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import dev.dsddr.aeronauticsaddon.index.AddonBlocks;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.ponder.SmoothMovementUtils;
import dev.simulated_team.simulated.ponder.instructions.CustomAnimateWorldSectionInstruction;
import dev.simulated_team.simulated.ponder.instructions.SimAnimateBEInstruction;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.OverlayInstructions;
import net.createmod.ponder.api.scene.PositionUtil;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.createmod.ponder.api.scene.SelectionUtil;
import net.createmod.ponder.api.scene.VectorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RedStoneWireBlock;

public class GravitatedScenes {
    public static void gravitite(final SceneBuilder builder, final SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final CreateSceneBuilder.EffectInstructions effects = scene.effects();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final PositionUtil grid = util.grid();

        scene.title("gravitite", "Canceling gravity with Gravitite");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-1f);
        scene.scaleSceneView(0.8f);

        final Selection groundSelection = select.fromTo(0, 0, 0, 8, 0, 8);
        final Selection shipSelection = select.fromTo(2, 2, 2, 7, 3, 6);
        final Selection blendSelection = select.fromTo(2, 3, 2, 6, 3, 2)
                .add(select.fromTo(2, 3, 6, 6, 3, 6));
        final BlockPos shipCenterPos = grid.at(4, 2, 4);
        final BlockPos gravititePos = grid.at(4, 3, 2);
        final BlockPos wirePos = grid.at(1, 1, 4);

        world.replaceBlocks(blendSelection, Blocks.AIR.defaultBlockState(), false);
        world.setBlock(gravititePos, AddonBlocks.GRAVITITE.getDefaultState(), false);
        world.setBlock(wirePos, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.POWER, 15), false);

        final ElementLink<WorldSectionElement> ground = world.showIndependentSection(groundSelection, Direction.UP);
        scene.idle(10);
        final ElementLink<WorldSectionElement> ship = world.showIndependentSection(shipSelection, Direction.DOWN);
        world.configureCenterOfRotation(ship, vector.centerOf(shipCenterPos));
        scene.idle(15);

        effects.indicateSuccess(gravititePos);
        overlay.showText(90)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vector.blockSurface(gravititePos, Direction.NORTH))
                .text("One Gravitite cancels gravity for the whole assembled ship.");
        scene.idle(100);

        overlay.showText(80)
                .placeNearTarget()
                .pointAt(vector.blockSurface(gravititePos, Direction.EAST))
                .text("It holds the hull neutral, so thrust and ballast still decide motion.");
        scene.idle(90);

        overlay.showText(90)
                .attachKeyFrame()
                .colored(PonderPalette.INPUT)
                .placeNearTarget()
                .pointAt(vector.topOf(wirePos))
                .text("Redstone sets strength: 0 turns it off, 15 gives full gravity cancel.");
        scene.idle(40);

        world.modifyBlock(wirePos, state -> state.setValue(RedStoneWireBlock.POWER, 0), false);
        effects.indicateRedstone(wirePos);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, vector.of(0, -0.6, 0), 25,
                SmoothMovementUtils.quadraticRise()));
        scene.idle(30);

        overlay.showText(70)
                .colored(PonderPalette.RED)
                .placeNearTarget()
                .pointAt(vector.topOf(wirePos))
                .text("With no signal, the ship starts to fall again.");
        scene.idle(80);

        world.modifyBlock(wirePos, state -> state.setValue(RedStoneWireBlock.POWER, 15), false);
        effects.indicateRedstone(wirePos);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.move(ship, vector.of(0, 0.6, 0), 20,
                SmoothMovementUtils.quadraticRise()));
        scene.idle(25);

        overlay.showText(70)
                .colored(PonderPalette.OUTPUT)
                .placeNearTarget()
                .pointAt(vector.blockSurface(gravititePos, Direction.SOUTH))
                .text("Power it again to return to zero-gravity flight.");
        scene.idle(90);

        world.hideIndependentSection(ship, Direction.UP);
        world.hideIndependentSection(ground, Direction.UP);
        scene.markAsFinished();
    }

    public static void stabilite(final SceneBuilder builder, final SceneBuildingUtil util) {
        final CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        final CreateSceneBuilder.WorldInstructions world = scene.world();
        final CreateSceneBuilder.EffectInstructions effects = scene.effects();
        final OverlayInstructions overlay = scene.overlay();
        final SelectionUtil select = util.select();
        final VectorUtil vector = util.vector();
        final PositionUtil grid = util.grid();

        scene.title("stabilite", "Leveling ships with Stabilite");
        scene.configureBasePlate(0, 0, 9);
        scene.setSceneOffsetY(-1f);
        scene.scaleSceneView(0.8f);

        final Selection groundSelection = select.fromTo(0, 0, 0, 8, 0, 8);
        final Selection shipSelection = select.fromTo(2, 2, 2, 7, 3, 6);
        final BlockPos stabilitePos = grid.at(4, 2, 4);
        final BlockPos steeringWheelPos = grid.at(4, 3, 4);
        final BlockPos wirePos = grid.at(1, 1, 4);

        world.setBlock(stabilitePos, AddonBlocks.STABILITE.getDefaultState(), false);
        world.setBlock(steeringWheelPos, SimBlocks.STEERING_WHEEL.getDefaultState(), false);
        world.setBlock(wirePos, Blocks.REDSTONE_WIRE.defaultBlockState().setValue(RedStoneWireBlock.POWER, 1), false);

        final ElementLink<WorldSectionElement> ground = world.showIndependentSection(groundSelection, Direction.UP);
        scene.idle(10);
        final ElementLink<WorldSectionElement> ship = world.showIndependentSection(shipSelection, Direction.DOWN);
        world.configureCenterOfRotation(ship, vector.centerOf(stabilitePos));
        scene.idle(15);

        effects.indicateSuccess(stabilitePos);
        overlay.showText(90)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vector.blockSurface(stabilitePos, Direction.WEST))
                .text("Stabilite levels roll and pitch while normal movement still works.");
        scene.idle(100);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(14, 0, -18), 18,
                SmoothMovementUtils.quadraticRise()));
        scene.idle(25);

        overlay.showText(80)
                .placeNearTarget()
                .pointAt(vector.blockSurface(stabilitePos, Direction.SOUTH))
                .text("It nudges the ship back toward the horizon instead of freezing yaw.");
        scene.idle(50);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(-14, 0, 18), 55,
                SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(65);

        overlay.showText(90)
                .attachKeyFrame()
                .colored(PonderPalette.INPUT)
                .placeNearTarget()
                .pointAt(vector.topOf(wirePos))
                .text("Redstone sets rigidity: low signal is gentle, and 15 snaps back hard.");
        scene.idle(35);

        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(10, 0, -12), 18,
                SmoothMovementUtils.quadraticRise()));
        scene.idle(25);
        world.modifyBlock(wirePos, state -> state.setValue(RedStoneWireBlock.POWER, 15), false);
        effects.indicateRedstone(wirePos);
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(-10, 0, 12), 18,
                SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(30);

        overlay.showText(60)
                .attachKeyFrame()
                .colored(PonderPalette.BLUE)
                .placeNearTarget()
                .pointAt(vector.topOf(steeringWheelPos))
                .text("Mount a Steering Wheel on top to add yaw control. This ignores Stabilite redstone.");
        overlay.showControls(vector.topOf(steeringWheelPos).add(0, 0.15, -0.4), Pointing.DOWN, 35).rightClick();
        scene.idle(12);

        scene.addInstruction(SimAnimateBEInstruction.steeringWheel(steeringWheelPos, -45, 18));
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(0, 24, 0), 18,
                SmoothMovementUtils.quadraticRise()));
        scene.idle(24);

        scene.addInstruction(SimAnimateBEInstruction.steeringWheel(steeringWheelPos, 45, 18));
        scene.addInstruction(CustomAnimateWorldSectionInstruction.rotate(ship, vector.of(0, -24, 0), 18,
                SmoothMovementUtils.quadraticRiseDual()));
        scene.idle(24);

        overlay.showText(100)
                .attachKeyFrame()
                .placeNearTarget()
                .pointAt(vector.topOf(steeringWheelPos))
                .text("Right-click the wheel, steer with A and D, then press Shift to step away.");
        scene.idle(110);

        world.hideIndependentSection(ship, Direction.UP);
        world.hideIndependentSection(ground, Direction.UP);
        scene.markAsFinished();
    }
}
