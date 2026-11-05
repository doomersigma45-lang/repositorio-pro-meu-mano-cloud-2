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
    public static final RegistryObject<EntityType<AshReaperEntity>> ASH_REAPER = ENTITIES.register("ash_reaper", () -> EntityType.Builder.m_20704_(AshReaperEntity::new, (MobCategory)MobCategory.MONSTER).m_20699_(1.1f, 3.3f).m_20719_().m_20702_(10).m_20712_("ash_reaper"));
    public static final RegistryObject<EntityType<MosslingEntity>> MOSSLING = ENTITIES.register("mossling", () -> EntityType.Builder.m_20704_(MosslingEntity::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.9f, 0.9f).m_20702_(8).m_20712_("mossling"));
    public static final RegistryObject<EntityType<MossColossusEntity>> MOSS_COLOSSUS = ENTITIES.register("moss_colossus", () -> EntityType.Builder.m_20704_(MossColossusEntity::new, (MobCategory)MobCategory.MONSTER).m_20699_(1.8f, 3.4f).m_20702_(10).m_20719_().m_20712_("moss_colossus"));
    public static final RegistryObject<EntityType<LumileuEntity>> LUMILEU = ENTITIES.register("lumileu", () -> EntityType.Builder.m_20704_(LumileuEntity::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.8f, 0.8f).m_20702_(8).m_20712_("lumileu"));
    public static final RegistryObject<EntityType<CinderStalkerEntity>> CINDER_STALKER = ENTITIES.register("cinder_stalker", () -> EntityType.Builder.m_20704_(CinderStalkerEntity::new, (MobCategory)MobCategory.MONSTER).m_20699_(1.4f, 0.9f).m_20719_().m_20702_(8).m_20712_("cinder_stalker"));

    private ModEntities() {
    }
}

