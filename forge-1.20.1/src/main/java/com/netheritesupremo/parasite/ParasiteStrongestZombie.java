package com.netheritesupremo.parasite;

import com.netheritesupremo.mob.StrongestZombie;
import com.netheritesupremo.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

/**
 * Zumbi Forte infectado: quase um chefe. Pula, arremessa quem acerta e, em vez de invocar zumbis comuns, invoca
 * zumbis infectados. Com metade da vida causa mais dano e ganha regeneracao.
 */
public class ParasiteStrongestZombie extends StrongestZombie implements ParasiteMob {
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean enraged;

    public ParasiteStrongestZombie(EntityType<? extends StrongestZombie> type, Level level) {
        super(type, level);
        this.xpReward = 300;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return StrongestZombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 1500.0D)
                .add(Attributes.ATTACK_DAMAGE, 45.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.38D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.ARMOR, 12.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    protected void addBehaviourGoals() {
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        ParasiteBehavior.addTargets(this);
    }

    @Override
    protected EntityType<? extends Zombie> minionType() {
        return ModEntities.PARASITE_ZOMBIE.get();
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (this.tickCount == 1) {
            this.bossEvent.setName(this.getDisplayName());
        }
        if (!this.enraged && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.enraged = true;
            AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
            if (dmg != null) {
                dmg.setBaseValue(dmg.getBaseValue() * 1.6D);
            }
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ZOMBIE_VILLAGER_CONVERTED,
                    SoundSource.HOSTILE, 3.0F, 0.5F);
        }
        if (this.enraged && this.tickCount % 20 == 0) {
            this.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 3, false, false, true));
        }
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void setCustomName(@Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.enraged = tag.getBoolean("Enraged");
        if (this.enraged) {
            this.bossEvent.setColor(BossEvent.BossBarColor.PURPLE);
        }
    }
}
