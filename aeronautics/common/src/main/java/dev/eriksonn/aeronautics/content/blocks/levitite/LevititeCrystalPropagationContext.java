package dev.eriksonn.aeronautics.content.blocks.levitite;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.CrystalPropagationContext;
import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendHelper;
import dev.eriksonn.aeronautics.index.*;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.LitBlazeBurnerBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

import java.util.Arrays;
import java.util.Optional;

public class LevititeCrystalPropagationContext implements CrystalPropagationContext {
    @Override
    public void onCrystallizationInitialize(final Level level, final BlockPos pos, final boolean isDormant) {
        level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.2f, 1.5f);
        if (!isDormant)
            LevititeBlendHelper.spawnParticles(level, pos, ParticleTypes.FLAME, 20);
        LevititeBlendHelper.spawnParticles(level, pos, ParticleTypes.SMOKE, 15);
    }


    @Override
    public void onCrystallize(final Level level, final BlockPos pos) {
        this.onDefaultCrystallize(level, pos);

        if (!level.isClientSide) {
            AeroSoundEvents.LEVITITE_BLEND_CRYSTALLIZE.play(level, null, pos, 1.0f, 1.0f);
            LevititeBlendHelper.spawnParticles(level, pos, ParticleTypes.FLAME, 30);
            AeroAdvancements.UNIDENTIFIED_FLOATING_OBJECT.awardToNearby(pos, level);
        }
    }

    @Override
    public void onCrystallizationFail(final Level level, final BlockPos pos, final int attempts, final boolean isDormant) {
        LevititeBlendHelper.spawnParticles(level, pos, ParticleTypes.SMOKE, 15);
    }

    @Override
    public BlockState getCrystalBlockState(final Level level, final BlockPos pos) {
        return AeroBlocks.LEVITITE.getDefaultState();
    }

    public boolean canSpreadTo(final FluidState state) {
        return state.is(LevititeBlendHelper.getFluid());
    }

    public static int[] getWeights(final Level level, final BlockPos pos) {
        final int[] weights = new int[2];
        for (final Direction dir : Direction.values()) {
            if (level.getBlockState(pos.relative(dir)).is(AeroTags.BlockTags.LEVITITE_ADJACENT_CATALYZER)) {
                weights[0]++;
            }
            if (level.getBlockState(pos.relative(dir)).is(AeroTags.BlockTags.LEVITITE_ADJACENT_SOUL_CATALYZER)) {
                weights[1]++;
            }
        }
        return weights;
    }

    public static CrystalPropagationContext getRandomContext(final CrystalPropagationContext self, final Level level, final BlockPos pos) {
        final int[] weights = getWeights(level, pos);
        final int sum = Arrays.stream(weights).sum();
        if (sum == 0) return self;
        if (weights[0] > 0 && (weights[1] == 0 || level.getRandom().nextInt(sum) < weights[0])) {
            return AeroLevititeBlendPropagationContexts.STANDARD_CONTEXT.get();
        } else {
            return AeroLevititeBlendPropagationContexts.SOUL_CONTEXT.get();
        }
    }

    @Override
    public CrystalPropagationContext getContextForSpread(final Level level, final BlockPos pos) {
        return getRandomContext(this, level, pos);
    }

    @Override
    public boolean matchesCatalystBlock(final Level level, final BlockPos fluidPos, final BlockPos catalystPos, final BlockState catalystState) {
        if (catalystState.getBlock() instanceof BlazeBurnerBlock
                && catalystState.getValue(BlazeBurnerBlock.HEAT_LEVEL).isAtLeast(BlazeBurnerBlock.HeatLevel.SMOULDERING)) {
            return true;
        }
        if (catalystState.getBlock() instanceof LitBlazeBurnerBlock) {
            return catalystState.getValue(LitBlazeBurnerBlock.FLAME_TYPE) == LitBlazeBurnerBlock.FlameType.REGULAR;
        }

        final Optional<Boolean> litState = catalystState.getOptionalValue(BlockStateProperties.LIT);
        if (litState.isPresent() && !litState.get()) {
            return false;
        }

        return catalystState.is(this.getCatalyzerTag());
    }

    @Override
    public boolean matchesCatalystItem(final Level level, final BlockPos fluidPos, final ItemStack catalystItem) {
        return catalystItem.is(AeroTags.ItemTags.LEVITITE_CATALYZER);
    }

    @Override
    public TagKey<Block> getCatalyzerTag() {
        return AeroTags.BlockTags.LEVITITE_CATALYZER;
    }
}
