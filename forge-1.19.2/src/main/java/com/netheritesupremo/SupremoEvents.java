package com.netheritesupremo;

import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SupremoEvents {
    // Valores de armadura por peca
    public static final double HELMET_ARMOR = 14.0D;
    public static final double CHESTPLATE_ARMOR = 20.0D;
    public static final double LEGGINGS_ARMOR = 17.0D;
    public static final double BOOTS_ARMOR = 13.0D;

    // Dano total (inclui o 1 de dano base da mao)
    public static final double SWORD_DAMAGE = 80.0D;
    public static final double AXE_DAMAGE = 100.0D;

    // Armadura: todo dano que nao seja queda/explosao fica com 10% (reduz 90%)
    public static final float DAMAGE_MULTIPLIER = 0.1F;

    // Espada: efeitos aplicados no alvo (duracao em ticks; amplificador 0 = nivel I)
    public static final int SWORD_EFFECT_TICKS = 200; // 10 segundos
    public static final int WEAKNESS_AMP = 1;         // Fraqueza II
    public static final int WITHER_AMP = 1;           // Wither II
    public static final int SLOWNESS_AMP = 2;         // Lentidao III

    // Picareta: velocidade de quebra enorme = quebra instantanea
    public static final float PICKAXE_SPEED = 1.0E6F;

    private static final String FLY_TAG = "netheritesupremo_fly";

    // Mesmos UUIDs que o jogo usa para armadura por slot e dano base de ataque
    private static final UUID HEAD_ID = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    private static final UUID CHEST_ID = UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E");
    private static final UUID LEGS_ID = UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D");
    private static final UUID FEET_ID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
    private static final UUID ATTACK_DAMAGE_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    // ---------------------------------------------------------------- atributos
    @SubscribeEvent
    public void onItemAttributes(ItemAttributeModifierEvent event) {
        Item item = event.getItemStack().getItem();
        EquipmentSlot slot = event.getSlotType();

        if (slot == EquipmentSlot.HEAD && item == Items.NETHERITE_HELMET) {
            setArmor(event, HEAD_ID, HELMET_ARMOR);
        } else if (slot == EquipmentSlot.CHEST && item == Items.NETHERITE_CHESTPLATE) {
            setArmor(event, CHEST_ID, CHESTPLATE_ARMOR);
        } else if (slot == EquipmentSlot.LEGS && item == Items.NETHERITE_LEGGINGS) {
            setArmor(event, LEGS_ID, LEGGINGS_ARMOR);
        } else if (slot == EquipmentSlot.FEET && item == Items.NETHERITE_BOOTS) {
            setArmor(event, FEET_ID, BOOTS_ARMOR);
        } else if (slot == EquipmentSlot.MAINHAND) {
            if (item == Items.NETHERITE_SWORD) {
                setAttackDamage(event, SWORD_DAMAGE);
            } else if (item == Items.NETHERITE_AXE) {
                setAttackDamage(event, AXE_DAMAGE);
            }
        }
    }

    private static void setArmor(ItemAttributeModifierEvent event, UUID id, double value) {
        event.removeAttribute(Attributes.ARMOR);
        event.addModifier(Attributes.ARMOR,
                new AttributeModifier(id, "Armor modifier", value, AttributeModifier.Operation.ADDITION));
    }

    private static void setAttackDamage(ItemAttributeModifierEvent event, double totalDamage) {
        event.removeAttribute(Attributes.ATTACK_DAMAGE);
        event.addModifier(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(ATTACK_DAMAGE_ID, "Weapon modifier", totalDamage - 1.0D,
                        AttributeModifier.Operation.ADDITION));
    }

    // ------------------------------------------------- tick: efeitos + inquebravel
    @SubscribeEvent
    public void onLivingTick(LivingEvent.LivingTickEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level.isClientSide) {
            return;
        }

        if (entity instanceof Player || entity.tickCount % 4 == 0) {
            restoreDurability(entity);
        }

        if (entity.tickCount % 10 == 0 && hasSetBonus(entity)) {
            entity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 40, 0, true, false, false));
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 40, 1, true, false, false)); // Forca II
        }
    }

    private static void restoreDurability(LivingEntity entity) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = entity.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamaged() && isSupremoItem(stack.getItem())) {
                stack.setDamageValue(0);
            }
        }
    }

    private static boolean isSupremoItem(Item item) {
        return item == Items.NETHERITE_HELMET
                || item == Items.NETHERITE_CHESTPLATE
                || item == Items.NETHERITE_LEGGINGS
                || item == Items.NETHERITE_BOOTS
                || item == Items.NETHERITE_SWORD
                || item == Items.NETHERITE_AXE
                || item == Items.NETHERITE_PICKAXE
                || item == Items.NETHERITE_SHOVEL;
    }

    // ------------------------------------------------------------------- voo
    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.player instanceof ServerPlayer player)) {
            return;
        }
        // Criativo e espectador ja voam por conta propria; nao mexemos neles.
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        CompoundTag data = player.getPersistentData();
        if (hasSetBonus(player)) {
            if (!player.getAbilities().mayfly) {
                player.getAbilities().mayfly = true;
                player.onUpdateAbilities();
                data.putBoolean(FLY_TAG, true);
            }
        } else if (data.getBoolean(FLY_TAG)) {
            data.remove(FLY_TAG);
            player.getAbilities().mayfly = false;
            player.getAbilities().flying = false;
            player.onUpdateAbilities();
        }
    }

    // ------------------------------------------- queda/explosao: dano zero
    @SubscribeEvent
    public void onAttacked(LivingAttackEvent event) {
        if (hasSetBonus(event.getEntity()) && isFallOrExplosion(event.getSource())) {
            event.setCanceled(true);
        }
    }

    private static boolean isFallOrExplosion(DamageSource source) {
        return source.isFall() || source.isExplosion();
    }

    // ------------------------------------- dano -90% (armadura) e efeitos (espada)
    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();

        // Quem veste o conjunto recebe so 10% de qualquer dano
        if (hasSetBonus(victim)) {
            event.setAmount(event.getAmount() * DAMAGE_MULTIPLIER);
        }

        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof LivingEntity attacker) {
            Item held = attacker.getMainHandItem().getItem();
            if (held == Items.NETHERITE_SWORD || held == Items.NETHERITE_AXE) {
                victim.setSecondsOnFire(8); // Fire Aspect II = 4s x 2
            }
            if (held == Items.NETHERITE_SWORD) {
                victim.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, SWORD_EFFECT_TICKS, WEAKNESS_AMP), attacker);
                victim.addEffect(new MobEffectInstance(MobEffects.WITHER, SWORD_EFFECT_TICKS, WITHER_AMP), attacker);
                victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, SWORD_EFFECT_TICKS, SLOWNESS_AMP), attacker);
            }
        }
    }

    // ------------------------------------------------ picareta: quebra instantanea
    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (event.getEntity().getMainHandItem().is(Items.NETHERITE_PICKAXE)) {
            event.setNewSpeed(PICKAXE_SPEED);
        }
    }

    // ----------------------------------------------------- escudo contra flechas
    @SubscribeEvent
    public void onProjectileImpact(ProjectileImpactEvent event) {
        Projectile projectile = event.getProjectile();
        if (!(projectile instanceof AbstractArrow arrow) || projectile instanceof ThrownTrident) {
            return;
        }
        HitResult hit = event.getRayTraceResult();
        if (!(hit instanceof EntityHitResult entityHit)) {
            return;
        }
        Entity target = entityHit.getEntity();
        if (!(target instanceof LivingEntity living) || arrow.getOwner() == target) {
            return;
        }
        if (!hasSetBonus(living)) {
            return;
        }
        // Mesmo comportamento do escudo: a flecha quica e nao causa dano.
        event.setCanceled(true);
        arrow.setDeltaMovement(arrow.getDeltaMovement().scale(-0.1D));
        arrow.setYRot(arrow.getYRot() + 180.0F);
        arrow.yRotO += 180.0F;
        arrow.playSound(SoundEvents.SHIELD_BLOCK, 1.0F, 0.8F + living.getRandom().nextFloat() * 0.4F);
    }

    // ---------------------------------------------------------------- conjunto
    private static int wornPieces(LivingEntity entity) {
        int count = 0;
        if (entity.getItemBySlot(EquipmentSlot.HEAD).is(Items.NETHERITE_HELMET)) count++;
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(Items.NETHERITE_CHESTPLATE)) count++;
        if (entity.getItemBySlot(EquipmentSlot.LEGS).is(Items.NETHERITE_LEGGINGS)) count++;
        if (entity.getItemBySlot(EquipmentSlot.FEET).is(Items.NETHERITE_BOOTS)) count++;
        return count;
    }

    private static boolean hasSetBonus(LivingEntity entity) {
        int worn = wornPieces(entity);
        return SupremoConfig.REQUIRE_FULL_SET.get() ? worn == 4 : worn > 0;
    }
}
