package com.netheritesupremo.claudecontent.registry;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.AshReaperEntity;
import com.netheritesupremo.claudecontent.entity.CinderStalkerEntity;
import com.netheritesupremo.claudecontent.entity.MosslingEntity;
import com.netheritesupremo.claudecontent.entity.MossColossusEntity;
import com.netheritesupremo.claudecontent.entity.LumileuEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ClaudeContent.MODID);

    /** BOSS: Ceifador de Cinzas */
    public static final RegistryObject<EntityType<AshReaperEntity>> ASH_REAPER = ENTITIES.register("ash_reaper",
            () -> EntityType.Builder.of(AshReaperEntity::new, MobCategory.MONSTER)
                    .sized(1.1F, 3.3F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("ash_reaper"));

    /** CRIATURA NEUTRA: Musgolim */
    public static final RegistryObject<EntityType<MosslingEntity>> MOSSLING = ENTITIES.register("mossling",
            () -> EntityType.Builder.of(MosslingEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(8)
                    .build("mossling"));

    /** Boss do Santuário das Raízes, separado do Ceifador de Cinzas. */
    public static final RegistryObject<EntityType<MossColossusEntity>> MOSS_COLOSSUS = ENTITIES.register("moss_colossus",
            () -> EntityType.Builder.of(MossColossusEntity::new, MobCategory.MONSTER)
                    .sized(1.8F, 3.4F).clientTrackingRange(10).fireImmune().build("moss_colossus"));

    /** Criatura pacífica luminosa das florestas. */
    public static final RegistryObject<EntityType<LumileuEntity>> LUMILEU = ENTITIES.register("lumileu",
            () -> EntityType.Builder.of(LumileuEntity::new, MobCategory.CREATURE)
                    .sized(0.8F, 0.8F).clientTrackingRange(8).build("lumileu"));

    /** CRIATURA HOSTIL: Espreitador de Brasa */
    public static final RegistryObject<EntityType<CinderStalkerEntity>> CINDER_STALKER = ENTITIES.register("cinder_stalker",
            () -> EntityType.Builder.of(CinderStalkerEntity::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("cinder_stalker"));

    private ModEntities() {}
}
