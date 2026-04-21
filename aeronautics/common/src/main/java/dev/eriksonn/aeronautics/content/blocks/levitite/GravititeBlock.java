package dev.eriksonn.aeronautics.content.blocks.levitite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class GravititeBlock extends Block {
    public GravititeBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (random.nextFloat() < 0.55f) {
            final double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
            final double y = pos.getY() + 1.01 + random.nextDouble() * 0.12;
            final double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            level.addParticle(ParticleTypes.END_ROD, x, y, z,
                    (random.nextDouble() - 0.5) * 0.01,
                    0.01 + random.nextDouble() * 0.016,
                    (random.nextDouble() - 0.5) * 0.01);
        }

        if (random.nextFloat() < 0.35f) {
            final Direction face = Direction.getRandom(random);
            final double x = pos.getX() + 0.5 + face.getStepX() * 0.53 + (face.getAxis() == Direction.Axis.X ? 0.0 : (random.nextDouble() - 0.5) * 0.7);
            final double y = pos.getY() + 0.2 + random.nextDouble() * 0.65;
            final double z = pos.getZ() + 0.5 + face.getStepZ() * 0.53 + (face.getAxis() == Direction.Axis.Z ? 0.0 : (random.nextDouble() - 0.5) * 0.7);
            level.addParticle(ParticleTypes.END_ROD, x, y, z,
                    face.getStepX() * 0.01 + (random.nextDouble() - 0.5) * 0.01,
                    0.004 + random.nextDouble() * 0.012,
                    face.getStepZ() * 0.01 + (random.nextDouble() - 0.5) * 0.01);
        }

        if (random.nextInt(4) == 0) {
            level.playLocalSound(
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    SoundEvents.BEACON_AMBIENT,
                    SoundSource.BLOCKS,
                    0.95f,
                    1.0f + random.nextFloat() * 0.05f,
                    false
            );
        }
    }
}
