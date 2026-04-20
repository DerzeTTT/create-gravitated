package dev.dsddr.aeronauticsaddon;

import dev.dsddr.aeronauticsaddon.content.ponder.AddonPonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;

public class AeronauticsAddonClient {
    public static void init() {
        PonderIndex.addPlugin(new AddonPonderPlugin());
    }
}
