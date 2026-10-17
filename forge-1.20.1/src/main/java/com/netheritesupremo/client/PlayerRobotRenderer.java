package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

/** Renderer de mob com modelo de jogador, uma skin e um tamanho. */
public class PlayerRobotRenderer<T extends Mob> extends HumanoidMobRenderer<T, PlayerModel<T>> {
    private final ResourceLocation texture;
    private final float scale;

    public PlayerRobotRenderer(EntityRendererProvider.Context context, ResourceLocation texture, float scale) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F * scale);
        this.texture = texture;
        this.scale = scale;
    }

    @Override
    protected void scale(T mob, PoseStack poseStack, float partialTick) {
        poseStack.scale(this.scale, this.scale, this.scale);
    }

    @Override
    public ResourceLocation getTextureLocation(T mob) {
        return this.texture;
    }
}
