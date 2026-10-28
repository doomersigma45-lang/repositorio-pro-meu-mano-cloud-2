package com.netheritesupremo;

import com.netheritesupremo.entity.GoldenZombie;
import com.netheritesupremo.entity.Necromancer;
import com.netheritesupremo.entity.NetherSpider;
import com.netheritesupremo.entity.NetheriteGolem;
import net.minecraft.world.entity.monster.Monster;
import com.netheritesupremo.entity.NetheriteKnight;
import com.netheritesupremo.entity.Netherbot;
import com.netheritesupremo.entity.NetheriteBombRobot;
import com.netheritesupremo.entity.NetheriteRobotRainbowBoss;
import com.netheritesupremo.entity.Precursor;
import com.netheritesupremo.entity.Robot;
import com.netheritesupremo.entity.UmbralMerchant;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.Villager;
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
        event.put(ModEntities.NETHER_SPIDER.get(), NetherSpider.createNetherSpiderAttributes().build());
        event.put(ModEntities.NECROMANCER.get(), Necromancer.createNecromancerAttributes().build());
        event.put(ModEntities.UMBRAL_MERCHANT.get(),
                Villager.createAttributes().add(Attributes.MAX_HEALTH, 40.0D).build());
        event.put(ModEntities.BOMB_ROBOT.get(), NetheriteBombRobot.createBombRobotAttributes().build());
        event.put(ModEntities.NETHERBOT.get(), Netherbot.createNetherbotAttributes().build());
        event.put(ModEntities.RAINBOW_BOSS.get(), NetheriteRobotRainbowBoss.createBossAttributes().build());
        event.put(ModEntities.PRECURSOR.get(), Precursor.createPrecursorAttributes().build());
        event.put(ModEntities.GIANT_GLOW_SQUID.get(), com.netheritesupremo.entity.GiantGlowSquid.createBossAttributes().build());
        event.put(ModEntities.GIANT_GLOW_SQUID_BABY.get(), com.netheritesupremo.entity.GiantGlowSquidBaby.createBabyAttributes().build());
        event.put(ModEntities.ROBOT.get(), Robot.createRobotAttributes().build());
        event.put(ModEntities.GOLDEN_ZOMBIE.get(), GoldenZombie.createGoldenZombieAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.NETHERITE_KNIGHT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteKnight::checkKnightSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.BOMB_ROBOT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.NETHERBOT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.GIANT_GLOW_SQUID.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.entity.GiantGlowSquid::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.NETHER_SPIDER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }
}
