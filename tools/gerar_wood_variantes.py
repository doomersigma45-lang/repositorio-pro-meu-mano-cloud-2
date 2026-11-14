"""Texturas do Ancient Wood (boss) e do Fungus Stalker (hibrido). Feitas por IA, nao enviadas pelo usuario."""
import random
from PIL import Image
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"
def nz(c, n): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)

def build(name, seed, wood, dark, light, extra, eye, cap, spots):
    random.seed(seed)
    T = Image.new("RGBA", (64, 64), (0, 0, 0, 0)); G = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
    def box(u, v, w, h, d, base, stripe=None, noise=6):
        for i in range(u, u + 2 * d + 2 * w):
            for j in range(v, v + d + h):
                c = stripe if stripe and (i * 7 // 3) % 4 == 0 else base
                T.putpixel((i, j), nz(c, noise) + (255,))
    for b in [(0, 0, 6, 7, 6), (0, 14, 6, 12, 4), (24, 14, 2, 16, 2), (34, 14, 3, 12, 3), (0, 32, 1, 5, 1), (12, 32, 3, 3, 3)]:
        box(*b, wood, dark)
        u, v, w, h, d = b
        for _ in range((2 * w + 2 * d) * (d + h) // 10):
            i, j = random.randint(u, u + 2 * d + 2 * w - 1), random.randint(v, v + d + h - 1)
            T.putpixel((i, j), random.choice([dark, light, extra, extra, (24, 16, 9)]) + (255,))
    # decoracoes
    box(0, 40, 12, 3, 12, cap)                         # chapeu de cogumelo grande
    box(0, 56, 6, 2, 6, cap)                           # topo do chapeu
    box(26, 56, 4, 2, 4, cap)                          # cogumelos pequenos
    box(50, 40, 2, 3, 2, (232, 222, 192))              # talo
    box(50, 46, 2, 7, 2, (52, 36, 22), (86, 62, 36))   # raiz
    box(44, 56, 3, 1, 3, (240, 120, 160))              # petalas
    box(58, 56, 1, 4, 1, (60, 120, 40))                # talo da flor
    for (u, v, w, h, d) in [(0, 40, 12, 3, 12), (0, 56, 6, 2, 6), (26, 56, 4, 2, 4)]:
        for _ in range(max(4, (2 * w + 2 * d) * (d + h) // 12)):
            i, j = random.randint(u, u + 2 * d + 2 * w - 1), random.randint(v, v + d + h - 1)
            T.putpixel((i, j), spots + (255,))
    for i in range(44, 56):                            # petalas coloridas
        for j in range(56, 60):
            T.putpixel((i, j), random.choice([(240, 120, 160), (250, 220, 70), (245, 245, 245), (120, 150, 255)]) + (255,))
    for (x, y) in [(7, 10), (8, 10), (7, 11), (8, 11), (10, 10), (11, 10), (10, 11), (11, 11)]:
        T.putpixel((x, y), eye + (255,)); G.putpixel((x, y), eye + (255,))
    T.save(R + name + ".png"); G.save(R + name + "_eyes.png")

build("ancient_wood", 515, (44, 30, 17), (26, 17, 10), (66, 46, 26), (52, 78, 32), (255, 200, 60), (150, 100, 60), (235, 225, 200))
build("fungus_stalker", 616, (74, 46, 28), (46, 28, 18), (104, 66, 40), (150, 52, 52), (255, 150, 90), (178, 34, 34), (245, 240, 225))
print("ok")
