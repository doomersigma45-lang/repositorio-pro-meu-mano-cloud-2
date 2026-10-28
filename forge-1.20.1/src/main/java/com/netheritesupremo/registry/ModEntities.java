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
            () -> EntityType.Builder.of(Robot::new, MobCategory.MISC)
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

    private ModEntities() {
    }
}
