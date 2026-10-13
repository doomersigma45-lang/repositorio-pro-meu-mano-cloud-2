package com.netheritesupremo;

import com.netheritesupremo.entity.NetheriteKnight;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
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
        ModEntities.ENTITIES.register(modBus);
        ModItems.ITEMS.register(modBus);
        modBus.addListener(this::commonSetup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, SupremoConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(new SupremoEvents());
        MinecraftForge.EVENT_BUS.register(new GolemSummoning());
        MinecraftForge.EVENT_BUS.register(new KnightEvents());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // Remove o teto de 30 pontos de armadura (vale no cliente e no servidor).
            ArmorCap.raise();
            // Regra de spawn do cavaleiro (1.19.2 registra aqui, nao por evento).
            SpawnPlacements.register(ModEntities.NETHERITE_KNIGHT.get(), SpawnPlacements.Type.ON_GROUND,
                    Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NetheriteKnight::checkKnightSpawnRules);
        });
    }
}
