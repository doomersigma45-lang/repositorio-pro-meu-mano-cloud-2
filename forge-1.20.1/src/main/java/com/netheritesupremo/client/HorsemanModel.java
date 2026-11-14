package com.netheritesupremo.client;

import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.RangedAttackMob;

/** Modelo de esqueleto sem a cabeca: no lugar dela vai a abobora (item de cabeca). */
public class HorsemanModel<T extends Mob & RangedAttackMob> extends SkeletonModel<T> {
    public HorsemanModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(T mob, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(mob, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.head.visible = false;
        this.hat.visible = false;
    }
}
