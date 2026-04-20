package dev.dsddr.aeronauticsaddon.index;

import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry;
import com.simibubi.create.foundation.block.connected.CTSpriteShifter;
import dev.dsddr.aeronauticsaddon.AeronauticsAddon;

import static com.simibubi.create.foundation.block.connected.AllCTTypes.OMNIDIRECTIONAL;

public class AddonSpriteShift {
    public static final CTSpriteShiftEntry GRAVITITE = omni("block/gravitite");
    public static final CTSpriteShiftEntry STABILITE = omni("block/stabilite");

    private static CTSpriteShiftEntry omni(final String name) {
        return CTSpriteShifter.getCT(OMNIDIRECTIONAL,
                AeronauticsAddon.path(name),
                AeronauticsAddon.path(name + "_connected"));
    }

    public static void init() {
    }
}
