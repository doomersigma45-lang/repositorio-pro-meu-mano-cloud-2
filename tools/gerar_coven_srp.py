#!/usr/bin/env python3
"""Gera os assets do lote 1.85.0 (criaturas do covil + novos parasitas): lang, modelos, loot, receitas, texturas.
Texturas desenhadas por codigo (sem copiar arte dos mods de referencia). Rodar da raiz do repositorio."""
import json, os, random
from PIL import Image, ImageDraw

RES = "forge-1.20.1/src/main/resources"
A = f"{RES}/assets/netheritesupremo"
D = f"{RES}/data/netheritesupremo"
NS = "netheritesupremo"

MOBS = {  # id: (en, pt, drops[(item,min,max,chance)])
    "imp": ("Imp", "Imp", [("minecraft:gunpowder", 0, 2, 1), ("minecraft:blaze_powder", 0, 1, 0.5)]),
    "werewolf": ("Werewolf", "Lobisomem", [("minecraft:leather", 1, 3, 1), ("minecraft:bone", 1, 2, 1)]),
    "shadow_person": ("Shadow Person", "Pessoa-Sombra", [("minecraft:coal", 1, 3, 1), ("minecraft:ender_pearl", 1, 1, 0.25)]),
    "ghost": ("Ghost", "Fantasma", [("minecraft:phantom_membrane", 0, 2, 1), ("minecraft:glowstone_dust", 1, 3, 1)]),
    "demon": ("Demon", "Demônio", [("minecraft:blaze_rod", 2, 4, 1), ("minecraft:gold_ingot", 3, 7, 1), ("minecraft:netherite_scrap", 1, 1, 0.5)]),
    "cambion": ("Cambion", "Cambion", [("minecraft:blaze_powder", 1, 3, 1), ("minecraft:gold_nugget", 2, 6, 1)]),
    "snake": ("Snake", "Cobra", [("minecraft:spider_eye", 0, 1, 1), ("minecraft:string", 0, 2, 1)]),
    "black_dog": ("Black Dog", "Cão Negro", [("minecraft:bone", 1, 2, 1), ("minecraft:coal", 0, 2, 1)]),
    "hellhound": ("Hellhound", "Cão Infernal", [("minecraft:bone", 1, 2, 1), ("minecraft:blaze_powder", 0, 2, 1)]),
    "raven": ("Raven", "Corvo", [("minecraft:feather", 1, 2, 1)]),
    "owl": ("Owl", "Coruja", [("minecraft:feather", 1, 2, 1)]),
    "toad": ("Toad", "Sapo", [("minecraft:slime_ball", 0, 2, 1)]),
    "parasite_head": ("Parasite Walking Head", "Cabeça Andante Parasita", [("minecraft:rotten_flesh", 0, 2, 1), ("minecraft:spider_eye", 0, 1, 1)]),
    "parasite_gnat": ("Parasite Gnat", "Mosquito Parasita", []),
    "parasite_bomber": ("Parasite Bomber", "Bombardeiro Parasita", [("minecraft:phantom_membrane", 0, 2, 1), ("minecraft:slime_ball", 1, 3, 1)]),
    "parasite_blaze": ("Parasite Blaze", "Blaze Parasita", [("minecraft:blaze_rod", 0, 1, 1), ("minecraft:slime_ball", 1, 3, 1)]),
    "parasite_rooter": ("Parasite Rooter", "Rooter Parasita", [("minecraft:slime_ball", 2, 5, 1), ("minecraft:rotten_flesh", 1, 3, 1), ("minecraft:bone", 0, 2, 1)]),
}
EXTRA_ENTITY = {"broom": ("Broom", "Vassoura"), "parasite_bomb": ("Parasite Bomb", "Bomba Parasita")}

EFFECTS = {
    "curse_solar_hatred": ("Curse of Solar Hatred", "Maldição do Ódio Solar"),
    "curse_hydrophobia": ("Curse of Hydrophobia", "Maldição da Hidrofobia"),
    "curse_overheating": ("Curse of Overheating", "Maldição do Superaquecimento"),
    "curse_lightning_rod": ("Curse of the Lightning Rod", "Maldição do Para-raios"),
    "curse_darkness": ("Curse of Darkness", "Maldição das Trevas"),
    "curse_misfortune": ("Curse of Misfortune", "Maldição do Infortúnio"),
    "curse_paranoia": ("Curse of Paranoia", "Maldição da Paranoia"),
}

FORTUNES = {
    "cloudy": ("The crystal ball is cloudy. Come back later.", "A bola de cristal está turva. Volte mais tarde."),
    "bad_luck": ("Your future looks dim.", "Seu futuro parece sombrio."),
    "good_luck": ("Your future looks very bright indeed.", "Seu futuro parece muito brilhante."),
    "illness": ("Weakness and misfortune dot your future.", "Fraqueza e infortúnio marcam o seu futuro."),
    "vitality": ("Wounds heal in your time of need.", "Feridas se curam quando você mais precisar."),
    "resilience": ("Skin becomes iron in your time of need.", "A pele vira ferro quando você mais precisar."),
    "courage": ("You will face adversity head on.", "Você enfrentará a adversidade de frente."),
    "treasure": ("You might be very rich, very soon.", "Você pode ficar muito rico, muito em breve."),
    "cornucopia": ("I see... an abundance of food?", "Vejo... fartura de comida?"),
    "meet_pet": ("You will soon encounter a new friend.", "Você logo encontrará um novo amigo."),
    "meet_demon": ("A powerful duke has seen you scrying, and has taken an interest in you.", "Um duque poderoso viu você espiar, e se interessou por você."),
    "meet_imps": ("Lesser demons have seen you scrying, and wish to challenge you.", "Demônios menores viram você espiar e querem desafiá-lo."),
    "meet_shadow_person": ("The shadows watch you.", "As sombras observam você."),
    "meet_dog": ("A rabid beast will soon stumble upon you.", "Uma fera raivosa logo vai te encontrar."),
    "thunderbolt": ("A bright flash of light occludes my vision.", "Um clarão ofusca a minha visão."),
    "meet_werewolf": ("The moon calls something hungry from the woods.", "A lua chama algo faminto da floresta."),
    "curse": ("Something wicked this way comes.", "Algo maligno vem em sua direção."),
}


def w(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)


def wj(path, obj):
    w(path, json.dumps(obj, indent=2, ensure_ascii=False) + "\n")


def lang():
    en, pt = {}, {}
    for k, (e, p, _) in MOBS.items():
        en[f"entity.{NS}.{k}"] = e
        pt[f"entity.{NS}.{k}"] = p
        en[f"item.{NS}.{k}_spawn_egg"] = f"{e} Spawn Egg"
        pt[f"item.{NS}.{k}_spawn_egg"] = f"Ovo de Geração de {p}"
    for k, (e, p) in EXTRA_ENTITY.items():
        en[f"entity.{NS}.{k}"] = e
        pt[f"entity.{NS}.{k}"] = p
    en[f"item.{NS}.broom"], pt[f"item.{NS}.broom"] = "Broom", "Vassoura"
    en[f"block.{NS}.crystal_ball"], pt[f"block.{NS}.crystal_ball"] = "Crystal Ball", "Bola de Cristal"
    en[f"block.{NS}.biome_purifier"], pt[f"block.{NS}.biome_purifier"] = "Biome Purifier", "Purificador de Bioma"
    en[f"enchantment.{NS}.magic_protection"], pt[f"enchantment.{NS}.magic_protection"] = "Magic Protection", "Proteção Mágica"
    for k, (e, p) in EFFECTS.items():
        en[f"effect.{NS}.{k}"], pt[f"effect.{NS}.{k}"] = e, p
    for k, (e, p) in FORTUNES.items():
        en[f"fortune.{NS}.{k}"], pt[f"fortune.{NS}.{k}"] = e, p
    for name, entries in (("en_us", en), ("pt_br", pt)):
        path = f"{A}/lang/{name}.json"
        text = open(path, encoding="utf-8").read().rstrip()
        existing = json.loads(text)
        assert text.endswith("}")
        body = text[:-1].rstrip()
        add = [f"  {json.dumps(k)}: {json.dumps(v, ensure_ascii=False)}" for k, v in entries.items() if k not in existing]
        if add:
            w(path, body + ",\n" + ",\n".join(add) + "\n}\n")


def loot():
    for k, (_, _, drops) in MOBS.items():
        if not drops:
            continue
        pools = []
        for item, lo, hi, chance in drops:
            entry = {"type": "minecraft:item", "name": item}
            if (lo, hi) != (1, 1):
                entry["functions"] = [{"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]
            pool = {"rolls": 1, "bonus_rolls": 0, "entries": [entry]}
            if chance < 1:
                pool["conditions"] = [{"condition": "minecraft:random_chance", "chance": chance}]
            pools.append(pool)
        wj(f"{D}/loot_tables/entities/{k}.json", {"type": "minecraft:entity", "pools": pools})
    for b in ("crystal_ball", "biome_purifier"):
        wj(f"{D}/loot_tables/blocks/{b}.json", {"type": "minecraft:block", "pools": [{"rolls": 1, "bonus_rolls": 0,
            "entries": [{"type": "minecraft:item", "name": f"{NS}:{b}"}], "conditions": [{"condition": "minecraft:survives_explosion"}]}]})


def models():
    for k in MOBS:
        wj(f"{A}/models/item/{k}_spawn_egg.json", {"parent": "minecraft:item/template_spawn_egg"})
    wj(f"{A}/models/item/broom.json", {"parent": "minecraft:item/generated", "textures": {"layer0": f"{NS}:item/broom"}})
    wj(f"{A}/models/item/crystal_ball.json", {"parent": f"{NS}:block/crystal_ball"})
    wj(f"{A}/models/item/biome_purifier.json", {"parent": f"{NS}:block/biome_purifier"})
    wj(f"{A}/blockstates/crystal_ball.json", {"variants": {"": {"model": f"{NS}:block/crystal_ball"}}})
    wj(f"{A}/blockstates/biome_purifier.json", {"multipart": [{"apply": {"model": f"{NS}:block/biome_purifier"}}]})
    wj(f"{A}/models/block/biome_purifier.json", {"parent": "minecraft:block/cube_all", "textures": {"all": f"{NS}:block/biome_purifier"}})
    faces = lambda t: {f: {"texture": t} for f in ("north", "south", "east", "west", "up", "down")}
    wj(f"{A}/models/block/crystal_ball.json", {
        "textures": {"orb": f"{NS}:block/crystal_ball", "base": "minecraft:block/dark_oak_planks", "particle": f"{NS}:block/crystal_ball"},
        "elements": [
            {"from": [4, 0, 4], "to": [12, 3, 12], "faces": faces("#base")},
            {"from": [3, 3, 3], "to": [13, 13, 13], "faces": faces("#orb")},
        ]})


def recipes():
    wj(f"{D}/recipes/broom.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": ["  S", " S ", "HHH"],
        "key": {"S": {"item": "minecraft:stick"}, "H": {"item": "minecraft:wheat"}}, "result": {"item": f"{NS}:broom"}})
    wj(f"{D}/recipes/crystal_ball.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": ["GGG", "GEG", " O "],
        "key": {"G": {"item": "minecraft:glass"}, "E": {"item": "minecraft:ender_pearl"}, "O": {"item": "minecraft:obsidian"}},
        "result": {"item": f"{NS}:crystal_ball"}})
    wj(f"{D}/recipes/biome_purifier.json", {"type": "minecraft:crafting_shaped", "category": "misc", "pattern": ["DGD", "GAG", "DGD"],
        "key": {"D": {"item": "minecraft:diamond"}, "G": {"item": "minecraft:gold_ingot"}, "A": {"item": "minecraft:golden_apple"}},
        "result": {"item": f"{NS}:biome_purifier"}})


def save(img, path):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path)


def textures():
    rnd = random.Random(185)
    # vassoura (16x16): cabo marrom na diagonal, cerdas de palha embaixo a esquerda
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    px = im.load()
    for i in range(10):
        px[14 - i, 1 + i] = (122 + rnd.randint(-10, 10), 82, 44, 255)
    for x in range(0, 7):
        for y in range(9, 16):
            if abs(x + y - 15) <= 2 and x + y <= 17 and not (x + y == 15 and x >= 5):
                px[x, y] = (214 + rnd.randint(-14, 8), 176 + rnd.randint(-14, 8), 86, 255)
    for x, y in ((6, 9), (5, 10), (4, 11)):
        px[x, y] = (70, 46, 26, 255)
    save(im, f"{A}/textures/item/broom.png")
    # bola de cristal (16x16): nevoa azul-violeta
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    px = im.load()
    for x in range(16):
        for y in range(16):
            v = (x * 3 + y * 5) % 11 / 11.0
            n = rnd.randint(-14, 14)
            px[x, y] = (int(70 + 110 * v) + n, int(90 + 90 * (1 - v)) + n, 230 + n // 2, 255)
    save(im, f"{A}/textures/block/crystal_ball.png")
    # purificador (16x16): verde luminoso com moldura clara
    im = Image.new("RGBA", (16, 16), (0, 0, 0, 255))
    px = im.load()
    for x in range(16):
        for y in range(16):
            edge = x in (0, 15) or y in (0, 15)
            c = (220, 255, 220) if edge else (60 + rnd.randint(0, 40), 200 + rnd.randint(-20, 30), 110 + rnd.randint(0, 40))
            if not edge and (x + y) % 5 == 0:
                c = (150, 255, 190)
            px[x, y] = c + (255,)
    d = ImageDraw.Draw(im)
    d.rectangle((6, 3, 9, 12), fill=(255, 255, 255, 255))
    d.rectangle((3, 6, 12, 9), fill=(255, 255, 255, 255))
    save(im, f"{A}/textures/block/biome_purifier.png")
    # icones dos efeitos (18x18)
    colors = {"curse_solar_hatred": (224, 176, 32), "curse_hydrophobia": (32, 96, 192), "curse_overheating": (208, 64, 16),
              "curse_lightning_rod": (176, 200, 255), "curse_darkness": (60, 40, 90), "curse_misfortune": (100, 140, 60),
              "curse_paranoia": (150, 110, 170)}
    for k, c in colors.items():
        im = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
        d = ImageDraw.Draw(im)
        d.ellipse((1, 1, 16, 16), fill=c + (255,), outline=(20, 10, 30, 255))
        if k == "curse_solar_hatred":
            d.ellipse((6, 6, 11, 11), fill=(255, 240, 150, 255))
            for a, b in ((9, 2), (9, 15), (2, 9), (15, 9)):
                d.point((a, b), fill=(255, 255, 200, 255))
        elif k == "curse_hydrophobia":
            d.polygon([(9, 3), (13, 10), (9, 14), (5, 10)], fill=(200, 230, 255, 255))
        elif k == "curse_overheating":
            d.polygon([(9, 3), (13, 11), (9, 14), (5, 11)], fill=(255, 210, 60, 255))
        elif k == "curse_lightning_rod":
            d.line([(10, 3), (7, 9), (11, 9), (8, 15)], fill=(255, 255, 120, 255), width=2)
        elif k == "curse_darkness":
            d.ellipse((5, 7, 12, 11), fill=(230, 230, 240, 255))
            d.ellipse((7, 8, 10, 10), fill=(10, 0, 20, 255))
        elif k == "curse_misfortune":
            d.line([(5, 5), (12, 12)], fill=(230, 240, 200, 255), width=2)
            d.line([(12, 5), (5, 12)], fill=(230, 240, 200, 255), width=2)
        else:
            d.ellipse((4, 6, 13, 12), fill=(250, 250, 250, 255))
            d.ellipse((7, 7, 10, 11), fill=(120, 20, 40, 255))
        save(im, f"{A}/textures/mob_effect/{k}.png")


if __name__ == "__main__":
    lang()
    loot()
    models()
    recipes()
    textures()
    print("ok")
