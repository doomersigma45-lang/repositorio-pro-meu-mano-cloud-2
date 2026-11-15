"""Recursos do liquido vermelho dos parasitas (1.68.0): texturas animadas, balde, blockstate/modelos e worldgen."""
import json
import math
import os

from PIL import Image

RES = os.path.join(os.path.dirname(__file__), "..", "forge-1.20.1", "src", "main", "resources")
ASSETS = os.path.join(RES, "assets", "netheritesupremo")
DATA = os.path.join(RES, "data", "netheritesupremo")


def wjson(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(obj, f, ensure_ascii=False)


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


def liquid(name, flow):
    frames = 16
    im = Image.new("RGBA", (16, 16 * frames), (0, 0, 0, 255))
    px = im.load()
    for f in range(frames):
        for y in range(16):
            for x in range(16):
                ph = 2 * math.pi * f / frames
                v = 0.5 + 0.25 * math.sin(2 * math.pi * x / 16 + ph) + 0.25 * math.sin(2 * math.pi * y / 16 * (2 if flow else 1) - ph * (2 if flow else 1))
                c = mix((84, 6, 12), (196, 22, 30), max(0.0, min(1.0, v)))
                px[x, f * 16 + y] = c + (255,)
    im.save(os.path.join(ASSETS, "textures", "block", name + ".png"))
    wjson(os.path.join(ASSETS, "textures", "block", name + ".png.mcmeta"), {"animation": {"frametime": 3, "interpolate": True}})


liquid("parasite_blood_still", False)
liquid("parasite_blood_flow", True)

# balde (16x16)
b = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
px = b.load()
GREY, GREY_D, RED, RED_D = (170, 170, 176, 255), (110, 110, 118, 255), (190, 22, 30, 255), (110, 8, 16, 255)
for y in range(4, 15):
    inset = (y - 4) // 4
    for x in range(3 + inset, 13 - inset):
        edge = x in (3 + inset, 12 - inset) or y == 14
        px[x, y] = GREY_D if edge else GREY
for y in (4, 5):
    for x in range(4, 12):
        px[x, y] = RED if y == 4 else RED_D
for x in range(5, 11):
    px[x, 3] = GREY_D
px[4, 2] = GREY_D
px[11, 2] = GREY_D
for x in range(5, 11):
    px[x, 1] = GREY_D
b.save(os.path.join(ASSETS, "textures", "item", "parasite_blood_bucket.png"))
wjson(os.path.join(ASSETS, "models", "item", "parasite_blood_bucket.json"),
      {"parent": "minecraft:item/generated", "textures": {"layer0": "netheritesupremo:item/parasite_blood_bucket"}})
wjson(os.path.join(ASSETS, "blockstates", "parasite_blood.json"), {"variants": {"": {"model": "netheritesupremo:block/parasite_blood"}}})
wjson(os.path.join(ASSETS, "models", "block", "parasite_blood.json"), {"textures": {"particle": "netheritesupremo:block/parasite_blood_still"}})

# worldgen: nascentes dentro de cavernas (raras)
wjson(os.path.join(DATA, "worldgen", "configured_feature", "parasite_blood_spring.json"), {
    "type": "minecraft:spring_feature",
    "config": {
        "state": {"Name": "netheritesupremo:parasite_blood", "Properties": {"falling": "true"}},
        "requires_block_below": True, "rock_count": 4, "hole_count": 1,
        "valid_blocks": ["minecraft:stone", "minecraft:granite", "minecraft:diorite", "minecraft:andesite",
                         "minecraft:deepslate", "minecraft:tuff", "minecraft:calcite"]}})
wjson(os.path.join(DATA, "worldgen", "placed_feature", "parasite_blood_spring.json"), {
    "feature": "netheritesupremo:parasite_blood_spring",
    "placement": [
        {"type": "minecraft:count", "count": 6},
        {"type": "minecraft:in_square"},
        {"type": "minecraft:height_range", "height": {"type": "minecraft:uniform", "min_inclusive": {"absolute": -56}, "max_inclusive": {"absolute": 40}}},
        {"type": "minecraft:rarity_filter", "chance": 4},
        {"type": "minecraft:biome"}]})
wjson(os.path.join(DATA, "forge", "biome_modifier", "add_parasite_blood.json"), {
    "type": "forge:add_features", "biomes": "#minecraft:is_overworld",
    "features": "netheritesupremo:parasite_blood_spring", "step": "fluid_springs"})

# lang
for fname, names in (("en_us", ("Parasite Blood", "Parasite Blood Bucket", "Parasite Phase")),
                     ("pt_br", ("Líquido Parasita", "Balde de Líquido Parasita", "Fase do Parasita"))):
    path = os.path.join(ASSETS, "lang", fname + ".json")
    with open(path, encoding="utf-8") as f:
        lang = json.load(f)
    lang["block.netheritesupremo.parasite_blood"] = names[0]
    lang["item.netheritesupremo.parasite_blood_bucket"] = names[1]
    lang["effect.netheritesupremo.parasite_phase"] = names[2]
    with open(path, "w", encoding="utf-8") as f:
        json.dump(lang, f, ensure_ascii=False, indent=2)
        f.write("\n")
print("ok")
