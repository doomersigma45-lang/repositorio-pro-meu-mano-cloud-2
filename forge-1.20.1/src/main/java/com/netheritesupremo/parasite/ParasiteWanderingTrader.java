package com.netheritesupremo.parasite;

import com.netheritesupremo.registry.ModEntities;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

/**
 * Vendedor Ambulante Parasita (mini chefe): o Aldeao Parasita com a roupa do vendedor ambulante e duas espadas de
 * diamante encantadas, uma em cada mao (cada golpe acerta duas vezes). Invoca Aldeoes Parasitas e, com metade da
 * vida, fica mais forte e mais rapido.
 */
public class ParasiteWanderingTrader extends ParasiteVillager {
    private static final int MAX_MINIONS = 4;

    private final ServerBossEvent bossEvent = new ServerBossEvent(this.getDisplayName(),
            BossEvent.BossBarColor.YELLOW, BossEvent.BossBarOverlay.NOTCHED_6);
    private boolean enraged;
    private int summonCooldown = 200;

    public ParasiteWanderingTrader(EntityType<? extends ZombieVillager> type, Level level) {
        super(type, level);
        this.xpReward = 120;
        this.setPersistenceRequired();
        ItemStack main = new ItemStack(Items.DIAMOND_SWORD);
        main.enchant(Enchantments.SHARPNESS, 5);
        main.enchant(Enchantments.SWEEPING_EDGE, 3);
        main.enchant(Enchantments.UNBREAKING, 3);
        ItemStack off = new ItemStack(Items.DIAMOND_SWORD);
        off.enchant(Enchantments.SHARPNESS, 5);
        off.enchant(Enchantments.KNOCKBACK, 2);
        off.enchant(Enchantments.UNBREAKING, 3);
        this.setItemSlot(EquipmentSlot.MAINHAND, main);
        this.setItemSlot(EquipmentSlot.OFFHAND, off);
        this.setDropChance(EquipmentSlot.MAINHAND, 0.3F);
        this.setDropChance(EquipmentSlot.OFFHAND, 0.3F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Zombie.createAttributes()
                .add(Attributes.MAX_HEALTH, 500.0D)
                .add(Attributes.ATTACK_DAMAGE, 12.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.6D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE, 0.0D);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    /** Segundo golpe com a espada da outra mao. */
    @Override
    public boolean doHurtTarget(Entity target) {
        boolean hit = super.doHurtTarget(target);
        if (hit && target instanceof LivingEntity living && living.isAlive()) {
            this.swing(InteractionHand.OFF_HAND);
            living.invulnerableTime = 0;
            living.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.8F + 3.0F);
        }
        return hit;
    }

    private void enrage() {
        this.enraged = true;
        AttributeInstance dmg = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null) {
            dmg.setBaseValue(dmg.getBaseValue() * 1.4D);
        }
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(0.40D);
        }
        this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        this.playSound(SoundEvents.RAVAGER_ROAR, 2.0F, 0.6F);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.level().isClientSide || !(this.level() instanceof ServerLevel level)) {
            return;
        }
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        if (!this.enraged && this.getHealth() <= this.getMaxHealth() / 2.0F) {
            this.enrage();
        }
        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive() || --this.summonCooldown > 0) {
            return;
        }
        this.summonCooldown = this.enraged ? 240 : 400;
        int alive = level.getEntitiesOfClass(ParasiteVillager.class, this.getBoundingBox().inflate(40.0D),
                e -> e.getClass() == ParasiteVillager.class).size();
        for (int i = 0; i < 2 && alive < MAX_MINIONS + (this.enraged ? 2 : 0); i++, alive++) {
            ParasiteVillager v = ModEntities.PARASITE_VILLAGER.get().create(level);
            if (v == null) {
                return;
            }
            v.moveTo(this.getX() + this.random.nextInt(7) - 3, this.getY(), this.getZ() + this.random.nextInt(7) - 3,
                    this.random.nextFloat() * 360.0F, 0.0F);
            v.setTarget(target);
            ParasiteBehavior.markInfected(v);
            level.addFreshEntity(v);
            level.playSound(null, v.getX(), v.getY(), v.getZ(), SoundEvents.SLIME_SQUISH, SoundSource.HOSTILE, 1.2F, 0.6F);
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Enraged", this.enraged);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.getBoolean("Enraged")) {
            this.enraged = true;
            this.bossEvent.setColor(BossEvent.BossBarColor.RED);
        }
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
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
