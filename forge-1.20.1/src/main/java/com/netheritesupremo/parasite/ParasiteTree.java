package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Registro da Arvore Parasita (coracao) e dos blocos que ela espalha: terra, tronco e folhas parasitas. */
public final class ParasiteTree {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteSupremo.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NetheriteSupremo.MODID);

    public static final RegistryObject<Block> DIRT = BLOCKS.register("parasite_dirt",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GRAY).strength(0.6F).sound(SoundType.GRAVEL)));
    public static final RegistryObject<Block> LOG = BLOCKS.register("parasite_log",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Block> LEAVES = BLOCKS.register("parasite_leaves",
            () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(0.4F).sound(SoundType.GRASS)));
    public static final RegistryObject<Block> HEART = BLOCKS.register("parasite_tree",
            () -> new ParasiteTreeBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED).strength(8.0F, 20.0F)
                    .sound(SoundType.WOOD).lightLevel(s -> 4)));

    public static final RegistryObject<Item> DIRT_ITEM = ITEMS.register("parasite_dirt", () -> new BlockItem(DIRT.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOG_ITEM = ITEMS.register("parasite_log", () -> new BlockItem(LOG.get(), new Item.Properties()));
    public static final RegistryObject<Item> LEAVES_ITEM = ITEMS.register("parasite_leaves", () -> new BlockItem(LEAVES.get(), new Item.Properties()));
    public static final RegistryObject<Item> HEART_ITEM = ITEMS.register("parasite_tree", () -> new BlockItem(HEART.get(), new Item.Properties()));

    public static final RegistryObject<BlockEntityType<ParasiteTreeBlockEntity>> TREE_BE = BLOCK_ENTITIES.register("parasite_tree",
            () -> BlockEntityType.Builder.of(ParasiteTreeBlockEntity::new, HEART.get()).build(null));

    private ParasiteTree() {
    }

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
    }
}
