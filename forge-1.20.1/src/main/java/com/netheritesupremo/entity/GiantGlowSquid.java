package com.netheritesupremo.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.GlowSquid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

/** Lula Brilhante Gigante (boss): 700 de vida, tinta brilhante de 10,7 de dano. Nasce em oceanos e cavernas subaquaticas grandes. */
public class GiantGlowSquid extends AbstractGiantSquid {
    public static final double MAX_HEALTH = 700.0D;
    public static final float INK_DAMAGE = 10.7F;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);

    public GiantGlowSquid(EntityType<? extends GlowSquid> type, Level level) {
        super(type, level);
        this.xpReward = 500;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createBossAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 20.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.8D)
                .add(Attributes.FOLLOW_RANGE, 48.0D);
    }

    @Override
    public float inkDamage() {
        return INK_DAMAGE;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    /**
     * Spawn natural: bem fundo (oceano profundo) ou em caverna alagada, com um bolsao de agua grande
     * (pelo menos 7x7x7 quase todo de agua) para caber o boss.
     */
    public static boolean checkSpawnRules(EntityType<? extends GlowSquid> type, ServerLevelAccessor level,
            MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (pos.getY() > level.getSeaLevel() - 10 || !level.getFluidState(pos).is(FluidTags.WATER)) {
            return false;
        }
        int water = 0;
        for (BlockPos p : BlockPos.betweenClosed(pos.offset(-3, -3, -3), pos.offset(3, 3, 3))) {
            if (level.getFluidState(p).is(FluidTags.WATER)) {
                water++;
            }
        }
        return water >= 300; // de 343
    }

    // --------------------------------------------------------------- barra de boss
    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
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
