#!/usr/bin/env python3
"""Texturas das maçãs de netherite e do bolo de netherite (pintadas por codigo; nao sao arte enviada pelo usuario)."""
import os, random, math
from PIL import Image
R = random.Random(11)
BASE = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources', 'assets', 'netheritesupremo', 'textures')
def clamp(v): return max(0, min(255, int(v)))
def mix(c, f): return tuple(clamp(x * f) for x in c)

def apple(path, body, hi):
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    cx, cy, rx, ry = 7.5, 9.5, 6.2, 5.6
    for x in range(16):
        for y in range(16):
            d = ((x - cx) / rx) ** 2 + ((y - cy) / ry) ** 2
            # reentrancia no topo (apple dip)
            if abs(x - 7.5) < 1.6 and y < 5: d += 0.6
            if d <= 1.0:
                lx, ly = (x - cx) / rx, (y - cy) / ry
                light = -0.55 * lx - 0.65 * ly            # luz vinda de cima/esquerda
                f = 0.72 + 0.45 * light + R.uniform(-0.04, 0.04)
                c = mix(body, f)
                if light > 0.62: c = tuple(clamp(a * 0.4 + b * 0.6) for a, b in zip(c, hi))
                img.putpixel((x, y), c + (255,))
    for (x, y) in ((7, 2), (7, 3), (8, 3), (8, 4)): img.putpixel((x, y), (92, 62, 40, 255))
    for (x, y) in ((9, 2), (10, 2), (10, 3), (9, 3)): img.putpixel((x, y), (46, 92, 70, 255))
    img.putpixel((5, 7), hi + (255,)); img.putpixel((5, 8), hi + (255,)); img.putpixel((6, 6), hi + (255,))
    img.save(path)

apple(f'{BASE}/item/netherite_apple.png', (76, 66, 72), (190, 190, 200))
apple(f'{BASE}/item/enchanted_netherite_apple.png', (82, 66, 74), (255, 214, 96))

GRAY = (112, 112, 120); FILL = (150, 150, 158); FROST = (178, 36, 50); FROST_L = (222, 88, 96); FROST_D = (120, 20, 34)
def noise(c, v=7):
    n = R.randint(-v, v); return tuple(clamp(x + n) for x in c)

# topo: cobertura vermelha com bolinhas claras
top = Image.new('RGBA', (16, 16))
for x in range(16):
    for y in range(16):
        edge = x in (0, 15) or y in (0, 15)
        top.putpixel((x, y), (noise(FROST_D if edge else FROST, 6)) + (255,))
for (x, y) in ((4, 4), (11, 5), (7, 9), (3, 11), (12, 12), (8, 3)): top.putpixel((x, y), FROST_L + (255,))
top.save(f'{BASE}/block/netherite_cake_top.png')
# fundo
bot = Image.new('RGBA', (16, 16))
for x in range(16):
    for y in range(16): bot.putpixel((x, y), noise(mix(GRAY, 0.8)) + (255,))
bot.save(f'{BASE}/block/netherite_cake_bottom.png')
# lateral: cobertura (so) no topo, bolo cinza embaixo; metade de cima transparente como no bolo original
side = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
drip = [3, 4, 3, 3, 5, 3, 3, 4, 4, 3, 3, 5, 3, 4, 3, 3]
for x in range(16):
    for y in range(8, 16):
        yy = y - 8
        if yy < drip[x]: c = FROST_D if yy == drip[x] - 1 and drip[x] > 3 else FROST
        elif yy == 4: c = FILL
        else: c = GRAY
        side.putpixel((x, y), noise(c, 5) + (255,))
side.save(f'{BASE}/block/netherite_cake_side.png')
# interior (face cortada): camadas cinza com recheio claro
inner = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
for x in range(16):
    for y in range(8, 16):
        yy = y - 8
        c = FROST if yy < 2 else (FILL if yy in (4, 5) else mix(GRAY, 1.12))
        inner.putpixel((x, y), noise(c, 5) + (255,))
inner.save(f'{BASE}/block/netherite_cake_inner.png')
# icone do item
icon = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
for x in range(2, 14):
    for y in range(7, 14):
        yy = y - 7
        c = FROST if yy < 2 else (FILL if yy == 4 else GRAY)
        if x in (2, 13) or y == 13: c = mix(c, 0.7)
        icon.putpixel((x, y), noise(c, 4) + (255,))
for x in range(3, 13): icon.putpixel((x, 6), FROST_L + (255,))
icon.putpixel((7, 4), (200, 30, 40, 255)); icon.putpixel((8, 4), (200, 30, 40, 255)); icon.putpixel((7, 5), (200, 30, 40, 255)); icon.putpixel((8, 5), (200, 30, 40, 255)); icon.putpixel((8, 3), (60, 90, 60, 255))
icon.save(f'{BASE}/item/netherite_cake.png')
print('ok')
