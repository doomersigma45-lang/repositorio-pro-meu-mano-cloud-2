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

    public static final RegistryObject<Item> NECROMANCER_SPAWN_EGG = ITEMS.register(
            "necromancer_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NECROMANCER, 0x14121C, 0x7B4FBF, new Item.Properties()));

    public static final RegistryObject<Item> NETHER_SPIDER_SPAWN_EGG = ITEMS.register(
            "nether_spider_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHER_SPIDER, 0x3B0F0F, 0xFF7A1A, new Item.Properties()));

    public static final RegistryObject<Item> GOLDEN_ZOMBIE_SPAWN_EGG = ITEMS.register(
            "golden_zombie_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GOLDEN_ZOMBIE, 0xF2C230, 0x8A5A00, new Item.Properties()));

    public static final RegistryObject<Item> UMBRAL_MERCHANT_SPAWN_EGG = ITEMS.register(
            "umbral_merchant_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.UMBRAL_MERCHANT, 0x2A1B4D, 0xC9A227, new Item.Properties()));

    public static final RegistryObject<Item> BOMB_ROBOT_SPAWN_EGG = ITEMS.register(
            "netherite_bomb_robot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.BOMB_ROBOT, 0x1A1A1A, 0xE01B1B, new Item.Properties()));

    public static final RegistryObject<Item> NETHERBOT_SPAWN_EGG = ITEMS.register(
            "netherbot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NETHERBOT, 0x2B2B2F, 0xFF8A1A, new Item.Properties()));

    public static final RegistryObject<Item> RAINBOW_BOSS_SPAWN_EGG = ITEMS.register(
            "netherite_robot_rainbow_boss_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RAINBOW_BOSS, 0xFF3B3B, 0x3BD9FF, new Item.Properties()));

    public static final RegistryObject<Item> RAINBOW_GEM = ITEMS.register(
            "rainbow_gem",
            () -> new com.netheritesupremo.item.RainbowGemItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> PRECURSOR_SPAWN_EGG = ITEMS.register(
            "precursor_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PRECURSOR, 0x2A1F5C, 0x35E0D0, new Item.Properties()));

    public static final RegistryObject<Item> PRECURSOR_CORE = ITEMS.register(
            "precursor_core",
            () -> new com.netheritesupremo.item.PrecursorCoreItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> GIANT_GLOW_SQUID_SPAWN_EGG = ITEMS.register(
            "giant_glow_squid_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GIANT_GLOW_SQUID, 0x0B3B4A, 0x7CF5E0, new Item.Properties()));

    public static final RegistryObject<Item> GIANT_GLOW_SQUID_EGG = ITEMS.register(
            "giant_glow_squid_egg",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.GIANT_GLOW_SQUID_EGG.get(),
                    new Item.Properties().rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> ROBOT_SPAWN_EGG = ITEMS.register(
            "robot_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.ROBOT, 0x1C2A33, 0x2BD9FF, new Item.Properties()));

    public static final RegistryObject<Item> KATANA = ITEMS.register(
            "katana",
            () -> new net.minecraft.world.item.SwordItem(com.netheritesupremo.item.KatanaTier.INSTANCE, 16, -1.0F,
                    new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> SHURIKEN = ITEMS.register(
            "shuriken",
            () -> new com.netheritesupremo.item.ShurikenItem(new Item.Properties().stacksTo(16)));

    public static final RegistryObject<Item> KUNAI = ITEMS.register(
            "kunai",
            () -> new com.netheritesupremo.item.KunaiItem(new Item.Properties().stacksTo(1)
                    .rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> NINJA_SPAWN_EGG = ITEMS.register(
            "ninja_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.NINJA, 0x1B1B2A, 0xC0272D, new Item.Properties()));

    public static final RegistryObject<Item> SAMURAI_SPAWN_EGG = ITEMS.register(
            "samurai_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.SAMURAI, 0x8E1B24, 0xE8E2D0, new Item.Properties()));

    public static final RegistryObject<Item> THIEF_SPAWN_EGG = ITEMS.register(
            "thief_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.THIEF, 0x3A2A1C, 0xD9A441, new Item.Properties()));

    public static final RegistryObject<Item> PRECURSOR_VILLAGER_SPAWN_EGG = ITEMS.register(
            "precursor_villager_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PRECURSOR_VILLAGER, 0x2A1F5C, 0x8FE3D9, new Item.Properties()));

    public static final RegistryObject<Item> OBSIDIAN_INGOT = ITEMS.register(
            "obsidian_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> OBSIDIAN_SWORD = ITEMS.register(
            "obsidian_sword",
            () -> new net.minecraft.world.item.SwordItem(com.netheritesupremo.item.ObsidianTier.INSTANCE, 31, -2.2F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_AXE = ITEMS.register(
            "obsidian_axe",
            () -> new net.minecraft.world.item.AxeItem(com.netheritesupremo.item.ObsidianTier.INSTANCE, 33.0F, -3.0F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_PICKAXE = ITEMS.register(
            "obsidian_pickaxe",
            () -> new net.minecraft.world.item.PickaxeItem(com.netheritesupremo.item.ObsidianTier.INSTANCE, 9, -2.6F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_SHOVEL = ITEMS.register(
            "obsidian_shovel",
            () -> new net.minecraft.world.item.ShovelItem(com.netheritesupremo.item.ObsidianTier.INSTANCE, 8.5F, -2.8F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_HOE = ITEMS.register(
            "obsidian_hoe",
            () -> new net.minecraft.world.item.HoeItem(com.netheritesupremo.item.ObsidianTier.INSTANCE, 5, -1.0F, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_HELMET = ITEMS.register(
            "obsidian_helmet",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.ObsidianArmorMaterial.INSTANCE, net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_CHESTPLATE = ITEMS.register(
            "obsidian_chestplate",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.ObsidianArmorMaterial.INSTANCE, net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_LEGGINGS = ITEMS.register(
            "obsidian_leggings",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.ObsidianArmorMaterial.INSTANCE, net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> OBSIDIAN_BOOTS = ITEMS.register(
            "obsidian_boots",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.ObsidianArmorMaterial.INSTANCE, net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties().rarity(net.minecraft.world.item.Rarity.RARE)));

    public static final RegistryObject<Item> NINJA_HELMET = ITEMS.register(
            "ninja_helmet",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.NinjaArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties()));

    public static final RegistryObject<Item> NINJA_CHESTPLATE = ITEMS.register(
            "ninja_chestplate",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.NinjaArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties()));

    public static final RegistryObject<Item> NINJA_LEGGINGS = ITEMS.register(
            "ninja_leggings",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.NinjaArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties()));

    public static final RegistryObject<Item> NINJA_BOOTS = ITEMS.register(
            "ninja_boots",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.NinjaArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties()));

    public static final RegistryObject<Item> FUSION_HELMET = ITEMS.register(
            "fusion_helmet",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.FusionArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.HELMET, new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FUSION_CHESTPLATE = ITEMS.register(
            "fusion_chestplate",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.FusionArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FUSION_LEGGINGS = ITEMS.register(
            "fusion_leggings",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.FusionArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.LEGGINGS, new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    public static final RegistryObject<Item> FUSION_BOOTS = ITEMS.register(
            "fusion_boots",
            () -> new net.minecraft.world.item.ArmorItem(com.netheritesupremo.item.FusionArmorMaterial.INSTANCE,
                    net.minecraft.world.item.ArmorItem.Type.BOOTS, new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));

    public static final RegistryObject<Item> CRYSTAL_PLANKS = ITEMS.register(
            "crystal_planks",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_PLANKS.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRIPPED_CRYSTAL_LOG = ITEMS.register(
            "stripped_crystal_log",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.STRIPPED_CRYSTAL_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRIPPED_CRYSTAL_WOOD = ITEMS.register(
            "stripped_crystal_wood",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.STRIPPED_CRYSTAL_WOOD.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_LOG = ITEMS.register(
            "crystal_log",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_WOOD = ITEMS.register(
            "crystal_wood",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_WOOD.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_STAIRS = ITEMS.register(
            "crystal_stairs",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_STAIRS.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_SLAB = ITEMS.register(
            "crystal_slab",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_SLAB.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_FENCE = ITEMS.register(
            "crystal_fence",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_FENCE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_FENCE_GATE = ITEMS.register(
            "crystal_fence_gate",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_FENCE_GATE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_BUTTON = ITEMS.register(
            "crystal_button",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_BUTTON.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_PRESSURE_PLATE = ITEMS.register(
            "crystal_pressure_plate",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_PRESSURE_PLATE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_LEAVES = ITEMS.register(
            "crystal_leaves",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRYSTAL_SAPLING = ITEMS.register(
            "crystal_sapling",
            () -> new net.minecraft.world.item.BlockItem(ModBlocks.CRYSTAL_SAPLING.get(), new Item.Properties()));

    public static final RegistryObject<Item> RUBY = ITEMS.register(
            "ruby",
            () -> new com.netheritesupremo.item.RubyItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    public static final RegistryObject<Item> DRAGON_EYE = ITEMS.register(
            "dragon_eye",
            () -> new com.netheritesupremo.item.DragonEyeItem(new Item.Properties().stacksTo(1).fireResistant()
                    .rarity(net.minecraft.world.item.Rarity.EPIC)));

    private ModItems() {
    }
}
