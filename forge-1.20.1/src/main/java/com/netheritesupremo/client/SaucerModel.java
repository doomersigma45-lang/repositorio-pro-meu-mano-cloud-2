package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.entity.AlienShip;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

/** Nave espacial em forma de disco (textura 256x128). */
public class SaucerModel extends EntityModel<AlienShip> {
    private final ModelPart root;
    private final ModelPart dome;

    public SaucerModel(ModelPart root) {
        this.root = root;
        this.dome = root.getChild("dome");
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition p = mesh.getRoot();
        p.addOrReplaceChild("hull", CubeListBuilder.create().texOffs(0, 0).addBox(-24, -12, -24, 48, 4, 48), PartPose.offset(0, 24, 0));
        p.addOrReplaceChild("belly", CubeListBuilder.create().texOffs(0, 52).addBox(-16, -8, -16, 32, 4, 32), PartPose.offset(0, 24, 0));
        p.addOrReplaceChild("upper", CubeListBuilder.create().texOffs(128, 52).addBox(-14, -16, -14, 28, 4, 28), PartPose.offset(0, 24, 0));
        p.addOrReplaceChild("dome", CubeListBuilder.create().texOffs(0, 90).addBox(-8, -22, -8, 16, 6, 16), PartPose.offset(0, 24, 0));
        p.addOrReplaceChild("cannon", CubeListBuilder.create().texOffs(80, 90).addBox(-2, -4, -2, 4, 6, 4), PartPose.offset(0, 24, 0));
        return LayerDefinition.create(mesh, 256, 128);
    }

    @Override
    public void setupAnim(AlienShip entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
            float headPitch) {
        this.dome.yRot = ageInTicks * 0.1F;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay,
            float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
