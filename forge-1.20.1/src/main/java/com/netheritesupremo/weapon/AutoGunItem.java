package com.netheritesupremo.weapon;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

/**
 * Arma automatica (Uzi e AK): segure o botao direito para atirar. Cada Pepita de Ferro vira varios tiros; no criativo a
 * municao e infinita.
 */
public class AutoGunItem extends Item {
    private final int interval;
    private final float damage;
    private final float inaccuracy;
    private final float velocity;
    private final int shotsPerAmmo;
    private final float pitch;
    private final String tooltip;

    public AutoGunItem(Properties properties, int interval, float damage, float inaccuracy, float velocity, int shotsPerAmmo, float pitch, String tooltip) {
        super(properties);
        this.interval = interval;
        this.damage = damage;
        this.inaccuracy = inaccuracy;
        this.velocity = velocity;
        this.shotsPerAmmo = shotsPerAmmo;
        this.pitch = pitch;
        this.tooltip = tooltip;
    }

    public int getInterval() {
        return this.interval;
    }

    public float getDamage() {
        return this.damage;
    }

    public float getVelocity() {
        return this.velocity;
    }

    public float getMobPitch() {
        return this.pitch;
    }

    /** Tiros por rajada quando usada por um mob (Uzi 6, AK 4). */
    public int getMobBurst() {
        return this.interval <= 2 ? 6 : 4;
    }

    private static boolean hasAmmoItem(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        for (ItemStack s : player.getInventory().items) {
            if (s.is(Items.IRON_NUGGET)) {
                return true;
            }
        }
        return player.getOffhandItem().is(Items.IRON_NUGGET);
    }

    private static void takeAmmoItem(Player player) {
        if (player.getAbilities().instabuild) {
            return;
        }
        for (ItemStack s : player.getInventory().items) {
            if (s.is(Items.IRON_NUGGET)) {
                s.shrink(1);
                return;
            }
        }
        if (player.getOffhandItem().is(Items.IRON_NUGGET)) {
            player.getOffhandItem().shrink(1);
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getOrCreateTag().getInt("Rounds") <= 0 && !hasAmmoItem(player)) {
            if (!level.isClientSide) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.6F);
                player.displayClientMessage(Component.translatable("message.netheritesupremo.no_ammo_nugget"), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int remaining) {
        if (level.isClientSide || !(user instanceof Player player)) {
            return;
        }
        int used = this.getUseDuration(stack) - remaining;
        if (used % this.interval != 0) {
            return;
        }
        int rounds = stack.getOrCreateTag().getInt("Rounds");
        if (rounds <= 0) {
            if (!hasAmmoItem(player)) {
                level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 0.8F, 1.6F);
                player.stopUsingItem();
                return;
            }
            takeAmmoItem(player);
            rounds = this.shotsPerAmmo;
        }
        stack.getOrCreateTag().putInt("Rounds", rounds - 1);
        GunBullet bullet = new GunBullet(level, player);
        bullet.setDamage(this.damage);
        bullet.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, this.velocity, this.inaccuracy);
        level.addFreshEntity(bullet);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 0.6F, this.pitch);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.tooltip));
        tooltip.add(Component.translatable("tooltip.netheritesupremo.ammo_nugget"));
    }
}
