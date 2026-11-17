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
import net.minecraft.world.entity.LivingEntity;

/** Espinhos e tentaculos de carne que saem das costas e dos ombros dos infectados (camada estatica). */
public class ParasiteSpikesModel<T extends LivingEntity> extends EntityModel<T> {
    public static final ModelLayerLocation HUMANOID = loc("humanoid");
    public static final ModelLayerLocation CREEPER = loc("creeper");
    public static final ModelLayerLocation ENDER = loc("ender");
    public static final ModelLayerLocation GOLEM = loc("golem");
    public static final ModelLayerLocation TUMORS = loc("tumors");

    private final ModelPart root;

    public ParasiteSpikesModel(ModelPart root) {
        this.root = root;
    }

    private static ModelLayerLocation loc(String name) {
        return new ModelLayerLocation(new ResourceLocation(NetheriteSupremo.MODID, "parasite_spikes"), name);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack pose, VertexConsumer vc, int light, int overlay, float r, float g, float b, float a) {
        this.root.render(pose, vc, light, overlay, r, g, b, a);
    }

    // x, y, z, rotX, rotY, rotZ, comprimento, espessura (+z e para tras; rotX positivo aponta para cima)
    private static final float[][] BODY = {
            {-2.5F, 2F, 2F, 0.9F, 0F, 0F, 9F, 1.5F},
            {2.5F, 2F, 2F, 0.9F, 0F, 0F, 9F, 1.5F},
            {-3F, 6F, 2F, 0.35F, -0.25F, 0F, 11F, 2F},
            {0F, 5F, 2F, 0.25F, 0F, 0F, 12F, 2F},
            {3F, 6F, 2F, 0.35F, 0.25F, 0F, 11F, 2F},
            {-2F, 10F, 2F, -0.3F, -0.2F, 0F, 8F, 1.5F},
            {2F, 10F, 2F, -0.3F, 0.2F, 0F, 8F, 1.5F},
            {-5.5F, -0.5F, 0F, 1.1F, -1.4F, 0F, 7F, 1.5F},
            {5.5F, -0.5F, 0F, 1.1F, 1.4F, 0F, 7F, 1.5F},
    };
    private static final float[][] ENDER_BODY = {
            {-3F, 2F, 2F, -0.45F, -0.2F, 0F, 24F, 1F},
            {3F, 2F, 2F, -0.45F, 0.2F, 0F, 24F, 1F},
            {-1F, 6F, 2F, -0.7F, -0.1F, 0F, 20F, 1F},
            {1F, 6F, 2F, -0.7F, 0.1F, 0F, 20F, 1F},
            {-2.5F, 1F, 2F, 0.9F, 0F, 0F, 12F, 2F},
            {2.5F, 1F, 2F, 0.9F, 0F, 0F, 12F, 2F},
            {0F, 4F, 2F, 0.3F, 0F, 0F, 14F, 2F},
            {-5.5F, 0F, 0F, 1.1F, -1.4F, 0F, 9F, 1.5F},
            {5.5F, 0F, 0F, 1.1F, 1.4F, 0F, 9F, 1.5F},
    };

    private static final float[][] GOLEM_BODY = {
            {-6F, -7F, 5F, 0.9F, -0.2F, 0F, 12F, 2.5F},
            {6F, -7F, 5F, 0.9F, 0.2F, 0F, 12F, 2.5F},
            {0F, -5F, 5F, 0.5F, 0F, 0F, 14F, 3F},
            {-6F, -1F, 5F, 0.3F, -0.2F, 0F, 11F, 2.5F},
            {6F, -1F, 5F, 0.3F, 0.2F, 0F, 11F, 2.5F},
            {0F, 1F, 5F, -0.1F, 0F, 0F, 10F, 2.5F},
            {-10F, -8F, 0F, 1.1F, -1.4F, 0F, 9F, 2F},
            {10F, -8F, 0F, 1.1F, 1.4F, 0F, 9F, 2F},
    };

    private static LayerDefinition build(float[][] spikes, float yTop, float lengthScale) {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (int i = 0; i < spikes.length; i++) {
            float[] s = spikes[i];
            float len = s[6] * lengthScale;
            float t = s[7];
            root.addOrReplaceChild("spike" + i, CubeListBuilder.create().texOffs(0, 0)
                    .addBox(-t / 2.0F, -t / 2.0F, 0.0F, t, t, len), PartPose.offsetAndRotation(s[0], yTop + s[1], s[2], s[3], s[4], s[5]));
        }
        return LayerDefinition.create(mesh, 64, 16);
    }

    public static LayerDefinition humanoid() {
        return build(BODY, 0.0F, 1.0F);
    }

    public static LayerDefinition creeper() {
        return build(BODY, 6.0F, 0.85F);
    }

    public static LayerDefinition golem() {
        return build(GOLEM_BODY, 0.0F, 1.0F);
    }

    public static LayerDefinition ender() {
        return build(ENDER_BODY, -14.0F, 1.0F);
    }

    // x, y, z (canto do cubo), largura, altura, profundidade: caroços de carne assimetricos no corpo e na cabeca
    private static final float[][] LUMPS = {
            {-6.0F, 0.5F, -1.5F, 3F, 4F, 3F},
            {3.5F, 6.0F, 1.0F, 4F, 3F, 3F},
            {-2.5F, 3.0F, 1.8F, 5F, 4F, 3F},
            {0.5F, -9.5F, -1.5F, 3F, 3F, 3F},
            {-4.5F, -5.5F, -2.5F, 3F, 3F, 3F},
            {1.5F, 8.5F, -3.5F, 3F, 2F, 2F},
            {4.5F, 0.0F, 1.0F, 2F, 6F, 2F},
            {-4.0F, 11.0F, -2.0F, 3F, 4F, 3F},
            {-1.0F, -3.0F, 4.0F, 2F, 2F, 2F},
    };

    public static LayerDefinition tumors() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        for (int i = 0; i < LUMPS.length; i++) {
            float[] l = LUMPS[i];
            root.addOrReplaceChild("lump" + i, CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, l[3], l[4], l[5]),
                    PartPose.offset(l[0], l[1], l[2]));
        }
        return LayerDefinition.create(mesh, 64, 16);
    }
}
