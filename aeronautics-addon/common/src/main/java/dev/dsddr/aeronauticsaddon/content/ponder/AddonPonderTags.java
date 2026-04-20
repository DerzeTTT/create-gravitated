package dev.dsddr.aeronauticsaddon.content.ponder;

import dev.dsddr.aeronauticsaddon.index.AddonBlocks;
import dev.simulated_team.simulated.index.SimPonderTags;
import net.createmod.catnip.registry.RegisteredObjectsHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;

public class AddonPonderTags {
    public static void register(final PonderTagRegistrationHelper<ResourceLocation> helper) {
        final PonderTagRegistrationHelper<ItemLike> itemHelper = helper.withKeyFunction(
                RegisteredObjectsHelper::getKeyOrThrow);

        itemHelper.addToTag(SimPonderTags.PHYSICS_BEHAVIOR)
                .add(AddonBlocks.GRAVITITE.asItem())
                .add(AddonBlocks.STABILITE.asItem());
    }
}
