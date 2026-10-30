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
        event.registerEntityRenderer(ModEntities.ROBOT.get(), RobotRenderer::new);
        event.registerEntityRenderer(ModEntities.UMBRAL_MERCHANT.get(), WanderingTraderRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterReloadListeners(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new GolemTexture());
    }
}
