package com.netheritesupremo.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.netheritesupremo.entity.ShurikenEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Shuriken: 6,5 de dano na mao; botao direito arremessa (1x a 3x esse dano). */
public class ShurikenItem extends Item {
    public static final float MELEE_DAMAGE = 6.5F;

    public ShurikenItem(Properties properties) {
        super(properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getDefaultAttributeModifiers(slot);
        }
        return ImmutableMultimap.of(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        MELEE_DAMAGE - 1.0F, AttributeModifier.Operation.ADDITION),
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        -1.0D, AttributeModifier.Operation.ADDITION));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SNOWBALL_THROW,
                SoundSource.PLAYERS, 0.6F, 1.8F);
        if (!level.isClientSide) {
            ShurikenEntity shuriken = new ShurikenEntity(level, player);
            shuriken.setItem(stack);
            shuriken.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.2F, 0.6F);
            level.addFreshEntity(shuriken);
        }
        player.awardStat(net.minecraft.stats.Stats.ITEM_USED.get(this));
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        player.getCooldowns().addCooldown(this, 6);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }
}
