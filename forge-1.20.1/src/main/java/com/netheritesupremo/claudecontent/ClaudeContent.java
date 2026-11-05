package com.netheritesupremo.claudecontent;

import com.netheritesupremo.claudecontent.registry.ModCreativeTabs;
import com.netheritesupremo.claudecontent.registry.ModEntities;
import com.netheritesupremo.claudecontent.registry.ModItems;
import com.netheritesupremo.claudecontent.registry.ModBlocks;
import com.netheritesupremo.claudecontent.registry.ModFeatures;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * Parte do Claude no mod conjunto.
 *
 * Para integrar no mod principal: remova @Mod desta classe e chame
 * ClaudeContent.register(modEventBus) no construtor da classe principal.
 */
public class ClaudeContent {
    public static final String MODID = "netheritesupremo";

    public static void register(IEventBus bus) {
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModCreativeTabs.TABS.register(bus);
    }
}
