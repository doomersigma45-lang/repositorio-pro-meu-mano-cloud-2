package com.netheritesupremo.blackrealm;

import com.netheritesupremo.NetheriteSupremo;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
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

/** Liquido preto (fluido + bloco + tipo) dos rios da Terra Negra. */
public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NetheriteSupremo.MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteSupremo.MODID);

    public static final RegistryObject<FluidType> BLACK_LIQUID_TYPE = FLUID_TYPES.register("black_liquid",
            () -> new FluidType(FluidType.Properties.create()
                    .descriptionId("block.netheritesupremo.black_liquid")
                    .density(4000).viscosity(8000)
                    .canSwim(false).canDrown(true).canPushEntity(true)
                    .supportsBoating(false).canExtinguish(false)) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return new ResourceLocation(NetheriteSupremo.MODID, "block/black_liquid_still");
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return new ResourceLocation(NetheriteSupremo.MODID, "block/black_liquid_flow");
                        }
                    });
                }
            });

    public static final RegistryObject<FlowingFluid> SOURCE = FLUIDS.register("black_liquid",
            () -> new ForgeFlowingFluid.Source(ModFluids.PROPERTIES));
    public static final RegistryObject<FlowingFluid> FLOWING = FLUIDS.register("flowing_black_liquid",
            () -> new ForgeFlowingFluid.Flowing(ModFluids.PROPERTIES));
    public static final RegistryObject<Block> BLOCK = BLOCKS.register("black_liquid",
            () -> new BlackLiquidBlock(ModFluids.SOURCE, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLACK)
                    .replaceable().noCollission().strength(100.0F).pushReaction(PushReaction.DESTROY)
                    .noLootTable().liquid()));

    public static final ForgeFlowingFluid.Properties PROPERTIES =
            new ForgeFlowingFluid.Properties(BLACK_LIQUID_TYPE, SOURCE, FLOWING)
                    .block(BLOCK).slopeFindDistance(3).levelDecreasePerBlock(1).tickRate(10);

    private ModFluids() {
    }

    public static void register(IEventBus bus) {
        FLUID_TYPES.register(bus);
        FLUIDS.register(bus);
        BLOCKS.register(bus);
    }
}
