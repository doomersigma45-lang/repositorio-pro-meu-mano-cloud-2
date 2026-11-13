"""Texturas do Escavador, Automato e Tartaruga Ancestral (feitas por IA, nao enviadas pelo usuario)."""
import random
from PIL import Image
random.seed(44)
R = "forge-1.20.1/src/main/resources/assets/netheritesupremo/textures/entity/"
def nz(c, n=8): return tuple(max(0, min(255, v + random.randint(-n, n))) for v in c)
def fill(img, x, y, w, h, c, n=8):
    for i in range(int(x), int(x + w)):
        for j in range(int(y), int(y + h)):
            if 0 <= i < img.width and 0 <= j < img.height:
                img.putpixel((i, j), nz(c, n) + (255,))
def box(img, u, v, w, h, d, c, n=8): fill(img, u, v, 2 * d + 2 * w, d + h, c, n)

# escavador 64x64
d = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
SK = (138, 128, 118)
for (u, v, w, h, dd) in [(0,0,6,6,16),(0,24,4,3,8),(26,24,5,4,9),(0,38,4,2,9),(28,38,3,16,3),(42,38,5,11,5)]:
    box(d, u, v, w, h, dd, SK, 9)
for _ in range(50):   # manchas e veias escuras
    d.putpixel((random.randint(0, 44), random.randint(0, 22)), (88, 76, 70, 255))
for y in range(6, 22, 3):  # costelas
    fill(d, 0, y, 44, 1, (92, 82, 76), 4)
fill(d, 40, 24, 5, 9, (120, 20, 20), 6)   # boca (baixo da cabeca)
fill(d, 9, 38, 4, 9, (120, 20, 20), 6)    # boca (cima da mandibula)
fill(d, 0, 52, 4, 3, (235, 230, 215), 4)  # dentes / espinhos
fill(d, 8, 52, 6, 3, (60, 54, 50), 4)     # garras
for p in [(36, 35), (39, 35), (36, 36), (39, 36)]:
    d.putpixel(p, (0, 0, 0, 255))
d.save(R + "digger.png")

# automato 64x64
a = Image.new("RGBA", (64, 64), (0, 0, 0, 0)); g = Image.new("RGBA", (64, 64), (0, 0, 0, 0))
MT = (126, 132, 142)
for (u, v, w, h, dd) in [(0,0,6,6,6),(0,14,12,14,6),(0,36,5,16,5),(22,36,1.2,5,3),(36,36,5,8,5)]:
    box(a, u, v, w, h, dd, MT, 7)
for x in range(0, 36, 6):
    fill(a, x, 20, 1, 8, (80, 84, 92), 3)           # emendas das placas
fill(a, 0, 22, 36, 1, (80, 84, 92), 3)
for x in range(0, 24, 6):
    fill(a, x, 4, 1, 6, (80, 84, 92), 3)
for (x, y) in [(6, 8), (7, 8), (8, 8), (9, 8), (10, 8)]:  # visor (frente da cabeca: (6,6) 6x6)
    pass
for x in range(7, 11):
    a.putpixel((x, 9), (80, 200, 255, 255)); g.putpixel((x, 9), (110, 215, 255, 255))
    a.putpixel((x, 10), (80, 200, 255, 255)); g.putpixel((x, 10), (110, 215, 255, 255))
for x in range(9, 13):                                    # nucleo no peito (frente do torso: (6,20) 12x14)
    for y in range(24, 28):
        a.putpixel((x + 3, y), (80, 200, 255, 255)); g.putpixel((x + 3, y), (130, 225, 255, 255))
a.save(R + "automaton.png"); g.save(R + "automaton_glow.png")

# tartaruga ancestral 128x128
t = Image.new("RGBA", (128, 128), (0, 0, 0, 0))
DK = (46, 44, 38)
for (u, v, w, h, dd) in [(0,0,20,8,24),(0,34,14,3,18),(0,57,16,2,20),(0,81,5,6,5),(22,81,5,1,2),(38,81,6,6,7),(66,81,8,7,6),(96,81,4,2,3)]:
    box(t, u, v, w, h, dd, DK, 6)
box(t, 0, 57, 16, 2, 20, (150, 140, 110), 6)           # barriga clara
box(t, 0, 81, 5, 6, 5, (70, 78, 60), 6)                 # patas esverdeadas
box(t, 38, 81, 6, 6, 7, (70, 78, 60), 6)
box(t, 66, 81, 8, 7, 6, (78, 86, 66), 6)
box(t, 96, 81, 4, 2, 3, (170, 140, 70), 5)              # bico
for x in range(0, 88, 8):
    fill(t, x, 0, 1, 32, (28, 27, 23), 3)               # placas osseas do casco
fill(t, 0, 12, 88, 1, (28, 27, 23), 3)
fill(t, 0, 36, 64, 1, (28, 27, 23), 3)
box(t, 0, 96, 2, 2, 6, (225, 220, 200), 5)              # fosseis
box(t, 18, 96, 3, 1, 3, (60, 130, 55), 8)               # plantas
for p in [(76, 85), (79, 85), (76, 86), (79, 86)]:
    t.putpixel(p, (10, 10, 10, 255))
for _ in range(60):
    t.putpixel((random.randint(0, 63), random.randint(34, 54)), (60, 130, 55, 255))
t.save(R + "ancient_turtle.png")
print("ok")
