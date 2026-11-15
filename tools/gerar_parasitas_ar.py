"""Texturas do Totem de Cranios e da Flor parasita (1.69.0), inspiradas nas imagens de referencia do usuario."""
import os
import random

from PIL import Image

OUT = os.path.join(os.path.dirname(__file__), "..", "forge-1.20.1", "src", "main", "resources", "assets",
                   "netheritesupremo", "textures", "entity")
random.seed(69)


def black(im, x0, y0, x1, y1, base=(20, 20, 24)):
    px = im.load()
    for y in range(y0, y1):
        for x in range(x0, x1):
            k = random.randint(-7, 9)
            px[x, y] = (max(0, base[0] + k), max(0, base[1] + k), max(0, base[2] + k + 1), 255)


def fill(im, x0, y0, x1, y1, a, b):
    px = im.load()
    for y in range(y0, y1):
        for x in range(x0, x1):
            t = random.random()
            px[x, y] = tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3)) + (255,)


def dots(im, x0, y0, x1, y1, col, n):
    px = im.load()
    for _ in range(n):
        px[random.randint(x0, x1 - 1), random.randint(y0, y1 - 1)] = col + (255,)


# ---- Totem: tronco preto + cranios de osso (UV do cranio 8x8x8 em (0,0): frente em x8..15, y8..15) ----
t = Image.new("RGBA", (64, 64), (0, 0, 0, 255))
black(t, 0, 0, 64, 64)
fill(t, 0, 0, 32, 16, (178, 174, 164), (222, 218, 204))
px = t.load()
for x, y in [(9, 10), (10, 10), (9, 11), (10, 11), (13, 10), (14, 10), (13, 11), (14, 11)]:
    px[x, y] = (14, 14, 16, 255)
for x in range(10, 14):
    px[x, 13] = (20, 18, 18, 255)
for x in (9, 11, 13):
    px[x, 14] = (236, 232, 214, 255)  # dentes
    px[x, 15] = (210, 206, 190, 255)
for x, y in [(11, 11), (12, 11), (11, 12), (12, 12)]:
    px[x, y] = (40, 36, 36, 255)  # nariz
dots(t, 0, 0, 32, 16, (120, 116, 108), 30)
t.save(os.path.join(OUT, "parasite_totem.png"))

# ---- Flor: haste preta + copa amarelo-esverdeada + pontos amarelos na base ----
b = Image.new("RGBA", (64, 64), (0, 0, 0, 255))
black(b, 0, 0, 64, 64, (18, 18, 20))
fill(b, 0, 0, 48, 20, (150, 190, 20), (214, 232, 40))
dots(b, 0, 0, 48, 20, (96, 130, 10), 60)
dots(b, 0, 46, 64, 64, (200, 220, 30), 40)
dots(b, 0, 26, 64, 46, (120, 150, 16), 12)
b.save(os.path.join(OUT, "parasite_bloom.png"))
print("ok")
