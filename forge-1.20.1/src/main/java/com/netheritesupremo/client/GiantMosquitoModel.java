package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.GiantMosquito;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Mosquito gigante (textura 64x64): cabeca, tromba, torax, abdomen, 6 patas finas e 2 asas que batem. */
public class GiantMosquitoModel extends EntityModel<GiantMosquito> {
    private final ModelPart root;
    private final ModelPart rightWing;
    private final ModelPart leftWing;
    private final ModelPart[] legs = new ModelPart[6];

    public GiantMosquitoModel(ModelPart root) {
        this.root = root;
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
        for (int i = 0; i < 6; i++) {
            this.legs[i] = root.getChild("leg" + i);
        }
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("thorax", CubeListBuilder.create().texOffs(0, 20).addBox(-3, -3, -4, 6, 6, 8), PartPose.offset(0, 12, 0));
        p.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -2.5F, 0, 5, 5, 14),
                PartPose.offsetAndRotation(0, 12, 3, 0.35F, 0, 0));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(30, 20).addBox(-2.5F, -2.5F, -5, 5, 5, 5),
                PartPose.offset(0, 12, -4));
        head.addOrReplaceChild("proboscis", CubeListBuilder.create().texOffs(0, 36).addBox(-0.5F, -0.5F, -8, 1, 1, 8),
                PartPose.offsetAndRotation(0, 1.5F, -5, 0.35F, 0, 0));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(20, 36).addBox(-0.5F, 0, -0.5F, 1, 10, 1);
        float[] z = {-2.5F, 0, 2.5F};
        for (int i = 0; i < 6; i++) {
            boolean left = i % 2 == 1;
            p.addOrReplaceChild("leg" + i, leg,
                    PartPose.offsetAndRotation(left ? 2.5F : -2.5F, 14, z[i / 2], 0, 0, left ? -0.5F : 0.5F));
        }
        p.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 48).addBox(-16, -0.5F, -3, 16, 1, 6),
                PartPose.offset(-2, 9, 0));
        p.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 48).addBox(0, -0.5F, -3, 16, 1, 6),
                PartPose.offset(2, 9, 0));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(GiantMosquito m, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        float flap = Mth.sin(ageInTicks * 2.2F) * 0.7F;
        this.rightWing.zRot = -0.2F + flap;
        this.leftWing.zRot = 0.2F - flap;
        for (int i = 0; i < 6; i++) {
            this.legs[i].xRot = Mth.sin(ageInTicks * 0.2F + i) * 0.1F + 0.2F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
