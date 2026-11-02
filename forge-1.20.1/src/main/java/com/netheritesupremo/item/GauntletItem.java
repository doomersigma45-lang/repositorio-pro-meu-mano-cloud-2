package com.netheritesupremo.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.netheritesupremo.entity.ShurikenEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

/**
 * Manopla Suprema: 5000 de dano. Segurando junta os poderes dos 4 itens x10 (ver GauntletEvents) e, depois de
 * {@link #INFINITE_KILLS} abates de mobs/jogadores, o dano vira infinito.
 */
public class GauntletItem extends Item {
    public static final float DAMAGE = 5000.0F;
    public static final int INFINITE_KILLS = 100; // facil de mudar depois
    public static final String KILLS = "netheritesupremo_kills";
    public static final int RUBY_X10_PROJECTILES = RubyItem.PROJECTILES * 10;
    public static final float RUBY_X10_DAMAGE = RubyItem.DAMAGE * 10.0F;

    public GauntletItem(Properties properties) {
        super(properties);
    }

    public static int kills(ItemStack stack) {
        return stack.hasTag() ? stack.getTag().getInt(KILLS) : 0;
    }

    public static boolean isInfinite(ItemStack stack) {
        return stack.getItem() instanceof GauntletItem && kills(stack) >= INFINITE_KILLS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND) {
            return super.getDefaultAttributeModifiers(slot);
        }
        return ImmutableMultimap.of(
                Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier",
                        DAMAGE - 1.0F, AttributeModifier.Operation.ADDITION),
                Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                        -2.0D, AttributeModifier.Operation.ADDITION));
    }

    public static final String EXPLOSIVE = "netheritesupremo_explosive";
    private static final String EXPLOSION_CD = "netheritesupremo_explosion_cd";
    public static final float PROJECTILE_EXPLOSION_POWER = 4.0F; // x90 projeteis: a explosao da joia (75) x90 travaria o jogo

    public static boolean isExplosive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(EXPLOSIVE);
    }

    /**
     * Botao direito: rajada de rubis x10 (explosivos se o modo estiver ligado).
     * Agachado: explosao da joia onde voce olha. Agachado olhando pra baixo: liga/desliga projeteis explosivos.
     * Portais: tecla R (ver PortalKey).
     */
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            if (sp.isShiftKeyDown() && sp.getXRot() > 60.0F) {
                boolean on = !isExplosive(stack);
                stack.getOrCreateTag().putBoolean(EXPLOSIVE, on);
                sp.displayClientMessage(Component.translatable(on ? "message.netheritesupremo.gauntlet.explosive_on"
                        : "message.netheritesupremo.gauntlet.explosive_off"), true);
            } else if (sp.isShiftKeyDown()) {
                long now = level.getGameTime();
                if (stack.getOrCreateTag().getLong(EXPLOSION_CD) > now) {
                    sp.displayClientMessage(Component.translatable("message.netheritesupremo.gauntlet.cooldown"), true);
                } else {
                    stack.getOrCreateTag().putLong(EXPLOSION_CD, now + RainbowGemItem.COOLDOWN_TICKS);
                    sp.addEffect(new net.minecraft.world.effect.MobEffectInstance(
                            net.minecraft.world.effect.MobEffects.DAMAGE_RESISTANCE, 100, 4, true, false, true));
                    net.minecraft.world.phys.Vec3 at = sp.pick(RainbowGemItem.RANGE, 1.0F, false).getLocation();
                    level.explode(sp, at.x, at.y, at.z,
                            com.netheritesupremo.SupremoConfig.RAINBOW_EXPLOSION_POWER.get().floatValue(),
                            com.netheritesupremo.SupremoConfig.RAINBOW_DESTROYS_BLOCKS.get()
                                    ? Level.ExplosionInteraction.TNT : Level.ExplosionInteraction.NONE);
                    level.playSound(null, sp.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 2.0F, 1.5F);
                }
            } else {
                boolean explosive = isExplosive(stack);
                level.playSound(null, sp.getX(), sp.getY(), sp.getZ(), SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.PLAYERS, 1.5F, 0.8F);
                for (int row = 0; row < 3; row++) {
                    for (int i = 0; i < RUBY_X10_PROJECTILES / 3; i++) {
                        float yaw = (i - (RUBY_X10_PROJECTILES / 3 - 1) / 2.0F) * 3.0F;
                        ShurikenEntity shot = new ShurikenEntity(level, sp);
                        shot.setItem(new ItemStack(com.netheritesupremo.registry.ModItems.RUBY.get()));
                        shot.setFixedDamage(RUBY_X10_DAMAGE);
                        shot.setNoDrop(true);
                        if (explosive) {
                            shot.setExplosionPower(PROJECTILE_EXPLOSION_POWER);
                        }
                        shot.shootFromRotation(sp, sp.getXRot() + (row - 1) * 6.0F, sp.getYRot() + yaw, 0.0F, 2.6F, 0.4F);
                        level.addFreshEntity(shot);
                    }
                }
                player.getCooldowns().addCooldown(this, 20);
            }
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.desc1").withStyle(ChatFormatting.GOLD));
        tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.desc2").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.desc3").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.desc4").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.desc5").withStyle(ChatFormatting.AQUA));
        tooltip.add(Component.translatable(isExplosive(stack) ? "item.netheritesupremo.gauntlet.mode_on"
                : "item.netheritesupremo.gauntlet.mode_off").withStyle(ChatFormatting.YELLOW));
        if (isInfinite(stack)) {
            tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.infinite").withStyle(ChatFormatting.LIGHT_PURPLE));
        } else {
            tooltip.add(Component.translatable("item.netheritesupremo.gauntlet.kills", kills(stack), INFINITE_KILLS)
                    .withStyle(ChatFormatting.RED));
        }
    }
}
