package dev.dsddr.aeronauticsaddon.index;

import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AddonTags {
    public static class BlockTags {
        public static final TagKey<Block> GRAVITITE_CATALYZER = create("gravitite_catalyzer");
        public static final TagKey<Block> STABILITE_CATALYZER = create("stabilite_catalyzer");

        private static TagKey<Block> create(final String path) {
            return TagKey.create(Registries.BLOCK, AeronauticsAddon.path(path));
        }
    }
}
