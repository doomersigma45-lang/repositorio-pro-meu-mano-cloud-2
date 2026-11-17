package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/** Liquido vermelho (sangue parasita): nasce em nascentes dentro de cavernas, pega com balde. Mob que encosta e infectado. */
public final class ParasiteBlood {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NetheriteSupremo.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteSupremo.MODID);

    public static final RegistryObject<FluidType> FLUID_TYPE = FLUID_TYPES.register("parasite_blood",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.netheritesupremo.parasite_blood")
                    .density(1200).viscosity(1500)
                    .canSwim(true).canDrown(true).canPushEntity(true)
                    .supportsBoating(true).canExtinguish(true)) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return new ResourceLocation(NetheriteSupremo.MODID, "block/parasite_blood_still");
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return new ResourceLocation(NetheriteSupremo.MODID, "block/parasite_blood_flow");
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> SOURCE = FLUIDS.register("parasite_blood",
            () -> new ForgeFlowingFluid.Source(ParasiteBlood.PROPERTIES) {
                @Override
                protected boolean isRandomlyTicking() {
                    return true;
                }

                @Override
                protected void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
                    touchWood(level, pos, random);
                }
            });
    public static final RegistryObject<FlowingFluid> FLOWING = FLUIDS.register("flowing_parasite_blood",
            () -> new ForgeFlowingFluid.Flowing(ParasiteBlood.PROPERTIES) {
                @Override
                protected boolean isRandomlyTicking() {
                    return true;
                }

                @Override
                protected void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
                    touchWood(level, pos, random);
                }
            });
    public static final RegistryObject<LiquidBlock> BLOCK = BLOCKS.register("parasite_blood",
            () -> new LiquidBlock(ParasiteBlood.SOURCE, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_RED)
                    .replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY)
                    .noLootTable().liquid().lightLevel(state -> 3)));
    public static final RegistryObject<Item> BUCKET = ITEMS.register("parasite_blood_bucket",
            () -> new BucketItem(ParasiteBlood.SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final ForgeFlowingFluid.Properties PROPERTIES =
            new ForgeFlowingFluid.Properties(FLUID_TYPE, SOURCE, FLOWING)
                    .block(BLOCK).bucket(BUCKET).slopeFindDistance(3).levelDecreasePerBlock(1).tickRate(10);

    private ParasiteBlood() {
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
        ITEMS.register(bus);
    }

    /** Sangue parasita encostando em madeira (tronco) faz nascer uma Arvore Parasita no lugar do tronco. */
    static void touchWood(Level level, BlockPos pos, RandomSource random) {
        if (level.isClientSide || random.nextInt(3) != 0) {
            return;
        }
        for (Direction d : Direction.values()) {
            BlockPos n = pos.relative(d);
            if (!level.getBlockState(n).is(BlockTags.LOGS)) {
                continue;
            }
            for (BlockPos q : BlockPos.betweenClosed(n.offset(-6, -3, -6), n.offset(6, 3, 6))) {
                if (level.getBlockState(q).is(ParasiteTree.HEART.get())) {
                    return; // ja existe uma arvore parasita aqui perto
                }
            }
            level.setBlock(n, ParasiteTree.HEART.get().defaultBlockState(), 3);
            level.playSound(null, n, net.minecraft.sounds.SoundEvents.SLIME_SQUISH, net.minecraft.sounds.SoundSource.BLOCKS, 1.2F, 0.5F);
            return;
        }
    }
}
