package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Bruxa Parasita (mini boss): joga pocoes que <b>infectam</b> os mobs atingidos (e enfraquecem/envenenam quem nao e
 * parasita), chama aranhas parasitas e, com menos da metade da vida, fica mais rapida e joga duas pocoes por vez.
 * A logica de impacto das pocoes fica em {@link ParasiteGeneralEvents}.
 */
public class ParasiteWitch extends Witch implements ParasiteMob {
    public static final String POTION_TAG = "ParasitePotion";

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.NOTCHED_10);
    private boolean enraged;
    private int summonCooldown = 200;

    public ParasiteWitch(EntityType<? extends Witch> type, Level level) {
        super(type, level);
        this.xpReward = 70;
        this.setPersistenceRequired();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.27D)
                .add(Attributes.ARMOR, 4.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals(); // a Witch guarda referencias aos objetivos de alvo; depois trocamos tudo
        this.goalSelector.getAvailableGoals().clear();
        this.targetSelector.getAvailableGoals().clear();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RangedAttackGoal(this, 1.0D, 30, 14.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));
        ParasiteBehavior.addTargets(this);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** Pocao parasita: a bruxa nao escolhe efeitos, sempre joga a pocao que infecta (marcada com POTION_TAG). */
    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (this.isDrinkingPotion()) {
            return;
        }
        int volley = this.enraged ? 2 : 1;
        for (int i = 0; i < volley; i++) {
            Vec3 v = target.getDeltaMovement();
            double dx = target.getX() + v.x - this.getX() + (i == 0 ? 0.0D : this.random.nextDouble() * 4.0D - 2.0D);
            double dy = target.getEyeY() - 1.1D - this.getY();
            double dz = target.getZ() + v.z - this.getZ() + (i == 0 ? 0.0D : this.random.nextDouble() * 4.0D - 2.0D);
            double d3 = Math.sqrt(dx * dx + dz * dz);
            ItemStack stack = new ItemStack(Items.SPLASH_POTION);
            PotionUtils.setPotion(stack, Potions.WEAKNESS);
            ThrownPotion potion = new ThrownPotion(this.level(), this);
            potion.setItem(stack);
            potion.setXRot(potion.getXRot() + 20.0F);
            potion.getPersistentData().putBoolean(POTION_TAG, true);
            potion.shoot(dx, dy + d3 * 0.2D, dz, 0.75F, 8.0F);
            this.level().addFreshEntity(potion);
        }
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_THROW, SoundSource.HOSTILE, 1.0F,
                0.8F + this.random.nextFloat() * 0.4F);
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
                speed.setBaseValue(speed.getBaseValue() * 1.25D);
            }
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
            level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.WITCH_CELEBRATE, SoundSource.HOSTILE, 2.0F, 0.6F);
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || --this.summonCooldown > 0) {
            return;
        }
        this.summonCooldown = 300;
        int near = level.getEntitiesOfClass(Mob.class, this.getBoundingBox().inflate(32.0D), e -> e instanceof ParasiteMob && e != this).size();
        if (near >= 12) {
            return;
        }
        int stage = ParasiteBehavior.phaseOf(this);
        for (int i = 0; i < 2; i++) {
            ParasiteSpider s = ModEntities.PARASITE_SPIDER.get().create(level);
            if (s == null) {
                continue;
            }
            s.moveTo(this.getX() + level.random.nextInt(5) - 2, this.getY(), this.getZ() + level.random.nextInt(5) - 2,
                    level.random.nextFloat() * 360.0F, 0.0F);
            OutbreakEvents.stampStage(s, stage);
            s.setTarget(target);
            level.addFreshEntity(s);
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
