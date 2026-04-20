package dev.dsddr.aeronauticsaddon;

import dev.dsddr.aeronauticsaddon.service.AddonHoneyGlueRange;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AeronauticsAddon {
    public static final String MOD_ID = "aeronautics_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private AeronauticsAddon() {
    }

    public static void init() {
        AddonHoneyGlueRange.getRange();
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
