package com.netheritesupremo.invasion;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.SupremoConfig;
import com.netheritesupremo.entity.AlienShip;
import com.netheritesupremo.entity.Precursor;
import com.netheritesupremo.entity.PrecursorSoldier;
import com.netheritesupremo.entity.PrecursorVillager;
import com.netheritesupremo.registry.ModEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * Invasao alienigena. Gatilhos (chance de comecar): matar guardiao/guardiao ancião 10%, entrar em Plutao 30%,
 * matar aldeao Precursor 60%, matar o boss Precursor 100%. A invasao tem 6 ondas (robos hostis reforcados,
 * soldados Precursores e naves que destroem casas) e termina com o boss Precursor. Os alienigenas vencem se
 * matarem todos os aldeoes rastreados da vila e o jogador 3 vezes; se perderem, deixam um bau de oferta de paz.
 */
public class InvasionManager {
    public static final String TAG = "netheritesupremo_invader";
    public static final int WAVES = 6;
    public static final int DEATHS_TO_WIN = 3;
    private static final ResourceKey<Level> PLUTO =
            ResourceKey.create(Registries.DIMENSION, new ResourceLocation("sistemasolar", "plutao"));
    private static final java.util.UUID BUFF_HEALTH = UUID.fromString("c3a1f2e0-6a11-4d0b-9d52-0a5c1e7b7701");
    private static final java.util.UUID BUFF_DAMAGE = UUID.fromString("c3a1f2e0-6a11-4d0b-9d52-0a5c1e7b7702");

    // ------------------------------------------------------------------ gatilhos
    /** Sorteia uma invasao com a chance dada (0-100). */
    public static boolean roll(ServerPlayer player, int chance) {
        if (!SupremoConfig.INVASION_ENABLED.get() || player.getServer() == null) {
            return false;
        }
        ServerLevel ow = player.getServer().overworld();
        InvasionData data = InvasionData.get(ow);
        if (data.aliensWon || data.active || data.pending != null || player.getRandom().nextInt(100) >= chance) {
            return false;
        }
        data.pending = player.getUUID();
        data.pendingAt = ow.getGameTime() + 600;
        data.setDirty();
        player.sendSystemMessage(Component.literal("§c⚠ Sinais de uma invasão alienígena detectados! Eles chegam em 30 segundos..."));
        return true;
    }

    @SubscribeEvent
    public void onDeath(LivingDeathEvent event) {
        LivingEntity dead = event.getEntity();
        if (!(dead.level() instanceof ServerLevel level) || level.getServer() == null
                || !SupremoConfig.INVASION_ENABLED.get()) {
            return;
        }
        ServerLevel ow = level.getServer().overworld();
        InvasionData data = InvasionData.get(ow);
        if (data.active) {
            if (dead instanceof ServerPlayer sp && sp.getUUID().equals(data.target)) {
                data.deaths++;
                data.setDirty();
                sp.sendSystemMessage(Component.literal("§cInvasão: você morreu " + data.deaths + "/" + DEATHS_TO_WIN + " vezes."));
                checkWin(ow, data);
            } else if (dead instanceof Villager && data.villagers.remove(dead.getUUID())) {
                data.setDirty();
                checkWin(ow, data);
            } else if (dead.getUUID().equals(data.boss)) {
                lose(ow, data);
            }
        } else if (!data.aliensWon && event.getSource().getEntity() instanceof ServerPlayer killer) {
            if (dead instanceof Guardian || dead instanceof ElderGuardian) {
                roll(killer, 10);
            } else if (dead instanceof PrecursorVillager) {
                roll(killer, 60);
            } else if (dead instanceof Precursor) {
                roll(killer, 100);
            }
        }
    }

    @SubscribeEvent
    public void onDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getTo().equals(PLUTO) && event.getEntity() instanceof ServerPlayer sp) {
            roll(sp, 30);
        }
    }

    // ---------------------------------------------------------------------- tick
    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = event.getServer();
        if (event.phase != TickEvent.Phase.END || server.getTickCount() % 20 != 0 || !SupremoConfig.INVASION_ENABLED.get()) {
            return;
        }
        ServerLevel ow = server.overworld();
        InvasionData data = InvasionData.get(ow);
        if (data.pending != null && ow.getGameTime() >= data.pendingAt) {
            ServerPlayer p = server.getPlayerList().getPlayer(data.pending);
            if (p != null && p.level() == ow) {
                data.pending = null;
                start(ow, data, p, p.blockPosition(), false);
            }
        }
        if (data.active) {
            tickActive(ow, data);
        } else if (data.aliensWon) {
            tickWon(ow, data, server);
        }
    }

    /** Comeca a invasao. 'test' roda sem jogador (ondas rapidas, usado nos testes/comandos). */
    public static void start(ServerLevel ow, InvasionData data, ServerPlayer player, BlockPos center, boolean test) {
        data.active = true;
        data.test = test;
        data.target = player == null ? null : player.getUUID();
        data.center = center;
        data.home = center;
        if (player != null) {
            BlockPos respawn = player.getRespawnPosition();
            if (respawn != null && player.getRespawnDimension() == Level.OVERWORLD) {
                data.home = respawn;
            }
        }
        data.wave = 0;
        data.nextWave = ow.getGameTime() + (test ? 20 : 200);
        data.bossSpawned = false;
        data.boss = null;
        data.deaths = 0;
        data.villagers.clear();
        for (Villager v : ow.getEntitiesOfClass(Villager.class, new AABB(center).inflate(80.0D))) {
            data.villagers.add(v.getUUID());
        }
        data.setDirty();
        ow.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§4§l🛸 INVASÃO ALIENÍGENA! §cRobôs, soldados e naves estão chegando. Proteja os aldeões!"), false);
        ow.getServer().getPlayerList().getPlayers().forEach(p -> p.playNotifySound(SoundEvents.RAID_HORN.get(), SoundSource.HOSTILE, 4.0F, 0.8F));
    }

    private static void tickActive(ServerLevel ow, InvasionData data) {
        MinecraftServer server = ow.getServer();
        ServerPlayer player = data.target == null ? null : server.getPlayerList().getPlayer(data.target);
        if (!data.test && (player == null || player.level() != ow)) {
            return; // pausa enquanto o alvo esta fora do Overworld
        }
        long now = ow.getGameTime();
        BlockPos anchor = player != null && player.level() == ow ? player.blockPosition() : data.center;
        List<Entity> invaders = invaders(ow);
        if (checkWin(ow, data)) {
            return;
        }
        if (data.wave < WAVES && now >= data.nextWave) {
            data.wave++;
            data.nextWave = now + (data.test ? 60 : 600);
            data.setDirty();
            if (invaders.size() < 70) {
                spawnWave(ow, data, anchor, data.wave);
            }
            server.getPlayerList().broadcastSystemMessage(Component.literal("§cInvasão: onda " + data.wave + "/" + WAVES), false);
        } else if (data.wave >= WAVES && !data.bossSpawned && now >= data.nextWave) {
            spawnBoss(ow, data, anchor);
        } else if (data.bossSpawned && now > data.bossTime + 12000) {
            lose(ow, data); // tempo esgotado
            return;
        }
        // invasores ociosos escolhem alvo
        for (Entity e : invaders) {
            if (e instanceof Mob m && m.isAlive() && m.getTarget() == null) {
                LivingEntity t = null;
                if (player != null && player.level() == ow && player.distanceToSqr(m) < 80 * 80 && !(e instanceof PrecursorSoldier)) {
                    t = player;
                }
                if (t == null) {
                    t = ow.getNearestEntity(Villager.class, net.minecraft.world.entity.ai.targeting.TargetingConditions.forCombat().ignoreLineOfSight(),
                            m, m.getX(), m.getY(), m.getZ(), m.getBoundingBox().inflate(60.0D));
                }
                if (t == null && player != null && player.level() == ow) {
                    t = player;
                }
                if (t != null && !(m instanceof Precursor && t instanceof Villager)) {
                    m.setTarget(t);
                }
            }
        }
        if (player != null && server.getTickCount() % 100 == 0) {
            player.displayClientMessage(Component.literal("§cInvasão — onda " + data.wave + "/" + WAVES
                    + " | aldeões vivos: " + data.villagers.size() + " | suas mortes: " + data.deaths + "/" + DEATHS_TO_WIN), true);
        }
    }

    private static boolean checkWin(ServerLevel ow, InvasionData data) {
        if (data.active && data.deaths >= DEATHS_TO_WIN && data.villagers.isEmpty()) {
            win(ow, data);
            return true;
        }
        return false;
    }

    // ------------------------------------------------------------------- ondas
    private static List<Entity> invaders(ServerLevel ow) {
        List<Entity> list = new ArrayList<>();
        for (Entity e : ow.getAllEntities()) {
            if (e.getTags().contains(TAG)) {
                list.add(e);
            }
        }
        return list;
    }

    private static void spawnWave(ServerLevel ow, InvasionData data, BlockPos anchor, int w) {
        int soldiers = 3 + w;
        int bombs = 3 + w;
        int netherbots = w >= 3 ? (w - 1) / 2 : 0;
        long ships = invaders(ow).stream().filter(e -> e instanceof AlienShip).count();
        int shipGoal = Math.min(3, (w + 1) / 2);
        for (int i = 0; i < soldiers; i++) spawn(ow, ModEntities.PRECURSOR_SOLDIER.get(), anchor, false, false);
        for (int i = 0; i < bombs; i++) spawn(ow, ModEntities.BOMB_ROBOT.get(), anchor, true, false);
        for (int i = 0; i < netherbots; i++) spawn(ow, ModEntities.NETHERBOT.get(), anchor, true, false);
        for (long i = ships; i < shipGoal; i++) spawn(ow, ModEntities.ALIEN_SHIP.get(), anchor, false, true);
    }

    private static void spawnBoss(ServerLevel ow, InvasionData data, BlockPos anchor) {
        Entity boss = spawn(ow, ModEntities.PRECURSOR.get(), anchor, false, false);
        data.bossSpawned = true;
        data.bossTime = ow.getGameTime();
        data.boss = boss == null ? null : boss.getUUID();
        data.setDirty();
        ow.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§4§l☠ O PRECURSOR CHEGOU! §cDerrote o chefe para encerrar a invasão."), false);
    }

    private static Entity spawn(ServerLevel ow, EntityType<? extends Mob> type, BlockPos anchor, boolean buff, boolean air) {
        double ang = ow.random.nextDouble() * Math.PI * 2.0D;
        double r = 26.0D + ow.random.nextDouble() * 14.0D;
        int x = anchor.getX() + (int) (Math.cos(ang) * r);
        int z = anchor.getZ() + (int) (Math.sin(ang) * r);
        if (!ow.hasChunkAt(new BlockPos(x, 64, z))) {
            return null;
        }
        int y = ow.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z);
        Mob mob = type.create(ow);
        if (mob == null) {
            return null;
        }
        mob.moveTo(x + 0.5D, air ? y + 28.0D : y + (type == ModEntities.BOMB_ROBOT.get() || type == ModEntities.NETHERBOT.get() ? 3.0D : 0.0D), z + 0.5D,
                ow.random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(ow, ow.getCurrentDifficultyAt(mob.blockPosition()), MobSpawnType.EVENT, null, null);
        mob.setPersistenceRequired();
        mob.addTag(TAG);
        if (buff) {
            applyBuff(mob);
        }
        ow.addFreshEntity(mob);
        return mob;
    }

    /** Robos da invasao: o dobro de vida e 50% mais dano. */
    private static void applyBuff(Mob mob) {
        var hp = mob.getAttribute(Attributes.MAX_HEALTH);
        if (hp != null && hp.getModifier(BUFF_HEALTH) == null) {
            hp.addPermanentModifier(new AttributeModifier(BUFF_HEALTH, "invasion health", 1.0D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        var dmg = mob.getAttribute(Attributes.ATTACK_DAMAGE);
        if (dmg != null && dmg.getModifier(BUFF_DAMAGE) == null) {
            dmg.addPermanentModifier(new AttributeModifier(BUFF_DAMAGE, "invasion damage", 0.5D, AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        mob.setHealth(mob.getMaxHealth());
    }

    // ---------------------------------------------------------------- resultado
    public static void clearInvaders(ServerLevel ow) {
        for (Entity e : invaders(ow)) {
            ow.sendParticles(ParticleTypes.POOF, e.getX(), e.getY() + 1.0D, e.getZ(), 12, 0.4D, 0.6D, 0.4D, 0.02D);
            e.discard();
        }
    }

    public static void win(ServerLevel ow, InvasionData data) {
        clearInvaders(ow);
        data.active = false;
        data.aliensWon = true;
        data.setDirty();
        ow.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§4§l🛸 OS ALIENÍGENAS VENCERAM! §cAgora Aldeões Precursores surgem nas vilas e cidades futuristas vão aparecer."), false);
    }

    public static void lose(ServerLevel ow, InvasionData data) {
        clearInvaders(ow);
        data.active = false;
        data.bossSpawned = false;
        data.boss = null;
        data.setDirty();
        BlockPos chest = placePeaceChest(ow, data.home, false);
        ow.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§a§lA invasão foi derrotada! §aOs alienígenas recuaram e deixaram um baú de oferta de paz em frente à casa do jogador"
                        + (chest == null ? "." : " (" + chest.getX() + ", " + chest.getY() + ", " + chest.getZ() + ").")), false);
    }

    /** Bau de oferta de paz com itens poderosos. 'exact' = no ponto dado; senao procura um lugar livre ao lado da casa. */
    public static BlockPos placePeaceChest(ServerLevel ow, BlockPos home, boolean exact) {
        int[][] offs = exact ? new int[][] {{0, 0}} : new int[][] {{3, 0}, {-3, 0}, {0, 3}, {0, -3}, {4, 4}, {-4, -4}, {5, 0}, {-5, 0}};
        for (int[] o : offs) {
            BlockPos column = home.offset(o[0], 0, o[1]);
            ow.getChunk(column.getX() >> 4, column.getZ() >> 4);
            BlockPos pos = exact ? column : new BlockPos(column.getX(),
                    ow.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, column.getX(), column.getZ()), column.getZ());
            if (!exact && !ow.getBlockState(pos).isAir()) {
                continue;
            }
            Direction facing = Direction.getNearest(home.getX() - pos.getX(), 0, home.getZ() - pos.getZ());
            BlockState state = Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing.getAxis().isHorizontal() ? facing : Direction.NORTH);
            ow.setBlock(pos, state, 3);
            BlockEntity be = ow.getBlockEntity(pos);
            if (be instanceof ChestBlockEntity chest) {
                List<ItemStack> items = peaceItems();
                for (int i = 0; i < items.size() && i < chest.getContainerSize(); i++) {
                    chest.setItem(i, items.get(i));
                }
                chest.setChanged();
            }
            return pos;
        }
        return null;
    }

    private static List<ItemStack> peaceItems() {
        List<ItemStack> l = new ArrayList<>();
        l.add(new ItemStack(Items.NETHERITE_BLOCK, 4));
        l.add(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 16));
        l.add(new ItemStack(Items.TOTEM_OF_UNDYING, 4));
        l.add(new ItemStack(Items.NETHER_STAR, 3));
        l.add(new ItemStack(Items.DIAMOND_BLOCK, 16));
        l.add(new ItemStack(Items.ELYTRA));
        l.add(new ItemStack(Items.BEACON, 2));
        l.add(new ItemStack(Items.EXPERIENCE_BOTTLE, 64));
        l.add(new ItemStack(Items.GOLDEN_CARROT, 64));
        l.add(new ItemStack(Items.ENDER_PEARL, 16));
        for (ItemStack s : new ItemStack[] {new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE),
                new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS)}) {
            s.enchant(Enchantments.ALL_DAMAGE_PROTECTION, 4);
            s.enchant(Enchantments.UNBREAKING, 3);
            s.enchant(Enchantments.MENDING, 1);
            l.add(s);
        }
        ItemStack sword = new ItemStack(Items.NETHERITE_SWORD);
        sword.enchant(Enchantments.SHARPNESS, 5);
        sword.enchant(Enchantments.UNBREAKING, 3);
        sword.enchant(Enchantments.MENDING, 1);
        l.add(sword);
        ItemStack pick = new ItemStack(Items.NETHERITE_PICKAXE);
        pick.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
        pick.enchant(Enchantments.UNBREAKING, 3);
        pick.enchant(Enchantments.MENDING, 1);
        l.add(pick);
        return l;
    }

    // -------------------------------------------------------- depois da vitoria
    private static void tickWon(ServerLevel ow, InvasionData data, MinecraftServer server) {
        long t = server.getTickCount();
        if (t % 600 == 0) {
            for (ServerPlayer p : ow.players()) {
                List<Villager> vs = ow.getEntitiesOfClass(Villager.class, p.getBoundingBox().inflate(64.0D));
                int pv = ow.getEntitiesOfClass(PrecursorVillager.class, p.getBoundingBox().inflate(64.0D)).size();
                if (!vs.isEmpty() && pv < 5) {
                    Villager v = vs.get(ow.random.nextInt(vs.size()));
                    PrecursorVillager pvil = ModEntities.PRECURSOR_VILLAGER.get().create(ow);
                    if (pvil != null) {
                        pvil.moveTo(v.getX() + ow.random.nextInt(5) - 2, v.getY(), v.getZ() + ow.random.nextInt(5) - 2);
                        pvil.finalizeSpawn(ow, ow.getCurrentDifficultyAt(pvil.blockPosition()), MobSpawnType.EVENT, null, null);
                        ow.addFreshEntity(pvil);
                    }
                }
            }
        }
        if (t % 3600 == 0 && data.cities.size() < 5 && !ow.players().isEmpty()) {
            ServerPlayer p = ow.players().get(ow.random.nextInt(ow.players().size()));
            for (int tries = 0; tries < 6; tries++) {
                double ang = ow.random.nextDouble() * Math.PI * 2.0D;
                double r = 180.0D + ow.random.nextDouble() * 100.0D;
                BlockPos c = new BlockPos((int) (p.getX() + Math.cos(ang) * r), 64, (int) (p.getZ() + Math.sin(ang) * r));
                if (placeCity(ow, data, c)) {
                    break;
                }
            }
        }
    }

    /** Gera uma cidade futurista no Overworld (mesmo template da cidade de Plutao). */
    public static boolean placeCity(ServerLevel ow, InvasionData data, BlockPos center) {
        for (long l : data.cities) {
            BlockPos o = BlockPos.of(l);
            if (Math.abs(o.getX() - center.getX()) < 150 && Math.abs(o.getZ() - center.getZ()) < 150) {
                return false;
            }
        }
        Optional<StructureTemplate> template = ow.getStructureManager().get(new ResourceLocation(NetheriteSupremo.MODID, "pluto_city"));
        if (template.isEmpty()) {
            return false;
        }
        int x0 = center.getX() - 30;
        int z0 = center.getZ() - 30;
        for (int cx = x0 >> 4; cx <= (x0 + 60) >> 4; cx++) {
            for (int cz = z0 >> 4; cz <= (z0 + 60) >> 4; cz++) {
                ow.getChunk(cx, cz);
            }
        }
        BlockPos mid = new BlockPos(center.getX(), 64, center.getZ());
        if (ow.getBiome(mid).is(BiomeTags.IS_OCEAN) || ow.getBiome(mid).is(BiomeTags.IS_RIVER)) {
            return false;
        }
        int gy = ow.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, center.getX(), center.getZ());
        BlockPos.MutableBlockPos m = new BlockPos.MutableBlockPos();
        for (int x = x0; x < x0 + 61; x++) {
            for (int z = z0; z < z0 + 61; z++) {
                for (int y = gy; y < gy + 46; y++) {
                    m.set(x, y, z);
                    if (!ow.getBlockState(m).isAir()) {
                        ow.setBlock(m, Blocks.AIR.defaultBlockState(), 2);
                    }
                }
            }
        }
        BlockPos origin = new BlockPos(x0, gy - 5, z0);
        template.get().placeInWorld(ow, origin, origin, new StructurePlaceSettings(), ow.random, 2);
        data.cities.add(center.asLong());
        data.setDirty();
        ow.getServer().getPlayerList().broadcastSystemMessage(Component.literal(
                "§b🏙 Uma cidade futurista alienígena surgiu em " + center.getX() + ", " + center.getZ()), false);
        return true;
    }
}
