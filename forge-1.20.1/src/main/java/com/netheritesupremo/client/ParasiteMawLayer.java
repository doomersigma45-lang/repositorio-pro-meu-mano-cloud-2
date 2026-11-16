package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

/** Dentes afiados em 3D na frente da boca (acompanham a rotacao da cabeca). */
public class ParasiteMawLayer<T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_maw"), "main");

    private final ModelPart maw;

    public ParasiteMawLayer(RenderLayerParent<T, M> parent, EntityRendererProvider.Context context) {
        super(parent);
        this.maw = context.bakeLayer(LAYER).getChild("maw");
    }

    /** Dente que aponta para baixo (de cima) ou para cima (de baixo): base grossa e ponta fina. */
    private static void tooth(CubeListBuilder b, float x, boolean upper) {
        if (upper) {
            b.texOffs(0, 8).addBox(x, -2.9F, -4.6F, 0.8F, 1.0F, 0.6F);
            b.texOffs(0, 8).addBox(x + 0.2F, -1.9F, -4.5F, 0.4F, 1.0F, 0.4F);
        } else {
            b.texOffs(0, 8).addBox(x, -1.0F, -4.6F, 0.8F, 1.0F, 0.6F);
            b.texOffs(0, 8).addBox(x + 0.2F, -2.0F, -4.5F, 0.4F, 1.0F, 0.4F);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        CubeListBuilder b = CubeListBuilder.create();
        // origem = pivo da cabeca (pescoco); a face fica em z=-4 e vai de y=-10 (topo) a y=0 (queixo)
        for (float x : new float[] {-3.5F, -2.1F, 1.3F, 2.7F}) {
            tooth(b, x, true);
        }
        for (float x : new float[] {-2.8F, 2.0F}) {
            tooth(b, x, false);
        }
        mesh.getRoot().addOrReplaceChild("maw", b, PartPose.ZERO);
        return LayerDefinition.create(mesh, 16, 16);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource buffer, int light, T entity, float limbSwing,
                       float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        this.maw.copyFrom(this.getParentModel().head);
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(ParasiteTextures.CLAWS));
        this.maw.render(pose, vc, light, LivingEntityRenderer.getOverlayCoords(entity, 0.0F));
    }
}
