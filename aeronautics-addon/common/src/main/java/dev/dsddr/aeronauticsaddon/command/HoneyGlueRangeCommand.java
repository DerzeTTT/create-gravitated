package dev.dsddr.aeronauticsaddon.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.dsddr.aeronauticsaddon.service.AddonHoneyGlueRange;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public final class HoneyGlueRangeCommand {
    private HoneyGlueRangeCommand() {
    }

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("creategravitated")
                .then(Commands.literal("honey_glue_range")
                        .executes(HoneyGlueRangeCommand::getRange)
                        .then(Commands.literal("get")
                                .executes(HoneyGlueRangeCommand::getRange))
                        .then(Commands.literal("set")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("blocks", IntegerArgumentType.integer(1))
                                        .executes(HoneyGlueRangeCommand::setRange)))
                        .then(Commands.literal("reset")
                                .requires(source -> source.hasPermission(2))
                                .executes(HoneyGlueRangeCommand::resetRange))));
    }

    private static int getRange(final CommandContext<CommandSourceStack> context) {
        final int range = AddonHoneyGlueRange.getRange();
        context.getSource().sendSuccess(() -> Component.literal("Honey Glue range is set to " + range + " blocks."), false);
        return range;
    }

    private static int setRange(final CommandContext<CommandSourceStack> context) {
        final int range = IntegerArgumentType.getInteger(context, "blocks");
        AddonHoneyGlueRange.setRange(range);
        context.getSource().sendSuccess(() -> Component.literal("Honey Glue range set to " + range + " blocks."), true);
        return range;
    }

    private static int resetRange(final CommandContext<CommandSourceStack> context) {
        AddonHoneyGlueRange.resetRange();
        context.getSource().sendSuccess(
                () -> Component.literal("Honey Glue range reset to " + AddonHoneyGlueRange.DEFAULT_HONEY_GLUE_RANGE + " blocks."),
                true
        );
        return AddonHoneyGlueRange.DEFAULT_HONEY_GLUE_RANGE;
    }
}
