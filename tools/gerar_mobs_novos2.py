"""Texturas do mosquito gigante e do strongest zombie (feitas por IA, nao enviadas pelo usuario)."""
import random
from PIL import Image
random.seed(11)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/"

def nz(c, n=8):
    return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)

def fill(img, x, y, w, h, c, n=8):
    for i in range(x, x + w):
        for j in range(y, y + h):
            if 0 <= i < img.width and 0 <= j < img.height:
                img.putpixel((i, j), nz(c, n) + (255,))

def box(img, u, v, w, h, d, c, n=8):
    fill(img, u, v, 2 * d + 2 * w, d + h, c, n)

# mosquito 64x64
m = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
B = (52, 46, 40)
box(m, 0, 0, 5, 5, 14, B)
for y in range(8, 19, 4):
    fill(m, 14, y, 40, 1, (200, 190, 170), 4)
box(m, 0, 20, 6, 6, 8, (70, 60, 50))
box(m, 30, 20, 5, 5, 5, (60, 52, 44))
fill(m, 35, 25, 5, 5, (60, 52, 44), 4)
for (x, y) in [(35, 25), (36, 25), (38, 25), (39, 25), (35, 26), (39, 26)]:
    m.putpixel((x, y), (190, 30, 30, 255))
box(m, 0, 36, 1, 1, 8, (30, 28, 26), 3)
box(m, 20, 36, 1, 10, 1, (40, 36, 32), 3)
fill(m, 0, 48, 44, 7, (185, 205, 215), 6)
for x in range(0, 44, 4):
    fill(m, x, 49, 1, 5, (120, 140, 150), 4)
m.save(R + "entity/giant_mosquito.png")

# strongest zombie 64x64 (layout de jogador)
z = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
SK, CL, PT = (58, 98, 52), (70, 30, 30), (40, 40, 70)
box(z, 0, 0, 8, 8, 8, SK)
fill(z, 8, 8, 8, 8, SK)
for p in [(10, 11), (11, 11), (13, 11), (14, 11)]:
    z.putpixel(p, (220, 30, 20, 255))
fill(z, 10, 14, 4, 1, (30, 10, 10), 3)
box(z, 16, 16, 8, 12, 4, CL)
box(z, 40, 16, 4, 12, 4, SK)
box(z, 32, 48, 4, 12, 4, SK)
box(z, 0, 16, 4, 12, 4, PT)
box(z, 16, 48, 4, 12, 4, PT)
for (x, y, w, h) in [(20, 20, 3, 1), (24, 24, 4, 1), (4, 22, 2, 1)]:
    fill(z, x, y, w, h, (25, 15, 15), 3)
z.save(R + "entity/strongest_zombie.png")

# cabeca (item/bloco 16x16)
s = Image.new("RGBA", (16, 16), (0, 0, 0, 255)); fill(s, 0, 0, 16, 16, SK, 8)
fill(s, 0, 0, 16, 3, (35, 22, 20), 4)
s.save(R + "item/strongest_zombie_head_side.png")
f = Image.new("RGBA", (16, 16), (0, 0, 0, 255)); fill(f, 0, 0, 16, 16, SK, 8)
fill(f, 0, 0, 16, 3, (35, 22, 20), 4)
for x0 in (3, 10):
    fill(f, x0, 6, 3, 2, (225, 30, 20), 0)
fill(f, 5, 11, 6, 2, (30, 10, 10), 2)
f.save(R + "item/strongest_zombie_head_front.png")
print("ok")
