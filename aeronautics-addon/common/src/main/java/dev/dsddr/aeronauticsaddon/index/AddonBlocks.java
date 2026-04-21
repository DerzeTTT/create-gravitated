package dev.dsddr.aeronauticsaddon.index;

import com.simibubi.create.foundation.block.connected.SimpleCTBehaviour;
import com.tterrag.registrate.util.entry.BlockEntry;
import dev.dsddr.aeronauticsaddon.AeronauticsAddon;
import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonGravititeBlock;
import dev.dsddr.aeronauticsaddon.content.blocks.levitite.AddonStabiliteBlock;
import dev.eriksonn.aeronautics.content.components.Levitating;
import dev.eriksonn.aeronautics.content.particle.LevititeSparklePartcleData;
import dev.eriksonn.aeronautics.index.AeroDataComponents;
import dev.eriksonn.aeronautics.index.AeroSoundEvents;
import dev.ryanhcode.sable.index.SableTags;
import dev.simulated_team.simulated.index.sounds.SimLazySoundType;
import dev.simulated_team.simulated.registrate.SimulatedRegistrate;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Optional;

import static com.simibubi.create.foundation.data.CreateRegistrate.connectedTextures;

public class AddonBlocks {
    private static final SimulatedRegistrate REGISTRATE = AeronauticsAddon.getRegistrate();
    private static final Levitating GRAVITITE_LEVITATING = new Levitating(0.93f,
            Optional.of(new LevititeSparklePartcleData(16777215)));
    private static final Levitating STABILITE_LEVITATING = new Levitating(0.93f,
            Optional.of(new LevititeSparklePartcleData(LevititeSparklePartcleData.LEVITITE_PINK)));

    public static final BlockEntry<AddonGravititeBlock> GRAVITITE =
            REGISTRATE.block("gravitite", AddonGravititeBlock::new)
                    .properties(p -> p.lightLevel($ -> 10))
                    .properties(p -> p.strength(7, 20))
                    .properties(p -> p.sound(new SimLazySoundType(1.0f, 1.0f,
                            AeroSoundEvents.LEVITITE_BREAK::event,
                            () -> SoundEvents.AMETHYST_BLOCK_STEP,
                            AeroSoundEvents.LEVITITE_PLACE::event,
                            () -> SoundEvents.AMETHYST_BLOCK_HIT,
                            () -> SoundEvents.AMETHYST_BLOCK_FALL)))
                    .onRegister(connectedTextures(() -> new SimpleCTBehaviour(AddonSpriteShift.GRAVITITE)))
                    .tag(SableTags.ALWAYS_CHUNK_RENDERING)
                    .item(BlockItem::new)
                    .properties(p -> p.rarity(Rarity.UNCOMMON))
                    .properties(p -> p.component(AeroDataComponents.LEVITATING, GRAVITITE_LEVITATING))
                    .build()
                    .register();

    public static final BlockEntry<AddonStabiliteBlock> STABILITE =
            REGISTRATE.block("stabilite", AddonStabiliteBlock::new)
                    .properties(p -> p.lightLevel($ -> 10))
                    .properties(p -> p.strength(7, 20))
                    .properties(p -> p.sound(new SimLazySoundType(1.0f, 1.0f,
                            AeroSoundEvents.LEVITITE_BREAK::event,
                            () -> SoundEvents.AMETHYST_BLOCK_STEP,
                            AeroSoundEvents.LEVITITE_PLACE::event,
                            () -> SoundEvents.AMETHYST_BLOCK_HIT,
                            () -> SoundEvents.AMETHYST_BLOCK_FALL)))
                    .onRegister(connectedTextures(() -> new SimpleCTBehaviour(AddonSpriteShift.STABILITE)))
                    .tag(SableTags.ALWAYS_CHUNK_RENDERING)
                    .item(BlockItem::new)
                    .properties(p -> p.rarity(Rarity.EPIC))
                    .properties(p -> p.component(AeroDataComponents.LEVITATING, STABILITE_LEVITATING))
                    .build()
                    .register();

    public static void init() {
        AddonSpriteShift.init();
    }
}
