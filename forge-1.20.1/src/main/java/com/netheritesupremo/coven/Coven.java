package com.netheritesupremo.coven;

import com.netheritesupremo.NetheriteSupremo;
import com.netheritesupremo.parasite.BiomePurifierBlock;
import com.netheritesupremo.parasite.ParasiteBlaze;
import com.netheritesupremo.parasite.ParasiteBomb;
import com.netheritesupremo.parasite.ParasiteBomber;
import com.netheritesupremo.parasite.ParasiteGnat;
import com.netheritesupremo.parasite.ParasiteHead;
import com.netheritesupremo.parasite.ParasiteRooter;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registros do lote "covil" (criaturas, maldicoes, bola de cristal, vassoura, protecao magica; inspirados no Bewitchment)
 * e dos novos parasitas (inspirados no Scape and Run: Parasites). Codigo, modelos e texturas proprios.
 */
public final class Coven {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, NetheriteSupremo.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, NetheriteSupremo.MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, NetheriteSupremo.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, NetheriteSupremo.MODID);

    // ------------------------------------------------------------------ criaturas do covil
    public static final RegistryObject<EntityType<CovenMob>> IMP = mob("imp", CovenMob.Kind.IMP, 0.5F, 1.1F, 0x8B1A10, 0xFF8A20);
    public static final RegistryObject<EntityType<CovenMob>> WEREWOLF = mob("werewolf", CovenMob.Kind.WEREWOLF, 0.7F, 2.1F, 0x3B2A1E, 0xD8C080);
    public static final RegistryObject<EntityType<CovenMob>> SHADOW_PERSON = mob("shadow_person", CovenMob.Kind.SHADOW_PERSON, 0.6F, 1.95F, 0x050507, 0xF0F0F0);
    public static final RegistryObject<EntityType<CovenMob>> GHOST = mob("ghost", CovenMob.Kind.GHOST, 0.6F, 1.9F, 0xB8C8E0, 0xFFFFFF);
    public static final RegistryObject<EntityType<CovenMob>> DEMON = mob("demon", CovenMob.Kind.DEMON, 0.9F, 2.9F, 0x500000, 0xFF3010);
    public static final RegistryObject<EntityType<CovenMob>> CAMBION = mob("cambion", CovenMob.Kind.CAMBION, 0.6F, 1.95F, 0x2A1030, 0xFF6020);
    public static final RegistryObject<EntityType<CovenMob>> SNAKE = mob("snake", CovenMob.Kind.SNAKE, 0.6F, 0.3F, 0x4A7A20, 0xD8D040);

    public static final RegistryObject<EntityType<CovenDog>> BLACK_DOG = ENTITIES.register("black_dog",
            () -> EntityType.Builder.<CovenDog>of(CovenDog::new, MobCategory.MONSTER).sized(0.6F, 0.85F).clientTrackingRange(10).build("black_dog"));
    public static final RegistryObject<EntityType<CovenDog>> HELLHOUND = ENTITIES.register("hellhound",
            () -> EntityType.Builder.<CovenDog>of(CovenDog::new, MobCategory.MONSTER).sized(0.6F, 0.85F).fireImmune().clientTrackingRange(10).build("hellhound"));
    public static final RegistryObject<EntityType<CovenBird>> RAVEN = ENTITIES.register("raven",
            () -> EntityType.Builder.<CovenBird>of(CovenBird::new, MobCategory.CREATURE).sized(0.5F, 0.9F).clientTrackingRange(8).build("raven"));
    public static final RegistryObject<EntityType<CovenBird>> OWL = ENTITIES.register("owl",
            () -> EntityType.Builder.<CovenBird>of(CovenBird::new, MobCategory.CREATURE).sized(0.6F, 1.0F).clientTrackingRange(8).build("owl"));
    public static final RegistryObject<EntityType<CovenToad>> TOAD = ENTITIES.register("toad",
            () -> EntityType.Builder.<CovenToad>of(CovenToad::new, MobCategory.CREATURE).sized(0.5F, 0.5F).clientTrackingRange(8).build("toad"));
    public static final RegistryObject<EntityType<Broom>> BROOM = ENTITIES.register("broom",
            () -> EntityType.Builder.<Broom>of(Broom::new, MobCategory.MISC).sized(1.2F, 0.3F).clientTrackingRange(10).build("broom"));

    // ------------------------------------------------------------------ novos parasitas
    public static final RegistryObject<EntityType<ParasiteHead>> PARASITE_HEAD = ENTITIES.register("parasite_head",
            () -> EntityType.Builder.<ParasiteHead>of(ParasiteHead::new, MobCategory.MONSTER).sized(0.6F, 0.6F).clientTrackingRange(8).build("parasite_head"));
    public static final RegistryObject<EntityType<ParasiteGnat>> PARASITE_GNAT = ENTITIES.register("parasite_gnat",
            () -> EntityType.Builder.<ParasiteGnat>of(ParasiteGnat::new, MobCategory.MONSTER).sized(0.4F, 0.4F).clientTrackingRange(8).build("parasite_gnat"));
    public static final RegistryObject<EntityType<ParasiteBomber>> PARASITE_BOMBER = ENTITIES.register("parasite_bomber",
            () -> EntityType.Builder.<ParasiteBomber>of(ParasiteBomber::new, MobCategory.MONSTER).sized(1.2F, 0.6F).clientTrackingRange(10).build("parasite_bomber"));
    public static final RegistryObject<EntityType<ParasiteBlaze>> PARASITE_BLAZE = ENTITIES.register("parasite_blaze",
            () -> EntityType.Builder.<ParasiteBlaze>of(ParasiteBlaze::new, MobCategory.MONSTER).sized(0.6F, 1.8F).fireImmune().clientTrackingRange(10).build("parasite_blaze"));
    public static final RegistryObject<EntityType<ParasiteRooter>> PARASITE_ROOTER = ENTITIES.register("parasite_rooter",
            () -> EntityType.Builder.<ParasiteRooter>of(ParasiteRooter::new, MobCategory.MONSTER).sized(0.9F, 2.6F).clientTrackingRange(10).build("parasite_rooter"));
    public static final RegistryObject<EntityType<ParasiteBomb>> PARASITE_BOMB = ENTITIES.register("parasite_bomb",
            () -> EntityType.Builder.<ParasiteBomb>of(ParasiteBomb::new, MobCategory.MISC).sized(0.3F, 0.3F).clientTrackingRange(4).updateInterval(10).build("parasite_bomb"));

    // ------------------------------------------------------------------ itens e blocos
    public static final RegistryObject<Item> BROOM_ITEM = ITEMS.register("broom", () -> new BroomItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Block> CRYSTAL_BALL = BLOCKS.register("crystal_ball",
            () -> new CrystalBallBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(1.0F)
                    .sound(SoundType.GLASS).noOcclusion().lightLevel(s -> 6)));
    public static final RegistryObject<Item> CRYSTAL_BALL_ITEM = ITEMS.register("crystal_ball",
            () -> new BlockItem(CRYSTAL_BALL.get(), new Item.Properties()));

    public static final RegistryObject<Block> BIOME_PURIFIER = BLOCKS.register("biome_purifier",
            () -> new BiomePurifierBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GREEN).strength(3.0F)
                    .sound(SoundType.AMETHYST).lightLevel(s -> 12)));
    public static final RegistryObject<Item> BIOME_PURIFIER_ITEM = ITEMS.register("biome_purifier",
            () -> new BlockItem(BIOME_PURIFIER.get(), new Item.Properties()));

    // ------------------------------------------------------------------ efeitos e encantamento
    public static final RegistryObject<MobEffect> CURSE_SOLAR_HATRED = EFFECTS.register("curse_solar_hatred", () -> new CurseEffect(CurseEffect.Type.SOLAR_HATRED, 0xE0B020));
    public static final RegistryObject<MobEffect> CURSE_HYDROPHOBIA = EFFECTS.register("curse_hydrophobia", () -> new CurseEffect(CurseEffect.Type.HYDROPHOBIA, 0x2060C0));
    public static final RegistryObject<MobEffect> CURSE_OVERHEATING = EFFECTS.register("curse_overheating", () -> new CurseEffect(CurseEffect.Type.OVERHEATING, 0xD04010));
    public static final RegistryObject<MobEffect> CURSE_LIGHTNING_ROD = EFFECTS.register("curse_lightning_rod", () -> new CurseEffect(CurseEffect.Type.LIGHTNING_ROD, 0xB0C8FF));
    public static final RegistryObject<MobEffect> CURSE_DARKNESS = EFFECTS.register("curse_darkness", () -> new CurseEffect(CurseEffect.Type.DARKNESS, 0x181020));
    public static final RegistryObject<MobEffect> CURSE_MISFORTUNE = EFFECTS.register("curse_misfortune", () -> new CurseEffect(CurseEffect.Type.MISFORTUNE, 0x507030));
    public static final RegistryObject<MobEffect> CURSE_PARANOIA = EFFECTS.register("curse_paranoia", () -> new CurseEffect(CurseEffect.Type.PARANOIA, 0x806090));

    public static final RegistryObject<Enchantment> MAGIC_PROTECTION = ENCHANTMENTS.register("magic_protection", MagicProtectionEnchantment::new);

    // ------------------------------------------------------------------ ovos
    public static final RegistryObject<Item> IMP_EGG = egg("imp", IMP);
    public static final RegistryObject<Item> WEREWOLF_EGG = egg("werewolf", WEREWOLF);
    public static final RegistryObject<Item> SHADOW_PERSON_EGG = egg("shadow_person", SHADOW_PERSON);
    public static final RegistryObject<Item> GHOST_EGG = egg("ghost", GHOST);
    public static final RegistryObject<Item> DEMON_EGG = egg("demon", DEMON);
    public static final RegistryObject<Item> CAMBION_EGG = egg("cambion", CAMBION);
    public static final RegistryObject<Item> SNAKE_EGG = egg("snake", SNAKE);
    public static final RegistryObject<Item> BLACK_DOG_EGG = ITEMS.register("black_dog_spawn_egg",
            () -> new ForgeSpawnEggItem(BLACK_DOG, 0x0A0A0E, 0xC02020, new Item.Properties()));
    public static final RegistryObject<Item> HELLHOUND_EGG = ITEMS.register("hellhound_spawn_egg",
            () -> new ForgeSpawnEggItem(HELLHOUND, 0x501008, 0xFFA020, new Item.Properties()));
    public static final RegistryObject<Item> RAVEN_EGG = ITEMS.register("raven_spawn_egg",
            () -> new ForgeSpawnEggItem(RAVEN, 0x08080C, 0x606880, new Item.Properties()));
    public static final RegistryObject<Item> OWL_EGG = ITEMS.register("owl_spawn_egg",
            () -> new ForgeSpawnEggItem(OWL, 0x6A4A2A, 0xEAD8B0, new Item.Properties()));
    public static final RegistryObject<Item> TOAD_EGG = ITEMS.register("toad_spawn_egg",
            () -> new ForgeSpawnEggItem(TOAD, 0x5A5230, 0xA08840, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_HEAD_EGG = ITEMS.register("parasite_head_spawn_egg",
            () -> new ForgeSpawnEggItem(PARASITE_HEAD, 0xC8B8A8, 0xB01818, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_GNAT_EGG = ITEMS.register("parasite_gnat_spawn_egg",
            () -> new ForgeSpawnEggItem(PARASITE_GNAT, 0x201818, 0xC02020, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BOMBER_EGG = ITEMS.register("parasite_bomber_spawn_egg",
            () -> new ForgeSpawnEggItem(PARASITE_BOMBER, 0x502028, 0xE0C060, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_BLAZE_EGG = ITEMS.register("parasite_blaze_spawn_egg",
            () -> new ForgeSpawnEggItem(PARASITE_BLAZE, 0x601010, 0xFFC040, new Item.Properties()));
    public static final RegistryObject<Item> PARASITE_ROOTER_EGG = ITEMS.register("parasite_rooter_spawn_egg",
            () -> new ForgeSpawnEggItem(PARASITE_ROOTER, 0x3A1A22, 0xC03040, new Item.Properties()));

    private static RegistryObject<EntityType<CovenMob>> mob(String id, CovenMob.Kind kind, float w, float h, int c1, int c2) {
        return ENTITIES.register(id, () -> {
            EntityType.Builder<CovenMob> b = EntityType.Builder.<CovenMob>of((t, l) -> new CovenMob(t, l, kind), MobCategory.MONSTER)
                    .sized(w, h).clientTrackingRange(10);
            if (kind.fireImmune) {
                b.fireImmune();
            }
            return b.build(id);
        });
    }

    private static RegistryObject<Item> egg(String id, RegistryObject<EntityType<CovenMob>> type) {
        int c1 = switch (id) {
            case "imp" -> 0x8B1A10;
            case "werewolf" -> 0x3B2A1E;
            case "shadow_person" -> 0x050507;
            case "ghost" -> 0xB8C8E0;
            case "demon" -> 0x500000;
            case "cambion" -> 0x2A1030;
            default -> 0x4A7A20;
        };
        int c2 = switch (id) {
            case "imp" -> 0xFF8A20;
            case "werewolf" -> 0xD8C080;
            case "shadow_person" -> 0xF0F0F0;
            case "ghost" -> 0xFFFFFF;
            case "demon" -> 0xFF3010;
            case "cambion" -> 0xFF6020;
            default -> 0xD8D040;
        };
        return ITEMS.register(id + "_spawn_egg", () -> new ForgeSpawnEggItem(type, c1, c2, new Item.Properties()));
    }

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
        ITEMS.register(bus);
        BLOCKS.register(bus);
        EFFECTS.register(bus);
        ENCHANTMENTS.register(bus);
    }

    private Coven() {
    }
}
