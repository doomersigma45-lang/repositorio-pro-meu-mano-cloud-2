package com.netheritesupremo.coven;

import com.netheritesupremo.NetheriteSupremo;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Atributos e regras de spawn das criaturas do covil e dos novos parasitas. */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CovenBusEvents {
    private CovenBusEvents() {
    }

    @SubscribeEvent
    public static void onAttributes(EntityAttributeCreationEvent event) {
        event.put(Coven.IMP.get(), CovenMob.createAttributes(CovenMob.Kind.IMP).build());
        event.put(Coven.WEREWOLF.get(), CovenMob.createAttributes(CovenMob.Kind.WEREWOLF).build());
        event.put(Coven.SHADOW_PERSON.get(), CovenMob.createAttributes(CovenMob.Kind.SHADOW_PERSON).build());
        event.put(Coven.GHOST.get(), CovenMob.createAttributes(CovenMob.Kind.GHOST).build());
        event.put(Coven.DEMON.get(), CovenMob.createAttributes(CovenMob.Kind.DEMON).build());
        event.put(Coven.CAMBION.get(), CovenMob.createAttributes(CovenMob.Kind.CAMBION).build());
        event.put(Coven.SNAKE.get(), CovenMob.createAttributes(CovenMob.Kind.SNAKE).build());
        event.put(Coven.BLACK_DOG.get(), CovenDog.createBlackDogAttributes().build());
        event.put(Coven.HELLHOUND.get(), CovenDog.createHellhoundAttributes().build());
        event.put(Coven.RAVEN.get(), CovenBird.createBirdAttributes().build());
        event.put(Coven.OWL.get(), CovenBird.createBirdAttributes().build());
        event.put(Coven.TOAD.get(), CovenToad.createToadAttributes().build());
        event.put(Coven.PARASITE_HEAD.get(), com.netheritesupremo.parasite.ParasiteHead.createAttributes().build());
        event.put(Coven.PARASITE_GNAT.get(), com.netheritesupremo.parasite.ParasiteGnat.createAttributes().build());
        event.put(Coven.PARASITE_BOMBER.get(), com.netheritesupremo.parasite.ParasiteBomber.createAttributes().build());
        event.put(Coven.PARASITE_BLAZE.get(), com.netheritesupremo.parasite.ParasiteBlaze.createAttributes().build());
        event.put(Coven.PARASITE_ROOTER.get(), com.netheritesupremo.parasite.ParasiteRooter.createAttributes().build());
    }

    @SubscribeEvent
    public static void onSpawnPlacements(SpawnPlacementRegisterEvent event) {
        night(event, Coven.WEREWOLF.get());
        night(event, Coven.SHADOW_PERSON.get());
        night(event, Coven.GHOST.get());
        event.register(Coven.BLACK_DOG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                (type, level, reason, pos, random) -> Monster.isDarkEnoughToSpawn(level, pos, random)
                        && Mob.checkMobSpawnRules(type, level, reason, pos, random),
                SpawnPlacementRegisterEvent.Operation.OR);
        anyLight(event, Coven.IMP.get());
        anyLight(event, Coven.CAMBION.get());
        calm(event, Coven.HELLHOUND.get());
        anyLight(event, Coven.SNAKE.get());
        calm(event, Coven.RAVEN.get());
        calm(event, Coven.OWL.get());
        calm(event, Coven.TOAD.get());
    }

    private static <T extends Monster> void night(SpawnPlacementRegisterEvent event, net.minecraft.world.entity.EntityType<T> type) {
        event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }

    private static <T extends Monster> void anyLight(SpawnPlacementRegisterEvent event, net.minecraft.world.entity.EntityType<T> type) {
        event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkAnyLightMonsterSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }

    private static <T extends Mob> void calm(SpawnPlacementRegisterEvent event, net.minecraft.world.entity.EntityType<T> type) {
        event.register(type, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Mob::checkMobSpawnRules, SpawnPlacementRegisterEvent.Operation.OR);
    }
}
