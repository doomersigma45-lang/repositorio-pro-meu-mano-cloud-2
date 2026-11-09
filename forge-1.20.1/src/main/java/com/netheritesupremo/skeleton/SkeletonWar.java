package com.netheritesupremo.skeleton;

import com.mojang.datafixers.util.Pair;
import com.netheritesupremo.entity.SkeletonKing;
import com.netheritesupremo.entity.WarTank;
import com.netheritesupremo.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.StructureTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Guerra dos esqueletos. Quando um jogador mata o Rei Esqueleto, os esqueletos declaram guerra contra as
 * vilas e outposts proximos: chegam em ondas com a Old Gun e so vencem se matarem todos os moradores
 * (aldeoes, golems e saqueadores). Se vencem, o lugar fica conquistado e esqueletos passam a surgir ali.
 */
@Mod.EventBusSubscriber(modid = "netheritesupremo")
public final class SkeletonWar {
    public static final int WARMUP = 0;
    public static final int ATTACKING = 1;
    public static final int CONQUERED = 2;
    public static final int DEFENDED = 3;
    private static final String[] STATE_NAMES = {"WARMUP", "ATTACKING", "CONQUERED", "DEFENDED"};

    public static final int AREA = 44;          // raio da area do lugar atacado
    public static final int FORCED_RADIUS = 2;  // chunks mantidos carregados durante o ataque
    public static final int MAX_ALIVE = 12;
    public static final int MAX_RESIDENTS = 12;
    public static final int WAVES = 4;
    public static final int WAVE_SIZE = 5;
    public static final long MAX_ATTACK_TICKS = 24000L;

    private SkeletonWar() {
    }

    // ================================================================= dados
    public static final class Site {
        public BlockPos center;
        public String kind;
        public int state;
        public int wave;
        public int maxWaves = WAVES;
        public int waveSize = WAVE_SIZE;
        public long stateTick;
        public long lastWave;
        public int initDefenders;
        public boolean forced;

        CompoundTag save() {
            CompoundTag t = new CompoundTag();
            t.putInt("x", center.getX());
            t.putInt("y", center.getY());
            t.putInt("z", center.getZ());
            t.putString("kind", kind);
            t.putInt("state", state);
            t.putInt("wave", wave);
            t.putInt("maxWaves", maxWaves);
            t.putInt("waveSize", waveSize);
            t.putLong("stateTick", stateTick);
            t.putLong("lastWave", lastWave);
            t.putInt("initDefenders", initDefenders);
            t.putBoolean("forced", forced);
            return t;
        }

        static Site load(CompoundTag t) {
            Site s = new Site();
            s.center = new BlockPos(t.getInt("x"), t.getInt("y"), t.getInt("z"));
            s.kind = t.getString("kind");
            s.state = t.getInt("state");
            s.wave = t.getInt("wave");
            s.maxWaves = t.getInt("maxWaves");
            s.waveSize = t.getInt("waveSize");
            s.stateTick = t.getLong("stateTick");
            s.lastWave = t.getLong("lastWave");
            s.initDefenders = t.getInt("initDefenders");
            s.forced = t.getBoolean("forced");
            return s;
        }

        boolean active() {
            return state == WARMUP || state == ATTACKING;
        }
    }

    public static final class Data extends SavedData {
        public final List<Site> sites = new ArrayList<>();

        public static Data get(ServerLevel overworld) {
            return overworld.getDataStorage().computeIfAbsent(Data::load, Data::new, "netheritesupremo_skeleton_war");
        }

        static Data load(CompoundTag tag) {
            Data d = new Data();
            ListTag list = tag.getList("sites", 10);
            for (int i = 0; i < list.size(); i++) {
                d.sites.add(Site.load(list.getCompound(i)));
            }
            return d;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            ListTag list = new ListTag();
            for (Site s : sites) {
                list.add(s.save());
            }
            tag.put("sites", list);
            return tag;
        }

        public boolean hasActive() {
            for (Site s : sites) {
                if (s.active()) {
                    return true;
                }
            }
            return false;
        }
    }

    public static String stateName(int state) {
        return STATE_NAMES[Math.max(0, Math.min(STATE_NAMES.length - 1, state))];
    }

    // ============================================================ declaracao
    public static final String MARK_KEY = "netheritesupremo_war_mark";

    /** Chamado quando o Rei morre: o jogador que o matou recebe a Marca de Guerra (a guerra comeca quando ele chegar a uma vila ou outpost). */
    public static void onKingDied(ServerLevel level, SkeletonKing king, DamageSource source) {
        ServerPlayer killer = null;
        if (source.getEntity() instanceof ServerPlayer p) {
            killer = p;
        } else if (king.getKillCredit() instanceof ServerPlayer p) {
            killer = p;
        }
        if (killer == null) {
            return;
        }
        mark(killer);
        killer.sendSystemMessage(Component.translatable("message.netheritesupremo.skeleton_war.marked"));
    }

    // ------------------------------------------------------------- marca de guerra
    private static CompoundTag persisted(net.minecraft.world.entity.player.Player p) {
        CompoundTag root = p.getPersistentData();
        if (!root.contains(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG, 10)) {
            root.put(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG, new CompoundTag());
        }
        return root.getCompound(net.minecraft.world.entity.player.Player.PERSISTED_NBT_TAG);
    }

    public static boolean isMarked(net.minecraft.world.entity.player.Player p) {
        return persisted(p).getBoolean(MARK_KEY);
    }

    public static void mark(ServerPlayer p) {
        persisted(p).putBoolean(MARK_KEY, true);
        applyMarkEffect(p);
    }

    public static void unmark(ServerPlayer p) {
        persisted(p).remove(MARK_KEY);
        p.removeEffect(ModEffects.WAR_MARK.get());
    }

    private static void applyMarkEffect(ServerPlayer p) {
        p.addEffect(new MobEffectInstance(ModEffects.WAR_MARK.get(), MobEffectInstance.INFINITE_DURATION, 0, false, true, true));
    }

    /** Vila ou outpost de pilhagem em volta de {@code pos}: devolve {centro, tipo} ou null. */
    public static Pair<BlockPos, String> siteAt(ServerLevel level, BlockPos pos) {
        var sm = level.structureManager();
        var village = sm.getStructureWithPieceAt(pos, StructureTags.VILLAGE);
        if (village.isValid()) {
            return Pair.of(village.getBoundingBox().getCenter(), "village");
        }
        var outpost = sm.getStructureWithPieceAt(pos, BuiltinStructures.PILLAGER_OUTPOST);
        if (outpost.isValid()) {
            return Pair.of(outpost.getBoundingBox().getCenter(), "outpost");
        }
        return null;
    }

    /** Um jogador marcado chegou a um lugar: tira a marca e comeca a guerra ali (se o lugar ainda nao foi atacado). */
    public static boolean arrive(MinecraftServer server, Pair<BlockPos, String> site, String by) {
        ServerLevel ow = server.overworld();
        Data data = Data.get(ow);
        for (Site s : data.sites) {
            if (horizontal(s.center, site.getFirst()) < 200) {
                return false;
            }
        }
        Site s = newSite(site.getFirst(), site.getSecond());
        broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.declared", by));
        broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.target",
                Component.translatable("message.netheritesupremo.skeleton_war.kind." + s.kind),
                s.center.getX(), s.center.getZ()));
        begin(ow, data, s);
        return true;
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.side.isClient() || !(event.player instanceof ServerPlayer p)
                || p.tickCount % 20 != 0) {
            return;
        }
        if (!isMarked(p)) {
            return;
        }
        if (!p.hasEffect(ModEffects.WAR_MARK.get())) {
            applyMarkEffect(p); // leite, /effect clear ou morte nao tiram a marca
        }
        ServerLevel level = p.serverLevel();
        if (level.dimension() != net.minecraft.world.level.Level.OVERWORLD) {
            return;
        }
        Pair<BlockPos, String> site = siteAt(level, p.blockPosition());
        if (site == null) {
            return;
        }
        unmark(p);
        if (!arrive(level.getServer(), site, p.getGameProfile().getName())) {
            p.sendSystemMessage(Component.translatable("message.netheritesupremo.skeleton_war.faded"));
        }
    }

    /** Declara a guerra: procura ate 2 vilas e 1 outpost perto de {@code origin}. Devolve quantos alvos achou. */
    public static int declare(MinecraftServer server, BlockPos origin, String by) {
        ServerLevel ow = server.overworld();
        Data data = Data.get(ow);
        if (data.hasActive()) {
            return 0;
        }
        broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.declared", by));
        Registry<Structure> reg = ow.registryAccess().registryOrThrow(Registries.STRUCTURE);
        HolderSet<Structure> villages = reg.getTag(StructureTags.VILLAGE).<HolderSet<Structure>>map(t -> t).orElse(null);
        Optional<Holder.Reference<Structure>> outpostHolder = reg.getHolder(BuiltinStructures.PILLAGER_OUTPOST);
        HolderSet<Structure> outposts = outpostHolder.<HolderSet<Structure>>map(h -> HolderSet.direct(h)).orElse(null);
        int[][] offsets = {{0, 0}, {600, 0}, {-600, 0}, {0, 600}, {0, -600}, {500, 500}, {-500, -500}};
        List<Site> added = new ArrayList<>();
        int villageCount = 0;
        int outpostCount = 0;
        for (int[] off : offsets) {
            BlockPos from = origin.offset(off[0], 0, off[1]);
            if (villages != null && villageCount < 2) {
                BlockPos p = find(ow, villages, from);
                if (p != null && isFree(data, added, p)) {
                    added.add(newSite(p, "village"));
                    villageCount++;
                }
            }
            if (outposts != null && outpostCount < 1) {
                BlockPos p = find(ow, outposts, from);
                if (p != null && isFree(data, added, p)) {
                    added.add(newSite(p, "outpost"));
                    outpostCount++;
                }
            }
            if (villageCount >= 2 && outpostCount >= 1) {
                break;
            }
        }
        if (added.isEmpty()) {
            broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.none"));
            return 0;
        }
        for (Site s : added) {
            begin(ow, data, s);
            broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.target",
                    Component.translatable("message.netheritesupremo.skeleton_war.kind." + s.kind),
                    s.center.getX(), s.center.getZ()));
        }
        return added.size();
    }

    private static BlockPos find(ServerLevel ow, HolderSet<Structure> set, BlockPos from) {
        try {
            Pair<BlockPos, Holder<Structure>> r = ow.getChunkSource().getGenerator()
                    .findNearestMapStructure(ow, set, from, 40, false);
            return r == null ? null : r.getFirst();
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private static boolean isFree(Data data, List<Site> added, BlockPos p) {
        for (Site s : data.sites) {
            if (horizontal(s.center, p) < 200) {
                return false;
            }
        }
        for (Site s : added) {
            if (horizontal(s.center, p) < 200) {
                return false;
            }
        }
        return true;
    }

    private static double horizontal(BlockPos a, BlockPos b) {
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    private static Site newSite(BlockPos center, String kind) {
        Site s = new Site();
        s.center = center;
        s.kind = kind;
        return s;
    }

    /** Comando de teste: ataca exatamente o lugar indicado (poucas ondas). */
    public static Site addTestSite(ServerLevel ow, BlockPos pos) {
        Data data = Data.get(ow);
        Site s = newSite(pos, "test");
        s.maxWaves = 2;
        s.waveSize = 3;
        begin(ow, data, s);
        return s;
    }

    private static void begin(ServerLevel ow, Data data, Site s) {
        s.state = WARMUP;
        s.stateTick = ow.getGameTime();
        setForced(ow, s, true);
        data.sites.add(s);
        data.setDirty();
    }

    // ================================================================ tick
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = event.getServer();
        if (server.getTickCount() % 20 != 0) {
            return;
        }
        ServerLevel ow = server.overworld();
        Data data = Data.get(ow);
        if (data.sites.isEmpty()) {
            return;
        }
        long now = ow.getGameTime();
        boolean dirty = false;
        for (Site s : new ArrayList<>(data.sites)) {
            switch (s.state) {
                case WARMUP -> dirty |= tickWarmup(server, ow, data, s, now);
                case ATTACKING -> dirty |= tickAttacking(server, ow, s, now);
                case CONQUERED -> {
                    if (now % 100 < 20) {
                        tickConquered(ow, s);
                    }
                }
                default -> {
                }
            }
        }
        if (dirty) {
            data.setDirty();
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        ServerLevel ow = event.getServer().overworld();
        Data data = Data.get(ow);
        for (Site s : data.sites) {
            if (s.forced && !s.active()) {
                setForced(ow, s, false);
            }
        }
        data.setDirty();
    }

    private static boolean tickWarmup(MinecraftServer server, ServerLevel ow, Data data, Site s, long now) {
        long elapsed = now - s.stateTick;
        if (elapsed < 100) {
            return false;
        }
        int defenders = defenders(ow, s).size();
        if (defenders > 0) {
            s.initDefenders = defenders;
            s.state = ATTACKING;
            s.stateTick = now;
            s.lastWave = now - 1200;
            return true;
        }
        if (elapsed > 1200) {
            setForced(ow, s, false);
            data.sites.remove(s);
            return true;
        }
        return false;
    }

    private static boolean tickAttacking(MinecraftServer server, ServerLevel ow, Site s, long now) {
        List<AbstractSkeleton> attackers = attackers(ow, s);
        List<LivingEntity> defenders = defenders(ow, s);
        if (defenders.isEmpty()) {
            conquer(server, ow, s, attackers);
            return true;
        }
        if (s.wave >= s.maxWaves && attackers.isEmpty() && tanksAlive(ow, s) == 0) {
            defend(server, ow, s, attackers);
            return true;
        }
        if (now - s.stateTick > MAX_ATTACK_TICKS) {
            defend(server, ow, s, attackers);
            return true;
        }
        boolean dirty = false;
        if (s.wave < s.maxWaves && attackers.size() < MAX_ALIVE && (attackers.size() <= 2 || now - s.lastWave >= 1200)) {
            spawnWave(ow, s, defenders);
            if (s.wave >= 1 || "test".equals(s.kind)) {
                spawnTank(ow, s, defenders);
            }
            s.wave++;
            s.lastWave = now;
            dirty = true;
        }
        for (AbstractSkeleton a : attackers) {
            LivingEntity t = a.getTarget();
            if (t == null || !t.isAlive()) {
                LivingEntity nearest = nearest(a, defenders);
                if (nearest != null) {
                    a.setTarget(nearest);
                }
            }
        }
        return dirty;
    }

    private static LivingEntity nearest(Mob from, List<LivingEntity> list) {
        LivingEntity best = null;
        double bestD = Double.MAX_VALUE;
        for (LivingEntity e : list) {
            double d = from.distanceToSqr(e);
            if (d < bestD) {
                bestD = d;
                best = e;
            }
        }
        return best;
    }

    private static void conquer(MinecraftServer server, ServerLevel ow, Site s, List<AbstractSkeleton> attackers) {
        s.state = CONQUERED;
        s.stateTick = ow.getGameTime();
        setForced(ow, s, false);
        clearVehicles(ow, s, false);
        for (AbstractSkeleton a : attackers) {
            a.removeTag(SkeletonFaction.WAR_TAG);
            a.setTarget(null);
            a.setPersistenceRequired();
        }
        broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.conquered",
                Component.translatable("message.netheritesupremo.skeleton_war.kind." + s.kind),
                s.center.getX(), s.center.getZ()));
    }

    private static void defend(MinecraftServer server, ServerLevel ow, Site s, List<AbstractSkeleton> attackers) {
        s.state = DEFENDED;
        s.stateTick = ow.getGameTime();
        setForced(ow, s, false);
        for (AbstractSkeleton a : attackers) {
            a.kill();
        }
        clearVehicles(ow, s, true);
        broadcast(server, Component.translatable("message.netheritesupremo.skeleton_war.defended",
                Component.translatable("message.netheritesupremo.skeleton_war.kind." + s.kind),
                s.center.getX(), s.center.getZ()));
    }

    // ============================================================ entidades
    private static AABB area(Site s, int extra) {
        int r = AREA + extra;
        return new AABB(s.center.getX() - r, -64, s.center.getZ() - r, s.center.getX() + r, 400, s.center.getZ() + r);
    }

    public static boolean isDefender(LivingEntity e) {
        return e.isAlive() && (e instanceof Villager || e instanceof IronGolem || e instanceof Pillager
                || e instanceof Vindicator || e instanceof Evoker);
    }

    public static List<LivingEntity> defenders(ServerLevel ow, Site s) {
        return ow.getEntitiesOfClass(LivingEntity.class, area(s, 0), SkeletonWar::isDefender);
    }

    public static List<AbstractSkeleton> attackers(ServerLevel ow, Site s) {
        return ow.getEntitiesOfClass(AbstractSkeleton.class, area(s, 40),
                m -> m.isAlive() && m.getTags().contains(SkeletonFaction.WAR_TAG)
                        && !m.getTags().contains(WarTank.CREW_TAG));
    }

    private static void setForced(ServerLevel ow, Site s, boolean forced) {
        if (s.forced == forced) {
            return;
        }
        ChunkPos c = new ChunkPos(s.center);
        for (int dx = -FORCED_RADIUS; dx <= FORCED_RADIUS; dx++) {
            for (int dz = -FORCED_RADIUS; dz <= FORCED_RADIUS; dz++) {
                ow.setChunkForced(c.x + dx, c.z + dz, forced);
            }
        }
        s.forced = forced;
    }

    private static BlockPos surface(ServerLevel ow, Site s, double x, double z) {
        int bx = (int) Math.floor(x);
        int bz = (int) Math.floor(z);
        if ("test".equals(s.kind)) {
            return new BlockPos(bx, s.center.getY(), bz);
        }
        if (!ow.hasChunkAt(new BlockPos(bx, 64, bz))) {
            return null;
        }
        int y = ow.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, bx, bz);
        BlockPos pos = new BlockPos(bx, y, bz);
        if (!ow.getFluidState(pos.below()).isEmpty()) {
            return null;
        }
        return pos;
    }

    private static void spawnWave(ServerLevel ow, Site s, List<LivingEntity> defenders) {
        for (int i = 0; i < s.waveSize; i++) {
            double angle = ow.random.nextDouble() * Math.PI * 2.0D;
            double r = 22.0D + ow.random.nextDouble() * 10.0D;
            BlockPos pos = surface(ow, s, s.center.getX() + 0.5D + Math.cos(angle) * r,
                    s.center.getZ() + 0.5D + Math.sin(angle) * r);
            if (pos == null) {
                continue;
            }
            float roll = ow.random.nextFloat();
            AbstractSkeleton mob;
            if (roll < 0.6F) {
                mob = EntityType.SKELETON.create(ow);
            } else if (roll < 0.85F) {
                mob = EntityType.STRAY.create(ow);
            } else {
                mob = EntityType.WITHER_SKELETON.create(ow);
            }
            if (mob == null) {
                continue;
            }
            mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, ow.random.nextFloat() * 360.0F, 0.0F);
            mob.finalizeSpawn(ow, ow.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
            mob.addTag(SkeletonFaction.WAR_TAG);
            mob.setPersistenceRequired();
            mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
            mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
            mob.setDropChance(EquipmentSlot.HEAD, 0.0F);
            mob.setDropChance(EquipmentSlot.CHEST, 0.0F);
            mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
            var follow = mob.getAttribute(Attributes.FOLLOW_RANGE);
            if (follow != null) {
                follow.setBaseValue(64.0D);
            }
            SkeletonFaction.prepare(mob);
            LivingEntity target = nearest(mob, defenders);
            if (target != null) {
                mob.setTarget(target);
            }
            ow.addFreshEntity(mob);
            mount(ow, mob, pos);
        }
    }

    /** Cada atacante chega montado num cavalo esqueleto (so durante a guerra). */
    private static void mount(ServerLevel ow, AbstractSkeleton rider, BlockPos pos) {
        SkeletonHorse horse = EntityType.SKELETON_HORSE.create(ow);
        if (horse == null) {
            return;
        }
        horse.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, rider.getYRot(), 0.0F);
        horse.finalizeSpawn(ow, ow.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
        horse.setTamed(true);
        horse.equipSaddle(SoundSource.NEUTRAL);
        horse.addTag(SkeletonFaction.WAR_HORSE_TAG);
        horse.setPersistenceRequired();
        ow.addFreshEntity(horse);
        rider.startRiding(horse, true);
    }

    /** Fim da guerra: cavalos e tanques somem. Os tripulantes morrem se o ataque foi derrotado; senao ficam como moradores. */
    private static void clearVehicles(ServerLevel ow, Site s, boolean killCrew) {
        for (Mob v : ow.getEntitiesOfClass(Mob.class, area(s, 70), e -> e.getTags().contains(SkeletonFaction.WAR_HORSE_TAG))) {
            for (Entity passenger : new ArrayList<>(v.getPassengers())) {
                if (passenger instanceof Mob crew && crew.getTags().contains(WarTank.CREW_TAG)) {
                    if (killCrew) {
                        crew.kill();
                    } else {
                        crew.setNoAi(false);
                        crew.removeTag(WarTank.CREW_TAG);
                        crew.removeTag(SkeletonFaction.WAR_TAG);
                        crew.setPersistenceRequired();
                    }
                }
            }
            v.ejectPassengers();
            v.discard();
        }
    }

    private static int tanksAlive(ServerLevel ow, Site s) {
        return ow.getEntitiesOfClass(WarTank.class, area(s, 60), t -> t.isAlive() && t.getTags().contains(SkeletonFaction.WAR_HORSE_TAG)).size();
    }

    /** Tanque de guerra com um esqueleto de tripulante: surge a partir da segunda onda (e logo de cara no teste). */
    private static void spawnTank(ServerLevel ow, Site s, List<LivingEntity> defenders) {
        if (tanksAlive(ow, s) >= 2) {
            return;
        }
        for (int attempt = 0; attempt < 6; attempt++) {
            double angle = ow.random.nextDouble() * Math.PI * 2.0D;
            double r = 24.0D + ow.random.nextDouble() * 10.0D;
            BlockPos pos = surface(ow, s, s.center.getX() + 0.5D + Math.cos(angle) * r, s.center.getZ() + 0.5D + Math.sin(angle) * r);
            if (pos == null) {
                continue;
            }
            WarTank tank = ModEntities.WAR_TANK.get().create(ow);
            AbstractSkeleton crew = EntityType.SKELETON.create(ow);
            if (tank == null || crew == null) {
                return;
            }
            tank.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, ow.random.nextFloat() * 360.0F, 0.0F);
            tank.addTag(SkeletonFaction.WAR_HORSE_TAG);
            tank.setPersistenceRequired();
            LivingEntity target = nearest(tank, defenders);
            if (target != null) {
                tank.setTarget(target);
            }
            ow.addFreshEntity(tank);
            crew.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, tank.getYRot(), 0.0F);
            crew.finalizeSpawn(ow, ow.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
            crew.addTag(SkeletonFaction.WAR_TAG);
            crew.addTag(WarTank.CREW_TAG);
            crew.setPersistenceRequired();
            SkeletonFaction.prepare(crew);
            crew.setNoAi(true);
            ow.addFreshEntity(crew);
            crew.startRiding(tank, true);
            return;
        }
    }

    /** O cavalo de guerra corre ate o alvo do cavaleiro e para a uma distancia de tiro. */
    @SubscribeEvent
    public static void onHorseTick(LivingEvent.LivingTickEvent event) {
        if (!(event.getEntity() instanceof SkeletonHorse horse) || horse.level().isClientSide
                || !horse.getTags().contains(SkeletonFaction.WAR_HORSE_TAG)) {
            return;
        }
        if (!(horse.getFirstPassenger() instanceof Mob rider) || rider.getTarget() == null) {
            return;
        }
        LivingEntity t = rider.getTarget();
        Vec3 d = new Vec3(t.getX() - horse.getX(), 0.0D, t.getZ() - horse.getZ());
        double dist = d.length();
        Vec3 motion = horse.getDeltaMovement();
        if (dist > 9.0D) {
            Vec3 dir = d.scale(1.0D / dist);
            double speed = 0.32D;
            horse.setDeltaMovement(dir.x * speed, motion.y, dir.z * speed);
            float yaw = (float) (Math.toDegrees(Math.atan2(-dir.x, dir.z)));
            horse.setYRot(yaw);
            horse.yRotO = yaw;
            horse.setYBodyRot(yaw);
            rider.setYRot(yaw);
            if (horse.horizontalCollision && horse.onGround()) {
                horse.setDeltaMovement(horse.getDeltaMovement().x, 0.55D, horse.getDeltaMovement().z);
            }
        } else {
            horse.setDeltaMovement(motion.x * 0.3D, motion.y, motion.z * 0.3D);
        }
    }

    /** Lugar conquistado: os moradores e saqueadores nao voltam (nao nascem nem carregam la). */
    @SubscribeEvent
    public static void onJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide() || !(event.getLevel() instanceof ServerLevel level)
                || level.dimension() != net.minecraft.world.level.Level.OVERWORLD
                || !(event.getEntity() instanceof LivingEntity living) || !isDefender(living)) {
            return;
        }
        if (isConquered(level, living.blockPosition())) {
            event.setCanceled(true);
        }
    }

    public static boolean isConquered(ServerLevel ow, BlockPos pos) {
        for (Site s : Data.get(ow).sites) {
            if (s.state == CONQUERED && Math.abs(s.center.getX() - pos.getX()) <= AREA + 20
                    && Math.abs(s.center.getZ() - pos.getZ()) <= AREA + 20) {
                return true;
            }
        }
        return false;
    }

    private static void spawnResident(ServerLevel ow, Site s, double x, double z) {
        BlockPos pos = surface(ow, s, x, z);
        if (pos == null) {
            return;
        }
        float roll = ow.random.nextFloat();
        Mob mob;
        if (roll < 0.65F) {
            mob = EntityType.SKELETON.create(ow);
        } else if (roll < 0.85F) {
            mob = ModEntities.SKELETON_VILLAGER.get().create(ow);
        } else {
            mob = EntityType.STRAY.create(ow);
        }
        if (mob == null) {
            return;
        }
        mob.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, ow.random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(ow, ow.getCurrentDifficultyAt(pos), MobSpawnType.EVENT, null, null);
        mob.setPersistenceRequired();
        mob.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, MobEffectInstance.INFINITE_DURATION, 0, false, false));
        ow.addFreshEntity(mob);
    }

    /** Lugar conquistado: de tempos em tempos surge um esqueleto (ou esqueleto villager) por la. */
    private static void tickConquered(ServerLevel ow, Site s) {
        boolean test = "test".equals(s.kind);
        if (!ow.hasChunkAt(s.center)) {
            return;
        }
        // aldeoes, golems e saqueadores que sobraram (ou voltaram) somem: o lugar agora e dos esqueletos
        for (LivingEntity e : ow.getEntitiesOfClass(LivingEntity.class, area(s, 20), SkeletonWar::isDefender)) {
            e.discard();
        }
        if (!test && ow.getNearestPlayer(s.center.getX(), s.center.getY(), s.center.getZ(), 128.0D, false) == null) {
            return;
        }
        List<Mob> residents = ow.getEntitiesOfClass(Mob.class, area(s, 8),
                m -> m.isAlive() && SkeletonFaction.isFaction(m) && !(m instanceof SkeletonKing)
                        && !m.getTags().contains(SkeletonFaction.WAR_TAG));
        if (residents.size() >= MAX_RESIDENTS - 1 || ow.random.nextFloat() > 0.6F) {
            return;
        }
        // eles vivem em grupos: surgem de 2 a 4 juntos
        double angle = ow.random.nextDouble() * Math.PI * 2.0D;
        double r = 6.0D + ow.random.nextDouble() * 18.0D;
        double cx = s.center.getX() + 0.5D + Math.cos(angle) * r;
        double cz = s.center.getZ() + 0.5D + Math.sin(angle) * r;
        int group = Math.min(2 + ow.random.nextInt(3), MAX_RESIDENTS - residents.size());
        for (int i = 0; i < group; i++) {
            spawnResident(ow, s, cx + (ow.random.nextDouble() - 0.5D) * 6.0D, cz + (ow.random.nextDouble() - 0.5D) * 6.0D);
        }
    }

    // ============================================================== util
    private static void broadcast(MinecraftServer server, Component message) {
        server.getPlayerList().broadcastSystemMessage(message, false);
    }

    public static void stopAll(ServerLevel ow, boolean everything) {
        Data data = Data.get(ow);
        for (Site s : new ArrayList<>(data.sites)) {
            if (everything || s.active()) {
                setForced(ow, s, false);
                for (AbstractSkeleton a : attackers(ow, s)) {
                    a.kill();
                }
                clearVehicles(ow, s, true);
                data.sites.remove(s);
            }
        }
        data.setDirty();
    }
}
