package com.netheritesupremo.parasite;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.registry.ModEntities;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Rituais dos infectados. Um Jogador Parasita (sempre o lider) junta esqueletos, endermans e outros jogadores
 * parasitas por perto, que formam uma roda em volta dele e dancam girando. No meio do ritual o lider sacrifica um mob
 * que estiver por perto (raio e relampago) e todos ganham forca e regeneracao. Durante o ritual os participantes demoram
 * bem mais para perceber qualquer alvo (so notam depois de varias tentativas seguidas), a nao ser que sejam atacados.
 * O estado dos rituais fica so na memoria (nao e salvo no mundo).
 */
@Mod.EventBusSubscriber(modid = NetheriteSupremo.MODID)
public final class ParasiteRituals {
    public static final String NOTICE_TAG = "RitualNotice";
    private static final String COOLDOWN_TAG = "RitualCooldownUntil";
    /** Tentativas de mirar (uma a cada ~10 ticks) que um participante precisa ter ate notar um alvo: ~7 s. */
    private static final int NOTICE_NEEDED = 15;
    private static final int MIN_OTHERS = 3;
    private static final int MAX_OTHERS = 8;
    private static final int MAX_RITUALS = 4;
    private static final int SACRIFICE_TICKS = 120;

    public static final class Ritual {
        final ServerLevel level;
        final UUID leader;
        public final Vec3 center;
        final List<UUID> members = new ArrayList<>();
        final Map<UUID, Vec3> slots = new HashMap<>();
        int age;
        final int duration;
        int victimState;
        UUID victim;
        int victimStart;

        Ritual(ServerLevel level, UUID leader, Vec3 center, int duration) {
            this.level = level;
            this.leader = leader;
            this.center = center;
            this.duration = duration;
        }

        Vec3 altar() {
            return this.center.add(0.0D, 0.0D, 2.0D);
        }
    }

    private static final List<Ritual> RITUALS = new ArrayList<>();
    private static final Map<UUID, Ritual> BY_MEMBER = new HashMap<>();

    private ParasiteRituals() {
    }

    public static boolean isRitualist(Mob mob) {
        return BY_MEMBER.containsKey(mob.getUUID());
    }

    public static int activeCount() {
        return RITUALS.size();
    }

    /** Onde o participante deve ficar agora (lider: no centro). */
    public static Vec3 slotOf(Mob mob) {
        Ritual r = BY_MEMBER.get(mob.getUUID());
        if (r == null) {
            return null;
        }
        return mob.getUUID().equals(r.leader) ? r.center : r.slots.get(mob.getUUID());
    }

    public static Vec3 centerOf(Mob mob) {
        Ritual r = BY_MEMBER.get(mob.getUUID());
        return r == null ? null : r.center;
    }

    private static boolean isRitualType(Mob m) {
        return m instanceof ParasitePlayer || m instanceof EnderMan && m instanceof ParasiteMob
                || m instanceof AbstractSkeleton && m instanceof ParasiteMob;
    }

    private static boolean hurtRecently(Mob m) {
        return m.getLastHurtByMob() != null && m.tickCount - m.getLastHurtByMobTimestamp() < 60;
    }

    // ------------------------------------------------------------------ inicio
    /** Tenta iniciar um ritual com este lider; minOthers = quantos participantes sao necessarios alem dele. */
    public static boolean tryStart(ServerLevel level, ParasitePlayer leader, int minOthers) {
        if (BY_MEMBER.containsKey(leader.getUUID()) || RITUALS.size() >= MAX_RITUALS) {
            return false;
        }
        List<Mob> cands = level.getEntitiesOfClass(Mob.class, leader.getBoundingBox().inflate(24.0D),
                m -> m != leader && m.isAlive() && isRitualType(m) && m.getTarget() == null && !BY_MEMBER.containsKey(m.getUUID()));
        if (cands.size() < minOthers) {
            return false;
        }
        cands.sort(Comparator.comparingDouble(leader::distanceToSqr));
        Ritual r = new Ritual(level, leader.getUUID(), leader.position(), 1200 + level.random.nextInt(1200));
        BY_MEMBER.put(leader.getUUID(), r);
        for (int i = 0; i < cands.size() && i < MAX_OTHERS; i++) {
            r.members.add(cands.get(i).getUUID());
            BY_MEMBER.put(cands.get(i).getUUID(), r);
        }
        RITUALS.add(r);
        level.playSound(null, leader.getX(), leader.getY(), leader.getZ(), SoundEvents.WARDEN_ROAR, SoundSource.HOSTILE, 1.0F, 0.6F);
        return true;
    }

    /** Usado pelo comando de teste: inicia um ritual com o Jogador Parasita mais proximo do ponto dado. */
    public static String forceStart(ServerLevel level, Vec3 at) {
        List<ParasitePlayer> leaders = level.getEntitiesOfClass(ParasitePlayer.class, new AABB(at, at).inflate(64.0D), Mob::isAlive);
        if (leaders.isEmpty()) {
            return "nenhum Jogador Parasita perto";
        }
        leaders.sort(Comparator.comparingDouble(p -> p.distanceToSqr(at)));
        return tryStart(level, leaders.get(0), 1) ? "ritual iniciado" : "sem participantes suficientes";
    }

    private static void tryStartAll(MinecraftServer server) {
        for (ServerLevel level : server.getAllLevels()) {
            if (level.dimension() == Level.END) {
                continue;
            }
            List<ParasitePlayer> leaders = new ArrayList<>();
            for (Entity e : level.getAllEntities()) {
                if (e instanceof ParasitePlayer p && p.isAlive() && p.getTarget() == null && !BY_MEMBER.containsKey(p.getUUID())
                        && level.getGameTime() >= p.getPersistentData().getLong(COOLDOWN_TAG)) {
                    leaders.add(p);
                }
            }
            for (ParasitePlayer p : leaders) {
                if (level.random.nextInt(4) == 0) {
                    tryStart(level, p, MIN_OTHERS);
                }
            }
        }
    }

    // ------------------------------------------------------------------ andamento
    private static double groundY(ServerLevel level, double x, double y, double z) {
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(Math.floor(x), Math.floor(y) + 2, Math.floor(z));
        for (int i = 0; i < 7; i++) {
            boolean solid = !level.getBlockState(p).getCollisionShape(level, p).isEmpty();
            if (solid && level.getBlockState(p.above()).getCollisionShape(level, p.above()).isEmpty()
                    && level.getBlockState(p.above(2)).getCollisionShape(level, p.above(2)).isEmpty()) {
                return p.getY() + 1.0D;
            }
            p.move(0, -1, 0);
        }
        return y;
    }

    private static void end(Ritual r) {
        Entity le = r.level.getEntity(r.leader);
        if (le != null) {
            le.getPersistentData().putLong(COOLDOWN_TAG, r.level.getGameTime() + 4800L);
        }
        BY_MEMBER.remove(r.leader);
        if (le != null) {
            le.getPersistentData().remove(NOTICE_TAG);
        }
        for (UUID id : r.members) {
            BY_MEMBER.remove(id);
            Entity e = r.level.getEntity(id);
            if (e != null) {
                e.getPersistentData().remove(NOTICE_TAG);
            }
        }
    }

    /** @return false quando o ritual terminou. */
    private static boolean update(Ritual r) {
        ServerLevel level = r.level;
        if (!(level.getEntity(r.leader) instanceof ParasitePlayer leader) || !leader.isAlive() || leader.getTarget() != null
                || hurtRecently(leader)) {
            end(r);
            return false;
        }
        r.members.removeIf(id -> {
            boolean bad = !(level.getEntity(id) instanceof Mob m) || !m.isAlive() || m.getTarget() != null || hurtRecently(m)
                    || m.distanceToSqr(r.center) > 40.0D * 40.0D;
            if (bad) {
                BY_MEMBER.remove(id);
                r.slots.remove(id);
                Entity e = level.getEntity(id);
                if (e != null) {
                    e.getPersistentData().remove(NOTICE_TAG);
                }
            }
            return bad;
        });
        if (r.members.size() < 2 || r.age >= r.duration) {
            end(r);
            return false;
        }
        r.age += 5;
        // roda: os participantes ocupam posicoes em circulo que giram devagar (a "danca")
        int n = r.members.size();
        double radius = 3.5D + 0.3D * n;
        for (int i = 0; i < n; i++) {
            double a = i * (Math.PI * 2.0D / n) + r.age * 0.03D;
            double x = r.center.x + Math.cos(a) * radius;
            double z = r.center.z + Math.sin(a) * radius;
            r.slots.put(r.members.get(i), new Vec3(x, groundY(level, x, r.center.y, z), z));
        }
        // efeitos
        level.sendParticles(ParticleTypes.CRIMSON_SPORE, r.center.x, r.center.y + 0.3D, r.center.z, 6, 0.8D, 0.1D, 0.8D, 0.02D);
        for (UUID id : r.members) {
            if (level.getEntity(id) instanceof Mob m && level.random.nextInt(3) == 0) {
                level.sendParticles(ParticleTypes.SOUL, m.getX(), m.getY() + m.getBbHeight(), m.getZ(), 1, 0.2D, 0.2D, 0.2D, 0.01D);
            }
        }
        if (r.age % 20 == 0) {
            level.playSound(null, r.center.x, r.center.y, r.center.z, SoundEvents.WARDEN_HEARTBEAT, SoundSource.HOSTILE, 1.2F, 0.7F);
        }
        sacrifice(r, leader);
        return true;
    }

    private static Mob pickVictim(Ritual r) {
        List<Mob> l = r.level.getEntitiesOfClass(Mob.class, new AABB(r.center, r.center).inflate(32.0D),
                m -> m.isAlive() && !(m instanceof ParasiteMob) && !m.hasCustomName() && m.getMaxHealth() <= 100.0F
                        && !(m instanceof TamableAnimal t && t.isTame()) && !m.isPassenger() && !m.isLeashed());
        l.sort(Comparator.comparingDouble(m -> m.distanceToSqr(r.center)));
        return l.isEmpty() ? null : l.get(0);
    }

    private static void sacrifice(Ritual r, ParasitePlayer leader) {
        ServerLevel level = r.level;
        if (r.victimState == 0 && r.age >= r.duration / 2) {
            Mob v = pickVictim(r);
            r.victimState = v == null ? 2 : 1;
            if (v != null) {
                r.victim = v.getUUID();
                r.victimStart = r.age;
            }
        }
        if (r.victimState != 1) {
            return;
        }
        Vec3 altar = r.altar();
        if (!(level.getEntity(r.victim) instanceof Mob v) || !v.isAlive()) {
            r.victimState = 2;
            return;
        }
        v.getNavigation().stop();
        v.setTarget(null);
        v.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 6, false, false, false));
        if (v.position().distanceToSqr(altar) > 0.5D) {
            v.moveTo(altar.x, altar.y, altar.z, v.getYRot(), 0.0F);
        }
        leader.getLookControl().setLookAt(altar.x, altar.y + 1.0D, altar.z);
        level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, altar.x, altar.y + 0.5D, altar.z, 5, 0.4D, 0.6D, 0.4D, 0.02D);
        if (r.age - r.victimStart < SACRIFICE_TICKS) {
            return;
        }
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        if (bolt != null) {
            bolt.moveTo(altar.x, altar.y, altar.z);
            bolt.setVisualOnly(true);
            level.addFreshEntity(bolt);
        }
        v.hurt(level.damageSources().magic(), 10000.0F);
        r.victimState = 2;
        // recompensa: forca e regeneracao para todos, vermes e uma poca de sangue no altar
        List<Mob> all = new ArrayList<>();
        all.add(leader);
        for (UUID id : r.members) {
            if (level.getEntity(id) instanceof Mob m) {
                all.add(m);
            }
        }
        for (Mob m : all) {
            m.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 0));
            m.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 1));
        }
        for (int i = 0; i < 2; i++) {
            ParasiteWorm worm = ModEntities.PARASITE_WORM.get().create(level);
            if (worm != null) {
                worm.moveTo(altar.x + level.random.nextDouble() - 0.5D, altar.y + 0.2D, altar.z + level.random.nextDouble() - 0.5D,
                        level.random.nextFloat() * 360.0F, 0.0F);
                level.addFreshEntity(worm);
            }
        }
        BlockPos ap = BlockPos.containing(altar);
        if (ForgeEventFactory.getMobGriefingEvent(level, leader) && level.getBlockState(ap).isAir()
                && level.getBlockState(ap.below()).isFaceSturdy(level, ap.below(), net.minecraft.core.Direction.UP)) {
            level.setBlock(ap, ParasiteBlood.BLOCK.get().defaultBlockState(), 3);
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        int tick = event.getServer().getTickCount();
        if (tick % 5 == 0 && !RITUALS.isEmpty()) {
            Iterator<Ritual> it = RITUALS.iterator();
            while (it.hasNext()) {
                if (!update(it.next())) {
                    it.remove();
                }
            }
        }
        if (tick % 100 == 50) {
            tryStartAll(event.getServer());
        }
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        RITUALS.clear();
        BY_MEMBER.clear();
    }

    /** Durante o ritual o participante so "percebe" um alvo depois de varias tentativas; se foi ferido, percebe na hora. */
    @SubscribeEvent
    public static void onChangeTarget(LivingChangeTargetEvent event) {
        if (!(event.getEntity() instanceof Mob mob) || mob.level().isClientSide) {
            return;
        }
        LivingEntity nt = event.getNewTarget();
        if (nt == null || !isRitualist(mob) || ParasiteBehavior.isParasitic(nt)) {
            return;
        }
        if (hurtRecently(mob) && mob.getLastHurtByMob() == nt) {
            return;
        }
        int n = mob.getPersistentData().getInt(NOTICE_TAG) + 1;
        if (n < NOTICE_NEEDED) {
            mob.getPersistentData().putInt(NOTICE_TAG, n);
            event.setCanceled(true);
        } else {
            mob.getPersistentData().remove(NOTICE_TAG);
        }
    }

    // ------------------------------------------------------------------ objetivo
    /** Faz o participante ir ate seu lugar na roda, olhar para o centro e dancar (pulos e bracos). */
    public static class RitualGoal extends Goal {
        private final Mob mob;
        private int repath;

        public RitualGoal(Mob mob) {
            this.mob = mob;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            return this.mob.getTarget() == null && isRitualist(this.mob);
        }

        @Override
        public boolean canContinueToUse() {
            return this.canUse();
        }

        @Override
        public void stop() {
            this.mob.getNavigation().stop();
        }

        @Override
        public void tick() {
            Vec3 slot = slotOf(this.mob);
            Vec3 center = centerOf(this.mob);
            if (slot == null || center == null) {
                return;
            }
            boolean leader = this.mob instanceof ParasitePlayer && slot == center;
            double dx = slot.x - this.mob.getX();
            double dz = slot.z - this.mob.getZ();
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > 1.2D) {
                if (--this.repath <= 0) {
                    this.repath = 4;
                    this.mob.getNavigation().moveTo(slot.x, slot.y, slot.z, d > 8.0D ? 1.3D : 1.0D);
                }
            } else {
                this.mob.getNavigation().stop();
            }
            this.mob.getLookControl().setLookAt(center.x, center.y + (leader ? 6.0D : 1.0D), center.z);
            if (this.mob.onGround() && this.mob.getRandom().nextInt(40) == 0) {
                this.mob.getJumpControl().jump();
            }
            if (this.mob.getRandom().nextInt(25) == 0) {
                this.mob.swing(InteractionHand.MAIN_HAND);
            }
        }
    }
}
