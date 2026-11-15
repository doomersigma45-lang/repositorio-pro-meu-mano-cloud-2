"""Texturas dos infectados (1.67.1): carne, osso e sangue sobre os UVs dos modelos de vanilla + espinhos de carne.
Inspirado visualmente no estilo dos infectados do Scape and Run: Parasites (nada copiado deles)."""
import math
import os
import random

from PIL import Image

OUT = os.path.join(os.path.dirname(__file__), "..", "forge-1.20.1", "src", "main", "resources", "assets",
                   "netheritesupremo", "textures", "entity")
random.seed(671)

BLOOD = (150, 14, 20)
FLESH = (112, 22, 28)
FLESH_L = (168, 44, 46)
DARK = (46, 8, 12)
BONE = (214, 206, 186)
BONE_D = (160, 150, 132)
BLACK = (22, 18, 22)
GREY = (74, 76, 80)


def mix(a, b, t):
    return tuple(int(a[i] + (b[i] - a[i]) * t) for i in range(3))


class Noise:
    def __init__(self, seed):
        self.r = random.Random(seed)
        self.cache = {}

    def lat(self, x, y):
        k = (x, y)
        if k not in self.cache:
            self.cache[k] = self.r.random()
        return self.cache[k]

    def get(self, x, y, scale):
        fx, fy = x / scale, y / scale
        x0, y0 = math.floor(fx), math.floor(fy)
        tx, ty = fx - x0, fy - y0
        tx, ty = tx * tx * (3 - 2 * tx), ty * ty * (3 - 2 * ty)
        a = self.lat(x0, y0) * (1 - tx) + self.lat(x0 + 1, y0) * tx
        b = self.lat(x0, y0 + 1) * (1 - tx) + self.lat(x0 + 1, y0 + 1) * tx
        return a * (1 - ty) + b * ty


PH = 1


def sheet(w, h, base, base2, flesh_amt, bone_amt, seed, vein=BLOOD):
    flesh_amt = min(0.8, flesh_amt + 0.12 * (PH - 1))
    bone_amt = bone_amt + 0.05 * (PH - 1)
    """Folha inteira: material base (pele/pelo) + manchas de carne + osso exposto + veias."""
    n1, n2, n3 = Noise(seed), Noise(seed + 1), Noise(seed + 2)
    im = Image.new("RGBA", (w, h), (0, 0, 0, 255))
    px = im.load()
    for y in range(h):
        for x in range(w):
            c = mix(base, base2, n3.get(x, y, 3.0))
            f = n1.get(x, y, 6.0) * 0.7 + n1.get(x, y, 2.5) * 0.3
            if f > 1 - flesh_amt:
                t = n2.get(x, y, 2.0)
                c = mix(FLESH, FLESH_L, t) if t > 0.35 else mix(DARK, FLESH, t * 2)
                if n2.get(x + 40, y, 1.6) > 0.8:
                    c = BLOOD
            b = n2.get(x + 7, y + 3, 7.0)
            if b > 1 - bone_amt and f > 0.35:
                c = mix(BONE_D, BONE, n3.get(x, y, 1.5))
            # veias finas e escuras
            if n3.get(x * 2, y, 2.2) > 0.87:
                c = vein
            if random.random() < 0.05:
                c = mix(c, (0, 0, 0), 0.35)
            if PH >= 3:
                c = mix(c, DARK, 0.28)
            px[x, y] = c + (255,)
    return im


def put(im, pts, col):
    px = im.load()
    for x, y in pts:
        if 0 <= x < im.width and 0 <= y < im.height:
            px[x, y] = col + (255,)


def save(im, name):
    os.makedirs(OUT, exist_ok=True)
    suffix = "" if PH == 1 or name == "parasite_spikes" else "_%d" % PH
    im.save(os.path.join(OUT, name + suffix + ".png"))


GLOW = (255, 70, 50)
EYE_DARK = (20, 0, 0)

def build():
    global PH
    # --- zumbi (64x64): pele cinza, carne exposta, osso ---
    im = sheet(64, 64, (78, 92, 76), (60, 70, 62), 0.42, 0.14, 10)
    put(im, [(9, 11), (10, 11), (13, 11), (14, 11)], GLOW)
    put(im, [(9, 12), (14, 12)], EYE_DARK)
    put(im, [(10, 14), (11, 14), (12, 14), (13, 14), (10, 15), (13, 15)], DARK)
    put(im, [(11, 15), (12, 15)], BONE)  # dentes
    save(im, "parasite_zombie")

    # --- esqueleto (64x32): osso com manchas de sangue e carne ---
    im = sheet(64, 32, BONE, BONE_D, 0.34, 0.0, 20)
    put(im, [(9, 11), (10, 11), (9, 12), (10, 12), (13, 11), (14, 11), (13, 12), (14, 12)], EYE_DARK)
    put(im, [(10, 11), (13, 11)], GLOW)
    put(im, [(10, 14), (11, 14), (12, 14), (13, 14)], DARK)
    put(im, [(10, 15), (12, 15)], BONE)
    save(im, "parasite_skeleton")

    # --- creeper (64x32): pele escura rachada com carne ---
    im = sheet(64, 32, (38, 58, 40), (24, 40, 28), 0.42, 0.06, 30)
    put(im, [(9, 10), (10, 10), (13, 10), (14, 10), (9, 11), (10, 11), (13, 11), (14, 11)], GLOW)
    put(im, [(11, 12), (12, 12), (10, 13), (11, 13), (12, 13), (13, 13), (10, 14), (13, 14)], DARK)
    save(im, "parasite_creeper")

    # --- aranha (64x32): couro preto com faixas de carne ---
    im = sheet(64, 32, (30, 24, 26), (18, 14, 16), 0.4, 0.08, 40)
    put(im, [(42, 15), (43, 15), (45, 15), (46, 15), (41, 17), (47, 17), (43, 17), (45, 17)], GLOW)
    save(im, "parasite_spider")

    # --- vaca (64x32), porco (64x32), galinha (64x32): hide parcial + carne ---
    im = sheet(64, 32, (72, 52, 40), (30, 24, 24), 0.42, 0.1, 50)
    put(im, [(7, 9), (12, 9)], GLOW)
    save(im, "parasite_cow")
    im = sheet(64, 32, (200, 130, 130), (170, 96, 100), 0.42, 0.08, 60)
    put(im, [(10, 11), (14, 11)], GLOW)
    save(im, "parasite_pig")
    im = sheet(64, 32, (210, 206, 196), (150, 130, 126), 0.42, 0.08, 70)
    put(im, [(3, 5), (6, 5)], GLOW)
    save(im, "parasite_chicken")

    # --- verme / endermite (64x32) ---
    im = sheet(64, 32, (100, 20, 26), (60, 10, 16), 0.5, 0.18, 80)
    save(im, "parasite_worm")

    # --- enderman (64x32): preto com veias carmesim/magenta ---
    im = sheet(64, 32, (16, 14, 20), (28, 18, 30), 0.3, 0.04, 90, vein=(190, 24, 84))
    for y in range(32):
        for x in range(64):
            px = im.load()
            if random.random() < 0.012:
                px[x, y] = (200, 30, 90, 255)
    put(im, [(9, 12), (10, 12), (11, 12), (12, 12), (13, 12), (14, 12), (9, 13), (14, 13)], (255, 40, 110))
    put(im, [(10, 14), (11, 14), (12, 14), (13, 14)], (90, 6, 40))
    save(im, "parasite_enderman")



for _ph in (1, 2, 3):
    PH = _ph
    build()

PH = 1
# --- espinhos / tentaculos (64x16): base clara, corpo de carne, ponta de sangue ---
im = Image.new("RGBA", (64, 16), (0, 0, 0, 255))
px = im.load()
nz = Noise(99)
for y in range(16):
    for x in range(64):
        t = (x % 32) / 32.0
        c = mix(BONE, FLESH_L, min(1, t * 3)) if t < 0.33 else mix(FLESH, BLOOD, (t - 0.33) * 1.4)
        c = mix(c, DARK, nz.get(x, y, 2.0) * 0.5)
        px[x, y] = c + (255,)
save(im, "parasite_spikes")
print("ok")
