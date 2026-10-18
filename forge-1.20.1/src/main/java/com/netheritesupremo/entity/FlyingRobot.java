package com.netheritesupremo.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Base dos robos hostis voadores: movimento aereo, imunes a fogo, explosoes, projeteis e queda,
 * e chama nas costas ao voar.
 */
public abstract class FlyingRobot extends Monster {
    protected FlyingRobot(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setNoGravity(true);
    }

    /** Particula da chama que sai das costas enquanto voa. */
    protected abstract ParticleOptions backFlame();

    /** Por padrao os robos sao imunes a fogo; o Precursor sobrescreve. */
    protected boolean immuneToFire() {
        return true;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return (this.immuneToFire() && source.is(DamageTypeTags.IS_FIRE))
                || source.is(DamageTypeTags.IS_EXPLOSION)
                || source.is(DamageTypeTags.IS_PROJECTILE)
                || source.is(DamageTypeTags.IS_FALL)
                || super.isInvulnerableTo(source);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, BlockState state, BlockPos pos) {
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide && !this.onGround()) {
            float rad = this.yBodyRot * ((float) Math.PI / 180.0F);
            double x = this.getX() + Mth.sin(rad) * 0.4D;
            double z = this.getZ() - Mth.cos(rad) * 0.4D;
            double y = this.getY() + this.getBbHeight() * 0.6D;
            for (int i = 0; i < 2; i++) {
                this.level().addParticle(this.backFlame(),
                        x + (this.random.nextDouble() - 0.5D) * 0.3D, y + (this.random.nextDouble() - 0.5D) * 0.3D,
                        z + (this.random.nextDouble() - 0.5D) * 0.3D, 0.0D, -0.02D, 0.0D);
            }
        }
    }
}
