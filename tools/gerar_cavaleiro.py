"""Texturas do Cavaleiro sem Cabeca (esqueleto preto com detalhes verdes brilhantes) e da espada dele. Feitas por IA."""
import random
from PIL import Image, ImageDraw
random.seed(1031)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/"
def nz(c, n): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)
T = Image.new("RGBA", (64, 32), (0, 0, 0, 0)); G = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
BONE = (22, 22, 28); DARK = (10, 10, 14); GREEN = (70, 255, 110)
def box(u, v, w, h, d):
    for i in range(u, u + 2 * d + 2 * w):
        for j in range(v, v + d + h):
            T.putpixel((i, j), nz(DARK if (i + j) % 7 == 0 else BONE, 5) + (255,))
for b in [(0, 0, 8, 8, 8), (16, 16, 8, 12, 4), (40, 16, 2, 12, 2), (0, 16, 2, 12, 2)]:
    box(*b)
def glow(x, y):
    if 0 <= x < 64 and 0 <= y < 32 and T.getpixel((x, y))[3]:
        T.putpixel((x, y), GREEN + (255,)); G.putpixel((x, y), GREEN + (255,))
for y in (21, 23, 25, 27):                  # costelas na frente do tronco (20..27, 20..31)
    for x in range(21, 27):
        if (x + y) % 3: glow(x, y)
for y in range(20, 32):                     # coluna nas costas (32..39)
    glow(35, y); glow(36, y)
for (u, v) in ((40, 16), (0, 16)):          # juntas nos bracos e nas pernas
    for fx in range(u + 2, u + 6):
        for fy in (v + 6, v + 7, v + 11):
            glow(fx, fy)
for _ in range(25):                          # veias verdes aleatorias
    glow(random.randint(16, 40), random.randint(20, 31))
T.save(R + "entity/headless_horseman.png"); G.save(R + "entity/headless_horseman_glow.png")

# espada: lamina preta com fio verde brilhante
S = Image.new("RGBA", (16, 16), (0, 0, 0, 0)); d = ImageDraw.Draw(S)
for i in range(2, 13):
    d.point((i + 1, 14 - i), (24, 24, 30, 255)); d.point((i, 14 - i), (14, 14, 18, 255)); d.point((i + 2, 14 - i), (70, 255, 110, 255))
d.line((2, 12, 5, 9), fill=(90, 60, 30, 255)); d.line((3, 9, 6, 12), fill=(150, 110, 50, 255)); d.point((1, 14), (70, 255, 110, 255)); d.point((2, 14), (90, 60, 30, 255))
S.save(R + "item/horseman_sword.png")
print("ok")
