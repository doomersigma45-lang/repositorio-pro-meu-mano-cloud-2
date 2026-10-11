package com.netheritesupremo;

import net.minecraftforge.common.MinecraftForge;
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
        modBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SupremoConfig.SPEC);
        MinecraftForge.EVENT_BUS.register(new SupremoEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Remove o teto de 30 pontos de armadura (vale no cliente e no servidor).
        event.enqueueWork(ArmorCap::raise);
    }
}
