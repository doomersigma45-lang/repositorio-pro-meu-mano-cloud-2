"""Texturas do Guardiao Ancestral (golem de pedra) e de O Abismo (feitas por IA, nao enviadas pelo usuario)."""
import random
from PIL import Image
random.seed(77)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"
def nz(c, n=8): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)
def fill(img, x, y, w, h, c, n=8):
    for i in range(x, x + w):
        for j in range(y, y + h):
            if 0 <= i < img.width and 0 <= j < img.height: img.putpixel((i, j), nz(c, n) + (255,))
def faces(u, v, w, h, d):
    return [(u + d, v, w, d), (u + d + w, v, w, d), (u, v + d, d, h), (u + d, v + d, w, h), (u + d + w, v + d, d, h), (u + 2 * d + w, v + d, w, h)]
def paint(img, boxes, base, n, edge):
    for (u, v, w, h, d) in boxes:
        fill(img, u, v, 2 * d + 2 * w, d + h, base, n)
        for (fx, fy, fw, fh) in faces(u, v, w, h, d):
            for i in range(fx, fx + fw):
                for j in range(fy, fy + fh):
                    if i in (fx, fx + fw - 1) or j in (fy, fy + fh - 1):
                        img.putpixel((i, j), nz(edge, 5) + (255,))
RUNES = ["1110011100", "0100101010", "1011101001", "0111010110"]
def rune(T, G, fx, fy, color):
    """pequena runa 3x5 aleatoria"""
    pat = [[random.random() < 0.6 for _ in range(3)] for _ in range(5)]
    for r in range(5): pat[r][1] = pat[r][1] or r in (0, 2, 4)
    for r in range(5):
        for c in range(3):
            if pat[r][c] and 0 <= fx + c < 128 and 0 <= fy + r < 128 and T.getpixel((fx + c, fy + r))[3]:
                T.putpixel((fx + c, fy + r), color + (255,)); G.putpixel((fx + c, fy + r), color + (255,))

# ================= Guardiao Ancestral (128x128)
T = Image.new("RGBA", (128, 128), (0, 0, 0, 0)); G = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
BOX = {"head": (0, 0, 14, 12, 12), "brow": (54, 0, 14, 3, 3), "torso": (0, 24, 24, 18, 14), "shoulder": (0, 58, 10, 10, 10),
       "uarm": (42, 58, 8, 14, 8), "fist": (0, 80, 11, 10, 11), "thigh": (46, 80, 9, 6, 9), "shin": (0, 102, 11, 7, 11), "pebble": (92, 0, 5, 5, 5)}
paint(T, BOX.values(), (122, 120, 114), 9, (84, 82, 78))
for _ in range(260):   # rachaduras e musgo
    x, y = random.randint(0, 127), random.randint(0, 127)
    if T.getpixel((x, y))[3]:
        T.putpixel((x, y), random.choice([(72, 70, 66, 255), (88, 104, 70, 255), (150, 148, 140, 255)]))
GOLD = (255, 205, 70)
hu, hv, hw, hh, hd = BOX["head"]
fx, fy = hu + hd, hv + hd        # frente da cabeca
for ex in (fx + 2, fx + 8):      # duas fendas douradas
    for dx in range(4):
        for dy in range(2):
            T.putpixel((ex + dx, fy + 8 + dy), GOLD + (255,)); G.putpixel((ex + dx, fy + 8 + dy), GOLD + (255,))
for name in ("torso", "uarm", "fist", "thigh", "shin", "shoulder"):   # runas brilhantes
    u, v, w, h, d = BOX[name]
    for (fx_, fy_, fw, fh) in faces(u, v, w, h, d)[2:]:
        for _ in range(max(1, (fw * fh) // 45)):
            rune(T, G, random.randint(fx_ + 1, max(fx_ + 1, fx_ + fw - 4)), random.randint(fy_ + 1, max(fy_ + 1, fy_ + fh - 6)), random.choice([GOLD, (255, 230, 140)]))
T.save(R + "ancestral_guardian.png"); G.save(R + "ancestral_guardian_glow.png")

# ================= O Abismo (128x128)
T = Image.new("RGBA", (128, 128), (0, 0, 0, 0)); G = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
BOX = {"top": (0, 0, 22, 7, 20), "bottom": (0, 28, 22, 7, 20), "back": (0, 56, 24, 20, 8), "side": (0, 86, 7, 10, 14),
       "core": (66, 56, 10, 10, 10), "spike": (88, 0, 4, 8, 4), "lump": (88, 14, 6, 6, 6),
       "t1": (66, 78, 5, 8, 5), "t2": (88, 78, 4, 8, 4), "t3": (106, 78, 3, 8, 3)}
for k, b in BOX.items():
    if k != "core": paint(T, [b], (24, 14, 34), 7, (14, 8, 22))
paint(T, [BOX["core"]], (150, 40, 255), 10, (200, 120, 255))
for _ in range(500):     # veias roxas fracas
    x, y = random.randint(0, 127), random.randint(0, 127)
    if T.getpixel((x, y))[3] and not (66 <= x < 106 and 56 <= y < 76):
        T.putpixel((x, y), random.choice([(44, 20, 62, 255), (58, 26, 84, 255), (12, 6, 20, 255)]))
cu, cv, cw, ch, cd = BOX["core"]
for i in range(cu, cu + 2 * cd + 2 * cw):
    for j in range(cv, cv + cd + ch):
        if T.getpixel((i, j))[3]: G.putpixel((i, j), (190, 90, 255, 255))
for k in ("top", "bottom", "back", "side", "lump"):    # varios olhos pequenos espalhados
    u, v, w, h, d = BOX[k]
    for (fx_, fy_, fw, fh) in faces(u, v, w, h, d):
        for _ in range(max(1, (fw * fh) // 40)):
            ex, ey = random.randint(fx_, fx_ + fw - 2), random.randint(fy_, fy_ + fh - 2)
            for dx, dy, c in ((0, 0, (255, 255, 255)), (1, 0, (230, 140, 255)), (0, 1, (230, 140, 255)), (1, 1, (230, 140, 255))):
                if T.getpixel((ex + dx, ey + dy))[3]:
                    T.putpixel((ex + dx, ey + dy), c + (255,)); G.putpixel((ex + dx, ey + dy), c + (255,))
T.save(R + "the_abyss.png"); G.save(R + "the_abyss_glow.png")
print("texturas ok")
