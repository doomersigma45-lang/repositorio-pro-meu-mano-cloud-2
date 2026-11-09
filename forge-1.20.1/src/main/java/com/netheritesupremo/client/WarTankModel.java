package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.entity.WarTank;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Tanque de guerra: casco, duas esteiras e uma torre com canhao que gira (textura 128x128). A frente e -Z. */
public class WarTankModel extends EntityModel<WarTank> {
    private final ModelPart root;
    private final ModelPart turret;

    public WarTankModel(ModelPart root) {
        this.root = root;
        this.turret = root.getChild("turret");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("hull", CubeListBuilder.create().texOffs(0, 0).addBox(-11, -12, -16, 22, 6, 32), PartPose.offset(0, 24, 0));
        p.addOrReplaceChild("tread_left", CubeListBuilder.create().texOffs(0, 64).addBox(-2.5F, -7, -16, 5, 7, 32), PartPose.offset(-13.5F, 24, 0));
        p.addOrReplaceChild("tread_right", CubeListBuilder.create().texOffs(0, 64).addBox(-2.5F, -7, -16, 5, 7, 32), PartPose.offset(13.5F, 24, 0));
        PartDefinition turret = p.addOrReplaceChild("turret", CubeListBuilder.create().texOffs(0, 40).addBox(-7, -18, -6, 14, 6, 14), PartPose.offset(0, 24, 0));
        turret.addOrReplaceChild("barrel", CubeListBuilder.create().texOffs(60, 40).addBox(-1.5F, -16.5F, -22, 3, 3, 16), PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(WarTank entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.turret.yRot = netHeadYaw * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
