package com.netheritesupremo.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * Aranha do nether. Estende a aranha (modelo, escalada de paredes, sons). Hostil a jogadores e
 * piglins; domavel com barras de ouro (domada nao ataca jogadores); serve de montaria; solta bolas de
 * fogo; reproduz com fungos do nether (com outra aranha do nether ou qualquer outra aranha) gerando um
 * filhote passivo.
 */
public class NetherSpider extends Spider {
    public static final double MAX_HEALTH = 65.0D;
    public static final double MOVEMENT_SPEED = 0.45D; // cavalos chegam a no maximo 0.3375
    public static final float MIN_DAMAGE = 6.0F;
    public static final float MAX_DAMAGE = 12.0F;

    public static final int TAME_ONE_IN = 3;
    public static final float GOLD_INGOT_HEAL = 10.0F;
    public static final int LOVE_TICKS = 600;
    public static final int BREED_COOLDOWN = 6000;
    public static final int GROW_TICKS = 24000;

    public static final double FIREBALL_MIN_DISTANCE = 5.0D;
    public static final double FIREBALL_MAX_DISTANCE = 18.0D;

    private static final EntityDataAccessor<Boolean> DATA_TAMED =
            SynchedEntityData.defineId(NetherSpider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_BABY =
            SynchedEntityData.defineId(NetherSpider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNER =
            SynchedEntityData.defineId(NetherSpider.class, EntityDataSerializers.OPTIONAL_UUID);

    private int inLove;
    private int breedCooldown;
    private int babyAge;

    public NetherSpider(EntityType<? extends Spider> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createNetherSpiderAttributes() {
        return Spider.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, MAX_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, MOVEMENT_SPEED)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_TAMED, false);
        this.entityData.define(DATA_BABY, false);
        this.entityData.define(DATA_OWNER, Optional.empty());
    }

    // ------------------------------------------------------------------ estado
    public boolean isTame() {
        return this.entityData.get(DATA_TAMED);
    }

    @Override
    public boolean isBaby() {
        return this.entityData.get(DATA_BABY);
    }

    public void setBaby(boolean baby) {
        this.entityData.set(DATA_BABY, baby);
        this.babyAge = 0;
    }

    @Nullable
    public UUID getOwnerUUID() {
        return this.entityData.get(DATA_OWNER).orElse(null);
    }

    public void tameBy(@Nullable UUID owner) {
        this.entityData.set(DATA_TAMED, true);
        this.entityData.set(DATA_OWNER, Optional.ofNullable(owner));
        this.setTarget(null);
        this.setPersistenceRequired();
    }

    public boolean isOwnedBy(LivingEntity entity) {
        return this.isTame() && entity.getUUID().equals(this.getOwnerUUID());
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        if (DATA_BABY.equals(key)) {
            this.refreshDimensions();
        }
        super.onSyncedDataUpdated(key);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions dimensions = super.getDimensions(pose);
        return this.isBaby() ? dimensions.scale(0.5F) : dimensions;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tame", this.isTame());
        tag.putBoolean("Baby", this.isBaby());
        tag.putInt("BabyAge", this.babyAge);
        tag.putInt("BreedCooldown", this.breedCooldown);
        tag.putInt("InLove", this.inLove);
        if (this.getOwnerUUID() != null) {
            tag.putUUID("Owner", this.getOwnerUUID());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(DATA_TAMED, tag.getBoolean("Tame"));
        this.entityData.set(DATA_BABY, tag.getBoolean("Baby"));
        this.babyAge = tag.getInt("BabyAge");
        this.breedCooldown = tag.getInt("BreedCooldown");
        this.inLove = tag.getInt("InLove");
        this.entityData.set(DATA_OWNER, tag.hasUUID("Owner") ? Optional.of(tag.getUUID("Owner")) : Optional.empty());
        if (this.isTame()) {
            this.setPersistenceRequired();
        }
    }

    // ------------------------------------------------------------------ combate
    /** Imune a fogo (pelo tipo da entidade) e a explosoes. */
    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return source.is(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(source);
    }

    /** Dano corpo a corpo sorteado entre 6 e 12. */
    @Override
    public boolean doHurtTarget(Entity target) {
        this.getAttribute(Attributes.ATTACK_DAMAGE)
                .setBaseValue(MIN_DAMAGE + this.random.nextFloat() * (MAX_DAMAGE - MIN_DAMAGE));
        return super.doHurtTarget(target);
    }

    /** Filhote e aranha domada nunca escolhem jogadores como alvo. */
    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (target != null && (this.isBaby() || (this.isTame() && target instanceof Player))) {
            super.setTarget(null);
            return;
        }
        super.setTarget(target);
    }

    @Override
    protected boolean shouldDespawnInPeaceful() {
        return !this.isTame();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SpiderBreedGoal(this));
        this.goalSelector.addGoal(3, new FireballGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                other -> !this.isTame() && !this.isBaby()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, 10, true, false,
                other -> !this.isBaby()));
    }

    // ------------------------------------------------------------------ montaria
    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        Entity first = this.getFirstPassenger();
        return first instanceof Player player && this.isOwnedBy(player) ? player : null;
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
                this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));
                super.travel(new Vec3(strafe, travelVector.y, forward));
            } else {
                this.setDeltaMovement(Vec3.ZERO);
            }
            this.calculateEntityAnimation(false);
        } else {
            super.travel(travelVector);
        }
    }

    // ------------------------------------------------------- interacao (domar, montar, reproduzir)
    private static boolean isFungus(ItemStack stack) {
        return stack.is(Items.CRIMSON_FUNGUS) || stack.is(Items.WARPED_FUNGUS);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        boolean client = this.level().isClientSide;

        if (!this.isTame()) {
            if (stack.is(Items.GOLD_INGOT) && !this.isBaby()) {
                if (!client) {
                    if (!player.getAbilities().instabuild) {
                        stack.shrink(1);
                    }
                    if (this.random.nextInt(TAME_ONE_IN) == 0) {
                        this.tameBy(player.getUUID());
                        this.level().broadcastEntityEvent(this, (byte) 7);
                    } else {
                        this.level().broadcastEntityEvent(this, (byte) 6);
                    }
                }
                return InteractionResult.sidedSuccess(client);
            }
            return super.mobInteract(player, hand);
        }

        if (stack.is(Items.GOLD_INGOT) && this.getHealth() < this.getMaxHealth()) {
            if (!client) {
                this.heal(GOLD_INGOT_HEAL);
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
            }
            return InteractionResult.sidedSuccess(client);
        }
        if (!this.isOwnedBy(player)) {
            return InteractionResult.PASS;
        }
        if (isFungus(stack) && !this.isBaby() && this.inLove <= 0 && this.breedCooldown <= 0) {
            if (!client) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.inLove = LOVE_TICKS;
                this.level().broadcastEntityEvent(this, (byte) 7);
            }
            return InteractionResult.sidedSuccess(client);
        }
        if (!this.isBaby() && !this.isVehicle() && !player.isSecondaryUseActive() && stack.isEmpty()) {
            if (!client) {
                player.startRiding(this);
            }
            return InteractionResult.sidedSuccess(client);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 6 || id == 7) {
            ParticleOptions particle = id == 7 ? ParticleTypes.HEART : ParticleTypes.SMOKE;
            for (int i = 0; i < 7; i++) {
                this.level().addParticle(particle, this.getRandomX(1.0D), this.getRandomY() + 0.5D,
                        this.getRandomZ(1.0D), this.random.nextGaussian() * 0.02D, this.random.nextGaussian() * 0.02D,
                        this.random.nextGaussian() * 0.02D);
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.inLove > 0) {
                this.inLove--;
            }
            if (this.breedCooldown > 0) {
                this.breedCooldown--;
            }
            if (this.isBaby() && ++this.babyAge >= GROW_TICKS) {
                this.setBaby(false);
            }
        }
    }

    // ------------------------------------------------------------------ reproducao
    /** Procura parceiro (outra aranha do nether apaixonada ou qualquer outra aranha adulta), anda ate ele e gera um filhote. */
    static class SpiderBreedGoal extends Goal {
        private final NetherSpider spider;
        private Spider partner;

        SpiderBreedGoal(NetherSpider spider) {
            this.spider = spider;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (spider.inLove <= 0 || spider.isBaby() || spider.breedCooldown > 0 || spider.isVehicle()) {
                return false;
            }
            List<Spider> candidates = spider.level().getEntitiesOfClass(Spider.class,
                    spider.getBoundingBox().inflate(16.0D), this::isPartner);
            partner = candidates.stream().min(Comparator.comparingDouble(spider::distanceToSqr)).orElse(null);
            return partner != null;
        }

        private boolean isPartner(Spider other) {
            if (other == spider || !other.isAlive() || other.isBaby()) {
                return false;
            }
            if (other instanceof NetherSpider nether) {
                return nether.inLove > 0 && nether.breedCooldown <= 0;
            }
            return true; // aranha comum, aranha das cavernas, etc.
        }

        @Override
        public boolean canContinueToUse() {
            return partner != null && partner.isAlive() && spider.inLove > 0 && !spider.isVehicle();
        }

        @Override
        public void stop() {
            partner = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            spider.getLookControl().setLookAt(partner, 10.0F, spider.getMaxHeadXRot());
            spider.getNavigation().moveTo(partner, 1.0D);
            double reach = (spider.getBbWidth() + partner.getBbWidth()) * 0.5D + 0.8D;
            if (spider.distanceToSqr(partner) > reach * reach) {
                return;
            }
            if (!(spider.level() instanceof ServerLevel level)) {
                return;
            }
            NetherSpider baby = com.netheritesupremo.registry.ModEntities.NETHER_SPIDER.get().create(level);
            if (baby != null) {
                baby.setBaby(true);
                baby.moveTo(spider.getX(), spider.getY(), spider.getZ(), 0.0F, 0.0F);
                if (spider.getOwnerUUID() != null) {
                    baby.tameBy(spider.getOwnerUUID());
                }
                level.addFreshEntity(baby);
                level.sendParticles(ParticleTypes.HEART, spider.getX(), spider.getY(0.8D), spider.getZ(),
                        7, 0.5D, 0.5D, 0.5D, 0.02D);
                if (level.getGameRules().getBoolean(net.minecraft.world.level.GameRules.RULE_DOMOBLOOT)) {
                    level.addFreshEntity(new ExperienceOrb(level, spider.getX(), spider.getY(), spider.getZ(),
                            spider.getRandom().nextInt(7) + 1));
                }
            }
            spider.inLove = 0;
            spider.breedCooldown = BREED_COOLDOWN;
            if (partner instanceof NetherSpider other) {
                other.inLove = 0;
                other.breedCooldown = BREED_COOLDOWN;
            }
            partner = null;
        }
    }

    // ------------------------------------------------------------ bolas de fogo
    static class FireballGoal extends Goal {
        private final NetherSpider spider;
        private int cooldown = 30;

        FireballGoal(NetherSpider spider) {
            this.spider = spider;
            this.setFlags(EnumSet.noneOf(Flag.class)); // roda junto com o resto
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
            if (target == null || !target.isAlive() || spider.isBaby() || spider.isVehicle()
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
            cooldown = 40 + spider.getRandom().nextInt(30);
            Level level = spider.level();
            double sx = spider.getX();
            double sy = spider.getY(0.7D);
            double sz = spider.getZ();
            double dx = target.getX() - sx;
            double dy = target.getY(0.5D) - sy;
            double dz = target.getZ() - sz;
            float damage = MIN_DAMAGE + spider.getRandom().nextFloat() * (MAX_DAMAGE - MIN_DAMAGE);
            NetherSpiderFireball fireball = new NetherSpiderFireball(level, spider, dx, dy, dz, damage);
            fireball.setPos(sx, sy, sz);
            level.addFreshEntity(fireball);
            level.playSound(null, spider.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
        }
    }
}
