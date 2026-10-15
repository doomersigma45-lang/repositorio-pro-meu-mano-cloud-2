package com.netheritesupremo.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import com.netheritesupremo.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.FollowParentGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

/**
 * Aranha do Nether. Hostil com jogadores e piglins; domavel com barras de ouro (domada, nao ataca
 * jogadores); montaria rapida; escala paredes; solta bolas de fogo; imune a fogo e explosoes;
 * reproduz com fungos do Nether (com outra aranha do Nether ou com qualquer aranha) gerando um
 * filhote passivo.
 */
public class NetherSpider extends TamableAnimal {
    public static final double MAX_HEALTH = 65.0D;
    public static final double MIN_DAMAGE = 6.0D;
    public static final double MAX_DAMAGE = 12.0D;
    public static final double MOVEMENT_SPEED = 0.35D;
    /** Velocidade montada. Um cavalo no maximo chega a 0.3375. */
    public static final float RIDDEN_SPEED = 0.45F;
    public static final double FIREBALL_MIN_DISTANCE = 4.0D;
    public static final double FIREBALL_MAX_DISTANCE = 24.0D;
    public static final float TAME_HEAL = 10.0F;

    public NetherSpider(EntityType<? extends NetherSpider> type, Level level) {
        super(type, level);
        this.xpReward = 12;
    }

    public static AttributeSupplier.Builder createSpiderAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, MIN_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    /** Spawn natural: no chao, fora do modo pacifico. */
    public static boolean checkSpiderSpawnRules(EntityType<NetherSpider> type, ServerLevelAccessor level,
                                                MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    // ------------------------------------------------------------------ IA
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FireballGoal(this));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.3D, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new MateWithOtherSpiderGoal(this));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2D, false));
        this.goalSelector.addGoal(6, new FollowParentGoal(this, 1.1D));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
        // Selvagem: ataca jogadores (menos quem segura uma barra de ouro na mao)
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                player -> !isHoldingGold((Player) player)) {
            @Override
            public boolean canUse() {
                return !NetherSpider.this.isTame() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return !NetherSpider.this.isTame() && super.canContinueToUse();
            }
        });
        // Ataca piglins (domada ou nao)
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                other -> other instanceof Piglin || other instanceof PiglinBrute));
    }

    private static boolean isHoldingGold(Player player) {
        return player.getMainHandItem().is(Items.GOLD_INGOT) || player.getOffhandItem().is(Items.GOLD_INGOT);
    }

    /** Filhote e passivo; domada nunca ataca jogadores. */
    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.isBaby()) {
            return false;
        }
        if (this.isTame() && target instanceof Player) {
            return false;
        }
        return super.canAttack(target);
    }

    /** Dano corpo a corpo sorteado entre 6 e 12. */
    @Override
    public boolean doHurtTarget(Entity target) {
        AttributeInstance damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.setBaseValue(MIN_DAMAGE + this.random.nextDouble() * (MAX_DAMAGE - MIN_DAMAGE));
        }
        return super.doHurtTarget(target);
    }

    // ----------------------------------------------------- imunidades / aranha
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(source);
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public void makeStuckInBlock(BlockState state, Vec3 motionMultiplier) {
        if (!state.is(Blocks.COBWEB)) {
            super.makeStuckInBlock(state, motionMultiplier);
        }
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    /** Escala qualquer bloco contra o qual esteja empurrando (como a aranha). */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision && !this.isSpectator();
    }

    @Override
    protected float getStandingEyeHeight(Pose pose, EntityDimensions dimensions) {
        return 0.85F;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 2;
    }

    /** So some por distancia se for selvagem. */
    @Override
    public boolean removeWhenFarAway(double distanceSqr) {
        return !this.isTame() && !this.hasCustomName();
    }

    // -------------------------------------------------------- reproducao
    @Override
    public boolean isFood(ItemStack stack) {
        return stack.is(Items.CRIMSON_FUNGUS) || stack.is(Items.WARPED_FUNGUS);
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        NetherSpider baby = ModEntities.NETHER_SPIDER.get().create(level);
        if (baby != null) {
            copyOwnerTo(baby);
        }
        return baby;
    }

    void copyOwnerTo(NetherSpider baby) {
        if (this.isTame() && this.getOwnerUUID() != null) {
            baby.setOwnerUUID(this.getOwnerUUID());
            baby.setTame(true);
        }
    }

    // ----------------------------------------------- interacao (domar / montar)
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.isTame()) {
            if (stack.is(Items.GOLD_INGOT) && !this.isBaby()) {
                if (!this.level().isClientSide) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.random.nextInt(3) == 0) {
                        this.tame(player);
                        this.navigation.stop();
                        this.setTarget(null);
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return super.mobInteract(player, hand);
        }
        if (this.isFood(stack)) {
            return super.mobInteract(player, hand);
        }
        if (stack.is(Items.GOLD_INGOT) && this.getHealth() < this.getMaxHealth()) {
            this.heal(TAME_HEAL);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        if (this.isOwnedBy(player) && !this.isBaby() && !player.isSecondaryUseActive() && !this.isVehicle()) {
            if (!this.level().isClientSide) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    // ---------------------------------------------------------- montaria
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity passenger = this.getFirstPassenger();
        if (passenger instanceof Player player && this.isTame() && this.isOwnedBy(player)) {
            return player;
        }
        return null;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty() && this.isTame() && !this.isBaby();
    }

    @Override
    public double getPassengersRidingOffset() {
        return this.getBbHeight() * 0.9D;
    }

    @Override
    public void travel(Vec3 travelVector) {
        LivingEntity rider = this.getControllingPassenger();
        if (this.isAlive() && this.isVehicle() && rider != null) {
            this.setYRot(rider.getYRot());
            this.yRotO = this.getYRot();
            this.setXRot(rider.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.yBodyRot;
            float strafe = rider.xxa * 0.5F;
            float forward = rider.zza;
            if (forward <= 0.0F) {
                forward *= 0.25F;
            }
            if (this.isControlledByLocalInstance()) {
                this.setSpeed(RIDDEN_SPEED);
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
        } else {
            super.travel(travelVector);
        }
    }

    // ------------------------------------------------------------------ sons
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.SPIDER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.SPIDER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.SPIDER_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(SoundEvents.SPIDER_STEP, 0.15F, 1.0F);
    }

    // ------------------------------------------------------- bolas de fogo
    static class FireballGoal extends Goal {
        private final NetherSpider spider;
        private int cooldown = 40;

        FireballGoal(NetherSpider spider) {
            this.spider = spider;
            this.setFlags(EnumSet.noneOf(Flag.class));
        }

        @Override
        public boolean canUse() {
            return validTarget();
        }

        @Override
        public boolean canContinueToUse() {
            return validTarget();
        }

        private boolean validTarget() {
            LivingEntity target = spider.getTarget();
            if (spider.isBaby() || spider.isVehicle() || target == null || !target.isAlive()
                    || !spider.hasLineOfSight(target)) {
                return false;
            }
            double distSqr = spider.distanceToSqr(target);
            return distSqr >= FIREBALL_MIN_DISTANCE * FIREBALL_MIN_DISTANCE
                    && distSqr <= FIREBALL_MAX_DISTANCE * FIREBALL_MAX_DISTANCE;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = spider.getTarget();
            if (target == null) {
                return;
            }
            spider.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--cooldown > 0) {
                return;
            }
            cooldown = 50 + spider.getRandom().nextInt(30);
            Level level = spider.level();
            double sx = spider.getX();
            double sy = spider.getY(0.6D);
            double sz = spider.getZ();
            double dx = target.getX() - sx;
            double dy = target.getY(0.5D) - sy;
            double dz = target.getZ() - sz;
            SmallFireball fireball = new SmallFireball(level, spider, dx, dy, dz);
            fireball.setPos(sx, sy, sz);
            level.addFreshEntity(fireball);
            level.playSound(null, spider.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }

    // ------------------------- reproducao com outros tipos de aranha (vanilla/mods)
    static class MateWithOtherSpiderGoal extends Goal {
        private static final double SEARCH_RADIUS = 16.0D;

        private final NetherSpider spider;
        private Spider partner;
        private int loveTime;

        MateWithOtherSpiderGoal(NetherSpider spider) {
            this.spider = spider;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!spider.isInLove() || spider.isBaby()) {
                return false;
            }
            // Se existe outra aranha do Nether apaixonada, quem cuida e o BreedGoal normal.
            List<NetherSpider> mates = spider.level().getEntitiesOfClass(NetherSpider.class,
                    spider.getBoundingBox().inflate(SEARCH_RADIUS),
                    other -> other != spider && other.isInLove() && !other.isBaby());
            if (!mates.isEmpty()) {
                return false;
            }
            List<Spider> spiders = spider.level().getEntitiesOfClass(Spider.class,
                    spider.getBoundingBox().inflate(SEARCH_RADIUS), Entity::isAlive);
            partner = spiders.stream().min(Comparator.comparingDouble(spider::distanceToSqr)).orElse(null);
            return partner != null;
        }

        @Override
        public boolean canContinueToUse() {
            return partner != null && partner.isAlive() && spider.isInLove();
        }

        @Override
        public void start() {
            loveTime = 0;
        }

        @Override
        public void stop() {
            partner = null;
            loveTime = 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            spider.getLookControl().setLookAt(partner, 10.0F, spider.getMaxHeadXRot());
            spider.getNavigation().moveTo(partner, 1.0D);
            if (spider.distanceToSqr(partner) < 6.25D && ++loveTime >= 40) {
                breed();
            }
        }

        private void breed() {
            if (!(spider.level() instanceof ServerLevel server)) {
                return;
            }
            NetherSpider baby = ModEntities.NETHER_SPIDER.get().create(server);
            if (baby != null) {
                baby.setBaby(true);
                baby.moveTo(spider.getX(), spider.getY(), spider.getZ(), 0.0F, 0.0F);
                spider.copyOwnerTo(baby);
                server.addFreshEntityWithPassengers(baby);
                if (server.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
                    server.addFreshEntity(new ExperienceOrb(server, spider.getX(), spider.getY(), spider.getZ(),
                            spider.getRandom().nextInt(7) + 1));
                }
            }
            spider.setAge(6000);
            spider.resetLove();
            server.broadcastEntityEvent(spider, (byte) 18);
            partner = null;
        }
    }
}
