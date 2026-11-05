/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.level.levelgen.feature.Feature
 *  net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package com.claudecontent.registry;

import com.claudecontent.worldgen.RootsSanctuaryFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModFeatures {
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create((ResourceKey)Registries.FEATURE, (String)"claude_content");
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ROOTS_SANCTUARY = FEATURES.register("roots_sanctuary", RootsSanctuaryFeature::new);

    private ModFeatures() {
    }
}

