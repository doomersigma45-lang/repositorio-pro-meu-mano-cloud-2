"""Gera texturas do ganso, galinha de netherite e aranha colossal (feitas por IA, nao enviadas pelo usuario)."""
import random, os
from PIL import Image
random.seed(7)
OUT = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"

def noise(c, n=10):
    return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)

def fill(img, x0, y0, w, h, c, n=10):
    for x in range(x0, x0 + w):
        for y in range(y0, y0 + h):
            if 0 <= x < img.width and 0 <= y < img.height:
                img.putpixel((x, y), noise(c, n) + (255,))

def box(img, u, v, w, h, d, c, n=10):
    fill(img, u, v, 2 * d + 2 * w, d + h, c, n)

# ---- ganso 64x64
g = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
W, O, G = (240, 240, 240), (232, 134, 26), (205, 205, 210)
box(g, 0, 0, 6, 6, 12, W)
fill(g, 0, 12, 36, 6, G, 6)
box(g, 0, 20, 2, 8, 2, W)
box(g, 14, 20, 4, 3, 5, W)
box(g, 34, 20, 2, 1, 3, O, 5)
box(g, 0, 34, 1, 5, 1, O, 5)
box(g, 6, 34, 3, 1, 4, O, 5)
box(g, 0, 42, 1, 5, 6, G, 6)
box(g, 20, 42, 4, 2, 3, W)
for p in [(18, 26), (24, 26)]:
    g.putpixel(p, (10, 10, 10, 255))
g.save(OUT + "goose.png")

# ---- galinha de netherite 64x32 (UV da galinha)
c = Image.new("RGBA", (64, 32), (0, 0, 0, 0))
N = (72, 60, 64)
box(c, 0, 0, 4, 6, 3, N)
box(c, 14, 0, 4, 2, 2, (225, 170, 45), 6)
box(c, 14, 4, 2, 2, 2, (150, 40, 30), 6)
box(c, 0, 9, 6, 6, 8, N)
fill(c, 14, 14, 6, 8, (96, 82, 86), 8)
box(c, 26, 0, 3, 5, 3, (225, 170, 45), 6)
box(c, 24, 13, 6, 4, 1, (58, 48, 52))
for p in [(3, 4), (6, 4)]:
    c.putpixel(p, (255, 120, 30, 255))
c.save(OUT + "netherite_chicken.png")

# ---- aranha colossal 64x64
s = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
B = (34, 27, 25)
box(s, 32, 4, 8, 8, 8, B)
box(s, 0, 0, 6, 6, 6, B)
box(s, 0, 12, 10, 8, 12, B)
fill(s, 12, 12, 10, 12, (30, 24, 22), 6)
for (x, y) in [(16, 14), (17, 15), (16, 16), (17, 17), (16, 18), (17, 19), (16, 20), (17, 21)]:
    s.putpixel((x, y), (178, 36, 31, 255))
box(s, 18, 0, 16, 2, 2, B)
for x in range(18, 54, 6):
    fill(s, x, 2, 2, 2, (70, 58, 52), 6)
fill(s, 0, 32, 4, 3, (205, 195, 165), 8)
fill(s, 0, 40, 8, 6, (205, 195, 165), 8)
for p in [(41, 14), (42, 14), (45, 14), (46, 14), (42, 16), (45, 16), (43, 13), (44, 13)]:
    s.putpixel(p, (200, 30, 25, 255))
s.save(OUT + "colossal_spider.png")
print("ok")
