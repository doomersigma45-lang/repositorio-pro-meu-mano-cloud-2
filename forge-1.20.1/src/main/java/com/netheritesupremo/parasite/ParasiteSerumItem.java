package com.netheritesupremo.parasite;

import com.netheritesupremo.skeleton.ModEffects;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Soro Acelerador / Retardador: muda a fase da infeccao de parasitas. Clicar num parasita afeta so ele; usar no ar afeta
 * todos os parasitas num raio de 6 blocos. O acelerador leva cada um para o comeco da fase seguinte (a 3 e a ultima);
 * o retardador volta uma fase (a 1 e a primeira).
 */
public class ParasiteSerumItem extends Item {
    private final boolean accelerate;

    public ParasiteSerumItem(Properties properties, boolean accelerate) {
        super(properties);
        this.accelerate = accelerate;
    }

    /** Move a marca de infeccao para o comeco da fase seguinte/anterior. @return true se a fase mudou. */
    public static boolean shiftPhase(Mob mob, boolean up) {
        int phase = ParasiteBehavior.phaseOf(mob);
        long now = mob.level().getDayTime();
        long since;
        if (up) {
            if (phase >= 3) {
                return false;
            }
            since = now - (phase == 1 ? 5L : 11L) * ParasiteBehavior.DAY - 100L;
        } else {
            if (phase <= 1) {
                return false;
            }
            since = phase == 3 ? now - 5L * ParasiteBehavior.DAY - 100L : now;
        }
        mob.getPersistentData().putLong(ParasiteBehavior.SINCE_TAG, since);
        mob.removeEffect(ModEffects.PARASITE_PHASE.get());
        ParasiteBehavior.updatePhase(mob);
        return true;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (!(target instanceof Mob mob) || !(target instanceof ParasiteMob)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (shiftPhase(mob, this.accelerate)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 20);
            player.displayClientMessage(Component.literal("Fase do parasita: " + ParasiteBehavior.phaseOf(mob)), true);
            return InteractionResult.CONSUME;
        }
        player.displayClientMessage(Component.literal(this.accelerate ? "Esse parasita ja esta na ultima fase." : "Esse parasita ja esta na primeira fase."), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            return InteractionResultHolder.success(stack);
        }
        List<Mob> around = level.getEntitiesOfClass(Mob.class, player.getBoundingBox().inflate(6.0D), m -> m instanceof ParasiteMob && m.isAlive());
        int changed = 0;
        for (Mob m : around) {
            if (shiftPhase(m, this.accelerate)) {
                changed++;
            }
        }
        if (changed > 0) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 20);
            player.displayClientMessage(Component.literal("Parasitas afetados: " + changed), true);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.pass(stack);
    }
}
