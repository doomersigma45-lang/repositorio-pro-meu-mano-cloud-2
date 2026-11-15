"""Gera os recursos dos parasitas (1.67.0): icones dos efeitos, skin do jogador parasita, loot, ovos, spawn e lang."""
import json
import os
import random

from PIL import Image

RES = os.path.join(os.path.dirname(__file__), "..", "forge-1.20.1", "src", "main", "resources")
ASSETS = os.path.join(RES, "assets", "netheritesupremo")
DATA = os.path.join(RES, "data", "netheritesupremo")
random.seed(67)


def wjson(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False)


# ---------- icones dos efeitos (18x18) ----------
def icon(name, body, spot, ring):
    im = Image.new("RGBA", (18, 18), (0, 0, 0, 0))
    px = im.load()
    for y in range(18):
        for x in range(18):
            d = ((x - 8.5) ** 2 + (y - 8.5) ** 2) ** 0.5
            if d < 6.8:
                px[x, y] = body + (255,)
            elif d < 7.8:
                px[x, y] = ring + (255,)
    for x, y in spot:
        px[x, y] = (230, 255, 120, 255)
    os.makedirs(os.path.join(ASSETS, "textures", "mob_effect"), exist_ok=True)
    im.save(os.path.join(ASSETS, "textures", "mob_effect", name + ".png"))


# Infestacao: verme enrolado no centro; Parasitado: olho verde.
icon("infestation", (76, 122, 30), [(6, 7), (7, 6), (8, 6), (9, 7), (10, 8), (9, 9), (8, 10), (7, 11), (8, 12), (9, 12), (10, 11)], (30, 60, 12))
icon("parasitized", (47, 90, 18), [(5, 8), (6, 7), (7, 6), (8, 6), (9, 6), (10, 6), (11, 7), (12, 8), (11, 9), (10, 10), (9, 10), (8, 10), (7, 10), (6, 9), (8, 8), (9, 8)], (20, 40, 8))

# ---------- skin do jogador parasita (64x64, modelo de 4px de braco) ----------
skin = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
sp = skin.load()


def paint(u, v, w, h, d, base, var=14):
    for y in range(v, v + d + h):
        for x in range(u, u + 2 * (w + d)):
            if y < v + d and not (d <= x - u < d + 2 * w):
                continue
            k = random.randint(-var, var)
            sp[x, y] = (max(0, min(255, base[0] + k)), max(0, min(255, base[1] + k)), max(0, min(255, base[2] + k)), 255)


SKIN = (86, 140, 66)
CLOTH = (44, 74, 52)
PANTS = (30, 46, 40)
paint(0, 0, 8, 8, 8, SKIN)           # cabeca
paint(16, 16, 8, 12, 4, CLOTH)       # corpo
paint(40, 16, 4, 12, 4, SKIN)        # braco direito
paint(32, 48, 4, 12, 4, SKIN)        # braco esquerdo
paint(0, 16, 4, 12, 4, PANTS)        # perna direita
paint(16, 48, 4, 12, 4, PANTS)       # perna esquerda
# rosto (frente da cabeca: x 8..15, y 8..15)
for x in range(8, 16):
    sp[x, 8] = (40, 70, 30, 255)
for x, y in ((9, 11), (10, 11), (13, 11), (14, 11)):
    sp[x, y] = (210, 255, 70, 255)
for x in range(10, 14):
    sp[x, 14] = (20, 40, 16, 255)
for x, y in ((9, 13), (14, 13)):
    sp[x, y] = (40, 70, 30, 255)
# veias escuras e rasgos na roupa
for _ in range(40):
    x, y = random.randint(20, 27), random.randint(20, 31)
    sp[x, y] = (20, 34, 24, 255)
for _ in range(30):
    x, y = random.randint(44, 55), random.randint(20, 31)
    sp[x, y] = (46, 90, 30, 255)
skin.save(os.path.join(ASSETS, "textures", "entity", "parasite_player.png"))

# ---------- mobs ----------
MOBS = {
    "worm": ("Parasite", "Parasita", "Parasite Spawn Egg", "Ovo de Geração de Parasita"),
    "cow": ("Parasite Cow", "Vaca Parasita", "Parasite Cow Spawn Egg", "Ovo de Geração de Vaca Parasita"),
    "pig": ("Parasite Pig", "Porco Parasita", "Parasite Pig Spawn Egg", "Ovo de Geração de Porco Parasita"),
    "chicken": ("Parasite Chicken", "Galinha Parasita", "Parasite Chicken Spawn Egg", "Ovo de Geração de Galinha Parasita"),
    "zombie": ("Parasite Zombie", "Zumbi Parasita", "Parasite Zombie Spawn Egg", "Ovo de Geração de Zumbi Parasita"),
    "skeleton": ("Parasite Skeleton", "Esqueleto Parasita", "Parasite Skeleton Spawn Egg", "Ovo de Geração de Esqueleto Parasita"),
    "creeper": ("Parasite Creeper", "Creeper Parasita", "Parasite Creeper Spawn Egg", "Ovo de Geração de Creeper Parasita"),
    "spider": ("Parasite Spider", "Aranha Parasita", "Parasite Spider Spawn Egg", "Ovo de Geração de Aranha Parasita"),
    "player": ("Parasite Player", "Jogador Parasita", "Parasite Player Spawn Egg", "Ovo de Geração de Jogador Parasita"),
}
DROPS = {
    "worm": [("minecraft:slime_ball", 0, 1)],
    "cow": [("minecraft:rotten_flesh", 1, 3), ("minecraft:leather", 0, 2)],
    "pig": [("minecraft:rotten_flesh", 1, 3)],
    "chicken": [("minecraft:rotten_flesh", 0, 2), ("minecraft:feather", 0, 2)],
    "zombie": [("minecraft:rotten_flesh", 1, 3), ("minecraft:slime_ball", 0, 2)],
    "skeleton": [("minecraft:bone", 1, 3), ("minecraft:slime_ball", 0, 2)],
    "creeper": [("minecraft:gunpowder", 1, 3), ("minecraft:slime_ball", 0, 2)],
    "spider": [("minecraft:string", 1, 3), ("minecraft:spider_eye", 0, 2)],
    "player": [("minecraft:rotten_flesh", 2, 4), ("minecraft:bone", 1, 3), ("minecraft:slime_ball", 1, 3)],
}
for k, drops in DROPS.items():
    pools = []
    for item, lo, hi in drops:
        pools.append({"rolls": 1, "bonus_rolls": 0, "entries": [{"type": "minecraft:item", "name": item, "functions": [
            {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": lo, "max": hi}}]}]})
    wjson(os.path.join(DATA, "loot_tables", "entities", "parasite_%s.json" % k), {"type": "minecraft:entity", "pools": pools})
    wjson(os.path.join(ASSETS, "models", "item", "parasite_%s_spawn_egg.json" % k), {"parent": "minecraft:item/template_spawn_egg"})

# o verme surge raramente em qualquer lugar do Overworld
wjson(os.path.join(DATA, "forge", "biome_modifier", "add_parasite_worm.json"), {
    "type": "forge:add_spawns", "biomes": "#minecraft:is_overworld",
    "spawners": {"type": "netheritesupremo:parasite_worm", "weight": 2, "minCount": 1, "maxCount": 2}})

# ---------- lang ----------
for fname, idx_mob, idx_egg, eff in (("en_us", 0, 2, ("Infestation", "Parasitized")), ("pt_br", 1, 3, ("Infestação", "Parasitado"))):
    path = os.path.join(ASSETS, "lang", fname + ".json")
    with open(path, encoding="utf-8") as f:
        lang = json.load(f)
    for k, names in MOBS.items():
        lang["entity.netheritesupremo.parasite_%s" % k] = names[idx_mob]
        lang["item.netheritesupremo.parasite_%s_spawn_egg" % k] = names[idx_egg]
    lang["effect.netheritesupremo.infestation"] = eff[0]
    lang["effect.netheritesupremo.parasitized"] = eff[1]
    with open(path, "w", encoding="utf-8") as f:
        json.dump(lang, f, ensure_ascii=False, indent=2)
        f.write("\n")
print("ok")
