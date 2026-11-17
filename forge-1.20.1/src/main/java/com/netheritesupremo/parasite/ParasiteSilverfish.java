package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * Traca Parasita (mini boss): traca gigante que espalha o virus. Infecta todos os mobs a 6 blocos a cada 3 s, bota 3
 * vermes a cada 10 s (ate 14 parasitas por perto) e, com menos da metade da vida, fica 30% mais rapida.
 */
public class ParasiteSilverfish extends Silverfish implements ParasiteMob {
    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.GREEN, BossEvent.BossBarOverlay.NOTCHED_6);
    private boolean enraged;
    private int broodCooldown = 100;

    public ParasiteSilverfish(EntityType<? extends Silverfish> type, Level level) {
        super(type, level);
        this.xpReward = 60;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 180.0D)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.enraged && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.enraged = true;
            AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
            if (speed != null) {
                speed.setBaseValue(speed.getBaseValue() * 1.3D);
            }
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        }
        if (this.tickCount % 60 == 0) {
            for (Mob m : level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(6.0D), e -> !(e instanceof ParasiteMob))) {
                ParasiteBehavior.infect(m);
            }
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || --this.broodCooldown > 0) {
            return;
        }
        this.broodCooldown = 200;
        int near = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(32.0D), e -> e instanceof ParasiteMob && e != this).size();
        if (near >= 14) {
            return;
        }
        int stage = ParasiteBehavior.phaseOf(this);
        for (int i = 0; i < 3; i++) {
            ParasiteWorm w = ModEntities.PARASITE_WORM.get().create(level);
            if (w == null) {
                continue;
            }
            w.moveTo(this.getX() + level.random.nextInt(5) - 2, this.getY(), this.getZ() + level.random.nextInt(5) - 2,
                    level.random.nextFloat() * 360.0F, 0.0F);
            OutbreakEvents.stampStage(w, stage);
            level.addFreshEntity(w);
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
}
