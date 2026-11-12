package com.netheritesupremo.client;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.entity.Netherbot;
import com.netheritesupremo.entity.Precursor;
import com.netheritesupremo.entity.NetheriteRobotRainbowBoss;
import com.netheritesupremo.entity.NetheriteBombRobot;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.NETHERITE_GOLEM.get(), NetheriteGolemRenderer::new);
        event.registerEntityRenderer(ModEntities.NETHERITE_KNIGHT.get(), NetheriteKnightRenderer::new);
        event.registerEntityRenderer(ModEntities.NETHER_SPIDER.get(), NetherSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.GOLDEN_ZOMBIE.get(), GoldenZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.NECROMANCER.get(), NecromancerRenderer::new);
        event.registerEntityRenderer(ModEntities.RED_STEVE.get(), RedSteveRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETON_KING.get(), SkeletonKingRenderer::new);
        event.registerEntityRenderer(ModEntities.SKELETON_VILLAGER.get(), SkeletonVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.WAR_TANK.get(), WarTankRenderer::new);
        event.registerEntityRenderer(ModEntities.GOOSE.get(), GooseRenderer::new);
        event.registerEntityRenderer(ModEntities.STALKER.get(), StalkerRenderer::new);
        event.registerEntityRenderer(ModEntities.COLOSSAL_BEETLE.get(), BeetleRenderer::new);
        event.registerEntityRenderer(ModEntities.FUNGUS.get(), FungusRenderer::new);
        event.registerEntityRenderer(ModEntities.GIANT_MOSQUITO.get(), GiantMosquitoRenderer::new);
        event.registerEntityRenderer(ModEntities.STRONGEST_ZOMBIE.get(), StrongestZombieRenderer::new);
        event.registerEntityRenderer(ModEntities.NETHERITE_CHICKEN.get(), NetheriteChickenRenderer::new);
        event.registerEntityRenderer(ModEntities.COLOSSAL_SPIDER.get(), ColossalSpiderRenderer::new);
        event.registerEntityRenderer(ModEntities.WEB_SHOT.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.TANK_SHELL.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 1.4F, true));
        event.registerEntityRenderer(ModEntities.OLD_GUN_BULLET.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 0.6F, true));
        event.registerEntityRenderer(ModEntities.BOMB_ROBOT.get(), ctx -> new PlayerRobotRenderer<NetheriteBombRobot>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/bomb_robot.png"), 1.0F));
        event.registerEntityRenderer(ModEntities.NETHERBOT.get(), ctx -> new PlayerRobotRenderer<Netherbot>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/netherbot.png"), 1.6F));
        event.registerEntityRenderer(ModEntities.RAINBOW_BOSS.get(), ctx -> new PlayerRobotRenderer<NetheriteRobotRainbowBoss>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/netherite_robot_rainbow_boss.png"), 2.3F));
        event.registerEntityRenderer(ModEntities.PRECURSOR.get(), ctx -> new PlayerRobotRenderer<Precursor>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/precursor.png"), 2.6F));
        event.registerEntityRenderer(ModEntities.GIANT_GLOW_SQUID.get(), ctx -> new GiantGlowSquidRenderer(ctx, 4.5F));
        event.registerEntityRenderer(ModEntities.GIANT_GLOW_SQUID_BABY.get(), ctx -> new GiantGlowSquidRenderer(ctx, 2.0F));
        event.registerEntityRenderer(ModEntities.GLOW_INK_BALL.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(ctx, 2.0F, true));
        event.registerEntityRenderer(ModEntities.SHURIKEN.get(),
                ctx -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<com.netheritesupremo.entity.ShurikenEntity>(ctx, 1.0F, true));
        event.registerEntityRenderer(ModEntities.NINJA.get(), ctx -> new PlayerRobotRenderer<com.netheritesupremo.entity.Ninja>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/ninja.png"), 1.0F));
        event.registerEntityRenderer(ModEntities.SAMURAI.get(), ctx -> new PlayerRobotRenderer<com.netheritesupremo.entity.Samurai>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/samurai.png"), 1.2F));
        event.registerEntityRenderer(ModEntities.THIEF.get(), ThiefRenderer::new);
        event.registerEntityRenderer(ModEntities.PRECURSOR_VILLAGER.get(), PrecursorVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.HYBRID_VILLAGER.get(), PrecursorVillagerRenderer::new);
        event.registerEntityRenderer(ModEntities.ROBOT.get(), RobotRenderer::new);
        event.registerEntityRenderer(ModEntities.AQUATIC_ALIEN.get(), AlienRenderer::new);
        event.registerEntityRenderer(ModEntities.PRECURSOR_SOLDIER.get(), ctx -> new PlayerRobotRenderer<com.netheritesupremo.entity.PrecursorSoldier>(ctx,
                new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/precursor_soldier.png"), 1.1F));
        event.registerEntityRenderer(ModEntities.ALIEN_SHIP.get(), SaucerRenderer::new);
        event.registerEntityRenderer(ModEntities.ANOMALOCARIS.get(), ctx -> new CambrianRenderer<com.netheritesupremo.entity.Anomalocaris>(
                ctx, CambrianRenderer.ANOMALOCARIS_LAYER, SegmentedModel.ANOMALOCARIS, "anomalocaris", 0.9F));
        event.registerEntityRenderer(ModEntities.TRILOBITE.get(), ctx -> new CambrianRenderer<com.netheritesupremo.entity.Trilobite>(
                ctx, CambrianRenderer.TRILOBITE_LAYER, SegmentedModel.TRILOBITE, "trilobite", 0.4F));
        event.registerEntityRenderer(ModEntities.OPABINIA.get(), ctx -> new CambrianRenderer<com.netheritesupremo.entity.Opabinia>(
                ctx, CambrianRenderer.OPABINIA_LAYER, SegmentedModel.OPABINIA, "opabinia", 0.5F));
        event.registerEntityRenderer(ModEntities.UMBRAL_MERCHANT.get(), WanderingTraderRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SaucerRenderer.LAYER, SaucerModel::createLayer);
        event.registerLayerDefinition(WarTankRenderer.LAYER, WarTankModel::createLayer);
        event.registerLayerDefinition(GooseRenderer.LAYER, GooseModel::createLayer);
        event.registerLayerDefinition(StalkerRenderer.LAYER, StalkerModel::createLayer);
        event.registerLayerDefinition(BeetleRenderer.LAYER, BeetleModel::createLayer);
        event.registerLayerDefinition(FungusRenderer.LAYER, FungusModel::createLayer);
        event.registerLayerDefinition(GiantMosquitoRenderer.LAYER, GiantMosquitoModel::createLayer);
        event.registerLayerDefinition(ColossalSpiderRenderer.LAYER, ColossalSpiderModel::createLayer);
        event.registerLayerDefinition(CambrianRenderer.ANOMALOCARIS_LAYER, () -> SegmentedModel.createLayer(SegmentedModel.ANOMALOCARIS));
        event.registerLayerDefinition(CambrianRenderer.TRILOBITE_LAYER, () -> SegmentedModel.createLayer(SegmentedModel.TRILOBITE));
        event.registerLayerDefinition(CambrianRenderer.OPABINIA_LAYER, () -> SegmentedModel.createLayer(SegmentedModel.OPABINIA));
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GolemTexture());
        event.registerReloadListener(new FusionArmorTextures());
    }
}
