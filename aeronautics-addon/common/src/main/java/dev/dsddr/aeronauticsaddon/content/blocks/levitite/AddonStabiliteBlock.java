package dev.dsddr.aeronauticsaddon.content.blocks.levitite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class AddonStabiliteBlock extends Block {
    private static final DustParticleOptions STABILITE_DUST = new DustParticleOptions(new Vector3f(0.82f, 0.42f, 1.0f), 0.95f);

    public AddonStabiliteBlock(final BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(final BlockState state, final Level level, final BlockPos pos, final RandomSource random) {
        if (random.nextFloat() < 0.5f) {
            final double x = pos.getX() + 0.15 + random.nextDouble() * 0.7;
            final double y = pos.getY() + 1.01 + random.nextDouble() * 0.12;
            final double z = pos.getZ() + 0.15 + random.nextDouble() * 0.7;
            level.addParticle(STABILITE_DUST, x, y, z,
                    (random.nextDouble() - 0.5) * 0.008,
                    0.004 + random.nextDouble() * 0.01,
                    (random.nextDouble() - 0.5) * 0.008);
        }

        if (random.nextFloat() < 0.35f) {
            final Direction face = Direction.getRandom(random);
            final double x = pos.getX() + 0.5 + face.getStepX() * 0.53
                    + (face.getAxis() == Direction.Axis.X ? 0.0 : (random.nextDouble() - 0.5) * 0.7);
            final double y = pos.getY() + 0.2 + random.nextDouble() * 0.65;
            final double z = pos.getZ() + 0.5 + face.getStepZ() * 0.53
                    + (face.getAxis() == Direction.Axis.Z ? 0.0 : (random.nextDouble() - 0.5) * 0.7);
            level.addParticle(STABILITE_DUST, x, y, z,
                    face.getStepX() * 0.006 + (random.nextDouble() - 0.5) * 0.008,
                    0.003 + random.nextDouble() * 0.01,
                    face.getStepZ() * 0.006 + (random.nextDouble() - 0.5) * 0.008);
        }
    }
}
