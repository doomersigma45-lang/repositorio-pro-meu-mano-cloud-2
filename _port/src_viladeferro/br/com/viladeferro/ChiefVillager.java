/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.DifficultyInstance
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.effect.MobEffectInstance
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EquipmentSlot
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.entity.SpawnGroupData
 *  net.minecraft.world.entity.ai.attributes.AttributeSupplier$Builder
 *  net.minecraft.world.entity.ai.attributes.Attributes
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.npc.Villager
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.enchantment.EnchantmentHelper
 *  net.minecraft.world.item.enchantment.Enchantments
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.ServerLevelAccessor
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefCub;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.Tier;
import br.com.viladeferro.VilaDeFerro;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

public class ChiefVillager
extends FighterVillager {
    private int summonCooldown = 100;
    private boolean hasTotem;
    private boolean totemUsed;

    public ChiefVillager(EntityType<? extends Villager> type, Level level) {
        super(type, level);
        this.m_6593_((Component)Component.m_237115_((String)"entity.viladeferro.chefe_villager"));
        this.m_20340_(true);
    }

    public static AttributeSupplier.Builder createChiefAttributes() {
        return ChiefVillager.createFighterAttributes().m_22268_(Attributes.f_22276_, 80.0).m_22268_(Attributes.f_22281_, 9.0).m_22268_(Attributes.f_22278_, 0.5);
    }

    public void m_8107_() {
        super.m_8107_();
        if (this.m_9236_().f_46443_) {
            this.spawnAura();
        }
    }

    private void spawnAura() {
        boolean enraged = this.m_21023_(MobEffects.f_19600_);
        double angle = (double)this.f_19797_ * 0.35;
        for (int i = 0; i < 2; ++i) {
            double a = angle + Math.PI * (double)i;
            double y = this.m_20186_() + 0.2 + (double)((this.f_19797_ + i * 10) % 40) / 40.0 * (double)this.m_20206_();
            double x = this.m_20185_() + Math.cos(a) * 0.85;
            double z = this.m_20189_() + Math.sin(a) * 0.85;
            this.m_9236_().m_7106_((ParticleOptions)(enraged ? ParticleTypes.f_123745_ : ParticleTypes.f_123810_), x, y, z, 0.0, 0.02, 0.0);
        }
        if (enraged && this.f_19797_ % 4 == 0) {
            this.m_9236_().m_7106_((ParticleOptions)ParticleTypes.f_123744_, this.m_20208_(0.6), this.m_20187_(), this.m_20262_(0.6), 0.0, 0.03, 0.0);
        }
    }

    public void m_8119_() {
        Level level;
        super.m_8119_();
        if (this.m_9236_().f_46443_ || !((level = this.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.tickTotem();
        this.tickSummon(serverLevel);
    }

    private void tickTotem() {
        if (this.hasTotem && !this.m_21206_().m_150930_(Items.f_42747_)) {
            this.hasTotem = false;
            this.totemUsed = true;
            this.m_7292_(new MobEffectInstance(MobEffects.f_19600_, -1, 1, false, true));
            this.m_7292_(new MobEffectInstance(MobEffects.f_19606_, -1, 0, false, true));
            this.m_7292_(new MobEffectInstance(MobEffects.f_19605_, 300, 2, false, true));
        }
    }

    private void tickSummon(ServerLevel serverLevel) {
        IronGolem golem;
        LivingEntity target = this.m_5448_();
        if (target == null || !target.m_6084_()) {
            return;
        }
        if (--this.summonCooldown > 0) {
            return;
        }
        this.summonCooldown = 180;
        long nearby = serverLevel.m_6443_(FighterVillager.class, this.m_20191_().m_82400_(24.0), mob -> mob != this).size();
        int amount = (int)Math.max(0L, Math.min(3L, 6L - nearby));
        for (int i = 0; i < amount; ++i) {
            FighterVillager ally = (FighterVillager)((EntityType)VilaDeFerro.FIGHTER.get()).m_20615_((Level)serverLevel);
            if (ally == null) continue;
            double angle = Math.PI * 2 * (double)i / (double)Math.max(1, amount) + this.f_19796_.m_188500_() * 0.4;
            ally.m_7678_(this.m_20185_() + Math.cos(angle) * 2.5, this.m_20186_(), this.m_20189_() + Math.sin(angle) * 2.5, this.f_19796_.m_188501_() * 360.0f, 0.0f);
            ally.m_6518_((ServerLevelAccessor)serverLevel, serverLevel.m_6436_(ally.m_20183_()), MobSpawnType.EVENT, null, null);
            ally.setTier(Tier.AGGRESSIVE);
            ally.m_6710_(target);
            serverLevel.m_7967_((Entity)ally);
        }
        long golems = serverLevel.m_6443_(IronGolem.class, this.m_20191_().m_82400_(24.0), g -> Tier.of((Entity)g) == Tier.AGGRESSIVE).size();
        if (golems < 2L && (golem = (IronGolem)EntityType.f_20460_.m_20615_((Level)serverLevel)) != null) {
            double angle = this.f_19796_.m_188500_() * Math.PI * 2.0;
            golem.m_7678_(this.m_20185_() + Math.cos(angle) * 3.0, this.m_20186_(), this.m_20189_() + Math.sin(angle) * 3.0, this.f_19796_.m_188501_() * 360.0f, 0.0f);
            Tier.set(golem, Tier.AGGRESSIVE);
            golem.m_6710_(target);
            if (target instanceof Player) {
                golem.m_6925_(target.m_20148_());
                golem.m_6825_();
            }
            serverLevel.m_7967_((Entity)golem);
        }
    }

    @Override
    public SpawnGroupData m_6518_(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        SpawnGroupData result = super.m_6518_(level, difficulty, reason, data, tag);
        this.m_8061_(EquipmentSlot.MAINHAND, new ItemStack((ItemLike)Items.f_42391_));
        this.m_8061_(EquipmentSlot.OFFHAND, new ItemStack((ItemLike)Items.f_42747_));
        this.m_8061_(EquipmentSlot.HEAD, new ItemStack((ItemLike)Items.f_42472_));
        this.m_8061_(EquipmentSlot.CHEST, new ItemStack((ItemLike)Items.f_42473_));
        this.m_8061_(EquipmentSlot.LEGS, new ItemStack((ItemLike)Items.f_42474_));
        this.m_8061_(EquipmentSlot.FEET, new ItemStack((ItemLike)Items.f_42475_));
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.m_21409_(slot, 0.0f);
        }
        this.hasTotem = true;
        return result;
    }

    public void m_6667_(DamageSource source) {
        Level level;
        boolean alreadyDead = this.f_20890_ || this.m_213877_();
        super.m_6667_(source);
        if (alreadyDead || !((level = this.m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        this.dropGoodLoot();
        ServerPlayer killer = null;
        Entity entity = source.m_7639_();
        if (entity instanceof ServerPlayer) {
            ServerPlayer p;
            killer = p = (ServerPlayer)entity;
        } else {
            entity = this.f_20888_;
            if (entity instanceof ServerPlayer) {
                ServerPlayer p2 = (ServerPlayer)entity;
                if (this.f_20889_ > 0) {
                    killer = p2;
                }
            }
        }
        if (killer == null) {
            return;
        }
        Advancement advancement = serverLevel.m_7654_().m_129889_().m_136041_(new ResourceLocation("viladeferro", "derrotar_chefe"));
        if (advancement != null) {
            killer.m_8960_().m_135988_(advancement, "derrotado");
        }
        ItemStack chest = new ItemStack((ItemLike)Items.f_42473_);
        chest.m_41663_(Enchantments.f_44965_, 4);
        chest.m_41663_(Enchantments.f_44986_, 3);
        chest.m_41663_(Enchantments.f_44962_, 1);
        this.m_19983_(chest);
        ChiefCub cub = (ChiefCub)((EntityType)VilaDeFerro.CHIEF_CUB.get()).m_20615_((Level)serverLevel);
        if (cub != null) {
            cub.m_7678_(this.m_20185_(), this.m_20186_(), this.m_20189_(), this.f_19796_.m_188501_() * 360.0f, 0.0f);
            cub.m_21828_((Player)killer);
            cub.becomeBaby();
            serverLevel.m_7967_((Entity)cub);
        }
    }

    private void dropGoodLoot() {
        this.m_19983_(new ItemStack((ItemLike)Items.f_42436_, 1 + this.f_19796_.m_188503_(3)));
        if (this.f_19796_.m_188503_(100) < 15) {
            this.m_19983_(new ItemStack((ItemLike)Items.f_42437_));
        }
        this.m_19983_(new ItemStack((ItemLike)Items.f_41959_, 1 + this.f_19796_.m_188503_(2)));
        int books = 1 + this.f_19796_.m_188503_(2);
        for (int i = 0; i < books; ++i) {
            this.m_19983_(EnchantmentHelper.m_220292_((RandomSource)this.f_19796_, (ItemStack)new ItemStack((ItemLike)Items.f_42517_), (int)(20 + this.f_19796_.m_188503_(11)), (boolean)true));
        }
    }

    @Override
    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("HasTotem", this.hasTotem);
        tag.m_128379_("TotemUsed", this.totemUsed);
    }

    @Override
    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        this.hasTotem = tag.m_128471_("HasTotem");
        this.totemUsed = tag.m_128471_("TotemUsed");
    }
}

