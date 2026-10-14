package com.netheritesupremo.entity;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Necromante (boss). Usa o modelo de esqueleto com a textura propria. Voa, atravessa paredes,
 * lanca cabecas de wither e invoca esqueletos com armadura de diamante encantada, com arco ou
 * espada de diamante encantados.
 */
public class Necromancer extends Monster implements RangedAttackMob {
    public static final double MAX_HEALTH = 195.0D;
    public static final double ATTACK_DAMAGE = 55.5D;

    /** Tag dos esqueletos invocados (o necromante conta, limita e remove eles). */
    public static final String MINION_TAG = "netheritesupremo_minion";
    public static final int MAX_MINIONS = 6;
    public static final int MINIONS_PER_CAST = 3;
    public static final int MINION_LIFETIME_TICKS = 20 * 90;

    private static final double HOVER_DISTANCE = 8.0D;
    private static final double HOVER_HEIGHT = 3.5D;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);

    public Necromancer(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.moveControl = new NecromancerMoveControl(this);
        this.xpReward = 150;
        this.setPersistenceRequired(); // boss: nunca some por distancia
        this.setNoGravity(true);
    }

    public static AttributeSupplier.Builder createNecromancerAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D);
    }

    // ------------------------------------------------- voo e atravessar paredes
    @Override
    public void tick() {
        this.noPhysics = true; // atravessa blocos (e nao sufoca dentro deles)
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);
    }

    @Override
    public boolean causeFallDamage(float distance, float multiplier, DamageSource source) {
        return false;
    }

    // ------------------------------------------------------------------ IA
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new SummonSkeletonsGoal(this));
        this.goalSelector.addGoal(3, new SkullAttackGoal(this));
        this.goalSelector.addGoal(4, new HoverNearTargetGoal(this));
        this.goalSelector.addGoal(5, new MeleeStrikeGoal(this));
        this.goalSelector.addGoal(8, new WanderGoal(this));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 16.0F));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        // Mira em jogadores mesmo sem linha de visao: ele atravessa paredes
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, false, false, null));
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        double sx = this.getX();
        double sy = this.getEyeY() - 0.2D;
        double sz = this.getZ();
        double dx = target.getX() - sx;
        double dy = target.getY(0.5D) - sy;
        double dz = target.getZ() - sz;
        NecromancerSkull skull = new NecromancerSkull(this.level(), this, dx, dy, dz);
        Vec3 forward = new Vec3(dx, dy, dz).normalize();
        skull.setPos(sx + forward.x, sy + forward.y, sz + forward.z);
        this.level().addFreshEntity(skull);
        this.playSound(SoundEvents.WITHER_SHOOT, 1.0F, 1.0F);
    }

    // ------------------------------------------------------ esqueletos invocados
    private List<Skeleton> minions() {
        return this.level().getEntitiesOfClass(Skeleton.class, this.getBoundingBox().inflate(64.0D),
                skeleton -> skeleton.isAlive() && skeleton.getTags().contains(MINION_TAG));
    }

    private void summonMinions(LivingEntity target) {
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        int count = Math.min(MINIONS_PER_CAST, MAX_MINIONS - this.minions().size());
        for (int i = 0; i < count; i++) {
            Skeleton skeleton = EntityType.SKELETON.create(serverLevel);
            if (skeleton == null) {
                continue;
            }
            BlockPos spot = this.findSpawnSpot(serverLevel, target);
            skeleton.moveTo(spot.getX() + 0.5D, spot.getY(), spot.getZ() + 0.5D, this.random.nextFloat() * 360.0F, 0.0F);
            skeleton.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(spot), MobSpawnType.MOB_SUMMONED, null, null);
            this.equipMinion(skeleton);
            skeleton.addTag(MINION_TAG);
            skeleton.setTarget(target);
            serverLevel.addFreshEntityWithPassengers(skeleton);
            serverLevel.sendParticles(ParticleTypes.SOUL, skeleton.getX(), skeleton.getY() + 1.0D, skeleton.getZ(),
                    12, 0.3D, 0.6D, 0.3D, 0.02D);
        }
        this.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.5F, 0.7F);
    }

    private void equipMinion(Skeleton skeleton) {
        skeleton.setItemSlot(EquipmentSlot.HEAD, enchantedArmor(Items.DIAMOND_HELMET));
        skeleton.setItemSlot(EquipmentSlot.CHEST, enchantedArmor(Items.DIAMOND_CHESTPLATE));
        skeleton.setItemSlot(EquipmentSlot.LEGS, enchantedArmor(Items.DIAMOND_LEGGINGS));
        skeleton.setItemSlot(EquipmentSlot.FEET, enchantedArmor(Items.DIAMOND_BOOTS));

        ItemStack weapon;
        if (this.random.nextBoolean()) {
            weapon = new ItemStack(Items.BOW);
            weapon.enchant(Enchantments.POWER_ARROWS, 5);
            weapon.enchant(Enchantments.PUNCH_ARROWS, 2);
            weapon.enchant(Enchantments.FLAMING_ARROWS, 1);
        } else {
            weapon = new ItemStack(Items.DIAMOND_SWORD);
            weapon.enchant(Enchantments.SHARPNESS, 5);
            weapon.enchant(Enchantments.FIRE_ASPECT, 2);
        }
        weapon.enchant(Enchantments.UNBREAKING, 3);
        skeleton.setItemSlot(EquipmentSlot.MAINHAND, weapon);

        // Nao dropam o equipamento: invocar de novo nao pode virar fazenda de diamante encantado
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            skeleton.setDropChance(slot, 0.0F);
        }
    }

    private static ItemStack enchantedArmor(Item item) {
        ItemStack stack = new ItemStack(item);
        stack.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
        stack.enchant(Enchantments.UNBREAKING, 3);
        return stack;
    }

    private BlockPos findSpawnSpot(ServerLevel level, LivingEntity target) {
        for (int attempt = 0; attempt < 12; attempt++) {
            int x = target.getBlockX() + this.random.nextInt(13) - 6;
            int z = target.getBlockZ() + this.random.nextInt(13) - 6;
            for (int dy = 3; dy >= -4; dy--) {
                BlockPos pos = new BlockPos(x, target.getBlockY() + dy, z);
                if (isStandable(level, pos)) {
                    return pos;
                }
            }
        }
        return target.blockPosition();
    }

    private static boolean isStandable(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()
                && level.getBlockState(pos.above()).getCollisionShape(level, pos.above()).isEmpty()
                && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        // Esqueletos invocados somem depois de 90 segundos
        if (this.tickCount % 20 == 0) {
            for (Skeleton skeleton : this.minions()) {
                if (skeleton.tickCount > MINION_LIFETIME_TICKS) {
                    skeleton.kill();
                }
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide) {
            for (Skeleton skeleton : this.minions()) {
                skeleton.kill();
            }
        }
    }

    // -------------------------------------------------------------- barra de boss
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

    // ---------------------------------------------------------------- sons
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }

    // ============================================================ controle de voo
    /** Mesmo controle de movimento do vex: acelera direto ate o ponto, sem pathfinding. */
    static class NecromancerMoveControl extends MoveControl {
        NecromancerMoveControl(Necromancer boss) {
            super(boss);
        }

        @Override
        public void tick() {
            if (this.operation != MoveControl.Operation.MOVE_TO) {
                return;
            }
            Vec3 toTarget = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(),
                    this.wantedZ - this.mob.getZ());
            double distance = toTarget.length();
            if (distance < this.mob.getBoundingBox().getSize()) {
                this.operation = MoveControl.Operation.WAIT;
                this.mob.setDeltaMovement(this.mob.getDeltaMovement().scale(0.5D));
                return;
            }
            this.mob.setDeltaMovement(this.mob.getDeltaMovement().add(toTarget.scale(this.speedModifier * 0.05D / distance)));
            if (this.mob.getTarget() == null) {
                Vec3 motion = this.mob.getDeltaMovement();
                this.mob.setYRot(-((float) Mth.atan2(motion.x, motion.z)) * (180.0F / (float) Math.PI));
            } else {
                double dx = this.mob.getTarget().getX() - this.mob.getX();
                double dz = this.mob.getTarget().getZ() - this.mob.getZ();
                this.mob.setYRot(-((float) Mth.atan2(dx, dz)) * (180.0F / (float) Math.PI));
            }
            this.mob.yBodyRot = this.mob.getYRot();
        }
    }

    // ============================================================ objetivos (goals)
    /** Pairando a ~8 blocos do alvo, 3,5 blocos acima, mudando de lado de vez em quando. */
    static class HoverNearTargetGoal extends Goal {
        private final Necromancer boss;
        private double angle;
        private int nextRetarget;

        HoverNearTargetGoal(Necromancer boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            Vec3 away = boss.position().subtract(boss.getTarget().position());
            this.angle = Mth.atan2(away.z, away.x);
            this.nextRetarget = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = boss.getTarget();
            if (target == null) {
                return;
            }
            boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (boss.tickCount >= nextRetarget) {
                nextRetarget = boss.tickCount + 60 + boss.getRandom().nextInt(40);
                angle += (boss.getRandom().nextDouble() - 0.5D) * 1.2D;
            }
            double x = target.getX() + Math.cos(angle) * HOVER_DISTANCE;
            double z = target.getZ() + Math.sin(angle) * HOVER_DISTANCE;
            double y = target.getY() + HOVER_HEIGHT;
            boss.getMoveControl().setWantedPosition(x, y, z, 1.4D);
        }
    }

    /** Sem alvo: flutua sem rumo por perto. */
    static class WanderGoal extends Goal {
        private final Necromancer boss;

        WanderGoal(Necromancer boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return boss.getTarget() == null && !boss.getMoveControl().hasWanted()
                    && boss.getRandom().nextInt(reducedTickDelay(7)) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return boss.getTarget() == null && boss.getMoveControl().hasWanted();
        }

        @Override
        public void start() {
            BlockPos origin = boss.blockPosition();
            double x = origin.getX() + boss.getRandom().nextInt(15) - 7 + 0.5D;
            double y = origin.getY() + boss.getRandom().nextInt(8) - 2;
            double z = origin.getZ() + boss.getRandom().nextInt(15) - 7 + 0.5D;
            boss.getMoveControl().setWantedPosition(x, y, z, 0.5D);
        }
    }

    /** Lanca uma cabeca de wither no alvo a cada ~2 segundos. */
    static class SkullAttackGoal extends Goal {
        private final Necromancer boss;
        private int nextShot = 40;

        SkullAttackGoal(Necromancer boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = boss.getTarget();
            if (target == null || !target.isAlive() || boss.tickCount < nextShot) {
                return false;
            }
            double distSqr = boss.distanceToSqr(target);
            return distSqr >= 16.0D && distSqr <= 32.0D * 32.0D && boss.hasLineOfSight(target);
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = boss.getTarget();
            if (target != null) {
                boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
                boss.performRangedAttack(target, 1.0F);
            }
            nextShot = boss.tickCount + 35 + boss.getRandom().nextInt(25);
        }
    }

    /** Pancada corpo a corpo (55,5 de dano) quando o alvo chega muito perto. */
    static class MeleeStrikeGoal extends Goal {
        private final Necromancer boss;
        private int nextStrike;

        MeleeStrikeGoal(Necromancer boss) {
            this.boss = boss;
            this.setFlags(EnumSet.noneOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && boss.tickCount >= nextStrike
                    && boss.distanceToSqr(target) < 3.5D * 3.5D;
        }

        @Override
        public boolean canContinueToUse() {
            return false;
        }

        @Override
        public void start() {
            LivingEntity target = boss.getTarget();
            if (target != null) {
                boss.swing(InteractionHand.MAIN_HAND);
                boss.doHurtTarget(target);
            }
            nextStrike = boss.tickCount + 20;
        }
    }

    /** Invoca esqueletos encantados perto do alvo, no maximo 6 vivos ao mesmo tempo. */
    static class SummonSkeletonsGoal extends Goal {
        private final Necromancer boss;
        private int nextCast = 60;
        private int windup;

        SummonSkeletonsGoal(Necromancer boss) {
            this.boss = boss;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = boss.getTarget();
            return target != null && target.isAlive() && boss.tickCount >= nextCast
                    && boss.minions().size() < MAX_MINIONS;
        }

        @Override
        public boolean canContinueToUse() {
            return windup > 0 && boss.getTarget() != null && boss.getTarget().isAlive();
        }

        @Override
        public void start() {
            windup = 30; // 1,5 segundo de preparo
            boss.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 1.2F, 0.6F);
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = boss.getTarget();
            if (target == null) {
                return;
            }
            boss.getLookControl().setLookAt(target, 30.0F, 30.0F);
            boss.setDeltaMovement(boss.getDeltaMovement().scale(0.8D));
            if (boss.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, boss.getX(), boss.getY() + 1.2D, boss.getZ(),
                        3, 0.4D, 0.6D, 0.4D, 0.01D);
            }
            if (--windup <= 0) {
                boss.summonMinions(target);
                nextCast = boss.tickCount + 160 + boss.getRandom().nextInt(80);
            }
        }

        @Override
        public void stop() {
            windup = 0;
        }
    }
}
