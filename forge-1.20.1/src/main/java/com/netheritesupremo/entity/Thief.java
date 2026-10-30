package com.netheritesupremo.entity;

import com.netheritesupremo.registry.ModEntities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

/**
 * Ladrao: ataca em bando, so aparece a noite, 15 de vida, mais rapido que o jogador, espada de madeira ou
 * pedra. Ao acertar, pode roubar um item (de jogador, de mob com inventario ou do equipamento de um mob) e foge;
 * o que roubou cai quando ele morre.
 */
public class Thief extends Monster {
    private static final int MAX_STOLEN = 9;
    private static final float STEAL_CHANCE = 0.5F;
    private static final int FLEE_TICKS = 200;

    private final List<ItemStack> stolen = new ArrayList<>();
    private int fleeTicks;
    @Nullable
    private Vec3 fleeFrom;

    public Thief(EntityType<? extends Thief> type, Level level) {
        super(type, level);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder createThiefAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 15.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.32D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    /** So a noite, ao ar livre (sem exigir pouca luz como os outros monstros). */
    public static boolean checkSpawnRules(EntityType<? extends Thief> type, ServerLevelAccessor level,
            MobSpawnType reason, BlockPos pos, RandomSource random) {
        return level.getDifficulty() != Difficulty.PEACEFUL && !level.getLevel().isDay() && level.canSeeSky(pos)
                && Mob.checkMobSpawnRules(type, level, reason, pos, random);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance difficulty) {
        this.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(random.nextBoolean() ? Items.WOODEN_SWORD : Items.STONE_SWORD));
        this.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
            MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        data = super.finalizeSpawn(level, difficulty, reason, data, tag);
        this.populateDefaultEquipmentSlots(level.getRandom(), difficulty);
        return data;
    }

    // --------------------------------------------------------------------- roubo
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && !this.level().isClientSide && target instanceof LivingEntity victim && !(victim instanceof Thief)
                && this.stolen.size() < MAX_STOLEN && this.random.nextFloat() < STEAL_CHANCE) {
            ItemStack loot = this.steal(victim);
            if (!loot.isEmpty()) {
                this.stolen.add(loot);
                this.setPersistenceRequired();
                this.fleeTicks = FLEE_TICKS;
                this.fleeFrom = victim.position();
                this.setTarget(null);
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_PICKUP, SoundSource.HOSTILE, 1.0F, 0.6F);
                if (this.level() instanceof ServerLevel server) {
                    server.sendParticles(ParticleTypes.SMOKE, this.getX(), this.getY(1.0D), this.getZ(), 10, 0.3D, 0.5D, 0.3D, 0.02D);
                }
            }
        }
        return hit;
    }

    private ItemStack steal(LivingEntity victim) {
        if (victim instanceof Player player) {
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < player.getInventory().items.size(); i++) {
                if (!player.getInventory().items.get(i).isEmpty()) {
                    slots.add(i);
                }
            }
            if (slots.isEmpty()) {
                return ItemStack.EMPTY;
            }
            ItemStack stack = player.getInventory().items.get(slots.get(this.random.nextInt(slots.size())));
            return stack.split(Math.max(1, (stack.getCount() + 1) / 2)); // metade da pilha
        }
        if (victim instanceof InventoryCarrier carrier) {
            SimpleContainer inv = carrier.getInventory();
            List<Integer> slots = new ArrayList<>();
            for (int i = 0; i < inv.getContainerSize(); i++) {
                if (!inv.getItem(i).isEmpty()) {
                    slots.add(i);
                }
            }
            if (!slots.isEmpty()) {
                int slot = slots.get(this.random.nextInt(slots.size()));
                return inv.removeItem(slot, Math.max(1, (inv.getItem(slot).getCount() + 1) / 2));
            }
        }
        // Equipamento do mob (arma na mao, armadura...)
        List<EquipmentSlot> order = new ArrayList<>(List.of(EquipmentSlot.values()));
        Collections.shuffle(order, new java.util.Random(this.random.nextLong()));
        for (EquipmentSlot slot : order) {
            ItemStack worn = victim.getItemBySlot(slot);
            if (!worn.isEmpty()) {
                ItemStack taken = worn.copy();
                victim.setItemSlot(slot, ItemStack.EMPTY);
                return taken;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);
        for (ItemStack stack : this.stolen) {
            this.spawnAtLocation(stack);
        }
        this.stolen.clear();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        ListTag list = new ListTag();
        for (ItemStack stack : this.stolen) {
            list.add(stack.save(new CompoundTag()));
        }
        tag.put("Stolen", list);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.stolen.clear();
        for (Tag t : tag.getList("Stolen", Tag.TAG_COMPOUND)) {
            ItemStack stack = ItemStack.of((CompoundTag) t);
            if (!stack.isEmpty()) {
                this.stolen.add(stack);
            }
        }
    }

    /** De dia, sem ter roubado nada, o ladrao some (ele so aparece a noite). */
    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide && this.level().isDay() && this.stolen.isEmpty() && this.getTarget() == null
                && this.tickCount % 40 == 0 && this.random.nextInt(8) == 0 && this.level().canSeeSky(this.blockPosition())) {
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.POOF, this.getX(), this.getY(1.0D), this.getZ(), 10, 0.3D, 0.5D, 0.3D, 0.02D);
            }
            this.discard();
        }
    }

    // ------------------------------------------------------------------ objetivos
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FleeWithLootGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.1D, false));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.9D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this, Thief.class).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Villager.class, true));
    }

    /** Depois de roubar, corre para longe da vitima por ~10 segundos. */
    static class FleeWithLootGoal extends Goal {
        private final Thief thief;

        FleeWithLootGoal(Thief thief) {
            this.thief = thief;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return this.thief.fleeTicks > 0 && this.thief.fleeFrom != null && !this.thief.stolen.isEmpty();
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void tick() {
            this.thief.fleeTicks--;
            if (this.thief.getNavigation().isDone() && this.thief.fleeFrom != null) {
                Vec3 away = LandRandomPos.getPosAway(this.thief, 16, 7, this.thief.fleeFrom);
                if (away != null) {
                    this.thief.getNavigation().moveTo(away.x, away.y, away.z, 1.35D);
                }
            }
        }
    }
}
