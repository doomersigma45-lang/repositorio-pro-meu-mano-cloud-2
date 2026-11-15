package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.LivingEntity;

/** Modelo dos infectados "de ar": Totem de Cranios (tronco preto com cranios) e Flor (haste preta com copa amarela). */
public class AirborneModel<T extends LivingEntity> extends EntityModel<T> {
    private final ModelPart root;
    private final ModelPart sway;

    public AirborneModel(ModelPart root) {
        this.root = root;
        this.sway = root.getChild("sway");
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.sway.zRot = (float) Math.sin(ageInTicks * 0.06F) * 0.05F;
        this.sway.xRot = (float) Math.cos(ageInTicks * 0.05F) * 0.04F;
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
        this.root.render(pose, vc, light, overlay, r, g, b, a);
    }

    private static CubeListBuilder cube(int u, int v, float x, float y, float z, float w, float h, float d) {
        return CubeListBuilder.create().texOffs(u, v).addBox(x, y, z, w, h, d);
    }

    /** Galho fino que sai do tronco: comprimento len para cima, inclinado (lean) e girado ao redor do tronco (spin). */
    private static void branch(PartDefinition parent, String name, float y, float len, float lean, float spin) {
        parent.addOrReplaceChild(name, cube(0, 26, -1.0F, -len, -1.0F, 2.0F, len, 2.0F), PartPose.offsetAndRotation(0.0F, y, 0.0F, lean, spin, 0.0F));
    }

    public static LayerDefinition totem() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", cube(0, 46, -8.0F, -2.0F, -8.0F, 16.0F, 2.0F, 16.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition t1 = body.addOrReplaceChild("t1", cube(0, 30, -3.5F, -22.0F, -3.5F, 7.0F, 22.0F, 7.0F), PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, 0.0F, 0.0F, 0.06F));
        PartDefinition t2 = t1.addOrReplaceChild("t2", cube(0, 30, -3.0F, -20.0F, -3.0F, 6.0F, 20.0F, 6.0F), PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, 0.0F, 0.0F, -0.12F));
        PartDefinition t3 = t2.addOrReplaceChild("t3", cube(0, 30, -2.5F, -18.0F, -2.5F, 5.0F, 18.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -20.0F, 0.0F, 0.05F, 0.0F, 0.12F));
        // cranios presos ao tronco
        float[][] skulls = {
                {-7.0F, -6.0F, 1.0F, -1.2F}, {6.5F, -12.0F, -2.0F, 1.3F}, {1.0F, -17.0F, 6.5F, 3.1F},
                {-6.0F, -8.0F, 1.0F, -1.4F}, {6.0F, -6.0F, 0.0F, 1.5F},
                {-5.5F, -12.0F, 0.0F, -1.3F}, {4.0F, -16.0F, -5.0F, 0.4F}};
        for (int i = 0; i < skulls.length; i++) {
            PartDefinition seg = i < 3 ? t1 : (i < 5 ? t2 : t3);
            float[] s = skulls[i];
            seg.addOrReplaceChild("skull" + i, cube(0, 0, -4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.offsetAndRotation(s[0], s[1], s[2], 0.0F, s[3], 0.0F));
        }
        PartDefinition sway = root.addOrReplaceChild("sway", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        float[] ys = {-14.0F, -22.0F, -30.0F, -38.0F, -26.0F, -34.0F, -44.0F, -18.0F};
        for (int i = 0; i < ys.length; i++) {
            branch(sway, "branch" + i, ys[i], 22.0F + (i % 3) * 3.0F, 0.9F + 0.15F * (i % 3), i * 0.785F);
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    public static LayerDefinition bloom() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        // base em estrela
        for (int i = 0; i < 6; i++) {
            body.addOrReplaceChild("star" + i, cube(0, 46, 0.0F, -1.5F, -2.0F, 14.0F, 1.5F, 4.0F), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, i * 1.047F, 0.0F));
        }
        PartDefinition t1 = body.addOrReplaceChild("t1", cube(0, 30, -3.0F, -18.0F, -3.0F, 6.0F, 18.0F, 6.0F), PartPose.offset(0.0F, -1.0F, 0.0F));
        PartDefinition t2 = t1.addOrReplaceChild("t2", cube(0, 30, -2.5F, -16.0F, -2.5F, 5.0F, 16.0F, 5.0F), PartPose.offsetAndRotation(0.0F, -18.0F, 0.0F, 0.0F, 0.0F, 0.05F));
        PartDefinition t3 = t2.addOrReplaceChild("t3", cube(0, 30, -2.0F, -14.0F, -2.0F, 4.0F, 14.0F, 4.0F), PartPose.offsetAndRotation(0.0F, -16.0F, 0.0F, 0.0F, 0.0F, -0.05F));
        // copa amarela
        PartDefinition crown = t3.addOrReplaceChild("crown", cube(0, 0, -6.0F, -8.0F, -6.0F, 12.0F, 8.0F, 12.0F), PartPose.offset(0.0F, -14.0F, 0.0F));
        crown.addOrReplaceChild("crown2", cube(0, 0, -4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F), PartPose.offset(0.0F, -8.0F, 0.0F));
        PartDefinition sway = root.addOrReplaceChild("sway", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        // 4 tentaculos arqueados saindo da copa
        for (int i = 0; i < 4; i++) {
            PartDefinition tent = sway.addOrReplaceChild("tent" + i, CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -46.0F, 0.0F, 0.0F, i * 1.5708F, 0.0F));
            PartDefinition a = tent.addOrReplaceChild("a", cube(0, 26, -1.5F, -14.0F, -1.5F, 3.0F, 14.0F, 3.0F), PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.55F));
            PartDefinition b = a.addOrReplaceChild("b", cube(0, 26, -1.5F, -12.0F, -1.5F, 3.0F, 12.0F, 3.0F), PartPose.offsetAndRotation(0.0F, -14.0F, 0.0F, 0.0F, 0.0F, -0.9F));
            b.addOrReplaceChild("c", cube(0, 26, -1.0F, -10.0F, -1.0F, 2.0F, 10.0F, 2.0F), PartPose.offsetAndRotation(0.0F, -12.0F, 0.0F, 0.0F, 0.0F, -0.9F));
        }
        // pernas finas no meio da haste
        for (int i = 0; i < 4; i++) {
            branch(sway, "leg" + i, -22.0F, 16.0F, 2.2F, i * 1.5708F + 0.4F);
        }
        return LayerDefinition.create(mesh, 64, 64);
    }
}
