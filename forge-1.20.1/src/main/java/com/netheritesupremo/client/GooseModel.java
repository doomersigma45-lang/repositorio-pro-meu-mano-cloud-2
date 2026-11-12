package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.mob.Goose;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

/** Ganso (textura 64x64). */
public class GooseModel extends EntityModel<Goose> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightWing;
    private final ModelPart leftWing;

    public GooseModel(ModelPart root) {
        this.root = root;
        this.head = root.getChild("neck").getChild("head");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightWing = root.getChild("right_wing");
        this.leftWing = root.getChild("left_wing");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3, -3, -6, 6, 6, 12), PartPose.offset(0, 16, 0));
        p.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(20, 42).addBox(-2, -1, 0, 4, 2, 3), PartPose.offset(0, 14, 6));
        PartDefinition neck = p.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(0, 20).addBox(-1, -8, -1, 2, 8, 2),
                PartPose.offsetAndRotation(0, 14, -5, -0.3F, 0, 0));
        PartDefinition head = neck.addOrReplaceChild("head", CubeListBuilder.create().texOffs(14, 20).addBox(-2, -3, -2.5F, 4, 3, 5),
                PartPose.offset(0, -8, 0));
        head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(34, 20).addBox(-1, -0.5F, -3, 2, 1, 3),
                PartPose.offset(0, -1.5F, -2.5F));
        CubeListBuilder leg = CubeListBuilder.create().texOffs(0, 34).addBox(-0.5F, 0, -0.5F, 1, 5, 1)
                .texOffs(6, 34).addBox(-1.5F, 4, -2.5F, 3, 1, 4);
        p.addOrReplaceChild("right_leg", leg, PartPose.offset(-1.5F, 19, 1));
        p.addOrReplaceChild("left_leg", leg, PartPose.offset(1.5F, 19, 1));
        CubeListBuilder wing = CubeListBuilder.create().texOffs(0, 42).addBox(-0.5F, 0, -3, 1, 5, 6);
        p.addOrReplaceChild("right_wing", wing, PartPose.offset(-3.5F, 14, -1));
        p.addOrReplaceChild("left_wing", wing, PartPose.offset(3.5F, 14, -1));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void setupAnim(Goose goose, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.head.xRot = headPitch * ((float) Math.PI / 180F) + 0.3F;
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        boolean gliding = !goose.onGround() && !goose.isInWater();
        float flap = gliding ? 1.2F : 0.0F;
        this.rightWing.zRot = flap + 0.1F;
        this.leftWing.zRot = -flap - 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
