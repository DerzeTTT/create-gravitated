package dev.dsddr.aeronauticsaddon.neoforge;

import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import dev.dsddr.aeronauticsaddon.command.HoneyGlueRangeCommand;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(AeronauticsAddon.MOD_ID)
public class AeronauticsAddonNeoForge {
    private static final DeferredRegister.Blocks BLOCK_ALIASES = DeferredRegister.createBlocks(AeronauticsAddon.MOD_ID);
    private static final DeferredRegister.Items ITEM_ALIASES = DeferredRegister.createItems(AeronauticsAddon.MOD_ID);

    static {
        BLOCK_ALIASES.addAlias(ResourceLocation.fromNamespaceAndPath("aeronautics", "gravitite"), AeronauticsAddon.path("gravitite"));
        BLOCK_ALIASES.addAlias(ResourceLocation.fromNamespaceAndPath("aeronautics", "stabilite"), AeronauticsAddon.path("stabilite"));
        ITEM_ALIASES.addAlias(ResourceLocation.fromNamespaceAndPath("aeronautics", "gravitite"), AeronauticsAddon.path("gravitite"));
        ITEM_ALIASES.addAlias(ResourceLocation.fromNamespaceAndPath("aeronautics", "stabilite"), AeronauticsAddon.path("stabilite"));
    }

    public AeronauticsAddonNeoForge(final IEventBus modBus, final ModContainer modContainer) {
        BLOCK_ALIASES.register(modBus);
        ITEM_ALIASES.register(modBus);
        AeronauticsAddon.getRegistrate().registerEventListeners(modBus);
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
