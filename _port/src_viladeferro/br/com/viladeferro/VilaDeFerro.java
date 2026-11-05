/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.EntityType$Builder
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.entity.animal.IronGolem
 *  net.minecraft.world.entity.monster.Creeper
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.CreativeModeTabs
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Item$Properties
 *  net.minecraftforge.common.ForgeSpawnEggItem
 *  net.minecraftforge.event.BuildCreativeModeTabContentsEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$PlayerTickEvent
 *  net.minecraftforge.event.entity.EntityAttributeCreationEvent
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingChangeTargetEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.registries.DeferredRegister
 *  net.minecraftforge.registries.ForgeRegistries
 *  net.minecraftforge.registries.IForgeRegistry
 *  net.minecraftforge.registries.RegistryObject
 */
package br.com.viladeferro;

import br.com.viladeferro.ChiefCub;
import br.com.viladeferro.ChiefVillager;
import br.com.viladeferro.FighterVillager;
import br.com.viladeferro.Tier;
import br.com.viladeferro.VillageSeeder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryObject;

@Mod(value="viladeferro")
public class VilaDeFerro {
    public static final String MOD_ID = "viladeferro";
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ENTITY_TYPES, (String)"viladeferro");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((IForgeRegistry)ForgeRegistries.ITEMS, (String)"viladeferro");
    public static final RegistryObject<EntityType<FighterVillager>> FIGHTER = ENTITIES.register("guerreiro_villager", () -> EntityType.Builder.m_20704_(FighterVillager::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.6f, 1.95f).m_20702_(10).m_20712_("guerreiro_villager"));
    public static final RegistryObject<EntityType<ChiefVillager>> CHIEF = ENTITIES.register("chefe_villager", () -> EntityType.Builder.m_20704_(ChiefVillager::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.72f, 2.34f).m_20702_(12).m_20712_("chefe_villager"));
    public static final RegistryObject<EntityType<ChiefCub>> CHIEF_CUB = ENTITIES.register("filhote_chefe_villager", () -> EntityType.Builder.m_20704_(ChiefCub::new, (MobCategory)MobCategory.CREATURE).m_20699_(0.6f, 1.95f).m_20702_(10).m_20712_("filhote_chefe_villager"));
    public static final RegistryObject<Item> FIGHTER_EGG = ITEMS.register("guerreiro_villager_spawn_egg", () -> new ForgeSpawnEggItem(FIGHTER, 10119747, 3497027, new Item.Properties()));
    public static final RegistryObject<Item> CHIEF_EGG = ITEMS.register("chefe_villager_spawn_egg", () -> new ForgeSpawnEggItem(CHIEF, 6767407, 12884538, new Item.Properties()));

    public VilaDeFerro() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ENTITIES.register(bus);
        ITEMS.register(bus);
        bus.addListener(this::addCreative);
        bus.addListener(this::registerAttributes);
    }

    private void registerAttributes(EntityAttributeCreationEvent event) {
        event.put((EntityType)FIGHTER.get(), FighterVillager.createFighterAttributes().m_22265_());
        event.put((EntityType)CHIEF.get(), ChiefVillager.createChiefAttributes().m_22265_());
        event.put((EntityType)CHIEF_CUB.get(), ChiefCub.createCubAttributes().m_22265_());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.f_256731_) {
            event.accept(FIGHTER_EGG);
            event.accept(CHIEF_EGG);
        }
    }

    public static boolean isFriend(Entity e) {
        return e instanceof FighterVillager || e instanceof ChiefCub;
    }

    public static boolean isNeverTarget(Entity e) {
        return VilaDeFerro.isFriend(e) || e instanceof Creeper;
    }

    @Mod.EventBusSubscriber(modid="viladeferro")
    public static class CommonEvents {
        @SubscribeEvent
        public static void onWarriorHurt(LivingHurtEvent event) {
            if (!(event.getEntity() instanceof FighterVillager)) {
                return;
            }
            Entity entity = event.getSource().m_7639_();
            if (!(entity instanceof LivingEntity)) {
                return;
            }
            LivingEntity attacker = (LivingEntity)entity;
            LivingEntity victim = event.getEntity();
            if (victim.m_9236_().f_46443_ || VilaDeFerro.isNeverTarget((Entity)attacker)) {
                return;
            }
            for (FighterVillager ally : victim.m_9236_().m_45976_(FighterVillager.class, victim.m_20191_().m_82400_(24.0))) {
                if (ally == victim || ally.m_5448_() != null) continue;
                ally.m_6710_(attacker);
            }
        }

        @SubscribeEvent
        public static void onAttack(LivingAttackEvent event) {
            Entity source = event.getSource().m_7639_();
            if (source == null) {
                return;
            }
            LivingEntity victim = event.getEntity();
            if (VilaDeFerro.isFriend(source) && VilaDeFerro.isFriend((Entity)victim)) {
                event.setCanceled(true);
            } else if ((source instanceof IronGolem || source instanceof FighterVillager) && victim instanceof ChiefCub) {
                event.setCanceled(true);
            } else if (Tier.of(source) == Tier.PASSIVE && victim instanceof Player) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onChangeTarget(LivingChangeTargetEvent event) {
            LivingEntity target = event.getNewTarget();
            if (target == null) {
                return;
            }
            LivingEntity self = event.getEntity();
            if (self instanceof IronGolem && VilaDeFerro.isFriend((Entity)target)) {
                event.setCanceled(true);
            } else if (Tier.of((Entity)self) == Tier.PASSIVE && target instanceof Player) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            Player player;
            if (event.phase != TickEvent.Phase.END || !((player = event.player) instanceof ServerPlayer)) {
                return;
            }
            ServerPlayer player2 = (ServerPlayer)player;
            if (player2.f_19797_ % 100 != 0) {
                return;
            }
            VillageSeeder.trySeed(player2.m_284548_(), player2);
        }
    }
}

