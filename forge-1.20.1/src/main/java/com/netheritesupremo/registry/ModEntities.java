package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.Necromancer;
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

    private ModEntities() {
    }
}
