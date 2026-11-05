package com.netheritesupremo.claudecontent.registry;

import com.netheritesupremo.claudecontent.ClaudeContent;
import com.netheritesupremo.claudecontent.block.RootAltarBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ClaudeContent.MODID);
    public static final RegistryObject<Block> ROOT_ALTAR = BLOCKS.register("root_altar", () ->
            new RootAltarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN)
                    .strength(4.0F, 6.0F).sound(SoundType.MOSS).lightLevel(state -> 5)));
    public static final RegistryObject<Block> ANCIENT_SPROUT = BLOCKS.register("ancient_sprout", () ->
            new FlowerBlock(MobEffects.GLOWING, 4, BlockBehaviour.Properties.of().noCollission()
                    .instabreak().sound(SoundType.GRASS).lightLevel(state -> 7)));
    public static final RegistryObject<Item> ROOT_ALTAR_ITEM = ModItems.ITEMS.register("root_altar",
            () -> new BlockItem(ROOT_ALTAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANCIENT_SPROUT_ITEM = ModItems.ITEMS.register("ancient_sprout",
            () -> new BlockItem(ANCIENT_SPROUT.get(), new Item.Properties().rarity(net.minecraft.world.item.Rarity.UNCOMMON)));
    private ModBlocks() {}
}
