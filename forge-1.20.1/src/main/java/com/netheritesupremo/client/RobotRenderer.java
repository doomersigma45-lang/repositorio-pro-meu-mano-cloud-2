package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.entity.Robot;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/** Robo com modelo de jogador e uma das tres skins enviadas (sorteada ao nascer). */
public class RobotRenderer extends HumanoidMobRenderer<Robot, PlayerModel<Robot>> {
    private static final ResourceLocation[] TEXTURES = {
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/robot_1.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/robot_2.png"),
            new ResourceLocation(NetheriteSupremo.MODID, "textures/entity/robot_3.png")
    };

    public RobotRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.6F);
    }

    @Override
    protected void scale(Robot robot, PoseStack poseStack, float partialTick) {
        poseStack.scale(1.2F, 1.2F, 1.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(Robot robot) {
        return TEXTURES[robot.getVariant()];
    }
}
