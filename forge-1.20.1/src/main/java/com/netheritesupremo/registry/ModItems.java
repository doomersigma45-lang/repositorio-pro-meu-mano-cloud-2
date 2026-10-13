package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteSupremo.MODID);

    public static final RegistryObject<Item> NETHERITE_GOLEM_SPAWN_EGG = ITEMS.register(
            "netherite_golem_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERITE_GOLEM, 0x2B2326, 0xFF6A00, new Item.Properties()));

    public static final RegistryObject<Item> NETHERITE_KNIGHT_SPAWN_EGG = ITEMS.register(
            "netherite_knight_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERITE_KNIGHT, 0x1E1B1D, 0xB04A1A, new Item.Properties()));

    private ModItems() {
    }
}
