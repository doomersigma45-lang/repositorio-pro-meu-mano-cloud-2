/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.model.EntityModel
 *  net.minecraft.client.model.VillagerModel
 *  net.minecraft.client.model.geom.ModelLayers
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.client.renderer.entity.MobRenderer
 *  net.minecraft.client.renderer.entity.RenderLayerParent
 *  net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer
 *  net.minecraft.client.renderer.entity.layers.CustomHeadLayer
 *  net.minecraft.client.renderer.entity.layers.RenderLayer
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.TamableAnimal
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefVillager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.CrossedArmsItemLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;

public class ChiefRenderer<T extends Mob>
extends MobRenderer<T, VillagerModel<T>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/wandering_trader.png");

    public ChiefRenderer(EntityRendererProvider.Context context) {
        super(context, (EntityModel)new VillagerModel(context.bakeLayer(ModelLayers.WANDERING_TRADER)), 0.5f);
        this.render((RenderLayer)new CustomHeadLayer((RenderLayerParent)this, context.getModelSet(), context.getItemInHandRenderer()));
        this.render((RenderLayer)new CrossedArmsItemLayer((RenderLayerParent)this, context.getItemInHandRenderer()));
    }

    public ResourceLocation getTextureLocation(T entity) {
        return TEXTURE;
    }

    protected void scale(T entity, PoseStack stack, float partialTick) {
        TamableAnimal pet;
        AgeableMob ageable;
        float scale = 0.9375f;
        if (entity instanceof ChiefVillager) {
            scale *= 1.2f;
        } else if (entity instanceof AgeableMob && (ageable = (AgeableMob)entity).isBaby()) {
            scale *= 0.5f;
        }
        if (entity instanceof TamableAnimal && (pet = (TamableAnimal)entity).isInSittingPose()) {
            stack.lambda$new$0(0.0f, 0.45f, 0.0f);
        }
        stack.popPose(scale, scale, scale);
    }
}

