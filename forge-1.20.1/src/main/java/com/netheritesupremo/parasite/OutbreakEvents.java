package com.netheritesupremo.parasite;

import com.mojang.brigadier.Command;
import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import com.netheritesupremo.skeleton.ModEffects;
import java.util.List;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Surto natural: com mais de 10 infectados carregados no mundo, mobs que nascem naturalmente passam a nascer infectados
 * (dias 0-5: 1 em cada 3; depois: 2 em cada 3; dia 25: todos os hostis). A partir da fase 2 do surto aparecem os
 * infectados "de ar" (Totem e Flor), raros na fase 2 e um pouco mais comuns na 3.
 */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class OutbreakEvents {
    private static final String ROLL_TAG = "ParasiteOutbreakInfect";
    private static final long NATURAL_PARASITIZED_TICKS = 72000L;

    private OutbreakEvents() {
    }

    private static boolean eligible(Mob m) {
        return !(m instanceof ParasiteMob) && (m instanceof Enemy || m instanceof Animal)
                && !(m instanceof EnderDragon) && !(m instanceof WitherBoss) && m.getMaxHealth() <= 400.0F;
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        int tick = server.getTickCount();
        if (tick % 100 == 0) {
            int count = 0;
            for (ServerLevel level : server.getAllLevels()) {
                for (Entity e : level.getAllEntities()) {
                    if (e instanceof ParasiteMob && e.isAlive()) {
                        count++;
                    }
                }
            }
            ParasiteOutbreak data = ParasiteOutbreak.get(server);
            long now = server.overworld().getDayTime();
            if (count > ParasiteOutbreak.THRESHOLD && data.start < 0L) {
                data.start = now;
                data.setDirty();
            }
            ParasiteOutbreak.infected = count;
            ParasiteOutbreak.active = count > ParasiteOutbreak.THRESHOLD;
            ParasiteOutbreak.days = data.start < 0L ? 0.0D : Math.max(0.0D, (now - data.start) / (double) ParasiteBehavior.DAY);
        }
        if (tick % 200 == 0 && ParasiteOutbreak.active && ParasiteOutbreak.stage() >= 2) {
            trySpawnAirborne(server);
        }
    }

    /** Totem e Flor: raros na fase 2 (1 chance em 60 por jogador a cada 10 s) e menos raros na 3 (1 em 20). */
    private static void trySpawnAirborne(MinecraftServer server) {
        ServerLevel level = server.overworld();
        int stage = ParasiteOutbreak.stage();
        for (ServerPlayer player : level.players()) {
            if (level.random.nextInt(stage >= 3 ? 20 : 60) != 0) {
                continue;
            }
            int near = level.getEntitiesOfClass(AirborneParasite.class, player.getBoundingBox().inflate(128.0D)).size();
            if (near >= (stage >= 3 ? 4 : 2)) {
                continue;
            }
            double angle = level.random.nextDouble() * Math.PI * 2.0D;
            double dist = 24.0D + level.random.nextDouble() * 24.0D;
            int x = (int) Math.floor(player.getX() + Math.cos(angle) * dist);
            int z = (int) Math.floor(player.getZ() + Math.sin(angle) * dist);
            if (!level.hasChunkAt(new BlockPos(x, 64, z))) {
                continue;
            }
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos pos = new BlockPos(x, y, z);
            if (!level.getFluidState(pos).isEmpty() || !level.getFluidState(pos.below()).isEmpty()) {
                continue;
            }
            EntityType<AirborneParasite> type = level.random.nextBoolean() ? ModEntities.PARASITE_TOTEM.get() : ModEntities.PARASITE_BLOOM.get();
            AirborneParasite p = type.create(level);
            if (p == null) {
                continue;
            }
            p.moveTo(x + 0.5D, y, z + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
            stampStage(p, stage);
            level.addFreshEntity(p);
        }
    }

    /** Faz o infectado nascer ja na fase do surto (fase 2 = 5 dias de infeccao, fase 3 = 11). */
    private static void stampStage(Mob mob, int stage) {
        long offset = stage >= 3 ? 11L : (stage == 2 ? 5L : 0L);
        mob.getPersistentData().putLong(ParasiteBehavior.SINCE_TAG, mob.level().getDayTime() - offset * ParasiteBehavior.DAY);
    }

    /** Sorteia, na hora do spawn natural, se o mob nasce infectado. */
    @SubscribeEvent
    public static void onFinalize(MobSpawnEvent.FinalizeSpawn event) {
        Mob mob = event.getEntity();
        if (mob.level().isClientSide || !ParasiteOutbreak.active) {
            return;
        }
        MobSpawnType type = event.getSpawnType();
        if ((type != MobSpawnType.NATURAL && type != MobSpawnType.CHUNK_GENERATION) || !eligible(mob)) {
            return;
        }
        double days = ParasiteOutbreak.days;
        boolean infect;
        if (days >= 25.0D && mob instanceof Enemy) {
            infect = true;
        } else {
            int in3 = days < 5.0D ? 1 : 2;
            infect = mob.getRandom().nextInt(3) < in3;
        }
        if (infect) {
            mob.getPersistentData().putBoolean(ROLL_TAG, true);
        }
    }

    /** Troca o mob sorteado pela versao parasita (ou deixa Parasitado, se nao houver versao). */
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.loadedFromDisk() || event.getLevel().isClientSide || !(event.getEntity() instanceof Mob mob)) {
            return;
        }
        if (!mob.getPersistentData().getBoolean(ROLL_TAG)) {
            return;
        }
        mob.getPersistentData().remove(ROLL_TAG);
        if (!ParasiteBehavior.canInfect(mob)) {
            return;
        }
        stampStage(mob, ParasiteOutbreak.stage());
        if (ParasiteBehavior.versionFor(mob.getType()) != null) {
            event.setCanceled(true);
            ParasiteBehavior.spawnVersion(mob);
        } else {
            mob.addEffect(new MobEffectInstance(ModEffects.PARASITIZED.get(), (int) NATURAL_PARASITIZED_TICKS, 0, false, true, true));
        }
    }

    @SubscribeEvent
    public static void onCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("parasite").requires(s -> s.hasPermission(2))
                .then(Commands.literal("status").executes(ctx -> {
                    MinecraftServer server = ctx.getSource().getServer();
                    ParasiteOutbreak data = ParasiteOutbreak.get(server);
                    String msg = "Infectados carregados: " + ParasiteOutbreak.infected + " | surto "
                            + (data.start < 0L ? "ainda nao comecou" : String.format("%.1f dias, fase %d", ParasiteOutbreak.days, ParasiteOutbreak.stage()))
                            + " | nascimento natural " + (ParasiteOutbreak.active ? "ATIVO" : "inativo");
                    ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("meteor").executes(ctx -> {
                    net.minecraft.world.phys.Vec3 at = ctx.getSource().getPosition();
                    boolean ok = ParasiteMeteorEvents.spawn(ctx.getSource().getLevel(), at.x + 20.0D, at.z + 20.0D);
                    ctx.getSource().sendSuccess(() -> Component.literal(ok ? "Meteoro a caminho." : "Nao foi possivel (chunk descarregado ou sem altura)."), false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("ritual").executes(ctx -> {
                    String msg = ParasiteRituals.forceStart(ctx.getSource().getLevel(), ctx.getSource().getPosition());
                    ctx.getSource().sendSuccess(() -> Component.literal("Ritual: " + msg + " (ativos: " + ParasiteRituals.activeCount() + ")"), false);
                    return Command.SINGLE_SUCCESS;
                }))
                .then(Commands.literal("reset").executes(ctx -> {
                    ParasiteOutbreak data = ParasiteOutbreak.get(ctx.getSource().getServer());
                    data.start = -1L;
                    data.setDirty();
                    ParasiteOutbreak.days = 0.0D;
                    ctx.getSource().sendSuccess(() -> Component.literal("Surto reiniciado."), false);
                    return Command.SINGLE_SUCCESS;
                })));
    }
}
