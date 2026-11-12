package com.netheritesupremo.mob;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.registry.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Zumbi atingido por raio vira Strongest Zombie; a cabeca dele, usada, invoca zumbis defensores. */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class ZombieHeadEvents {
    public static final String DEFENDER_TAG = "netheritesupremo_zombie_defender";
    public static final String OWNER_KEY = "NetheriteSupremoDefenderOwner";
    public static final String COOLDOWN_KEY = "NetheriteSupremoHeadCd";
    public static final int DEFENDER_LIFETIME = 20 * 60;
    public static final int HEAD_COOLDOWN = 20 * 15;

    private ZombieHeadEvents() {
    }

    @SubscribeEvent
    public static void onLightning(EntityStruckByLightningEvent event) {
        if (event.getEntity().getType() != EntityType.ZOMBIE || !(event.getEntity().level() instanceof ServerLevel level)) {
            return;
        }
        Zombie old = (Zombie) event.getEntity();
        StrongestZombie sz = ModEntities.STRONGEST_ZOMBIE.get().create(level);
        if (sz == null) {
            return;
        }
        event.setCanceled(true);
        sz.moveTo(old.getX(), old.getY(), old.getZ(), old.getYRot(), old.getXRot());
        sz.setPersistenceRequired();
        if (old.hasCustomName()) {
            sz.setCustomName(old.getCustomName());
        }
        old.discard();
        level.addFreshEntity(sz);
    }

    @SubscribeEvent
    public static void onWearerHurt(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.level().isClientSide
                || !player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.STRONGEST_ZOMBIE_HEAD.get())
                || !(event.getSource().getEntity() instanceof LivingEntity attacker) || attacker == player
                || attacker.getTags().contains(DEFENDER_TAG)) {
            return;
        }
        long now = player.level().getGameTime();
        if (now < player.getPersistentData().getLong(COOLDOWN_KEY)) {
            return;
        }
        player.getPersistentData().putLong(COOLDOWN_KEY, now + HEAD_COOLDOWN);
        summonDefenders(player, attacker, 3);
    }

    public static void summonDefenders(Player owner, LivingEntity enemy, int count) {
        if (!(owner.level() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < count; i++) {
            Zombie z = EntityType.ZOMBIE.create(level);
            if (z == null) {
                return;
            }
            z.moveTo(owner.getX() + level.random.nextInt(5) - 2, owner.getY(), owner.getZ() + level.random.nextInt(5) - 2,
                    level.random.nextFloat() * 360.0F, 0.0F);
            z.addTag(DEFENDER_TAG);
            z.getPersistentData().putString(OWNER_KEY, owner.getUUID().toString());
            z.finalizeSpawn(level, level.getCurrentDifficultyAt(z.blockPosition()), MobSpawnType.MOB_SUMMONED, null, null);
            z.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, DEFENDER_LIFETIME, 0, false, false));
            z.setTarget(enemy);
            level.addFreshEntity(z);
            level.sendParticles(ParticleTypes.SOUL, z.getX(), z.getY() + 1.0D, z.getZ(), 6, 0.3D, 0.5D, 0.3D, 0.02D);
        }
    }

    @SubscribeEvent
    public static void onDefenderTick(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Zombie z && !z.level().isClientSide && z.tickCount > DEFENDER_LIFETIME
                && z.getTags().contains(DEFENDER_TAG)) {
            z.discard();
        }
    }

    /** Defensores nunca atacam o dono. */
    @SubscribeEvent
    public static void onDefenderTarget(LivingChangeTargetEvent event) {
        LivingEntity self = event.getEntity();
        LivingEntity next = event.getNewTarget();
        if (next != null && self.getTags().contains(DEFENDER_TAG)
                && next.getUUID().toString().equals(self.getPersistentData().getString(OWNER_KEY))) {
            event.setNewTarget(null);
        }
    }
}
