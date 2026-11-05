/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.RegistryObject
 */
package com.claudecontent.registry;

import com.claudecontent.registry.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"claude_content");
    public static final RegistryObject<CreativeModeTab> MAIN = TABS.register("main", () -> CreativeModeTab.builder().lambda$static$0((Component)Component.translatable((String)"itemGroup.claude_content")).lambda$static$0(() -> new ItemStack((ItemLike)ModItems.ASH_SIGIL.get())).lambda$static$0((params, output) -> ModItems.ITEMS.getEntries().forEach(item -> output.lambda$acceptAll$0((ItemLike)item.get()))).build());

    private ModCreativeTabs() {
    }
}

