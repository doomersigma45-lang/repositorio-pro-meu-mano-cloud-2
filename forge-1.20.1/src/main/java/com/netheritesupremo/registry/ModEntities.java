package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.Necromancer;
import com.netheritesupremo.entity.NetherSpider;
import com.netheritesupremo.entity.NetherSpider;
import com.netheritesupremo.entity.NetheriteGolem;
import com.netheritesupremo.entity.NetheriteKnight;
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

    // Aranha comum: 1.4 x 0.9; a do Nether e 30% maior
    public static final RegistryObject<EntityType<NetherSpider>> NETHER_SPIDER = ENTITIES.register(
            "nether_spider",
            () -> EntityType.Builder.of(NetherSpider::new, MobCategory.MONSTER)
                    .sized(1.82F, 1.17F)
                    .fireImmune()
                    .clientTrackingRange(8)
                    .build("nether_spider"));

    private ModEntities() {
    }
}
