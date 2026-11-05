/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.claudecontent.registry;

import com.claudecontent.entity.AshReaperEntity;
import com.claudecontent.entity.CinderStalkerEntity;
import com.claudecontent.entity.LumileuEntity;
import com.claudecontent.entity.MossColossusEntity;
import com.claudecontent.entity.MosslingEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"claude_content");
    public static final RegistryObject<EntityType<AshReaperEntity>> ASH_REAPER = ENTITIES.register("ash_reaper", () -> EntityType.Builder.lambda$createNothing$0(AshReaperEntity::new, (MobCategory)MobCategory.MONSTER).lambda$createNothing$0(1.1f, 3.3f).fireImmune().lambda$createNothing$0(10).lambda$createNothing$0("ash_reaper"));
    public static final RegistryObject<EntityType<MosslingEntity>> MOSSLING = ENTITIES.register("mossling", () -> EntityType.Builder.lambda$createNothing$0(MosslingEntity::new, (MobCategory)MobCategory.CREATURE).lambda$createNothing$0(0.9f, 0.9f).lambda$createNothing$0(8).lambda$createNothing$0("mossling"));
    public static final RegistryObject<EntityType<MossColossusEntity>> MOSS_COLOSSUS = ENTITIES.register("moss_colossus", () -> EntityType.Builder.lambda$createNothing$0(MossColossusEntity::new, (MobCategory)MobCategory.MONSTER).lambda$createNothing$0(1.8f, 3.4f).lambda$createNothing$0(10).fireImmune().lambda$createNothing$0("moss_colossus"));
    public static final RegistryObject<EntityType<LumileuEntity>> LUMILEU = ENTITIES.register("lumileu", () -> EntityType.Builder.lambda$createNothing$0(LumileuEntity::new, (MobCategory)MobCategory.CREATURE).lambda$createNothing$0(0.8f, 0.8f).lambda$createNothing$0(8).lambda$createNothing$0("lumileu"));
    public static final RegistryObject<EntityType<CinderStalkerEntity>> CINDER_STALKER = ENTITIES.register("cinder_stalker", () -> EntityType.Builder.lambda$createNothing$0(CinderStalkerEntity::new, (MobCategory)MobCategory.MONSTER).lambda$createNothing$0(1.4f, 0.9f).fireImmune().lambda$createNothing$0(8).lambda$createNothing$0("cinder_stalker"));

    private ModEntities() {
    }
}

