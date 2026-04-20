package dev.dsddr.aeronauticsaddon.index;

import com.tterrag.registrate.util.entry.ItemProviderEntry;
import dev.dsddr.aeronauticsaddon.content.ponder.scenes.GravitatedScenes;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AddonPonderScenes {
    public static void register(final PonderSceneRegistrationHelper<ResourceLocation> registry) {
        final PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> helper = registry.withKeyFunction(DeferredHolder::getId);

        helper.forComponents(AddonBlocks.GRAVITITE)
                .addStoryBoard("gravitated/gravitite", GravitatedScenes::gravitite);

        helper.forComponents(AddonBlocks.STABILITE)
                .addStoryBoard("gravitated/stabilite", GravitatedScenes::stabilite);
    }
}
