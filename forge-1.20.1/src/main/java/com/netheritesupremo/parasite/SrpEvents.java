package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.coven.Coven;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Cabecas Andantes: parasitas medios e grandes mortos deixam, as vezes, a cabeca viva (Walking Heads do Scape and Run). */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class SrpEvents {
    private SrpEvents() {
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || !(mob instanceof ParasiteMob) || !(mob.level() instanceof ServerLevel level)) {
            return;
        }
        if (mob instanceof ParasiteHead || mob instanceof ParasiteGnat || mob instanceof ParasiteWorm
                || mob instanceof ParasiteRooter || mob.getMaxHealth() < 18.0F || level.random.nextInt(5) != 0) {
            return;
        }
        int near = level.getEntitiesOfClass(ParasiteHead.class, mob.getBoundingBox().inflate(32.0D)).size();
        if (near >= 6) {
            return;
        }
        ParasiteHead head = Coven.PARASITE_HEAD.get().create(level);
        if (head == null) {
            return;
        }
        head.moveTo(mob.getX(), mob.getY() + 0.5D, mob.getZ(), level.random.nextFloat() * 360.0F, 0.0F);
        OutbreakEvents.stampStage(head, ParasiteBehavior.phaseOf(mob));
        level.addFreshEntity(head);
    }
}
