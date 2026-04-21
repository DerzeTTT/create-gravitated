package dev.dsddr.aeronauticsaddon.neoforge;

import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import dev.dsddr.aeronauticsaddon.AeronauticsAddonClient;
import dev.dsddr.aeronauticsaddon.neoforge.events.AddonNeoForgeClientEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(value = AeronauticsAddon.MOD_ID, dist = Dist.CLIENT)
public class AeronauticsAddonNeoForgeClient {
    public AeronauticsAddonNeoForgeClient(final IEventBus modBus, final ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(AddonNeoForgeClientEvents.class);
        AeronauticsAddonClient.init();
    }
}
