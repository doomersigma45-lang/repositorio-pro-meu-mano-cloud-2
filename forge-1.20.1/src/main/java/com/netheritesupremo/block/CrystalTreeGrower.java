package com.netheritesupremo.block;

import com.netheritesupremo.NetheriteSupremo;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;

/** Faz a muda cristalina virar a arvore (feature netheritesupremo:crystal_tree). */
public class CrystalTreeGrower extends AbstractTreeGrower {
    public static final ResourceKey<ConfiguredFeature<?, ?>> TREE = ResourceKey.create(Registries.CONFIGURED_FEATURE,
            new ResourceLocation(NetheriteSupremo.MODID, "crystal_tree"));

    @Nullable
    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean hasFlowers) {
        return TREE;
    }
}
