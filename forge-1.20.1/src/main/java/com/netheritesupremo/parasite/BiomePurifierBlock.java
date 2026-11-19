package com.netheritesupremo.parasite;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.levelgen.Heightmap;
import com.netheritesupremo.skeleton.ModEffects;

/**
 * Purificador de Bioma (inspirado no Biome Purifier do Scape and Run: Parasites): ao ser colocado, durante uns 8
 * segundos desfaz a infeccao num raio de 24 blocos (terra, madeira, folhas, arvores-coracao e sangue viram terra,
 * carvalho e agua / ar) e cura Parasitado e Infestacao de quem estiver na area. Some no fim.
 */
public class BiomePurifierBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_15;
    private static final int RADIUS = 24;

    public BiomePurifierBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean moving) {
        if (!level.isClientSide && !oldState.is(this)) {
            level.scheduleTick(pos, this, 10);
            level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.5F, 1.2F);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        purify(level, pos, random);
        int age = state.getValue(AGE);
        if (age >= 15) {
            level.playSound(null, pos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.5F, 1.2F);
            level.removeBlock(pos, false);
            return;
        }
        level.setBlock(pos, state.setValue(AGE, age + 1), 3);
        level.scheduleTick(pos, this, 10);
    }

    private static void purify(ServerLevel level, BlockPos center, RandomSource random) {
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, center.getX() + 0.5D, center.getY() + 1.0D, center.getZ() + 0.5D,
                12, 0.5D, 0.5D, 0.5D, 0.1D);
        for (int i = 0; i < 300; i++) {
            double ang = random.nextDouble() * Math.PI * 2.0D;
            double dist = Math.sqrt(random.nextDouble()) * RADIUS;
            int x = center.getX() + (int) Math.round(Math.cos(ang) * dist);
            int z = center.getZ() + (int) Math.round(Math.sin(ang) * dist);
            if (!level.hasChunkAt(new BlockPos(x, center.getY(), z))) {
                continue;
            }
            int top = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
            for (int y = top; y >= Math.max(level.getMinBuildHeight(), top - 22); y--) {
                BlockPos p = new BlockPos(x, y, z);
                BlockState s = level.getBlockState(p);
                if (s.is(ParasiteTree.DIRT.get())) {
                    level.setBlock(p, Blocks.DIRT.defaultBlockState(), 3);
                } else if (s.is(ParasiteTree.LOG.get())) {
                    level.setBlock(p, Blocks.OAK_LOG.defaultBlockState(), 3);
                } else if (s.is(ParasiteTree.LEAVES.get())) {
                    level.setBlock(p, Blocks.OAK_LEAVES.defaultBlockState().setValue(LeavesBlock.PERSISTENT, true), 3);
                } else if (s.is(ParasiteTree.HEART.get())) {
                    level.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                } else if (s.is(ParasiteBlood.BLOCK.get())) {
                    level.setBlock(p, s.getFluidState().isSource() ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
        for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class,
                new net.minecraft.world.phys.AABB(center).inflate(RADIUS),
                e -> e.hasEffect(ModEffects.PARASITIZED.get()) || e.hasEffect(ModEffects.INFESTATION.get()))) {
            e.removeEffect(ModEffects.PARASITIZED.get());
            e.removeEffect(ModEffects.INFESTATION.get());
        }
    }
}
