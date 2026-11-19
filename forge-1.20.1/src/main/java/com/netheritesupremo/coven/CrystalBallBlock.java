package com.netheritesupremo.coven;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * Bola de Cristal (fortunas do Bewitchment): clicar mostra a sorte do dia. Cada jogador recebe uma fortuna por dia de
 * jogo (24000 ticks); ela acontece na hora: efeitos, tesouro, um novo amigo ou uma visita nada amigavel.
 */
public class CrystalBallBlock extends Block {
    private static final VoxelShape SHAPE = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 13.0D, 13.0D);
    private static final String TAG = "NsFortuneAt";
    private static final String[] FORTUNES = {"bad_luck", "good_luck", "illness", "vitality", "resilience", "courage",
            "treasure", "cornucopia", "meet_pet", "meet_demon", "meet_imps", "meet_shadow_person", "meet_dog",
            "thunderbolt", "meet_werewolf", "curse"};

    public CrystalBallBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
            BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        if (!(player instanceof ServerPlayer sp) || !(level instanceof ServerLevel sl)) {
            return InteractionResult.PASS;
        }
        long now = level.getGameTime();
        long last = sp.getPersistentData().contains(TAG) ? sp.getPersistentData().getLong(TAG) : -100000L;
        if (now - last < 24000L) {
            sp.displayClientMessage(Component.translatable("fortune.netheritesupremo.cloudy"), true);
            return InteractionResult.CONSUME;
        }
        sp.getPersistentData().putLong(TAG, now);
        int index = level.random.nextInt(FORTUNES.length);
        String key = FORTUNES[index];
        sl.sendParticles(ParticleTypes.ENCHANT, pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D, 40, 0.4D, 0.4D, 0.4D, 0.5D);
        sl.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 1.5F, 0.8F);
        sp.sendSystemMessage(Component.translatable("fortune.netheritesupremo." + key));
        apply(key, sp, sl, pos);
        return InteractionResult.CONSUME;
    }

    private static void apply(String key, ServerPlayer player, ServerLevel level, BlockPos pos) {
        switch (key) {
            case "bad_luck" -> player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 12000, 1));
            case "good_luck" -> player.addEffect(new MobEffectInstance(MobEffects.LUCK, 12000, 1));
            case "illness" -> {
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 3600, 0));
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 1200, 0));
            }
            case "vitality" -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
            }
            case "resilience" -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800, 1));
            case "courage" -> player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 0));
            case "treasure" -> {
                drop(level, pos, new ItemStack(Items.GOLD_INGOT, 6 + level.random.nextInt(10)));
                drop(level, pos, new ItemStack(Items.EMERALD, 2 + level.random.nextInt(5)));
                if (level.random.nextInt(3) == 0) {
                    drop(level, pos, new ItemStack(Items.DIAMOND, 1 + level.random.nextInt(2)));
                }
            }
            case "cornucopia" -> {
                drop(level, pos, new ItemStack(Items.GOLDEN_CARROT, 4));
                drop(level, pos, new ItemStack(Items.COOKED_BEEF, 8));
                drop(level, pos, new ItemStack(Items.BREAD, 8));
            }
            case "meet_pet" -> {
                Mob pet = spawn(level, level.random.nextBoolean() ? Coven.OWL.get() : Coven.RAVEN.get(), pos);
                if (pet instanceof TamableAnimal tame) {
                    tame.tame(player);
                }
            }
            case "meet_demon" -> spawn(level, Coven.DEMON.get(), pos.offset(0, 0, 6));
            case "meet_imps" -> {
                spawn(level, Coven.IMP.get(), pos);
                spawn(level, Coven.IMP.get(), pos);
                spawn(level, Coven.CAMBION.get(), pos);
            }
            case "meet_shadow_person" -> spawn(level, Coven.SHADOW_PERSON.get(), pos);
            case "meet_dog" -> spawn(level, Coven.BLACK_DOG.get(), pos);
            case "meet_werewolf" -> spawn(level, Coven.WEREWOLF.get(), pos);
            case "thunderbolt" -> {
                LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                if (bolt != null) {
                    bolt.moveTo(Vec3.atBottomCenterOf(player.blockPosition().offset(7, 0, 0)));
                    level.addFreshEntity(bolt);
                }
            }
            case "curse" -> {
                MobEffect[] curses = {Coven.CURSE_MISFORTUNE.get(), Coven.CURSE_PARANOIA.get(),
                        Coven.CURSE_DARKNESS.get(), Coven.CURSE_SOLAR_HATRED.get()};
                player.addEffect(new MobEffectInstance(curses[level.random.nextInt(curses.length)], 2400, 0));
            }
            default -> {
            }
        }
    }

    private static void drop(ServerLevel level, BlockPos pos, ItemStack stack) {
        level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 1.2D, pos.getZ() + 0.5D, stack));
    }

    private static Mob spawn(ServerLevel level, EntityType<? extends Mob> type, BlockPos around) {
        for (int i = 0; i < 12; i++) {
            int x = around.getX() + level.random.nextInt(13) - 6;
            int z = around.getZ() + level.random.nextInt(13) - 6;
            int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z);
            Mob mob = type.create(level);
            if (mob == null) {
                return null;
            }
            mob.moveTo(x + 0.5D, y, z + 0.5D, level.random.nextFloat() * 360.0F, 0.0F);
            if (level.noCollision(mob)) {
                level.addFreshEntity(mob);
                return mob;
            }
            mob.discard();
        }
        return null;
    }
}
