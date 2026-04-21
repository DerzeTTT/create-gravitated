package dev.dsddr.aeronauticsaddon.content.blocks.levitite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public final class AddonCrystalCatalystHelper {
    private AddonCrystalCatalystHelper() {
    }

    public static boolean hasCardinalCatalysts(final Level level, final BlockPos fluidPos, final TagKey<Block> requiredTag) {
        for (final Direction direction : Direction.Plane.HORIZONTAL) {
            if (!level.getBlockState(fluidPos.relative(direction)).is(requiredTag)) {
                return false;
            }
        }

        return true;
    }
}
