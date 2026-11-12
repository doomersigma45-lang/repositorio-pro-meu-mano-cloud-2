"""Texturas do perseguidor, besouro colossal e fungus (feitas por IA, nao enviadas pelo usuario)."""
import random
from PIL import Image
random.seed(21)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"

def nz(c, n=8):
    return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)

def fill(img, x, y, w, h, c, n=8):
    for i in range(x, x + w):
        for j in range(y, y + h):
            if 0 <= i < img.width and 0 <= j < img.height:
                img.putpixel((i, j), nz(c, n) + (255,))

def box(img, u, v, w, h, d, c, n=8):
    fill(img, u, v, 2 * d + 2 * w, d + h, c, n)

# perseguidor 64x64
s = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
G = (30, 30, 36)
box(s, 0, 0, 4, 4, 4, G, 4)
box(s, 0, 10, 4, 14, 2, (36, 36, 42), 4)
box(s, 14, 10, 2, 20, 2, G, 4)
box(s, 24, 10, 2, 11, 2, (26, 26, 30), 4)
box(s, 34, 10, 2, 11, 2, (26, 26, 30), 4)
fill(s, 44, 10, 12, 6, (15, 15, 18), 3)
s.save(R + "stalker.png")
e = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
for p in [(4, 6), (7, 6), (5, 6), (6, 6)]:
    pass
for p in [(4, 6), (7, 6)]:
    e.putpixel(p, (255, 255, 255, 255))
e.save(R + "stalker_eyes.png")

# besouro 128x128
b = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
SH = (62, 38, 24)
box(b, 0, 0, 20, 14, 28, (46, 28, 18), 6)
box(b, 0, 44, 18, 6, 24, SH, 7)
for x in range(30, 80, 10):
    fill(b, x, 44, 1, 24, (30, 18, 12), 3)
box(b, 0, 76, 8, 6, 7, (40, 26, 18), 5)
for p in [(5, 85), (6, 85), (5, 86), (6, 86), (15, 85), (16, 85), (15, 86), (16, 86)]:
    b.putpixel(p, (5, 5, 5, 255))
box(b, 32, 76, 3, 3, 10, (30, 20, 14), 4)
box(b, 60, 76, 2, 6, 2, (30, 20, 14), 4)
box(b, 72, 76, 3, 10, 3, (34, 22, 15), 4)
b.save(R + "colossal_beetle.png")

# fungus 64x64
f = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
box(f, 0, 0, 8, 8, 14, (84, 62, 46), 9)
for _ in range(60):
    x, y = random.randint(0, 43), random.randint(0, 21)
    f.putpixel((x, y), nz((70, 120, 60), 10) + (255,))
box(f, 0, 24, 12, 3, 12, (150, 42, 42), 8)
fill(f, 12, 24, 12, 12, (155, 45, 45), 8)
for _ in range(12):
    x, y = random.randint(13, 22), random.randint(25, 34)
    f.putpixel((x, y), (235, 235, 225, 255))
fill(f, 24, 24, 12, 12, (215, 195, 150), 6)
for p in [(28, 28), (28, 29), (32, 28), (32, 29)]:
    f.putpixel(p, (255, 215, 40, 255))
box(f, 0, 40, 10, 2, 10, (140, 38, 38), 8)
box(f, 46, 24, 3, 10, 3, (60, 44, 34), 6)
box(f, 50, 40, 1, 3, 1, (220, 205, 170), 5)
box(f, 0, 54, 4, 2, 4, (190, 50, 50), 8)
f.save(R + "fungus.png")
print("ok")
