package com.netheritesupremo;

import com.netheritesupremo.entity.Necromancer;
import com.netheritesupremo.entity.NetheriteGolem;
import com.netheritesupremo.entity.NetheriteKnight;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ModBusEvents {
    private ModBusEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.NETHERITE_GOLEM.get(), NetheriteGolem.createGolemAttributes().build());
        event.put(ModEntities.NETHERITE_KNIGHT.get(), NetheriteKnight.createKnightAttributes().build());
        event.put(ModEntities.NECROMANCER.get(), Necromancer.createNecromancerAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.NETHERITE_KNIGHT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteKnight::checkKnightSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }
}
