package com.netheritesupremo.claudecontent.event;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.entity.AshReaperEntity;
import com.netheritesupremo.claudecontent.entity.CinderStalkerEntity;
import com.netheritesupremo.claudecontent.entity.MosslingEntity;
import com.netheritesupremo.claudecontent.entity.MossColossusEntity;
import com.netheritesupremo.claudecontent.entity.LumileuEntity;
import com.netheritesupremo.claudecontent.registry.ModEntities;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Eventos do barramento do mod (registro de atributos e regras de spawn). */
@Mod.EventBusSubscriber(modid = ClaudeContent.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModEvents {

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.ASH_REAPER.get(), AshReaperEntity.createAttributes().build());
        event.put(ModEntities.CINDER_STALKER.get(), CinderStalkerEntity.createAttributes().build());
        event.put(ModEntities.MOSSLING.get(), MosslingEntity.createAttributes().build());
        event.put(ModEntities.MOSS_COLOSSUS.get(), MossColossusEntity.createAttributes().build());
        event.put(ModEntities.LUMILEU.get(), LumileuEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        // Espreitador: spawna em qualquer luminosidade (Nether e desertos).
        event.register(ModEntities.CINDER_STALKER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);

        // Musgolim: spawn simples no chão.
        event.register(ModEntities.MOSSLING.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.LUMILEU.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }

    private ModEvents() {}
}
