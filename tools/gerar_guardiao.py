"""Texturas do Guardiao Ancestral e do guardiao de pedra (feitas por IA, nao enviadas pelo usuario)."""
import random
from PIL import Image
random.seed(33)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"
def nz(c, n=8): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)
def fill(img, x, y, w, h, c, n=8):
    for i in range(x, x + w):
        for j in range(y, y + h):
            if 0 <= i < img.width and 0 <= j < img.height:
                img.putpixel((i, j), nz(c, n) + (255,))
def box(img, u, v, w, h, d, c, n=8): fill(img, u, v, 2 * d + 2 * w, d + h, c, n)

# ---- guardiao ancestral 128x128
T = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
G = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
ROCK = (44, 36, 34)
regions = [(0,0,10,9,10),(44,0,3,6,3),(60,0,2,5,2),(0,24,16,20,9),(0,56,7,20,7),(30,56,8,6,8),(64,56,4,12,4),(0,86,6,9,6),(30,86,5,9,5),(56,86,6,3,8)]
for (u,v,w,h,d) in regions:
    box(T, u, v, w, h, d, ROCK, 7)
    # rachaduras de magma: linhas aleatorias dentro da area
    for _ in range(max(2, (w + d) // 2)):
        x = random.randint(u, u + 2 * d + 2 * w - 1); y = random.randint(v, v + d + h - 1)
        for _ in range(random.randint(3, 7)):
            if 0 <= x < 128 and 0 <= y < 128 and T.getpixel((x, y))[3]:
                T.putpixel((x, y), (255, 110, 20, 255)); G.putpixel((x, y), (255, 120, 25, 255))
            x += random.choice((-1, 0, 1)); y += random.choice((0, 1))
# olhos laranja (frente da cabeca: (10,10) 10x9)
for p in [(12, 13), (13, 13), (16, 13), (17, 13), (12, 14), (13, 14), (16, 14), (17, 14)]:
    T.putpixel(p, (255, 160, 30, 255)); G.putpixel(p, (255, 170, 40, 255))
T.save(R + "ancient_guardian.png"); G.save(R + "ancient_guardian_glow.png")

# ---- guardiao de pedra (layout do golem de ferro 128x128)
S = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
ST = (112, 112, 118)
for (u,v,w,h,d) in [(0,0,8,10,8),(24,0,2,4,2),(0,40,18,12,11),(0,70,9,5,6),(60,21,4,30,6),(60,58,4,30,6),(37,0,6,16,5),(60,0,6,16,5)]:
    box(S, u, v, w, h, d, ST, 9)
    for _ in range(4):
        x = random.randint(u, u + 2*d + 2*w - 1); y = random.randint(v, v + d + h - 1)
        if S.getpixel((x, y))[3]: S.putpixel((x, y), (70, 70, 76, 255))
for p in [(10, 12), (11, 12), (14, 12), (15, 12)]:
    S.putpixel(p, (255, 140, 30, 255))
S.save(R + "stone_guardian.png")
print("ok")
