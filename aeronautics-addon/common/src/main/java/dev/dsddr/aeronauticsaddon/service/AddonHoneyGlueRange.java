package dev.dsddr.aeronauticsaddon.service;

import dev.simulated_team.simulated.service.SimConfigService;

public final class AddonHoneyGlueRange {
    public static final int MIN_HONEY_GLUE_RANGE = 512;

    private AddonHoneyGlueRange() {
    }

    public static int getConfiguredMinimum() {
        return Math.max(MIN_HONEY_GLUE_RANGE, SimConfigService.INSTANCE.server().assembly.honeyGlueRange.get());
    }

    public static double ensureMinimum(final double value) {
        return Math.max(MIN_HONEY_GLUE_RANGE, value);
    }
}
