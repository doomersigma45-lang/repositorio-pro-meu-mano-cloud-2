package com.netheritesupremo.registry;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.block.GiantGlowSquidEggBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteSupremo.MODID);

    public static final RegistryObject<Block> GIANT_GLOW_SQUID_EGG = BLOCKS.register("giant_glow_squid_egg",
            () -> new GiantGlowSquidEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_CYAN)
                    .strength(0.5F).sound(SoundType.FROGSPAWN).noOcclusion().lightLevel(state -> 8)));

    public static final RegistryObject<Block> CRYSTAL_PLANKS = BLOCKS.register("crystal_planks",
            () -> new Block(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_PLANKS)));

    public static final RegistryObject<Block> STRIPPED_CRYSTAL_LOG = BLOCKS.register("stripped_crystal_log",
            () -> new net.minecraft.world.level.block.RotatedPillarBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_LOG)));

    public static final RegistryObject<Block> STRIPPED_CRYSTAL_WOOD = BLOCKS.register("stripped_crystal_wood",
            () -> new net.minecraft.world.level.block.RotatedPillarBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.STRIPPED_OAK_WOOD)));

    public static final RegistryObject<Block> CRYSTAL_LOG = BLOCKS.register("crystal_log",
            () -> new com.netheritesupremo.block.CrystalLogBlock(() -> ModBlocks.STRIPPED_CRYSTAL_LOG.get(), BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_LOG)));

    public static final RegistryObject<Block> CRYSTAL_WOOD = BLOCKS.register("crystal_wood",
            () -> new com.netheritesupremo.block.CrystalLogBlock(() -> ModBlocks.STRIPPED_CRYSTAL_WOOD.get(), BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_WOOD)));

    public static final RegistryObject<Block> CRYSTAL_STAIRS = BLOCKS.register("crystal_stairs",
            () -> new net.minecraft.world.level.block.StairBlock(() -> ModBlocks.CRYSTAL_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_STAIRS)));

    public static final RegistryObject<Block> CRYSTAL_SLAB = BLOCKS.register("crystal_slab",
            () -> new net.minecraft.world.level.block.SlabBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_SLAB)));

    public static final RegistryObject<Block> CRYSTAL_FENCE = BLOCKS.register("crystal_fence",
            () -> new net.minecraft.world.level.block.FenceBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_FENCE)));

    public static final RegistryObject<Block> CRYSTAL_FENCE_GATE = BLOCKS.register("crystal_fence_gate",
            () -> new net.minecraft.world.level.block.FenceGateBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_FENCE_GATE), net.minecraft.world.level.block.state.properties.WoodType.OAK));

    public static final RegistryObject<Block> CRYSTAL_BUTTON = BLOCKS.register("crystal_button",
            () -> new net.minecraft.world.level.block.ButtonBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_BUTTON), net.minecraft.world.level.block.state.properties.BlockSetType.OAK, 30, true));

    public static final RegistryObject<Block> CRYSTAL_PRESSURE_PLATE = BLOCKS.register("crystal_pressure_plate",
            () -> new net.minecraft.world.level.block.PressurePlateBlock(net.minecraft.world.level.block.PressurePlateBlock.Sensitivity.EVERYTHING, BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_PRESSURE_PLATE), net.minecraft.world.level.block.state.properties.BlockSetType.OAK));

    public static final RegistryObject<Block> CRYSTAL_LEAVES = BLOCKS.register("crystal_leaves",
            () -> new net.minecraft.world.level.block.LeavesBlock(BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_LEAVES)));

    public static final RegistryObject<Block> CRYSTAL_SAPLING = BLOCKS.register("crystal_sapling",
            () -> new net.minecraft.world.level.block.SaplingBlock(new com.netheritesupremo.block.CrystalTreeGrower(), BlockBehaviour.Properties.copy(net.minecraft.world.level.block.Blocks.OAK_SAPLING)));

    public static final RegistryObject<Block> DIMENSION_PORTAL = BLOCKS.register("dimension_portal",
            () -> new com.netheritesupremo.block.DimensionPortalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE).noCollission().noOcclusion().noLootTable()
                    .strength(-1.0F, 3600000.0F).lightLevel(state -> 12)
                    .isViewBlocking((state, level, pos) -> false)));

    public static final RegistryObject<Block> SATELLITE_PORTAL = BLOCKS.register("satellite_portal",
            () -> new com.netheritesupremo.block.SatellitePortalBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_CYAN).noCollission().noOcclusion().noLootTable()
                    .strength(50.0F, 1200.0F).lightLevel(state -> 15)
                    .isViewBlocking((state, level, pos) -> false)));

    private ModBlocks() {
    }
}
