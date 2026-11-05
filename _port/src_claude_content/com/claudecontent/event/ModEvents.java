/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.SpawnPlacements$Type
 *  net.minecraft.world.entity.monster.Monster
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent
 *  net.minecraftforge.event.entity.SpawnPlacementRegisterEvent$Operation
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.claudecontent.event;

import com.claudecontent.entity.AshReaperEntity;
import com.claudecontent.entity.CinderStalkerEntity;
import com.claudecontent.entity.LumileuEntity;
import com.claudecontent.entity.MossColossusEntity;
import com.claudecontent.entity.MosslingEntity;
import com.claudecontent.registry.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="claude_content", bus=Mod.EventBusSubscriber.Bus.MOD)
public final class ModEvents {
    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType)ModEntities.ASH_REAPER.get(), AshReaperEntity.createAttributes().m_22265_());
        event.put((EntityType)ModEntities.CINDER_STALKER.get(), CinderStalkerEntity.createAttributes().m_22265_());
        event.put((EntityType)ModEntities.MOSSLING.get(), MosslingEntity.createAttributes().m_22265_());
        event.put((EntityType)ModEntities.MOSS_COLOSSUS.get(), MossColossusEntity.createAttributes().m_22265_());
        event.put((EntityType)ModEntities.LUMILEU.get(), LumileuEntity.createAttributes().m_22265_());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register((EntityType)ModEntities.CINDER_STALKER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::m_219019_, SpawnPlacementRegisterEvent.Operation.OR);
        event.register((EntityType)ModEntities.MOSSLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::m_217057_, SpawnPlacementRegisterEvent.Operation.OR);
        event.register((EntityType)ModEntities.LUMILEU.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::m_217057_, SpawnPlacementRegisterEvent.Operation.OR);
    }

    private ModEvents() {
    }
}

