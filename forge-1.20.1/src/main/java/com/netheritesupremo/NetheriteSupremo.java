package com.netheritesupremo;

import com.netheritesupremo.enchant.ModEnchantments;
import com.netheritesupremo.enchant.MultiplierEvents;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(NetheriteSupremo.MODID)
public class NetheriteSupremo {
    public static final String MODID = "netheritesupremo";

    public NetheriteSupremo() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModEntities.ENTITIES.register(modBus);
        com.netheritesupremo.registry.ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEnchantments.ENCHANTMENTS.register(modBus);
        com.netheritesupremo.claudecontent.ClaudeContent.register(modBus);
        com.netheritesupremo.blackrealm.ModFluids.register(modBus);
        com.netheritesupremo.blackrealm.ModSounds.SOUNDS.register(modBus);
        com.netheritesupremo.skeleton.ModEffects.EFFECTS.register(modBus);
        com.netheritesupremo.viladeferro.VilaDeFerro.register(modBus);
        com.netheritesupremo.network.ModNetwork.register();
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addCreativeTabItems);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SupremoConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(new SupremoEvents());
        MinecraftForge.EVENT_BUS.register(new GolemSummoning());
        MinecraftForge.EVENT_BUS.register(new KnightEvents());
        MinecraftForge.EVENT_BUS.register(new NecromancerEvents());
        MinecraftForge.EVENT_BUS.register(new MultiplierEvents());
        MinecraftForge.EVENT_BUS.register(new TradeEvents());
        MinecraftForge.EVENT_BUS.register(new RobotSummoning());
        MinecraftForge.EVENT_BUS.register(new GemEvents());
        MinecraftForge.EVENT_BUS.register(new CoreEvents());
        MinecraftForge.EVENT_BUS.register(new DragonEyeEvents());
        MinecraftForge.EVENT_BUS.register(new GauntletEvents());
        MinecraftForge.EVENT_BUS.register(new AvaritiaCompat());
        MinecraftForge.EVENT_BUS.register(new SpectriteEvents());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.blackrealm.BlackRealmCommand());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.skeleton.SkeletonWarCommand());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.entity.TamedSpiderProtection());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.entity.AlienAllies());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.invasion.HybridEvents());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.invasion.InvasionManager());
        MinecraftForge.EVENT_BUS.register(new com.netheritesupremo.invasion.InvasionCommand());
        MinecraftForge.EVENT_BUS.register(new RecipeEvents());
        MinecraftForge.EVENT_BUS.register(new WeaponEvents());
        MinecraftForge.EVENT_BUS.register(new ObsidianEvents());
        MinecraftForge.EVENT_BUS.register(new NinjaArmorEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Remove o teto de 30 pontos de armadura (vale no cliente e no servidor).
        event.enqueueWork(ArmorCap::raise);
    }

    private void addCreativeTabItems(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.NETHERITE_GOLEM_SPAWN_EGG);
            event.accept(ModItems.NETHERITE_KNIGHT_SPAWN_EGG);
            event.accept(ModItems.NECROMANCER_SPAWN_EGG);
            event.accept(ModItems.SKELETON_KING_SPAWN_EGG);
            event.accept(ModItems.RED_STEVE_SPAWN_EGG);
            event.accept(ModItems.SKELETON_VILLAGER_SPAWN_EGG);
            event.accept(ModItems.WAR_TANK_SPAWN_EGG);
            event.accept(ModItems.GOOSE_SPAWN_EGG);
            event.accept(ModItems.NETHERITE_CHICKEN_SPAWN_EGG);
            event.accept(ModItems.COLOSSAL_SPIDER_SPAWN_EGG);
            event.accept(ModItems.NECROMANCER_SUMMONER);
            event.accept(ModItems.NETHER_SPIDER_SPAWN_EGG);
            event.accept(ModItems.GOLDEN_ZOMBIE_SPAWN_EGG);
            event.accept(ModItems.UMBRAL_MERCHANT_SPAWN_EGG);
            event.accept(ModItems.ROBOT_SPAWN_EGG);
            event.accept(ModItems.BOMB_ROBOT_SPAWN_EGG);
            event.accept(ModItems.NETHERBOT_SPAWN_EGG);
            event.accept(ModItems.RAINBOW_BOSS_SPAWN_EGG);
            event.accept(ModItems.PRECURSOR_SPAWN_EGG);
            event.accept(ModItems.NINJA_SPAWN_EGG);
            event.accept(ModItems.SAMURAI_SPAWN_EGG);
            event.accept(ModItems.THIEF_SPAWN_EGG);
            event.accept(ModItems.AQUATIC_ALIEN_SPAWN_EGG);
            event.accept(ModItems.PRECURSOR_SOLDIER_SPAWN_EGG);
            event.accept(ModItems.ALIEN_SHIP_SPAWN_EGG);
            event.accept(ModItems.ANOMALOCARIS_SPAWN_EGG);
            event.accept(ModItems.TRILOBITE_SPAWN_EGG);
            event.accept(ModItems.OPABINIA_SPAWN_EGG);
            event.accept(ModItems.PRECURSOR_VILLAGER_SPAWN_EGG);
            event.accept(ModItems.HYBRID_VILLAGER_SPAWN_EGG);
            event.accept(ModItems.GIANT_GLOW_SQUID_SPAWN_EGG);
        } else if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.OBSIDIAN_SWORD);
            event.accept(ModItems.OBSIDIAN_AXE);
            event.accept(ModItems.OBSIDIAN_HELMET);
            event.accept(ModItems.OBSIDIAN_CHESTPLATE);
            event.accept(ModItems.OBSIDIAN_LEGGINGS);
            event.accept(ModItems.OBSIDIAN_BOOTS);
            event.accept(ModItems.NINJA_HELMET);
            event.accept(ModItems.NINJA_CHESTPLATE);
            event.accept(ModItems.NINJA_LEGGINGS);
            event.accept(ModItems.NINJA_BOOTS);
            event.accept(ModItems.FUSION_HELMET);
            event.accept(ModItems.FUSION_CHESTPLATE);
            event.accept(ModItems.FUSION_LEGGINGS);
            event.accept(ModItems.FUSION_BOOTS);
            event.accept(ModItems.SPECTRITE_GEM);
            event.accept(ModItems.SPECTRITE_ORE);
            event.accept(ModItems.SPECTRITE_BLOCK);
            event.accept(ModItems.SPECTRITE_HELMET);
            event.accept(ModItems.SPECTRITE_CHESTPLATE);
            event.accept(ModItems.SPECTRITE_LEGGINGS);
            event.accept(ModItems.SPECTRITE_BOOTS);
            event.accept(ModItems.SPECTRITE_SWORD);
            event.accept(ModItems.SPECTRITE_PICKAXE);
            event.accept(ModItems.SPECTRITE_AXE);
            event.accept(ModItems.SPECTRITE_SHOVEL);
            event.accept(ModItems.SPECTRITE_ROD);
            event.accept(ModItems.SPECTRITE_SWORD_SPECIAL);
            event.accept(ModItems.SPECTRITE_PICKAXE_SPECIAL);
            event.accept(ModItems.SPECTRITE_AXE_SPECIAL);
            event.accept(ModItems.SPECTRITE_SHOVEL_SPECIAL);
            event.accept(ModItems.OLD_GUN);
            event.accept(ModItems.KATANA);
            event.accept(ModItems.SHURIKEN);
            event.accept(ModItems.KUNAI);
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.OBSIDIAN_PICKAXE);
            event.accept(ModItems.OBSIDIAN_SHOVEL);
            event.accept(ModItems.OBSIDIAN_HOE);
        } else if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(ModItems.BLACK_SOIL);
            event.accept(ModItems.CRYSTAL_LOG);
            event.accept(ModItems.CRYSTAL_WOOD);
            event.accept(ModItems.STRIPPED_CRYSTAL_LOG);
            event.accept(ModItems.STRIPPED_CRYSTAL_WOOD);
            event.accept(ModItems.CRYSTAL_LEAVES);
            event.accept(ModItems.CRYSTAL_SAPLING);
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.CRYSTAL_PLANKS);
            event.accept(ModItems.CRYSTAL_STAIRS);
            event.accept(ModItems.CRYSTAL_SLAB);
            event.accept(ModItems.CRYSTAL_FENCE);
            event.accept(ModItems.CRYSTAL_FENCE_GATE);
            event.accept(ModItems.CRYSTAL_BUTTON);
            event.accept(ModItems.CRYSTAL_PRESSURE_PLATE);
        } else if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.NETHERITE_APPLE);
            event.accept(ModItems.ENCHANTED_NETHERITE_APPLE);
            event.accept(ModItems.NETHERITE_CAKE);
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.OBSIDIAN_INGOT);
            event.accept(ModItems.RAINBOW_GEM);
            event.accept(ModItems.PRECURSOR_CORE);
            event.accept(ModItems.RUBY);
            event.accept(ModItems.DRAGON_EYE);
            event.accept(ModItems.GAUNTLET);
            event.accept(ModItems.GIANT_GLOW_SQUID_EGG);
        }
    }
}
