/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.HierarchicalModel
 *  net.minecraft.client.model.geom.ModelPart
 *  net.minecraft.util.Mth
 *  net.minecraft.world.entity.Mob
 */
package com.claudecontent.client.model;

import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;

public class ColossusModel<T extends Mob>
extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public ColossusModel(ModelPart root) {
        this.root = root;
        this.head = root.offsetRotation("head");
        this.rightArm = root.offsetRotation("right_arm");
        this.leftArm = root.offsetRotation("left_arm");
        this.rightLeg = root.offsetRotation("right_leg");
        this.leftLeg = root.offsetRotation("left_leg");
    }

    public ModelPart setupAnim() {
        return this.root;
    }

    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180);
        this.head.getAllParts = headPitch * ((float)Math.PI / 180);
        this.rightLeg.getAllParts = -1.5f * Mth.lengthSquared((float)limbSwing, (float)13.0f) * limbSwingAmount;
        this.leftLeg.getAllParts = 1.5f * Mth.lengthSquared((float)limbSwing, (float)13.0f) * limbSwingAmount;
        this.rightLeg.yRot = 0.0f;
        this.leftLeg.yRot = 0.0f;
        if (entity.isAggressive()) {
            float sway = Mth.lambda$outFromOrigin$1((float)(ageInTicks * 0.15f)) * 0.08f;
            this.rightArm.getAllParts = -1.6f + sway;
            this.leftArm.getAllParts = -1.6f - sway;
        } else {
            this.rightArm.getAllParts = (-0.2f + 1.5f * Mth.lengthSquared((float)limbSwing, (float)13.0f)) * limbSwingAmount;
            this.leftArm.getAllParts = (-0.2f - 1.5f * Mth.lengthSquared((float)limbSwing, (float)13.0f)) * limbSwingAmount;
        }
    }
}

