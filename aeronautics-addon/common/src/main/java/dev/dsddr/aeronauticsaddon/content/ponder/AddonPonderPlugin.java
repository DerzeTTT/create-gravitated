package dev.dsddr.aeronauticsaddon.content.ponder;

import com.simibubi.create.foundation.ponder.CreatePonderPlugin;
import com.simibubi.create.foundation.ponder.PonderWorldBlockEntityFix;
import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import dev.dsddr.aeronauticsaddon.index.AddonPonderScenes;
import net.createmod.ponder.api.level.PonderLevel;
import net.createmod.ponder.api.registration.IndexExclusionHelper;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class AddonPonderPlugin extends CreatePonderPlugin {
    @Override
    public String getModId() {
        return AeronauticsAddon.MOD_ID;
    }

    @Override
    public void registerScenes(final PonderSceneRegistrationHelper<ResourceLocation> helper) {
        AddonPonderScenes.register(helper);
    }

    @Override
    public void registerTags(final PonderTagRegistrationHelper<ResourceLocation> helper) {
        AddonPonderTags.register(helper);
    }

    @Override
    public void registerSharedText(final SharedTextRegistrationHelper helper) {
    }

    @Override
    public void onPonderLevelRestore(final PonderLevel ponderLevel) {
        PonderWorldBlockEntityFix.fixControllerBlockEntities(ponderLevel);
    }

    @Override
    public void indexExclusions(final IndexExclusionHelper helper) {
    }
}
