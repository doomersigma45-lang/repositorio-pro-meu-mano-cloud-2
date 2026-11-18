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

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWitch>> PARASITE_WITCH = ENTITIES.register(
            "parasite_witch",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteWitch>of(com.netheritesupremo.parasite.ParasiteWitch::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10)
                    .build("parasite_witch"));

    public static final RegistryObject<EntityType<com.netheritesupremo.weapon.GunBullet>> GUN_BULLET = ENTITIES.register(
            "gun_bullet",
            () -> EntityType.Builder.<com.netheritesupremo.weapon.GunBullet>of(com.netheritesupremo.weapon.GunBullet::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build("gun_bullet"));

    public static final RegistryObject<EntityType<com.netheritesupremo.weapon.RocketProjectile>> ROCKET = ENTITIES.register(
            "rocket",
            () -> EntityType.Builder.<com.netheritesupremo.weapon.RocketProjectile>of(com.netheritesupremo.weapon.RocketProjectile::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .updateInterval(1)
                    .build("rocket"));

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

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.AncestralGuardian>> ANCESTRAL_GUARDIAN = ENTITIES.register(
            "ancestral_guardian",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.AncestralGuardian::new, MobCategory.MONSTER)
                    .sized(3.0F, 7.0F)
                    .clientTrackingRange(16)
                    .build("ancestral_guardian"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.TheAbyss>> THE_ABYSS = ENTITIES.register(
            "the_abyss",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.TheAbyss::new, MobCategory.MONSTER)
                    .sized(4.0F, 8.0F)
                    .fireImmune()
                    .clientTrackingRange(20)
                    .build("the_abyss"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.AbyssSpawn>> ABYSS_SPAWN = ENTITIES.register(
            "abyss_spawn",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.AbyssSpawn::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F)
                    .clientTrackingRange(8)
                    .build("abyss_spawn"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.WoodStalker>> WOOD_STALKER = ENTITIES.register(
            "wood_stalker",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.WoodStalker::new, MobCategory.MONSTER)
                    .sized(0.8F, 3.0F)
                    .clientTrackingRange(10)
                    .build("wood_stalker"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.AncientWood>> ANCIENT_WOOD = ENTITIES.register(
            "ancient_wood",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.AncientWood::new, MobCategory.MONSTER)
                    .sized(1.8F, 7.0F)
                    .clientTrackingRange(16)
                    .build("ancient_wood"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.FungusStalker>> FUNGUS_STALKER = ENTITIES.register(
            "fungus_stalker",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.FungusStalker::new, MobCategory.MONSTER)
                    .sized(1.0F, 3.0F)
                    .clientTrackingRange(10)
                    .build("fungus_stalker"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.Hunter>> HUNTER = ENTITIES.register(
            "hunter",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.Hunter::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(10)
                    .build("hunter"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWorm>> PARASITE_WORM = ENTITIES.register(
            "parasite_worm",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteWorm::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.3F)
                    .clientTrackingRange(8)
                    .build("parasite_worm"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteCow>> PARASITE_COW = ENTITIES.register(
            "parasite_cow",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteCow::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.4F)
                    .clientTrackingRange(8)
                    .build("parasite_cow"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasitePig>> PARASITE_PIG = ENTITIES.register(
            "parasite_pig",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasitePig::new, MobCategory.MONSTER)
                    .sized(0.9F, 0.9F)
                    .clientTrackingRange(8)
                    .build("parasite_pig"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteChicken>> PARASITE_CHICKEN = ENTITIES.register(
            "parasite_chicken",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteChicken::new, MobCategory.MONSTER)
                    .sized(0.4F, 0.7F)
                    .clientTrackingRange(8)
                    .build("parasite_chicken"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteZombie>> PARASITE_ZOMBIE = ENTITIES.register(
            "parasite_zombie",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteZombie::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F)
                    .clientTrackingRange(8)
                    .build("parasite_zombie"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteSkeleton>> PARASITE_SKELETON = ENTITIES.register(
            "parasite_skeleton",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteSkeleton::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.99F)
                    .clientTrackingRange(8)
                    .build("parasite_skeleton"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteCreeper>> PARASITE_CREEPER = ENTITIES.register(
            "parasite_creeper",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteCreeper::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.7F)
                    .clientTrackingRange(8)
                    .build("parasite_creeper"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteSpider>> PARASITE_SPIDER = ENTITIES.register(
            "parasite_spider",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteSpider::new, MobCategory.MONSTER)
                    .sized(1.4F, 0.9F)
                    .clientTrackingRange(8)
                    .build("parasite_spider"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasitePlayer>> PARASITE_PLAYER = ENTITIES.register(
            "parasite_player",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasitePlayer::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.8F)
                    .clientTrackingRange(8)
                    .build("parasite_player"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteEnderman>> PARASITE_ENDERMAN = ENTITIES.register(
            "parasite_enderman",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteEnderman::new, MobCategory.MONSTER)
                    .sized(0.6F, 2.9F)
                    .clientTrackingRange(8)
                    .build("parasite_enderman"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.AirborneParasite>> PARASITE_TOTEM = ENTITIES.register(
            "parasite_totem",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.AirborneParasite::new, MobCategory.MONSTER)
                    .sized(1.4F, 3.6F)
                    .clientTrackingRange(10)
                    .build("parasite_totem"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.AirborneParasite>> PARASITE_BLOOM = ENTITIES.register(
            "parasite_bloom",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.AirborneParasite::new, MobCategory.MONSTER)
                    .sized(1.2F, 3.6F)
                    .clientTrackingRange(10)
                    .build("parasite_bloom"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWarden>> PARASITE_WARDEN = ENTITIES.register(
            "parasite_warden",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteWarden::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.9F).fireImmune().clientTrackingRange(16)
                    .build("parasite_warden"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWither>> PARASITE_WITHER = ENTITIES.register(
            "parasite_wither",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteWither::new, MobCategory.MONSTER)
                    .sized(0.9F, 3.5F).fireImmune().clientTrackingRange(16)
                    .build("parasite_wither"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteIronGolem>> PARASITE_IRON_GOLEM = ENTITIES.register(
            "parasite_iron_golem",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteIronGolem::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.7F).clientTrackingRange(10)
                    .build("parasite_iron_golem"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteNetheriteGolem>> PARASITE_NETHERITE_GOLEM = ENTITIES.register(
            "parasite_netherite_golem",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteNetheriteGolem::new, MobCategory.MONSTER)
                    .sized(1.75F, 3.4F).fireImmune().clientTrackingRange(10)
                    .build("parasite_netherite_golem"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteSheep>> PARASITE_SHEEP = ENTITIES.register(
            "parasite_sheep",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteSheep::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.3F).clientTrackingRange(8)
                    .build("parasite_sheep"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWolf>> PARASITE_WOLF = ENTITIES.register(
            "parasite_wolf",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteWolf::new, MobCategory.MONSTER)
                    .sized(0.6F, 0.85F).clientTrackingRange(8)
                    .build("parasite_wolf"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteStrongestZombie>> PARASITE_STRONGEST_ZOMBIE = ENTITIES.register(
            "parasite_strongest_zombie",
            () -> EntityType.Builder.of(com.netheritesupremo.parasite.ParasiteStrongestZombie::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.9F).clientTrackingRange(16)
                    .build("parasite_strongest_zombie"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteMeteor>> PARASITE_METEOR = ENTITIES.register(
            "parasite_meteor",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteMeteor>of(com.netheritesupremo.parasite.ParasiteMeteor::new, MobCategory.MISC)
                    .sized(1.2F, 1.2F).clientTrackingRange(16).updateInterval(1).fireImmune()
                    .build("parasite_meteor"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteVillager>> PARASITE_VILLAGER = ENTITIES.register(
            "parasite_villager",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteVillager>of(com.netheritesupremo.parasite.ParasiteVillager::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(8)
                    .build("parasite_villager"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteWanderingTrader>> PARASITE_WANDERING_TRADER = ENTITIES.register(
            "parasite_wandering_trader",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteWanderingTrader>of(com.netheritesupremo.parasite.ParasiteWanderingTrader::new, MobCategory.MONSTER)
                    .sized(0.7F, 2.25F).clientTrackingRange(10)
                    .build("parasite_wandering_trader"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteHunter>> PARASITE_HUNTER = ENTITIES.register(
            "parasite_hunter",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteHunter>of(com.netheritesupremo.parasite.ParasiteHunter::new, MobCategory.MONSTER)
                    .sized(0.6F, 1.95F).clientTrackingRange(10)
                    .build("parasite_hunter"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteCrawler>> PARASITE_CRAWLER = ENTITIES.register(
            "parasite_crawler",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteCrawler>of(com.netheritesupremo.parasite.ParasiteCrawler::new, MobCategory.MONSTER)
                    .sized(1.4F, 2.1F).clientTrackingRange(10)
                    .build("parasite_crawler"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteHound>> PARASITE_HOUND = ENTITIES.register(
            "parasite_hound",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteHound>of(com.netheritesupremo.parasite.ParasiteHound::new, MobCategory.MONSTER)
                    .sized(0.9F, 1.0F).clientTrackingRange(10)
                    .build("parasite_hound"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteBroodMother>> PARASITE_BROOD_MOTHER = ENTITIES.register(
            "parasite_brood_mother",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteBroodMother>of(com.netheritesupremo.parasite.ParasiteBroodMother::new, MobCategory.MONSTER)
                    .sized(2.2F, 2.6F).clientTrackingRange(10)
                    .build("parasite_brood_mother"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteChimera>> PARASITE_CHIMERA = ENTITIES.register(
            "parasite_chimera",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteChimera>of(com.netheritesupremo.parasite.ParasiteChimera::new, MobCategory.MONSTER)
                    .sized(1.3F, 1.3F).clientTrackingRange(10)
                    .build("parasite_chimera"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteAbomination>> PARASITE_ABOMINATION = ENTITIES.register(
            "parasite_abomination",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteAbomination>of(com.netheritesupremo.parasite.ParasiteAbomination::new, MobCategory.MONSTER)
                    .sized(0.9F, 2.8F).clientTrackingRange(10)
                    .build("parasite_abomination"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteAmalgam>> PARASITE_AMALGAM = ENTITIES.register(
            "parasite_amalgam",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteAmalgam>of(com.netheritesupremo.parasite.ParasiteAmalgam::new, MobCategory.MONSTER)
                    .sized(2.0F, 2.2F).clientTrackingRange(10)
                    .build("parasite_amalgam"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteSquid>> PARASITE_SQUID = ENTITIES.register(
            "parasite_squid",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteSquid>of(com.netheritesupremo.parasite.ParasiteSquid::new, MobCategory.WATER_CREATURE)
                    .sized(0.8F, 0.8F).clientTrackingRange(10)
                    .build("parasite_squid"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteGlowSquid>> PARASITE_GLOW_SQUID = ENTITIES.register(
            "parasite_glow_squid",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteGlowSquid>of(com.netheritesupremo.parasite.ParasiteGlowSquid::new, MobCategory.WATER_CREATURE)
                    .sized(0.8F, 0.8F).clientTrackingRange(10)
                    .build("parasite_glow_squid"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteBurster>> PARASITE_BURSTER = ENTITIES.register(
            "parasite_burster",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteBurster>of(com.netheritesupremo.parasite.ParasiteBurster::new, MobCategory.MONSTER)
                    .sized(1.2F, 1.4F).clientTrackingRange(10)
                    .build("parasite_burster"));

    public static final RegistryObject<EntityType<com.netheritesupremo.parasite.ParasiteSilverfish>> PARASITE_SILVERFISH = ENTITIES.register(
            "parasite_silverfish",
            () -> EntityType.Builder.<com.netheritesupremo.parasite.ParasiteSilverfish>of(com.netheritesupremo.parasite.ParasiteSilverfish::new, MobCategory.MONSTER)
                    .sized(1.0F, 0.7F).clientTrackingRange(10)
                    .build("parasite_silverfish"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.HeadlessHorseman>> HEADLESS_HORSEMAN = ENTITIES.register(
            "headless_horseman",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.HeadlessHorseman::new, MobCategory.MONSTER)
                    .sized(0.75F, 2.5F)
                    .clientTrackingRange(16)
                    .build("headless_horseman"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.HeadlessPup>> HEADLESS_PUP = ENTITIES.register(
            "headless_pup",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.HeadlessPup::new, MobCategory.CREATURE)
                    .sized(0.45F, 1.2F)
                    .clientTrackingRange(10)
                    .build("headless_pup"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.HorsemanSteed>> HORSEMAN_STEED = ENTITIES.register(
            "horseman_steed",
            () -> EntityType.Builder.of(com.netheritesupremo.mob.HorsemanSteed::new, MobCategory.CREATURE)
                    .sized(1.3964844F, 1.6F)
                    .clientTrackingRange(10)
                    .build("horseman_steed"));

    public static final RegistryObject<EntityType<com.netheritesupremo.mob.PumpkinBomb>> PUMPKIN_BOMB = ENTITIES.register(
            "pumpkin_bomb",
            () -> EntityType.Builder.<com.netheritesupremo.mob.PumpkinBomb>of(com.netheritesupremo.mob.PumpkinBomb::new, MobCategory.MISC)
                    .sized(0.4F, 0.4F)
                    .clientTrackingRange(4)
                    .updateInterval(10)
                    .build("pumpkin_bomb"));

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
