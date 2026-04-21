package dev.dsddr.aeronauticsaddon.neoforge.events;

import dev.dsddr.aeronauticsaddon.client.StabiliteWheelController;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public final class AddonNeoForgeClientEvents {
    private AddonNeoForgeClientEvents() {
    }

    @SubscribeEvent
    public static void postClientTick(final ClientTickEvent.Post event) {
        StabiliteWheelController.tick(Minecraft.getInstance());
    }
}
