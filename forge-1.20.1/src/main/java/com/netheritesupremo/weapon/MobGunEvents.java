package com.netheritesupremo.weapon;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.parasite.ParasiteMob;
import com.netheritesupremo.parasite.ParasitePlayer;
import com.netheritesupremo.parasite.ParasiteSkeleton;
import com.netheritesupremo.parasite.ParasiteZombie;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Zumbis e esqueletos (normais e parasitas) passam a saber atirar com Uzi, AK e RPG, e podem nascer com uma delas:
 * zumbi/esqueleto normal 5% / 8%, parasita 20% / 25% (o esqueleto troca o arco pela arma). O Jogador Parasita tem a propria logica.
 */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class MobGunEvents {
    private static final String ROLL_TAG = "GunRolled";

    private MobGunEvents() {
    }

    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)) {
            return;
        }
        boolean zombie = mob.getClass() == Zombie.class || mob instanceof ParasiteZombie;
        boolean skeleton = mob.getClass() == Skeleton.class || mob instanceof ParasiteSkeleton;
        if (!zombie && !skeleton) {
            return;
        }
        if (mob.goalSelector.getAvailableGoals().stream().noneMatch(w -> w.getGoal() instanceof MobGunGoal)) {
            mob.goalSelector.addGoal(1, new MobGunGoal(mob));
        }
        if (event.loadedFromDisk() || mob.getPersistentData().getBoolean(ROLL_TAG)) {
            return;
        }
        mob.getPersistentData().putBoolean(ROLL_TAG, true);
        boolean parasite = mob instanceof ParasiteMob;
        float chance = skeleton ? (parasite ? 0.25F : 0.08F) : (parasite ? 0.20F : 0.05F);
        if (mob.getRandom().nextFloat() >= chance) {
            return;
        }
        if (zombie && !mob.getMainHandItem().isEmpty()) {
            return; // so o zumbi de maos vazias pega uma arma
        }
        ItemStack gun = MobGuns.randomNewGun(mob.getRandom());
        mob.setItemSlot(EquipmentSlot.MAINHAND, gun);
        mob.setDropChance(EquipmentSlot.MAINHAND, 0.085F);
    }
}
