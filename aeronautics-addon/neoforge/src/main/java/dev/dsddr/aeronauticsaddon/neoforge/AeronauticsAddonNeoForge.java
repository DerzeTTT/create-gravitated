package dev.dsddr.aeronauticsaddon.neoforge;

import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import dev.dsddr.aeronauticsaddon.command.HoneyGlueRangeCommand;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(AeronauticsAddon.MOD_ID)
public class AeronauticsAddonNeoForge {
    public AeronauticsAddonNeoForge(final IEventBus modBus, final ModContainer modContainer) {
        AeronauticsAddon.init();
    }

    @EventBusSubscriber(modid = AeronauticsAddon.MOD_ID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void registerCommands(final RegisterCommandsEvent event) {
            HoneyGlueRangeCommand.register(event.getDispatcher());
        }
    }
}
