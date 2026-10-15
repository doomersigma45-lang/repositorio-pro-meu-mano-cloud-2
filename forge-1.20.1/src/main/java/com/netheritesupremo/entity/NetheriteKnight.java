package com.netheritesupremo.entity;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * Cavaleiro de netherite: 35 de vida e 1 de dano de mao. E neutro com jogadores (so revida se for
 * atacado), ataca criaturas hostis e escala paredes como uma aranha.
 * Veste o conjunto completo de netherite e empunha uma espada de netherite, mas o render nao
 * desenha a armadura (so a skin).
 *
 * So existe um por vez no mundo (spawn natural): ver {@link KnightSpawnData}.
 */
public class NetheriteKnight extends Monster {
    public static final double MAX_HEALTH = 35.0D;
    public static final double ATTACK_DAMAGE = 1.0D;

    public NetheriteKnight(EntityType<? extends Monster> type, Level level) {
        super(type, level);
        this.xpReward = 40;
    }

    public static AttributeSupplier.Builder createKnightAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    /**
     * Regra de spawn natural: em qualquer luz, so dentro da area de uma fortaleza do Nether ou de um
     * bastiao, e so se ainda nao existir um cavaleiro.
     */
    public static boolean checkKnightSpawnRules(EntityType<NetheriteKnight> type, ServerLevelAccessor level,
                                                MobSpawnType reason, BlockPos pos, RandomSource random) {
        if (reason == MobSpawnType.NATURAL) {
            MinecraftServer server = level.getServer();
            ServerLevel serverLevel = level.getLevel();
            if (server == null || !KnightSpawnData.get(server).canSpawnNaturally(serverLevel.getGameTime())) {
                return false;
            }
            if (!isInsideStructure(serverLevel, pos, BuiltinStructures.FORTRESS)
                    && !isInsideStructure(serverLevel, pos, BuiltinStructures.BASTION_REMNANT)) {
                return false;
            }
        }
        return Monster.checkAnyLightMonsterSpawnRules(type, level, reason, pos, random);
    }

    private static boolean isInsideStructure(ServerLevel level, BlockPos pos, ResourceKey<Structure> key) {
        Structure structure = level.registryAccess().registryOrThrow(Registries.STRUCTURE).get(key);
        return structure != null && level.structureManager().getStructureAt(pos, structure).isValid();
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new FollowTamerGoal(this));
        this.goalSelector.addGoal(6, new StayWithGroupGoal(this));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(0, new DefendOwnerGoal(this));
        // Se um cavaleiro for atacado (por mob ou jogador), o grupo inteiro por perto ataca o agressor
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        // Neutro com jogadores (so revida via HurtByTargetGoal); ataca criaturas hostis, menos outros cavaleiros
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Mob.class, 10, true, false,
                other -> other instanceof Enemy && !(other instanceof NetheriteKnight)));
    }

    // ------------------------------------------------------------ escalada
    @Override
    protected PathNavigation createNavigation(Level level) {
        return new WallClimberNavigation(this, level);
    }

    /** Escala qualquer bloco contra o qual esteja empurrando (como a aranha). */
    @Override
    public boolean onClimbable() {
        return this.horizontalCollision && !this.isSpectator();
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        MobSpawnType reason, @Nullable SpawnGroupData data,
                                        @Nullable CompoundTag tag) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);

        // O cavaleiro que nasce naturalmente e o unico: fica registrado no mundo e nao despawna.
        if (reason == MobSpawnType.NATURAL || reason == MobSpawnType.STRUCTURE
                || reason == MobSpawnType.CHUNK_GENERATION) {
            this.setPersistenceRequired();
            MinecraftServer server = level.getServer();
            if (server != null) {
                KnightSpawnData.get(server).register(this.getUUID(), level.getLevel().getGameTime());
            }
        }
        return result;
    }

    /** Vive em grupos de dez. */
    @Override
    public int getMaxSpawnClusterSize() {
        return KnightSpawnData.GROUP_SIZE;
    }

    // ------------------------------------------------------------ domesticacao
    private boolean tame;
    @Nullable
    private UUID owner;

    public boolean isTame() {
        return this.tame;
    }

    @Nullable
    public Player getOwner() {
        return this.owner == null ? null : this.level().getPlayerByUUID(this.owner);
    }

    public void tameBy(UUID ownerId) {
        this.tame = true;
        this.owner = ownerId;
        this.setTarget(null);
        this.setPersistenceRequired();
        MinecraftServer server = this.level().getServer();
        if (server != null) {
            KnightSpawnData.get(server).unregister(this.getUUID());
        }
    }

    private static boolean isTameItem(ItemStack stack) {
        return stack.is(Items.EMERALD_BLOCK) || stack.is(Items.IRON_BLOCK);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!this.tame && isTameItem(stack)) {
            if (this.level() instanceof ServerLevel serverLevel) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                this.tameBy(player.getUUID());
                serverLevel.sendParticles(ParticleTypes.HEART, this.getX(), this.getY(0.8D), this.getZ(),
                        7, 0.5D, 0.5D, 0.5D, 0.02D);
            }
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    /** Domesticado, nunca ataca jogadores. */
    @Override
    public boolean canAttack(LivingEntity target) {
        if (this.tame && target instanceof Player) {
            return false;
        }
        return super.canAttack(target);
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        if (this.tame && target instanceof Player) {
            super.setTarget(null);
            return;
        }
        super.setTarget(target);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Tame", this.tame);
        if (this.owner != null) {
            tag.putUUID("Owner", this.owner);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tame = tag.getBoolean("Tame");
        this.owner = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        if (this.tame) {
            this.setPersistenceRequired();
        }
    }

    // ------------------------------------------------------------ goals do grupo/dono
    /** Domesticado: defende o dono do ultimo mob que o atacou ou que ele atacou. */
    static class DefendOwnerGoal extends TargetGoal {
        private final NetheriteKnight knight;
        private LivingEntity candidate;

        DefendOwnerGoal(NetheriteKnight knight) {
            super(knight, false);
            this.knight = knight;
            this.setFlags(EnumSet.of(Flag.TARGET));
        }

        @Override
        public boolean canUse() {
            if (!knight.tame) {
                return false;
            }
            Player owner = knight.getOwner();
            if (owner == null) {
                return false;
            }
            LivingEntity found = owner.getLastHurtByMob();
            if (!valid(found, owner.getLastHurtByMobTimestamp(), owner)) {
                found = owner.getLastHurtMob();
                if (!valid(found, owner.getLastHurtMobTimestamp(), owner)) {
                    return false;
                }
            }
            candidate = found;
            return true;
        }

        private boolean valid(@Nullable LivingEntity entity, int timestamp, Player owner) {
            return entity != null && entity.isAlive() && owner.tickCount - timestamp < 100
                    && !(entity instanceof Player) && !(entity instanceof NetheriteKnight)
                    && knight.canAttack(entity);
        }

        @Override
        public void start() {
            this.mob.setTarget(candidate);
            super.start();
        }
    }

    /** Domesticado: acompanha o dono (e teletransporta se ficar muito longe). */
    static class FollowTamerGoal extends Goal {
        private final NetheriteKnight knight;
        private Player owner;
        private int recalc;

        FollowTamerGoal(NetheriteKnight knight) {
            this.knight = knight;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            if (!knight.tame || knight.getTarget() != null) {
                return false;
            }
            Player found = knight.getOwner();
            if (found == null || found.isSpectator() || knight.distanceToSqr(found) < 100.0D) {
                return false;
            }
            owner = found;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return knight.tame && knight.getTarget() == null && owner != null && owner.isAlive()
                    && knight.distanceToSqr(owner) > 16.0D;
        }

        @Override
        public void start() {
            recalc = 0;
        }

        @Override
        public void stop() {
            knight.getNavigation().stop();
            owner = null;
        }

        @Override
        public void tick() {
            knight.getLookControl().setLookAt(owner, 10.0F, knight.getMaxHeadXRot());
            if (--recalc <= 0) {
                recalc = 10;
                if (knight.distanceToSqr(owner) > 1024.0D) {
                    knight.moveTo(owner.getX(), owner.getY(), owner.getZ(), knight.getYRot(), knight.getXRot());
                    knight.getNavigation().stop();
                } else {
                    knight.getNavigation().moveTo(owner, 1.2D);
                }
            }
        }
    }

    /** Selvagem: fica perto dos outros cavaleiros do grupo. */
    static class StayWithGroupGoal extends Goal {
        private final NetheriteKnight knight;
        private NetheriteKnight mate;
        private int cooldown;

        StayWithGroupGoal(NetheriteKnight knight) {
            this.knight = knight;
            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (knight.tame || knight.getTarget() != null || --cooldown > 0) {
                return false;
            }
            cooldown = 20;
            List<NetheriteKnight> others = knight.level().getEntitiesOfClass(NetheriteKnight.class,
                    knight.getBoundingBox().inflate(32.0D), other -> other != knight && !other.tame && other.isAlive());
            mate = others.stream().min(Comparator.comparingDouble(knight::distanceToSqr)).orElse(null);
            return mate != null && knight.distanceToSqr(mate) > 100.0D;
        }

        @Override
        public boolean canContinueToUse() {
            return !knight.tame && knight.getTarget() == null && mate != null && mate.isAlive()
                    && !knight.getNavigation().isDone() && knight.distanceToSqr(mate) > 25.0D;
        }

        @Override
        public void start() {
            knight.getNavigation().moveTo(mate, 1.0D);
        }

        @Override
        public void stop() {
            mate = null;
        }
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.NETHERITE_SWORD));
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.PLAYER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.PLAYER_DEATH;
    }
}
