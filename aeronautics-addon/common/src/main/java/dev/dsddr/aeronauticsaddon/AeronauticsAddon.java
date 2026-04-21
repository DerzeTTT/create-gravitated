package dev.dsddr.aeronauticsaddon;

import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import dev.dsddr.aeronauticsaddon.events.AddonCommonEvents;
import dev.dsddr.aeronauticsaddon.index.AddonBlocks;
import dev.dsddr.aeronauticsaddon.index.AddonLevititeBlendPropagationContexts;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.ryanhcode.sable.platform.SableEventPlatform;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import dev.simulated_team.simulated.util.SimColors;
import net.createmod.catnip.lang.FontHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AeronauticsAddon {
    public static final String MOD_ID = "aeronautics_addon";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final FontHelper.Palette GRAVITITE_TOOLTIP = new FontHelper.Palette(
            TooltipHelper.styleFromColor(SimColors.ACTIVE_YELLOW),
            TooltipHelper.styleFromColor(SimColors.ADVANCABLE_GOLD));
    private static final FontHelper.Palette STABILITE_TOOLTIP = new FontHelper.Palette(
            TooltipHelper.styleFromColor(SimColors.MEDIA_OURPLE),
            TooltipHelper.styleFromColor(SimColors.EPIC_OURPLE));

    private static final NonNullSupplier<SimulatedRegistrate> REGISTRATE = NonNullSupplier.lazy(() ->
            (SimulatedRegistrate) new SimulatedRegistrate(Aeronautics.path("aeronautics"), MOD_ID)
                    .defaultCreativeTab((ResourceKey<CreativeModeTab>) null));

    public static void init() {
        setTooltips();
        AddonLevititeBlendPropagationContexts.init();
        AddonBlocks.init();
        listenCommonEvents();
    }

    private static void setTooltips() {
        getRegistrate().setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, getTooltipPalette(item))
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private static FontHelper.Palette getTooltipPalette(final Item item) {
        final ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
        if (itemId != null && MOD_ID.equals(itemId.getNamespace())) {
            return switch (itemId.getPath()) {
                case "gravitite" -> GRAVITITE_TOOLTIP;
                case "stabilite" -> STABILITE_TOOLTIP;
                default -> FontHelper.Palette.STANDARD_CREATE;
            };
        }

        return FontHelper.Palette.STANDARD_CREATE;
    }

    private static void listenCommonEvents() {
        SableEventPlatform.INSTANCE.onPhysicsTick(AddonCommonEvents::physicsTick);
        SableEventPlatform.INSTANCE.onSubLevelContainerReady(AddonCommonEvents::onSubLevelContainerReady);
    }

    public static SimulatedRegistrate getRegistrate() {
        return REGISTRATE.get();
    }

    public static ResourceLocation path(final String path) {
        return ResourceLocation.tryBuild(MOD_ID, path);
    }
}
