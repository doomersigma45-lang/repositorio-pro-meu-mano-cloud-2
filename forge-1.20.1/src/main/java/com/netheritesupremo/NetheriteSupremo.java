package com.netheritesupremo;

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
        ModItems.ITEMS.register(modBus);
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::addCreativeTabItems);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SupremoConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(new SupremoEvents());
        MinecraftForge.EVENT_BUS.register(new GolemSummoning());
        MinecraftForge.EVENT_BUS.register(new KnightEvents());
        MinecraftForge.EVENT_BUS.register(new NecromancerEvents());
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
        }
    }
}
