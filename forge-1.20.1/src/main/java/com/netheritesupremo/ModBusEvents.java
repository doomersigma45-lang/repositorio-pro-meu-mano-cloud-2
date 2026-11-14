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
        event.put(ModEntities.SKELETON_KING.get(), com.netheritesupremo.entity.SkeletonKing.createAttributes().build());
        event.put(ModEntities.SKELETON_VILLAGER.get(), com.netheritesupremo.skeleton.SkeletonVillager.createAttributes().build());
        event.put(ModEntities.WAR_TANK.get(), com.netheritesupremo.entity.WarTank.createAttributes().build());
        event.put(ModEntities.GIANT_MOSQUITO.get(), com.netheritesupremo.mob.GiantMosquito.createAttributes().build());
        event.put(ModEntities.STRONGEST_ZOMBIE.get(), com.netheritesupremo.mob.StrongestZombie.createAttributes().build());
        event.put(ModEntities.STALKER.get(), com.netheritesupremo.mob.Stalker.createAttributes().build());
        event.put(ModEntities.COLOSSAL_BEETLE.get(), com.netheritesupremo.mob.ColossalBeetle.createAttributes().build());
        event.put(ModEntities.FUNGUS.get(), com.netheritesupremo.mob.Fungus.createAttributes().build());
        event.put(ModEntities.ANCIENT_GUARDIAN.get(), com.netheritesupremo.mob.AncientGuardian.createAttributes().build());
        event.put(ModEntities.ANCESTRAL_GUARDIAN.get(), com.netheritesupremo.mob.AncestralGuardian.createAttributes().build());
        event.put(ModEntities.THE_ABYSS.get(), com.netheritesupremo.mob.TheAbyss.createAttributes().build());
        event.put(ModEntities.ABYSS_SPAWN.get(), com.netheritesupremo.mob.AbyssSpawn.createAttributes().build());
        event.put(ModEntities.STONE_GUARDIAN.get(), com.netheritesupremo.mob.StoneGuardian.createAttributes().build());
        event.put(ModEntities.DIGGER.get(), com.netheritesupremo.mob.Digger.createAttributes().build());
        event.put(ModEntities.AUTOMATON.get(), com.netheritesupremo.mob.Automaton.createAttributes().build());
        event.put(ModEntities.ANCIENT_TURTLE.get(), com.netheritesupremo.mob.AncientTurtle.createAttributes().build());
        event.put(ModEntities.GOOSE.get(), com.netheritesupremo.mob.Goose.createAttributes().build());
        event.put(ModEntities.NETHERITE_CHICKEN.get(), com.netheritesupremo.mob.NetheriteChicken.createAttributes().build());
        event.put(ModEntities.COLOSSAL_SPIDER.get(), com.netheritesupremo.mob.ColossalSpider.createAttributes().build());
        event.put(ModEntities.RED_STEVE.get(), com.netheritesupremo.entity.RedSteve.createAttributes().build());
        event.put(ModEntities.UMBRAL_MERCHANT.get(),
                Villager.createAttributes().add(Attributes.MAX_HEALTH, 40.0D).build());
        event.put(ModEntities.BOMB_ROBOT.get(), NetheriteBombRobot.createBombRobotAttributes().build());
        event.put(ModEntities.NETHERBOT.get(), Netherbot.createNetherbotAttributes().build());
        event.put(ModEntities.RAINBOW_BOSS.get(), NetheriteRobotRainbowBoss.createBossAttributes().build());
        event.put(ModEntities.PRECURSOR.get(), Precursor.createPrecursorAttributes().build());
        event.put(ModEntities.GIANT_GLOW_SQUID.get(), com.netheritesupremo.entity.GiantGlowSquid.createBossAttributes().build());
        event.put(ModEntities.GIANT_GLOW_SQUID_BABY.get(), com.netheritesupremo.entity.GiantGlowSquidBaby.createBabyAttributes().build());
        event.put(ModEntities.NINJA.get(), com.netheritesupremo.entity.Ninja.createNinjaAttributes().build());
        event.put(ModEntities.SAMURAI.get(), com.netheritesupremo.entity.Samurai.createSamuraiAttributes().build());
        event.put(ModEntities.THIEF.get(), com.netheritesupremo.entity.Thief.createThiefAttributes().build());
        event.put(ModEntities.HYBRID_VILLAGER.get(), com.netheritesupremo.entity.HybridVillager.createHybridAttributes().build());
        event.put(ModEntities.PRECURSOR_VILLAGER.get(), com.netheritesupremo.entity.PrecursorVillager.createAttributes().build());
        event.put(ModEntities.ROBOT.get(), Robot.createRobotAttributes().build());
        event.put(ModEntities.PRECURSOR_SOLDIER.get(), com.netheritesupremo.entity.PrecursorSoldier.createAttributes().build());
        event.put(ModEntities.ALIEN_SHIP.get(), com.netheritesupremo.entity.AlienShip.createAttributes().build());
        event.put(ModEntities.AQUATIC_ALIEN.get(), com.netheritesupremo.entity.AquaticAlien.createAlienAttributes().build());
        event.put(ModEntities.ANOMALOCARIS.get(), com.netheritesupremo.entity.Anomalocaris.createAttributes().build());
        event.put(ModEntities.TRILOBITE.get(), com.netheritesupremo.entity.Trilobite.createAttributes().build());
        event.put(ModEntities.OPABINIA.get(), com.netheritesupremo.entity.Opabinia.createAttributes().build());
        event.put(ModEntities.GOLDEN_ZOMBIE.get(), GoldenZombie.createGoldenZombieAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(ModEntities.GIANT_MOSQUITO.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.STRONGEST_ZOMBIE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.STALKER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.FUNGUS.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.COLOSSAL_BEETLE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.mob.ColossalBeetle::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.ANCIENT_GUARDIAN.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.mob.AncientGuardian::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.DIGGER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.mob.Digger::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.AUTOMATON.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.mob.Automaton::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.ANCIENT_TURTLE.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.mob.AncientTurtle::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.GOOSE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.mob.Goose::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.COLOSSAL_SPIDER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.RED_STEVE.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.entity.RedSteve::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
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
        event.register(ModEntities.NINJA.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.SAMURAI.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.entity.Samurai::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.THIEF.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, com.netheritesupremo.entity.Thief::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.PRECURSOR_VILLAGER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.ROBOT.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, net.minecraft.world.entity.Mob::checkMobSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.AQUATIC_ALIEN.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.entity.AquaticAlien::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.ANOMALOCARIS.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.entity.CambrianCreature::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.TRILOBITE.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.entity.CambrianCreature::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.OPABINIA.get(), SpawnPlacements.Type.IN_WATER,
                Heightmap.Types.OCEAN_FLOOR, com.netheritesupremo.entity.CambrianCreature::checkSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
        event.register(ModEntities.NETHER_SPIDER.get(), SpawnPlacements.Type.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Monster::checkAnyLightMonsterSpawnRules,
                SpawnPlacementRegisterEvent.Operation.OR);
    }
}
