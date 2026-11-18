package com.netheritesupremo.weapon;

import com.netheritesupremo.registry.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Uso das armas novas (Uzi, AK, RPG) por mobs: reconhecimento, sorteio e disparo com municao infinita. */
public final class MobGuns {
    private static final String BURST_TAG = "GunBurst";

    private MobGuns() {
    }

    public static boolean isNewGun(ItemStack s) {
        Item i = s.getItem();
        return i instanceof AutoGunItem || i instanceof RpgItem;
    }

    /** Qualquer arma de fogo do mod (as novas e a Old Gun). */
    public static boolean isAnyGun(ItemStack s) {
        return isNewGun(s) || s.is(ModItems.OLD_GUN.get());
    }

    /** Uzi 40%, AK 40%, RPG 20%. */
    public static ItemStack randomNewGun(RandomSource r) {
        int n = r.nextInt(10);
        return new ItemStack(n < 4 ? ModItems.UZI.get() : (n < 8 ? ModItems.AK.get() : ModItems.RPG.get()));
    }

    /** Old Gun 25%, Uzi 30%, AK 30%, RPG 15% (parasita jogador). */
    public static ItemStack randomGun(RandomSource r) {
        int n = r.nextInt(20);
        if (n < 5) {
            return new ItemStack(ModItems.OLD_GUN.get());
        }
        return new ItemStack(n < 11 ? ModItems.UZI.get() : (n < 17 ? ModItems.AK.get() : ModItems.RPG.get()));
    }

    private static void aim(Projectile p, Mob mob, LivingEntity t, float velocity, float inaccuracy) {
        double dx = t.getX() - mob.getX();
        double dz = t.getZ() - mob.getZ();
        double dy = t.getY(0.5D) - p.getY();
        double horizontal = Math.sqrt(dx * dx + dz * dz);
        p.shoot(dx, dy + horizontal * 0.02D, dz, velocity, inaccuracy);
    }

    /**
     * Da um tiro com a arma {@code gun} no alvo e devolve quantos ticks esperar para o proximo: Uzi e AK atiram em rajadas
     * (6 e 4 tiros) com pausa, o RPG 1 foguete a cada 3,5 a 5 s. Os mobs sao menos precisos que o jogador.
     */
    public static int fire(Mob mob, LivingEntity target, ItemStack gun) {
        Level level = mob.level();
        mob.swing(InteractionHand.MAIN_HAND);
        if (gun.getItem() instanceof RpgItem) {
            RocketProjectile rocket = new RocketProjectile(level, mob);
            aim(rocket, mob, target, 1.8F, 3.0F);
            level.addFreshEntity(rocket);
            level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 1.5F, 0.6F);
            return 70 + mob.getRandom().nextInt(30);
        }
        AutoGunItem g = (AutoGunItem) gun.getItem();
        GunBullet bullet = new GunBullet(level, mob);
        bullet.setDamage(g.getDamage());
        aim(bullet, mob, target, g.getVelocity(), 9.0F);
        level.addFreshEntity(bullet);
        level.playSound(null, mob.getX(), mob.getY(), mob.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.HOSTILE, 0.6F, g.getMobPitch());
        CompoundTag data = mob.getPersistentData();
        int burst = data.getInt(BURST_TAG);
        if (burst <= 0) {
            burst = g.getMobBurst();
        }
        burst--;
        data.putInt(BURST_TAG, burst);
        return burst > 0 ? g.getInterval() : 30 + mob.getRandom().nextInt(20);
    }
}
