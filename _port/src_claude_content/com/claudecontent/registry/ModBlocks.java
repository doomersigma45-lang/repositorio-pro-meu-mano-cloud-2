/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.item.BlockItem
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraft.world.item.Rarity
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FlowerBlock
 *  net.minecraft.world.level.block.SoundType
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 *  net.minecraft.world.level.material.MapColor
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package com.claudecontent.registry;

import com.claudecontent.block.RootAltarBlock;
import com.claudecontent.registry.ModItems;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.BLOCKS, (String)"claude_content");
    public static final RegistryObject<Block> ROOT_ALTAR = BLOCKS.register("root_altar", () -> new RootAltarBlock(BlockBehaviour.Properties.m_284310_().m_284180_(MapColor.f_283748_).m_60913_(4.0f, 6.0f).m_60918_(SoundType.f_154669_).m_60953_(state -> 5)));
    public static final RegistryObject<Block> ANCIENT_SPROUT = BLOCKS.register("ancient_sprout", () -> new FlowerBlock(MobEffects.f_19619_, 4, BlockBehaviour.Properties.m_284310_().m_60910_().m_60966_().m_60918_(SoundType.f_56740_).m_60953_(state -> 7)));
    public static final RegistryObject<Item> ROOT_ALTAR_ITEM = ModItems.ITEMS.register("root_altar", () -> new BlockItem((Block)ROOT_ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_SPROUT_ITEM = ModItems.ITEMS.register("ancient_sprout", () -> new BlockItem((Block)ANCIENT_SPROUT.get(), new Item.Properties().m_41497_(Rarity.UNCOMMON)));

    private ModBlocks() {
    }
}

