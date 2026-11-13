package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.GoldenZombie;
import com.netheritesupremo.entity.Necromancer;
import com.netheritesupremo.entity.NetherSpider;
import com.netheritesupremo.entity.NetheriteGolem;
import com.netheritesupremo.entity.NetheriteBombRobot;
import com.netheritesupremo.entity.Netherbot;
import com.netheritesupremo.entity.NetheriteKnight;
import com.netheritesupremo.entity.GiantGlowSquid;
import com.netheritesupremo.entity.GiantGlowSquidBaby;
import com.netheritesupremo.entity.GlowInkBall;
import com.netheritesupremo.entity.Precursor;
import com.netheritesupremo.entity.NetheriteRobotRainbowBoss;
import com.netheritesupremo.entity.Robot;
import com.netheritesupremo.entity.UmbralMerchant;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NetheriteSupremo.MODID);

    public static final RegistryObject<EntityType<NetheriteGolem>> NETHERITE_GOLEM = ENTITIES.register(
            "netherite_golem",
            () -> EntityType.Builder.of(NetheriteGolem::new, MobCategory.MISC)
                    .sized(1.75F, 3.4F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("netherite_golem"));

    public static final RegistryObject<EntityType<NetheriteKnight>> NETHERITE_KNIGHT = ENTITIES.register(
            "netherite_knight",
            () -> EntityType.Builder.of(NetheriteKnight::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("netherite_knight"));

    public static final RegistryObject<EntityType<Necromancer>> NECROMANCER = ENTITIES.register(
            "necromancer",
            () -> EntityType.Builder.of(Necromancer::new, MobCategory.MONSTER)
                    .sized(0.72F, 2.4F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("necromancer"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.SkeletonKing>> SKELETON_KING = ENTITIES.register(
            "skeleton_king",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.SkeletonKing::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.2F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("skeleton_king"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.RedSteve>> RED_STEVE = ENTITIES.register(
            "red_steve",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.RedSteve::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(10)
                    .build("red_steve"));

    public static final RegistryObject<EntityType<NetherSpider>> NETHER_SPIDER = ENTITIES.register(
            "nether_spider",
            () -> EntityType.Builder.of(NetherSpider::new, MobCategory.MONSTER)
                    .sized(1.82F, 1.17F) // aranha comum (1.4 x 0.9) + 30%
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("nether_spider"));

    public static final RegistryObject<EntityType<GoldenZombie>> GOLDEN_ZOMBIE = ENTITIES.register(
            "golden_zombie",
            () -> EntityType.Builder.of(GoldenZombie::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("golden_zombie"));

    public static final RegistryObject<EntityType<UmbralMerchant>> UMBRAL_MERCHANT = ENTITIES.register(
            "umbral_merchant",
            () -> EntityType.Builder.of(UmbralMerchant::new, MobCategory.MISC)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("umbral_merchant"));

    public static final RegistryObject<EntityType<Robot>> ROBOT = ENTITIES.register(
            "robot",
            () -> EntityType.Builder.of(Robot::new, MobCategory.CREATURE)
                    .sized(0.72F, 2.16F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("robot"));

    public static final RegistryObject<EntityType<NetheriteBombRobot>> BOMB_ROBOT = ENTITIES.register(
            "netherite_bomb_robot",
            () -> EntityType.Builder.of(NetheriteBombRobot::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("netherite_bomb_robot"));

    public static final RegistryObject<EntityType<Netherbot>> NETHERBOT = ENTITIES.register(
            "netherbot",
            () -> EntityType.Builder.of(Netherbot::new, MobCategory.MONSTER)
                    .sized(0.96F, 2.9F)
                    .fireImmune()
                    .clientTrackingRange(12)
                    .updateInterval(2)
                    .build("netherbot"));

    public static final RegistryObject<EntityType<NetheriteRobotRainbowBoss>> RAINBOW_BOSS = ENTITIES.register(
            "netherite_robot_rainbow_boss",
            () -> EntityType.Builder.of(NetheriteRobotRainbowBoss::new, MobCategory.MONSTER)
                    .sized(1.4F, 4.2F)
                    .fireImmune()
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build("netherite_robot_rainbow_boss"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.PrecursorSoldier>> PRECURSOR_SOLDIER = ENTITIES.register(
            "precursor_soldier",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.PrecursorSoldier::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.1F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .updateInterval(2)
                    .build("precursor_soldier"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.AlienShip>> ALIEN_SHIP = ENTITIES.register(
            "alien_ship",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.AlienShip::new, MobCategory.MONSTER)
                    .sized(3.0F, 1.2F)
                    .fireImmune()
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build("alien_ship"));

    public static final RegistryObject<EntityType<Precursor>> PRECURSOR = ENTITIES.register(
            "precursor",
            () -> EntityType.Builder.of(Precursor::new, MobCategory.MONSTER)
                    .sized(1.6F, 4.8F)
                    .clientTrackingRange(16)
                    .updateInterval(2)
                    .build("precursor"));

    public static final RegistryObject<EntityType<GiantGlowSquid>> GIANT_GLOW_SQUID = ENTITIES.register(
            "giant_glow_squid",
            () -> EntityType.Builder.of(GiantGlowSquid::new, MobCategory.WATER_CREATURE)
                    .sized(3.6F, 3.6F)
                    .clientTrackingRange(10)
                    .build("giant_glow_squid"));

    public static final RegistryObject<EntityType<GiantGlowSquidBaby>> GIANT_GLOW_SQUID_BABY = ENTITIES.register(
            "giant_glow_squid_baby",
            () -> EntityType.Builder.of(GiantGlowSquidBaby::new, MobCategory.WATER_CREATURE)
                    .sized(1.6F, 1.6F)
                    .clientTrackingRange(10)
                    .build("giant_glow_squid_baby"));

    public static final RegistryObject<EntityType<GlowInkBall>> GLOW_INK_BALL = ENTITIES.register(
            "glow_ink_ball",
            () -> EntityType.Builder.<GlowInkBall>of(GlowInkBall::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(4)
                    .updateInterval(2)
                    .build("glow_ink_ball"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.ShurikenEntity>> SHURIKEN = ENTITIES.register(
            "shuriken",
            () -> EntityType.Builder.<com.netheritesupremo.entity.ShurikenEntity>of(
                            com.netheritesupremo.entity.ShurikenEntity::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("shuriken"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Ninja>> NINJA = ENTITIES.register(
            "ninja",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Ninja::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("ninja"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Samurai>> SAMURAI = ENTITIES.register(
            "samurai",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Samurai::new, MobCategory.MONSTER)
                    .sized(0.72F, 2.34F)
                    .clientTrackingRange(10)
                    .build("samurai"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Thief>> THIEF = ENTITIES.register(
            "thief",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Thief::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("thief"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.AquaticAlien>> AQUATIC_ALIEN = ENTITIES.register(
            "aquatic_alien",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.AquaticAlien::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("aquatic_alien"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Anomalocaris>> ANOMALOCARIS = ENTITIES.register(
            "anomalocaris",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Anomalocaris::new, MobCategory.MONSTER)
                    .sized(1.8F, 0.9F)
                    .clientTrackingRange(10)
                    .build("anomalocaris"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Trilobite>> TRILOBITE = ENTITIES.register(
            "trilobite",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Trilobite::new, MobCategory.WATER_AMBIENT)
                    .sized(0.9F, 0.5F)
                    .clientTrackingRange(8)
                    .build("trilobite"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.Opabinia>> OPABINIA = ENTITIES.register(
            "opabinia",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.Opabinia::new, MobCategory.WATER_CREATURE)
                    .sized(1.0F, 0.6F)
                    .clientTrackingRange(8)
                    .build("opabinia"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.HybridVillager>> HYBRID_VILLAGER = ENTITIES.register(
            "hybrid_villager",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.HybridVillager::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("hybrid_villager"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.PrecursorVillager>> PRECURSOR_VILLAGER = ENTITIES.register(
            "precursor_villager",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.PrecursorVillager::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("precursor_villager"));

    public static final RegistryObject<EntityType<com.netheritesupremo.skeleton.SkeletonVillager>> SKELETON_VILLAGER = ENTITIES.register(
            "skeleton_villager",
            () -> EntityType.Builder.of(com.netheritesupremo.skeleton.SkeletonVillager::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(10)
                    .build("skeleton_villager"));

    public static final RegistryObject<EntityType<com.netheritesupremo.skeleton.OldGunBullet>> OLD_GUN_BULLET = ENTITIES.register(
            "old_gun_bullet",
            () -> EntityType.Builder.<com.netheritesupremo.skeleton.OldGunBullet>of(com.netheritesupremo.skeleton.OldGunBullet::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(5)
                    .build("old_gun_bullet"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.WarTank>> WAR_TANK = ENTITIES.register(
            "war_tank",
            () -> EntityType.Builder.of(com.netheritesupremo.entity.WarTank::new, MobCategory.MISC)
                    .sized(2.2F, 2.0F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("war_tank"));

    public static final RegistryObject<EntityType<com.netheritesupremo.entity.TankShell>> TANK_SHELL = ENTITIES.register(
            "tank_shell",
            () -> EntityType.Builder.<com.netheritesupremo.entity.TankShell>of(com.netheritesupremo.entity.TankShell::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(6)
                    .updateInterval(5)
                    .build("tank_shell"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Goose>> GOOSE = ENTITIES.register(
            "goose",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Goose::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.9F)
                    .clientTrackingRange(8)
                    .build("goose"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.NetheriteChicken>> NETHERITE_CHICKEN = ENTITIES.register(
            "netherite_chicken",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.NetheriteChicken::new, MobCategory.CREATURE)
                    .sized(0.6F, 1.05F)
                    .fireImmune()
                    .clientTrackingRange(10)
                    .build("netherite_chicken"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.ColossalSpider>> COLOSSAL_SPIDER = ENTITIES.register(
            "colossal_spider",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.ColossalSpider::new, MobCategory.MONSTER)
                    .sized(2.9F, 1.7F)
                    .clientTrackingRange(12)
                    .build("colossal_spider"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.WebShot>> WEB_SHOT = ENTITIES.register(
            "web_shot",
            () -> EntityType.Builder.<com.netheritesupremo.mob.WebShot>of(com.netheritesupremo.mob.WebShot::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(6)
                    .updateInterval(5)
                    .build("web_shot"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.GiantMosquito>> GIANT_MOSQUITO = ENTITIES.register(
            "giant_mosquito",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.GiantMosquito::new, MobCategory.CREATURE)
                    .sized(1.4F, 1.2F)
                    .clientTrackingRange(10)
                    .build("giant_mosquito"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.StrongestZombie>> STRONGEST_ZOMBIE = ENTITIES.register(
            "strongest_zombie",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.StrongestZombie::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.9F)
                    .clientTrackingRange(10)
                    .build("strongest_zombie"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Stalker>> STALKER = ENTITIES.register(
            "stalker",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Stalker::new, MobCategory.MONSTER)
                    .sized(0.6F, 3.0F)
                    .clientTrackingRange(10)
                    .build("stalker"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.ColossalBeetle>> COLOSSAL_BEETLE = ENTITIES.register(
            "colossal_beetle",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.ColossalBeetle::new, MobCategory.MONSTER)
                    .sized(1.6F, 2.0F)
                    .clientTrackingRange(10)
                    .build("colossal_beetle"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Fungus>> FUNGUS = ENTITIES.register(
            "fungus",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Fungus::new, MobCategory.MONSTER)
                    .sized(1.3F, 2.0F)
                    .clientTrackingRange(10)
                    .build("fungus"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.AncientGuardian>> ANCIENT_GUARDIAN = ENTITIES.register(
            "ancient_guardian",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.AncientGuardian::new, MobCategory.MONSTER)
                    .sized(1.9F, 5.0F)
                    .fireImmune()
                    .clientTrackingRange(16)
                    .build("ancient_guardian"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.StoneGuardian>> STONE_GUARDIAN = ENTITIES.register(
            "stone_guardian",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.StoneGuardian::new, MobCategory.MONSTER)
                    .sized(0.8F, 1.5F)
                    .clientTrackingRange(10)
                    .build("stone_guardian"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Digger>> DIGGER = ENTITIES.register(
            "digger",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Digger::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.5F)
                    .clientTrackingRange(10)
                    .build("digger"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Automaton>> AUTOMATON = ENTITIES.register(
            "automaton",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Automaton::new, MobCategory.CREATURE)
                    .sized(1.2F, 2.5F)
                    .clientTrackingRange(10)
                    .build("automaton"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.AncientTurtle>> ANCIENT_TURTLE = ENTITIES.register(
            "ancient_turtle",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.AncientTurtle::new, MobCategory.WATER_CREATURE)
                    .sized(2.4F, 1.8F)
                    .clientTrackingRange(10)
                    .build("ancient_turtle"));

    private ModEntities() {
    }
}
