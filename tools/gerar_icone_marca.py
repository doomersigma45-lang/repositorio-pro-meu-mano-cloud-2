#!/usr/bin/env python3
"""Icone 18x18 da Marca de Guerra (caveira vermelha); pintado por codigo, nao e arte enviada pelo usuario."""
import os
from PIL import Image
rows = [
 "..................",
 "......RRRRRR......",
 "....RRrrrrrrRR....",
 "...RrrrrrrrrrrR...",
 "..RrrrrrrrrrrrrR..",
 "..RrrrrrrrrrrrrR..",
 "..RrKKKrrrrKKKrR..",
 "..RrKKKrrrrKKKrR..",
 "..RrKKKrrrrKKKrR..",
 "..RrrrrrKKrrrrrR..",
 "...RrrrrKKrrrrR...",
 "....RrrrrrrrrR....",
 ".....RrrrrrrR.....",
 ".....RrRrRrRrR....",
 ".....RrRrRrRrR....",
 "......RRRRRRR.....",
 "..................",
 "..................",
]
col = {'R': (110, 8, 8, 255), 'r': (200, 40, 36, 255), 'K': (20, 4, 4, 255), '.': (0, 0, 0, 0)}
img = Image.new('RGBA', (18, 18))
for y, row in enumerate(rows):
    for x, ch in enumerate(row[:18]):
        img.putpixel((x, y), col[ch])
out = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources',
                   'assets', 'netheritesupremo', 'textures', 'mob_effect', 'war_mark.png')
img.save(out); print('ok')
