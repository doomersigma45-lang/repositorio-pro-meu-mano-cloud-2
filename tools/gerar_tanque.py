#!/usr/bin/env python3
"""Gera a textura do Tanque de Guerra (128x128, pintada por codigo; nao e arte enviada pelo usuario).
Uso: python3 tools/gerar_tanque.py"""
import os, random
from PIL import Image, ImageDraw
r = random.Random(5)
img = Image.new('RGBA', (128, 128), (0, 0, 0, 0))
px = img.load()
def fill(x0, y0, x1, y1, base, var=10):
    for x in range(x0, x1):
        for y in range(y0, y1):
            n = r.randint(-var, var)
            px[x, y] = (max(0, min(255, base[0] + n)), max(0, min(255, base[1] + n)), max(0, min(255, base[2] + n)), 255)
fill(0, 0, 108, 38, (78, 88, 62))            # casco
fill(0, 40, 56, 60, (86, 96, 68))             # torre
fill(60, 40, 98, 59, (44, 46, 48), 6)         # canhao
fill(0, 64, 74, 103, (38, 38, 40), 5)         # esteiras
d = ImageDraw.Draw(img)
for y in range(66, 103, 4):                   # elos das esteiras
    d.line([(0, y), (73, y)], fill=(22, 22, 24, 255))
for x in range(0, 74, 6):
    d.line([(x, 64), (x, 102)], fill=(26, 26, 28, 255))
for x in range(2, 108, 9):                    # rebites no casco
    for y in (3, 20, 34):
        d.point((x, y), fill=(120, 128, 100, 255))
for x in range(0, 56, 8):                     # juntas da torre
    d.line([(x, 40), (x, 59)], fill=(60, 68, 48, 255))
d.rectangle([62, 42, 96, 44], fill=(70, 72, 74, 255))
out = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources',
                   'assets', 'netheritesupremo', 'textures', 'entity', 'war_tank.png')
img.save(out)
print('war_tank.png', img.size)
