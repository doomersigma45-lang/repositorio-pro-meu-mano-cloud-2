/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 */
package com.claudecontent;

import com.claudecontent.registry.ModBlocks;
import com.claudecontent.registry.ModCreativeTabs;
import com.claudecontent.registry.ModEntities;
import com.claudecontent.registry.ModFeatures;
import com.claudecontent.registry.ModItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(value="claude_content")
public class ClaudeContent {
    public static final String MODID = "claude_content";

    public ClaudeContent() {
        ClaudeContent.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static void register(IEventBus bus) {
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModEntities.ENTITIES.register(bus);
        ModFeatures.FEATURES.register(bus);
        ModCreativeTabs.TABS.register(bus);
    }
}

