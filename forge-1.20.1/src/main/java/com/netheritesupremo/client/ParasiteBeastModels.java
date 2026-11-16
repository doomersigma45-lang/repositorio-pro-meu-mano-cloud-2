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
import net.minecraft.world.entity.Mob;

/**
 * Modelos originais (feitos por mim) dos parasitas "hibridos": Rastejador (tronco de zumbi sobre corpo de aranha),
 * Cao Parasita (lobo com dentes e espinhos de creeper) e Matriarca (massa de carne com tentaculos e presas).
 * A textura (128x128) tem tres retalhos lisos no canto de cima: olho (x 120), osso (x 112) e boca escura (x 104).
 */
public final class ParasiteBeastModels {
    public static final ModelLayerLocation CRAWLER = loc("crawler");
    public static final ModelLayerLocation HOUND = loc("hound");
    public static final ModelLayerLocation MOTHER = loc("mother");
    private static final float DEG = 0.017453292F;

    private ParasiteBeastModels() {
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_beast"), name);
    }

    private static void eye(PartDefinition p, String n, float x, float y, float z, float w, float h, float d) {
        p.addOrReplaceChild(n, CubeListBuilder.create().texOffs(120, 0).addBox(x, y, z, w, h, d), PartPose.ZERO);
    }

    private static void bone(PartDefinition p, String n, float x, float y, float z, float w, float h, float d) {
        p.addOrReplaceChild(n, CubeListBuilder.create().texOffs(112, 0).addBox(x, y, z, w, h, d), PartPose.ZERO);
    }

    private static void dark(PartDefinition p, String n, float x, float y, float z, float w, float h, float d) {
        p.addOrReplaceChild(n, CubeListBuilder.create().texOffs(104, 0).addBox(x, y, z, w, h, d), PartPose.ZERO);
    }

    /** Base comum: guarda a raiz e a desenha. */
    private abstract static class Base<T extends Mob> extends EntityModel<T> {
        protected final ModelPart root;

        Base(ModelPart root) {
            this.root = root;
        }

        @Override
        public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
            this.root.render(pose, vc, light, overlay, r, g, b, a);
        }
    }

    // ================================================================== Rastejador
    public static class Crawler<T extends Mob> extends Base<T> {
        private final ModelPart head;
        private final ModelPart rightArm;
        private final ModelPart leftArm;
        private final ModelPart[] right = new ModelPart[4];
        private final ModelPart[] left = new ModelPart[4];
        private static final float[] LEG_Z = {-6.0F, -2.0F, 2.0F, 6.0F};

        public Crawler(ModelPart root) {
            super(root);
            this.head = root.getChild("head");
            this.rightArm = root.getChild("right_arm");
            this.leftArm = root.getChild("left_arm");
            for (int i = 0; i < 4; i++) {
                this.right[i] = root.getChild("rl" + i);
                this.left[i] = root.getChild("ll" + i);
            }
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild("thorax", CubeListBuilder.create().texOffs(0, 8).addBox(-5, 10, -8, 10, 7, 12), PartPose.ZERO);
            root.addOrReplaceChild("abdomen", CubeListBuilder.create().texOffs(0, 40).addBox(-6, 9, 4, 12, 9, 12), PartPose.ZERO);
            root.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 66).addBox(-4, -1, -8, 8, 11, 4), PartPose.ZERO);
            PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(50, 66).addBox(-4, -8, -4, 8, 8, 8),
                    PartPose.offset(0, -1, -6));
            eye(head, "eye_l", -3.5F, -5.0F, -4.1F, 2.0F, 1.5F, 0.2F);
            eye(head, "eye_r", 1.5F, -5.0F, -4.1F, 2.0F, 1.5F, 0.2F);
            dark(head, "mouth", -3.0F, -2.5F, -4.05F, 6.0F, 2.0F, 0.1F);
            for (int i = 0; i < 4; i++) {
                bone(head, "tu" + i, -2.5F + i * 1.7F, -2.5F, -4.2F, 1.0F, 1.5F, 0.2F);
            }
            bone(head, "tl0", -1.7F, -1.0F, -4.2F, 1.0F, 1.0F, 0.2F);
            bone(head, "tl1", 0.7F, -1.0F, -4.2F, 1.0F, 1.0F, 0.2F);
            for (int i = 0; i < 3; i++) {
                bone(root, "spike" + i, -0.5F, 5.0F, 6.0F + i * 4.0F, 1.0F, 4.0F, 1.0F);
            }
            root.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 90).addBox(-3, -1, -1.5F, 3, 11, 3), PartPose.offset(-4, 1, -6));
            root.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 90).addBox(0, -1, -1.5F, 3, 11, 3), PartPose.offset(4, 1, -6));
            bone(root, "claw_r", -2.0F, 9.0F, -7.5F, 1.0F, 5.0F, 1.0F);
            for (int i = 0; i < 4; i++) {
                root.addOrReplaceChild("rl" + i, CubeListBuilder.create().texOffs(0, 110).addBox(-16, -1, -1, 16, 2, 2), PartPose.offset(-4, 13, LEG_Z[i]));
                root.addOrReplaceChild("ll" + i, CubeListBuilder.create().texOffs(0, 110).addBox(0, -1, -1, 16, 2, 2), PartPose.offset(4, 13, LEG_Z[i]));
            }
            return LayerDefinition.create(mesh, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.head.yRot = netHeadYaw * DEG;
            this.head.xRot = headPitch * DEG;
            float swing = limbSwing * 0.6662F;
            float[] yr = {-0.6F, -0.2F, 0.2F, 0.6F};
            for (int i = 0; i < 4; i++) {
                float ph = i * 1.5707964F;
                float turn = (float) Math.cos(swing * 2.0F + ph) * 0.4F * limbSwingAmount;
                float lift = Math.abs((float) Math.sin(swing + ph)) * 0.4F * limbSwingAmount;
                this.right[i].yRot = yr[i] + turn;
                this.right[i].zRot = -0.7F + lift;
                this.left[i].yRot = -yr[i] - turn;
                this.left[i].zRot = 0.7F - lift;
            }
            float sway = (float) Math.cos(ageInTicks * 0.09F) * 0.08F;
            this.rightArm.xRot = -1.3F + sway;
            this.leftArm.xRot = -1.3F - sway;
            this.rightArm.zRot = 0.1F;
            this.leftArm.zRot = -0.1F;
        }
    }

    // ================================================================== Cao Parasita
    public static class Hound<T extends Mob> extends Base<T> {
        private final ModelPart head;
        private final ModelPart tail;
        private final ModelPart fr;
        private final ModelPart fl;
        private final ModelPart br;
        private final ModelPart bl;

        public Hound(ModelPart root) {
            super(root);
            this.head = root.getChild("head");
            this.tail = root.getChild("tail");
            this.fr = root.getChild("fr");
            this.fl = root.getChild("fl");
            this.br = root.getChild("br");
            this.bl = root.getChild("bl");
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-4, 10, -8, 8, 8, 16), PartPose.ZERO);
            PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 40).addBox(-4, -5, -8, 8, 8, 8),
                    PartPose.offset(0, 12, -8));
            eye(head, "eye_l", -3.5F, -3.5F, -8.1F, 2.0F, 1.5F, 0.2F);
            eye(head, "eye_r", 1.5F, -3.5F, -8.1F, 2.0F, 1.5F, 0.2F);
            dark(head, "mouth", -3.5F, 0.5F, -8.05F, 7.0F, 2.0F, 0.1F);
            for (int i = 0; i < 4; i++) {
                bone(head, "tu" + i, -3.0F + i * 2.0F, 0.5F, -8.2F, 1.0F, 2.0F, 0.2F);
            }
            bone(head, "tl0", -2.0F, 1.5F, -8.2F, 1.0F, 1.5F, 0.2F);
            bone(head, "tl1", 1.0F, 1.5F, -8.2F, 1.0F, 1.5F, 0.2F);
            root.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 90).addBox(-1, 0, 0, 2, 2, 9), PartPose.offset(0, 11, 8));
            float[] zs = {-6.0F, -6.0F, 6.0F, 6.0F};
            float[] xs = {-3.0F, 3.0F, -3.0F, 3.0F};
            String[] names = {"fr", "fl", "br", "bl"};
            for (int i = 0; i < 4; i++) {
                root.addOrReplaceChild(names[i], CubeListBuilder.create().texOffs(0, 70).addBox(-1.5F, 0, -1.5F, 3, 8, 3), PartPose.offset(xs[i], 16, zs[i]));
            }
            for (int i = 0; i < 4; i++) {
                bone(root, "spike" + i, -0.5F, 6.0F, -5.0F + i * 3.5F, 1.0F, 4.0F, 1.0F);
            }
            return LayerDefinition.create(mesh, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            this.head.yRot = netHeadYaw * DEG;
            this.head.xRot = headPitch * DEG;
            float s = limbSwing * 0.6662F;
            float a = 1.4F * limbSwingAmount;
            this.fr.xRot = (float) Math.cos(s) * a;
            this.bl.xRot = (float) Math.cos(s) * a;
            this.fl.xRot = (float) Math.cos(s + Math.PI) * a;
            this.br.xRot = (float) Math.cos(s + Math.PI) * a;
            this.tail.yRot = (float) Math.sin(ageInTicks * 0.25F) * 0.3F;
            this.tail.xRot = -0.4F;
        }
    }

    // ================================================================== Matriarca
    public static class Mother<T extends Mob> extends Base<T> {
        private final ModelPart[] tentacles = new ModelPart[6];
        private final ModelPart[] legs = new ModelPart[4];

        public Mother(ModelPart root) {
            super(root);
            for (int i = 0; i < 6; i++) {
                this.tentacles[i] = root.getChild("t" + i);
            }
            for (int i = 0; i < 4; i++) {
                this.legs[i] = root.getChild("leg" + i);
            }
        }

        public static LayerDefinition createLayer() {
            MeshDefinition mesh = new MeshDefinition();
            PartDefinition root = mesh.getRoot();
            root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 8).addBox(-10, 2, -10, 20, 16, 20), PartPose.ZERO);
            root.addOrReplaceChild("sac_a", CubeListBuilder.create().texOffs(0, 50).addBox(-7, -3, -4, 5, 5, 5), PartPose.ZERO);
            root.addOrReplaceChild("sac_b", CubeListBuilder.create().texOffs(0, 50).addBox(3, -3, 2, 5, 5, 5), PartPose.ZERO);
            root.addOrReplaceChild("sac_c", CubeListBuilder.create().texOffs(0, 50).addBox(-2, -2, 6, 4, 4, 4), PartPose.ZERO);
            for (int i = 0; i < 5; i++) {
                bone(root, "spike" + i, -1.0F + (i % 2 == 0 ? -4.0F : 4.0F) * (i == 0 ? 0 : 1), -6.0F + (i % 3) * 0.5F, -8.0F + i * 4.0F, 2.0F, 8.0F, 2.0F);
            }
            // rosto na frente (z = -10): tres olhos, boca escura e duas fileiras de presas
            eye(root, "eye_a", -7.0F, 4.0F, -10.3F, 3.0F, 3.0F, 0.6F);
            eye(root, "eye_b", 4.0F, 4.0F, -10.3F, 3.0F, 3.0F, 0.6F);
            eye(root, "eye_c", -1.5F, 2.5F, -10.3F, 3.0F, 3.0F, 0.6F);
            dark(root, "mouth", -6.0F, 10.0F, -10.2F, 12.0F, 6.0F, 0.4F);
            for (int k = 0; k < 6; k++) {
                bone(root, "fu" + k, -5.5F + k * 2.0F, 10.0F, -10.8F, 1.0F, 3.0F, 0.6F);
                bone(root, "fl" + k, -4.5F + k * 2.0F, 13.0F, -10.8F, 1.0F, 3.0F, 0.6F);
            }
            float[] zs = {-6.0F, 0.0F, 6.0F};
            for (int i = 0; i < 3; i++) {
                root.addOrReplaceChild("t" + i, CubeListBuilder.create().texOffs(0, 64).addBox(-3, 0, -1.5F, 3, 14, 3), PartPose.offset(-10, 10, zs[i]));
                root.addOrReplaceChild("t" + (i + 3), CubeListBuilder.create().texOffs(0, 64).addBox(0, 0, -1.5F, 3, 14, 3), PartPose.offset(10, 10, zs[i]));
            }
            float[] lx = {-6.0F, 6.0F, -6.0F, 6.0F};
            float[] lz = {-6.0F, -6.0F, 6.0F, 6.0F};
            for (int i = 0; i < 4; i++) {
                root.addOrReplaceChild("leg" + i, CubeListBuilder.create().texOffs(0, 90).addBox(-2.5F, 0, -2.5F, 5, 6, 5), PartPose.offset(lx[i], 18, lz[i]));
            }
            return LayerDefinition.create(mesh, 128, 128);
        }

        @Override
        public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
            for (int i = 0; i < 6; i++) {
                float sway = (float) Math.sin(ageInTicks * 0.1F + i) * 0.25F;
                float side = i < 3 ? 1.0F : -1.0F;
                this.tentacles[i].zRot = side * (0.3F + sway);
                this.tentacles[i].xRot = (float) Math.cos(ageInTicks * 0.08F + i * 1.3F) * 0.2F;
            }
            float s = limbSwing * 0.5F;
            float a = 0.9F * limbSwingAmount;
            this.legs[0].xRot = (float) Math.cos(s) * a;
            this.legs[3].xRot = (float) Math.cos(s) * a;
            this.legs[1].xRot = (float) Math.cos(s + Math.PI) * a;
            this.legs[2].xRot = (float) Math.cos(s + Math.PI) * a;
        }
    }
}
