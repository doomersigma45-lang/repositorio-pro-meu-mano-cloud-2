package com.netheritesupremo.mob;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Fungus Stalker: hibrido de Wood Stalker e Fungus (bipede com quatro pernas, chapeu de cogumelo, cogumelos nas costas).
 * Dobro de HP e de dano de um Wood Stalker (250 / 50), todas as habilidades do Wood Stalker e os esporos do Fungus
 * (lentidao e fraqueza na mordida e em explosao de esporos), e regenera tambem perto de cogumelos.
 */
public class FungusStalker extends WoodStalker {
    public static final double MAX_HEALTH = WoodStalker.MAX_HEALTH * 2.0D;
    public static final double DAMAGE = WoodStalker.DAMAGE * 2.0D;

    public FungusStalker(EntityType<? extends FungusStalker> type, Level level) {
        super(type, level);
        this.xpReward = 60;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    private static boolean isSporeVictim(LivingEntity e) {
        if (e instanceof Fungus || e instanceof WoodStalker || !e.isAlive()) {
            return false;
        }
        return e instanceof Player p ? !p.isCreative() && !p.isSpectator() : !(e instanceof Enemy);
    }

    private static void spore(LivingEntity e) {
        e.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        e.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
    }

    @Override
    protected void onHitVictim(LivingEntity victim) {
        if (isSporeVictim(victim) || (victim instanceof Enemy && !(victim instanceof Fungus) && !(victim instanceof WoodStalker))) {
            spore(victim);
        }
    }

    @Override
    protected boolean isRegenBlock(BlockState s) {
        return super.isRegenBlock(s) || s.is(Blocks.RED_MUSHROOM_BLOCK) || s.is(Blocks.BROWN_MUSHROOM_BLOCK)
                || s.is(Blocks.MUSHROOM_STEM) || s.is(Blocks.RED_MUSHROOM) || s.is(Blocks.BROWN_MUSHROOM);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        LivingEntity target = this.getTarget();
        if (this.tickCount % 60 == 0 && target != null && this.distanceTo(target) < 8.0F) {
            level.sendParticles(ParticleTypes.SPORE_BLOSSOM_AIR, this.getX(), this.getY() + 1.5D, this.getZ(), 50, 2.5D, 1.0D, 2.5D, 0.0D);
            for (LivingEntity e : level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(Fungus.SPORE_RADIUS),
                    e -> isSporeVictim(e) || (e instanceof Enemy && !(e instanceof Fungus) && !(e instanceof WoodStalker) && e.isAlive()))) {
                spore(e);
            }
        }
    }
}
