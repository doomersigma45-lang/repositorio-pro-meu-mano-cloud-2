package com.netheritesupremo.item;

import java.util.List;
import javax.annotation.Nullable;
import com.netheritesupremo.entity.ShurikenEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/** Rubi: botao direito solta um leque de projeteis de rubi (nao gasta o item). */
public class RubyItem extends Item {
    public static final int PROJECTILES = 9;
    public static final float DAMAGE = 12.0F;

    public RubyItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.0F, 1.4F);
        if (!level.isClientSide) {
            for (int i = 0; i < PROJECTILES; i++) {
                float yawOffset = (i - (PROJECTILES - 1) / 2.0F) * 8.0F; // leque de -32 a +32 graus
                ShurikenEntity shot = new ShurikenEntity(level, player);
                shot.setItem(new ItemStack(this));
                shot.setFixedDamage(DAMAGE);
                shot.setNoDrop(true);
                shot.shootFromRotation(player, player.getXRot(), player.getYRot() + yawOffset, 0.0F, 2.4F, 0.4F);
                level.addFreshEntity(shot);
            }
        }
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheritesupremo.ruby.desc1").withStyle(ChatFormatting.RED));
    }
}
