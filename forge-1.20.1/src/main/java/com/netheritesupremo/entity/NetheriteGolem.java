package com.netheritesupremo.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

/**
 * Golem de netherite. Estende o golem de ferro, entao herda o comportamento neutro: so ataca o
 * jogador se for ferido por ele, protege aldeoes e ataca monstros hostis.
 */
public class NetheriteGolem extends IronGolem {
    // Atributos (o golem de ferro tem 100 de vida e 15 de dano)
    public static final double MAX_HEALTH = 1800.0D;
    public static final double ATTACK_DAMAGE = 500.0D;

    // Cura do proprio golem (itens usados com o botao direito)
    public static final float GOLD_INGOT_HEAL = 150.0F;
    public static final float NETHERITE_INGOT_HEAL = 450.0F;

    // Aura de fogo
    public static final double AURA_RADIUS = 4.0D;
    public static final int AURA_FIRE_SECONDS = 4;

    // Bolas de fogo
    public static final int FIREBALLS_PER_VOLLEY = 5;
    public static final double FIREBALL_SPREAD = 1.2D;
    public static final double FIREBALL_MIN_DISTANCE = 6.0D;
    public static final double FIREBALL_MAX_DISTANCE = 24.0D;

    public NetheriteGolem(EntityType<? extends IronGolem> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createGolemAttributes() {
        return IronGolem.createAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new FireballVolleyGoal(this));
        this.goalSelector.addGoal(3, new HealIronGolemsGoal(this));
    }

    // ------------------------------------------------------------ aura de fogo
    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide) {
            for (int i = 0; i < 2; i++) {
                double x = this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.4D;
                double y = this.getY() + this.random.nextDouble() * this.getBbHeight();
                double z = this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth() * 1.4D;
                this.level().addParticle(ParticleTypes.FLAME, x, y, z, 0.0D, 0.03D, 0.0D);
            }
            if (this.random.nextInt(6) == 0) {
                this.level().addParticle(ParticleTypes.LAVA,
                        this.getX() + (this.random.nextDouble() - 0.5D) * 2.0D,
                        this.getY() + this.random.nextDouble() * this.getBbHeight(),
                        this.getZ() + (this.random.nextDouble() - 0.5D) * 2.0D, 0.0D, 0.0D, 0.0D);
            }
        } else if (this.tickCount % 10 == 0) {
            AABB area = this.getBoundingBox().inflate(AURA_RADIUS, 1.0D, AURA_RADIUS);
            for (LivingEntity entity : this.level().getEntitiesOfClass(LivingEntity.class, area, this::shouldBurn)) {
                entity.setSecondsOnFire(AURA_FIRE_SECONDS);
            }
        }
    }

    /** A aura so queima monstros hostis e quem o golem esta atacando. Nunca jogadores neutros nem aldeoes. */
    private boolean shouldBurn(LivingEntity entity) {
        return entity != this
                && !(entity instanceof IronGolem)
                && !entity.fireImmune()
                && (entity instanceof Enemy || entity == this.getTarget());
    }

    // ----------------------------------------------- cura com barra de ouro/netherite
    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        float heal;
        if (stack.is(Items.GOLD_INGOT)) {
            heal = GOLD_INGOT_HEAL;
        } else if (stack.is(Items.NETHERITE_INGOT)) {
            heal = NETHERITE_INGOT_HEAL;
        } else {
            return InteractionResult.PASS;
        }
        if (this.getHealth() >= this.getMaxHealth()) {
            return InteractionResult.PASS;
        }
        this.heal(heal);
        this.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 0.7F + this.random.nextFloat() * 0.2F);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        return InteractionResult.sidedSuccess(this.level().isClientSide);
    }

    // ---------------------------------------------------------- bolas de fogo
    static class FireballVolleyGoal extends Goal {
        private final NetheriteGolem golem;
        private int cooldown = 30;

        FireballVolleyGoal(NetheriteGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.noneOf(Flag.class)); // roda junto com o ataque corpo a corpo
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
            LivingEntity target = golem.getTarget();
            if (target == null || !target.isAlive() || !golem.hasLineOfSight(target)) {
                return false;
            }
            double distSqr = golem.distanceToSqr(target);
            return distSqr >= FIREBALL_MIN_DISTANCE * FIREBALL_MIN_DISTANCE
                    && distSqr <= FIREBALL_MAX_DISTANCE * FIREBALL_MAX_DISTANCE;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = golem.getTarget();
            if (target == null) {
                return;
            }
            golem.getLookControl().setLookAt(target, 30.0F, 30.0F);
            if (--cooldown > 0) {
                return;
            }
            cooldown = 50 + golem.getRandom().nextInt(30);

            Level level = golem.level();
            double sx = golem.getX();
            double sy = golem.getY(0.65D);
            double sz = golem.getZ();
            for (int i = 0; i < FIREBALLS_PER_VOLLEY; i++) {
                double dx = target.getX() - sx + golem.getRandom().nextGaussian() * FIREBALL_SPREAD;
                double dy = target.getY(0.5D) - sy + golem.getRandom().nextGaussian() * FIREBALL_SPREAD * 0.5D;
                double dz = target.getZ() - sz + golem.getRandom().nextGaussian() * FIREBALL_SPREAD;
                GolemFireball fireball = new GolemFireball(level, golem, dx, dy, dz);
                fireball.setPos(sx, sy, sz);
                level.addFreshEntity(fireball);
            }
            level.playSound(null, golem.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 2.0F, 0.6F);
        }
    }

    // ----------------------------------------------------- cura de golens de ferro
    static class HealIronGolemsGoal extends Goal {
        private static final double SEARCH_RADIUS = 16.0D;

        private final NetheriteGolem golem;
        private IronGolem patient;
        private int searchCooldown;
        private int healTimer;

        HealIronGolemsGoal(NetheriteGolem golem) {
            this.golem = golem;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (golem.getTarget() != null || --searchCooldown > 0) {
                return false;
            }
            searchCooldown = 20;
            AABB area = golem.getBoundingBox().inflate(SEARCH_RADIUS);
            List<IronGolem> wounded = golem.level().getEntitiesOfClass(IronGolem.class, area,
                    other -> other != golem && other.isAlive() && other.getHealth() < other.getMaxHealth());
            patient = wounded.stream().min(Comparator.comparingDouble(golem::distanceToSqr)).orElse(null);
            return patient != null;
        }

        @Override
        public boolean canContinueToUse() {
            return patient != null && patient.isAlive() && patient.getHealth() < patient.getMaxHealth()
                    && golem.getTarget() == null && golem.distanceToSqr(patient) < 24.0D * 24.0D;
        }

        @Override
        public void start() {
            healTimer = 0;
        }

        @Override
        public void stop() {
            golem.getNavigation().stop();
            patient = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            golem.getLookControl().setLookAt(patient, 30.0F, 30.0F);
            if (golem.distanceToSqr(patient) > 9.0D) {
                golem.getNavigation().moveTo(patient, 1.0D);
                return;
            }
            golem.getNavigation().stop();
            if (++healTimer >= 20) {
                healTimer = 0;
                patient.heal(Math.max(25.0F, patient.getMaxHealth() * 0.25F));
                golem.playSound(SoundEvents.IRON_GOLEM_REPAIR, 1.0F, 1.0F);
                if (golem.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.HEART, patient.getX(), patient.getY(0.8D),
                            patient.getZ(), 5, 0.4D, 0.4D, 0.4D, 0.0D);
                }
            }
        }
    }
}
