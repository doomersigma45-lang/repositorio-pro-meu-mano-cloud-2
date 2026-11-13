package com.netheritesupremo.client;

import com.netheritesupremo.mob.Digger;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Escavador (64x64): quadrupede magro de costas arqueadas, bracos compridos, pernas curtas e mandibula que abre muito. */
public class DiggerModel extends EntityModel<Digger> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart armR;
    private final ModelPart armL;
    private final ModelPart legR;
    private final ModelPart legL;

    public DiggerModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.armR = root.getChild("arm_right");
        this.armL = root.getChild("arm_left");
        this.legR = root.getChild("leg_right");
        this.legL = root.getChild("leg_left");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, -8, 6, 6, 16)
                .texOffs(0, 24).addBox(-2, -5.5F, -3, 4, 3, 8)
                .texOffs(0, 52).addBox(-0.5F, -8, -5, 1, 3, 1)
                .addBox(-0.5F, -8.5F, -2, 1, 3, 1)
                .addBox(-0.5F, -8, 1, 1, 3, 1)
                .addBox(-0.5F, -7.5F, 4, 1, 3, 1), PartPose.offset(0, 11, 0));
        PartDefinition head = p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(26, 24).addBox(-2.5F, -3, -9, 5, 4, 9)
                .texOffs(0, 52).addBox(-2, 1, -9, 1, 2, 1).addBox(1, 1, -9, 1, 2, 1).addBox(-0.5F, 1, -9, 1, 2, 1),
                PartPose.offset(0, 9, -8));
        head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(0, 38).addBox(-2, 0, -9, 4, 2, 9)
                .texOffs(0, 52).addBox(-2, -2, -9, 1, 2, 1).addBox(1, -2, -9, 1, 2, 1).addBox(-0.5F, -2, -9, 1, 2, 1),
                PartPose.offset(0, 1, -0.5F));
        CubeListBuilder arm = CubeListBuilder.create().texOffs(28, 38).addBox(-1.5F, 0, -1.5F, 3, 16, 3)
                .texOffs(8, 52).addBox(-1.5F, 15, -3.5F, 1, 1, 2).addBox(-0.5F, 15, -3.5F, 1, 1, 2).addBox(0.5F, 15, -3.5F, 1, 1, 2);
        p.addOrReplaceChild("arm_right", arm, PartPose.offset(-4.5F, 8, -6));
        p.addOrReplaceChild("arm_left", arm, PartPose.offset(4.5F, 8, -6));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(42, 38).addBox(-2.5F, 0, -2.5F, 5, 11, 5);
        p.addOrReplaceChild("leg_right", leg, PartPose.offset(-3.5F, 13, 6));
        p.addOrReplaceChild("leg_left", leg, PartPose.offset(3.5F, 13, 6));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Digger d, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);
        float a = Mth.cos(limbSwing * 0.6662F) * 1.1F * limbSwingAmount;
        this.armR.xRot = a;
        this.armL.xRot = -a;
        this.legR.xRot = -a * 0.8F;
        this.legL.xRot = a * 0.8F;
        this.jaw.xRot = d.getTarget() != null ? 1.0F + Mth.sin(ageInTicks * 0.35F) * 0.12F : 0.05F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
