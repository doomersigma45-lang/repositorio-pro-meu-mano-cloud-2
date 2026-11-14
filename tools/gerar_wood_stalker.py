"""Textura do Wood Stalker (feita por IA, nao enviada pelo usuario). Madeira escura com veios, cascas e nos."""
import random
from PIL import Image
random.seed(404)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"
def nz(c, n): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)
T = Image.new("RGBA", (64, 64), (0, 0, 0, 0)); G = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
WOOD = (62, 42, 24); DARK = (38, 25, 14); LIGHT = (84, 58, 34); MOSS = (46, 66, 30)
def box(u, v, w, h, d):
    for i in range(u, u + 2 * d + 2 * w):
        for j in range(v, v + d + h):
            stripe = (i * 7 // 3) % 4 == 0          # veios verticais
            c = DARK if stripe else WOOD
            T.putpixel((i, j), nz(c, 6) + (255,))
    for _ in range((2 * w + 2 * d) * (d + h) // 14):  # nos, rachaduras e musgo
        i, j = random.randint(u, u + 2 * d + 2 * w - 1), random.randint(v, v + d + h - 1)
        T.putpixel((i, j), random.choice([DARK, LIGHT, MOSS, (24, 16, 9)]) + (255,))
for b in [(0, 0, 6, 7, 6), (0, 14, 6, 12, 4), (24, 14, 2, 16, 2), (34, 14, 3, 12, 3), (0, 32, 1, 5, 1), (12, 32, 3, 3, 3)]:
    box(*b)
for (x, y) in [(7, 10), (8, 10), (7, 11), (8, 11), (10, 10), (11, 10), (10, 11), (11, 11)]:   # olhos verde-palido (frente da cabeca: 6,6)
    T.putpixel((x, y), (190, 255, 120, 255)); G.putpixel((x, y), (190, 255, 120, 255))
T.save(R + "wood_stalker.png"); G.save(R + "wood_stalker_eyes.png")
