package com.netheritesupremo.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

/** Modelos proprios dos novos parasitas (Cabeca Andante, Mosquito e Rooter). */
public final class CovenModels {
    public static final ModelLayerLocation HEAD = loc("head");
    public static final ModelLayerLocation GNAT = loc("gnat");
    public static final ModelLayerLocation ROOTER = loc("rooter");

    private CovenModels() {
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "coven_" + name), "main");
    }

    /** Cabeca grande com quatro perninhas. Textura 64x32. */
    public static class Head<T extends LivingEntity> extends EntityModel<T> {
        private final ModelPart root;
        private final ModelPart[] legs = new ModelPart[4];

        public Head(ModelPart root) {
            this.root = root;
            for (int i = 0; i < 4; i++) {
                this.legs[i] = root.getChild("leg" + i);
            }
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition p = mesh.getRoot();
            p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -5.0F, 10.0F, 8.0F, 10.0F),
                    PartPose.offset(0.0F, 20.0F, 0.0F));
            float[][] pos = {{-3.5F, -3.5F}, {3.5F, -3.5F}, {-3.5F, 3.5F}, {3.5F, 3.5F}};
            for (int i = 0; i < 4; i++) {
                p.addOrReplaceChild("leg" + i, CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                        PartPose.offset(pos[i][0], 19.0F, pos[i][1]));
            }
            return LayerDefinition.create(mesh, 64, 32);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            ModelPart head = this.root.getChild("head");
            head.yRot = netHeadYaw * ((float) Math.PI / 180.0F);
            head.xRot = headPitch * ((float) Math.PI / 180.0F);
            float swing = Mth.cos(limbSwing * 1.3F) * 1.1F * limbSwingAmount;
            this.legs[0].xRot = swing;
            this.legs[1].xRot = -swing;
            this.legs[2].xRot = -swing;
            this.legs[3].xRot = swing;
        }

        @Override
        public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            this.root.render(pose, vc, light, overlay, r, g, b, a);
        }
    }

    /** Mosquito: corpo pequeno e duas asas batendo. Textura 64x32. */
    public static class Gnat<T extends LivingEntity> extends EntityModel<T> {
        private final ModelPart root;
        private final ModelPart wingL;
        private final ModelPart wingR;

        public Gnat(ModelPart root) {
            this.root = root;
            this.wingL = root.getChild("wing_l");
            this.wingR = root.getChild("wing_r");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition p = mesh.getRoot();
            p.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, -1.5F, -2.5F, 3.0F, 3.0F, 5.0F),
                    PartPose.offset(0.0F, 20.0F, 0.0F));
            p.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 8).addBox(-1.5F, -1.5F, -3.0F, 3.0F, 3.0F, 3.0F),
                    PartPose.offset(0.0F, 20.0F, -2.5F));
            p.addOrReplaceChild("wing_l", CubeListBuilder.create().texOffs(20, 8).addBox(0.0F, -0.5F, -1.5F, 6.0F, 1.0F, 3.0F),
                    PartPose.offset(1.0F, 18.5F, 0.0F));
            p.addOrReplaceChild("wing_r", CubeListBuilder.create().texOffs(20, 8).addBox(-6.0F, -0.5F, -1.5F, 6.0F, 1.0F, 3.0F),
                    PartPose.offset(-1.0F, 18.5F, 0.0F));
            return LayerDefinition.create(mesh, 64, 32);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            float flap = Mth.sin(ageInTicks * 2.6F) * 0.9F;
            this.wingL.zRot = flap;
            this.wingR.zRot = -flap;
        }

        @Override
        public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            this.root.render(pose, vc, light, overlay, r, g, b, a);
        }
    }

    /** Rooter: base, dois segmentos de caule que balancam e duas maxilas que abrem ao atacar. Textura 64x64. */
    public static class Rooter<T extends Mob> extends EntityModel<T> {
        private final ModelPart root;
        private final ModelPart stem1;
        private final ModelPart stem2;
        private final ModelPart jawTop;
        private final ModelPart jawBottom;

        public Rooter(ModelPart root) {
            this.root = root;
            ModelPart base = root.getChild("base");
            this.stem1 = base.getChild("stem1");
            this.stem2 = this.stem1.getChild("stem2");
            this.jawTop = this.stem2.getChild("jaw_top");
            this.jawBottom = this.stem2.getChild("jaw_bottom");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition p = mesh.getRoot();
            PartDefinition base = p.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-6.0F, -5.0F, -6.0F, 12.0F, 5.0F, 12.0F), PartPose.offset(0.0F, 24.0F, 0.0F));
            PartDefinition stem1 = base.addOrReplaceChild("stem1", CubeListBuilder.create().texOffs(0, 20)
                    .addBox(-3.0F, -12.0F, -3.0F, 6.0F, 12.0F, 6.0F), PartPose.offset(0.0F, -5.0F, 0.0F));
            PartDefinition stem2 = stem1.addOrReplaceChild("stem2", CubeListBuilder.create().texOffs(24, 20)
                    .addBox(-2.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(0.0F, -12.0F, 0.0F));
            stem2.addOrReplaceChild("jaw_top", CubeListBuilder.create().texOffs(0, 40)
                    .addBox(-4.0F, -3.0F, -8.0F, 8.0F, 3.0F, 8.0F), PartPose.offset(0.0F, -12.0F, 4.0F));
            stem2.addOrReplaceChild("jaw_bottom", CubeListBuilder.create().texOffs(32, 40)
                    .addBox(-4.0F, 0.0F, -8.0F, 8.0F, 3.0F, 8.0F), PartPose.offset(0.0F, -12.0F, 4.0F));
            return LayerDefinition.create(mesh, 64, 64);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.stem1.xRot = Mth.sin(ageInTicks * 0.08F) * 0.12F;
            this.stem2.zRot = Mth.cos(ageInTicks * 0.07F) * 0.15F;
            float open = entity.isAggressive() ? 0.55F + Mth.sin(ageInTicks * 0.6F) * 0.2F : 0.12F;
            this.jawTop.xRot = -open;
            this.jawBottom.xRot = open;
            this.stem2.yRot = netHeadYaw * ((float) Math.PI / 180.0F) * 0.5F;
        }

        @Override
        public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            this.root.render(pose, vc, light, overlay, r, g, b, a);
        }
    }
}
