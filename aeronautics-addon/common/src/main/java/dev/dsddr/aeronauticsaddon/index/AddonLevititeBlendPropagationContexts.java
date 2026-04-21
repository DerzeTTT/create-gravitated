package dev.dsddr.aeronauticsaddon.index;

import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonGravititeCrystalPropagationContext;
import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteCrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.index.AeroRegistries;
import foundry.veil.platform.registry.RegistryObject;

import java.util.function.Supplier;

public class AddonLevititeBlendPropagationContexts {
    public static final RegistryObject<CrystalPropagationContext>
            GRAVITITE_CONTEXT = create("gravitite", AddonGravititeCrystalPropagationContext::new),
            STABILITE_CONTEXT = create("stabilite", AddonStabiliteCrystalPropagationContext::new);

    private static RegistryObject<CrystalPropagationContext> create(final String id, final Supplier<CrystalPropagationContext> context) {
        return AeroRegistries.LEVITITE_CRYSTAL_PROPAGATION_CONTEXT.register(id, context);
    }

    public static void init() {
    }
}
