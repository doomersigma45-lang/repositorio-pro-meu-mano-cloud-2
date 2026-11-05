#!/usr/bin/env python3
"""Gera o laboratorio de robos (robot_lab.nbt): 4 andares subterraneos + pavilhao na superficie.
Andar 1 e 2 (faceis): Bombot; andar 3 (medio): Netherbot; andar 4 (dificil): Rainbow Bot + Netherbots.
Uso: python3 tools/gerar_laboratorio.py"""
import os, re, random
from gerar_cidade import write_nbt, B, I, D

SX = SZ = 47
SY = 44
BASE = {0: 33, 1: 25, 2: 17, 3: 9, 4: 1}      # y do piso de cada nivel (0 = teto/pavilhao)
AIR = 'minecraft:air'
HULL = 'minecraft:gray_concrete'
WHITE = 'minecraft:white_concrete'; LGRAY = 'minecraft:light_gray_concrete'; BLACK = 'minecraft:black_concrete'
CYAN = 'minecraft:cyan_concrete'; QUARTZ = 'minecraft:smooth_quartz'; IRON = 'minecraft:iron_block'
LANT = 'minecraft:sea_lantern'; BARS = 'minecraft:iron_bars'; GLASS = 'minecraft:glass'
CGLASS = 'minecraft:cyan_stained_glass'; BSTONE = 'minecraft:polished_blackstone_bricks'
RED = 'minecraft:red_nether_bricks'; NBRICK = 'minecraft:nether_bricks'
STAIR = 'minecraft:smooth_quartz_stairs[facing=east,half=bottom]'
RAINBOW = ['red', 'orange', 'yellow', 'lime', 'cyan', 'blue', 'purple', 'magenta']
def conc(c): return 'minecraft:%s_concrete' % c
def sglass(c): return 'minecraft:%s_stained_glass' % c

THEME = {  # piso, parede, destaque
    1: (WHITE, LGRAY, CYAN),
    2: (LGRAY, 'minecraft:gray_concrete', CYAN),
    3: (BSTONE, 'minecraft:blackstone', RED),
    4: (BLACK, BLACK, 'minecraft:purple_concrete'),
}
LOOTS = {1: 'netheritesupremo:chests/robot_lab_easy', 2: 'netheritesupremo:chests/robot_lab_easy',
         3: 'netheritesupremo:chests/robot_lab_medium', 4: 'netheritesupremo:chests/robot_lab_hard'}

class Lab:
    def __init__(self): self.b = {}; self.nbt = {}; self.ents = []
    def set(self, x, y, z, s, nbt=None):
        if 0 <= x < SX and 0 <= y < SY and 0 <= z < SZ:
            self.b[(x, y, z)] = s
            if nbt: self.nbt[(x, y, z)] = nbt
            elif (x, y, z) in self.nbt: del self.nbt[(x, y, z)]
    def get(self, x, y, z): return self.b.get((x, y, z), AIR)
    def air(self, x, y, z): return self.get(x, y, z) == AIR
    def box(self, x1, y1, z1, x2, y2, z2, s):
        for x in range(min(x1, x2), max(x1, x2) + 1):
            for y in range(min(y1, y2), max(y1, y2) + 1):
                for z in range(min(z1, z2), max(z1, z2) + 1): self.set(x, y, z, s)

def stairs(c, sx, sz, bu):
    """Escada descendo para -x, ABERTA: o buraco no piso de cima cobre a escada inteira (sem blocos no caminho),
    com grades em volta. Ocupa x sx..sx+7, z sz..sz+2; o andar de baixo comeca em x=sx-1."""
    bl = bu - 8
    for i in range(8):
        x = sx + i
        for z in range(sz, sz + 3):
            for y in range(bl + 1, bl + 1 + i): c.set(x, y, z, QUARTZ)      # enchimento sob o degrau
            c.set(x, bl + 1 + i, z, STAIR)
            if i < 7:
                for y in range(bl + 2 + i, bu + 1): c.set(x, y, z, AIR)      # abre tudo acima do degrau, ate o piso de cima
            for y in range(bu + 1, bu + 3): c.set(x, y, z, AIR)
    for x in range(sx, sx + 8):                                              # paredes laterais da escada (andar de baixo e borda)
        for y in range(bl + 1, bu + 1):
            for z in (sz - 1, sz + 3): c.set(x, y, z, QUARTZ)
    # grades no andar de cima: dois lados e a ponta oeste (a entrada e pelo lado leste, no degrau mais alto)
    for x in range(sx, sx + 7):
        for y in (bu + 1, bu + 2):
            c.set(x, y, sz - 1, BARS); c.set(x, y, sz + 3, BARS)
    for z in range(sz - 1, sz + 4):
        for y in (bu + 1, bu + 2): c.set(sx - 1, y, z, BARS)

STAIRS = {0: (19, 22), 1: (33, 5), 2: (4, 38), 3: (4, 5)}     # nivel de cima -> (sx, sz)

def keepout(f, x, z):
    """Celulas que a decoracao nao pode ocupar (escadas, portas, vaos)."""
    for lvl in (f, f - 1):
        if lvl in STAIRS:
            sx, sz = STAIRS[lvl]
            if sx - 2 <= x <= sx + 9 and sz - 2 <= z <= sz + 4: return True
    return False

ROOMS = [(2, 2), (31, 2), (2, 31), (31, 31)]     # canto (x0,z0) das 4 salas 14x14

def floor_build(c, f, rnd):
    fl, wl, ac = THEME[f]
    b = BASE[f]
    c.box(2, b + 1, 2, 44, b + 7, 44, AIR)
    c.box(2, b, 2, 44, b, 44, fl)
    # divisorias das salas (paredes x=16,30 e z=16,30 nos cantos) com portas de 3 de largura
    for (x0, z0) in ROOMS:
        xr = 16 if x0 == 2 else 30
        zr = 16 if z0 == 2 else 30
        for y in range(b + 1, b + 8):
            for z in range(z0, z0 + 14): c.set(xr, y, z, wl)
            for x in range(x0, x0 + 14): c.set(x, y, zr, wl)
            c.set(xr, y, zr, wl)
        zc = z0 + 6; xc = x0 + 6
        c.box(xr, b + 1, zc, xr, b + 4, zc + 2, AIR)
        c.box(xc, b + 1, zr, xc + 2, b + 4, zr, AIR)
        for dy in (4, 5):   # faixa de cor acima das portas
            c.box(xr, b + dy, zc, xr, b + dy, zc + 2, ac)
        c.box(xc, b + 5, zr, xc + 2, b + 5, zr, ac)
    # faixas de luz e luminarias no teto
    for x in range(4, 44, 6):
        for z in range(4, 44, 6):
            if c.air(x, b + 7, z): c.set(x, b + 7, z, LANT)
    # pilares do salao central + piso decorado
    for (px, pz) in ((19, 19), (27, 19), (19, 27), (27, 27)):
        c.box(px, b + 1, pz, px + 1, b + 6, pz + 1, ac)
        c.set(px, b + 7, pz, LANT)
    for x in range(17, 30):
        for z in range(17, 30):
            d = max(abs(x - 23), abs(z - 23))
            if d in (2, 4, 6) and not keepout(f, x, z):
                c.set(x, b, z, conc(RAINBOW[(d // 2 + f) % 8]) if f == 4 else ac)
    # decoracao das salas
    for k, (x0, z0) in enumerate(ROOMS):
        kind = (k + f) % 4
        for x in range(x0 + 1, x0 + 13):
            for z in range(z0 + 1, z0 + 13):
                if keepout(f, x, z): continue
                lx, lz = x - x0, z - z0
                if kind == 0 and lz in (3, 10) and 2 <= lx <= 11:            # bancadas
                    c.set(x, b + 1, z, IRON); c.set(x, b + 2, z, 'minecraft:quartz_slab[type=bottom]')
                elif kind == 1 and lx % 5 == 3 and lz % 5 == 3:              # tubos de ensaio
                    c.box(x, b + 1, z, x + 1, b + 5, z + 1, sglass(RAINBOW[(lx + lz + f) % 8]))
                    c.set(x, b + 6, z, ac)
                elif kind == 2 and (lx in (1, 12)) and lz % 3 == 1:          # racks de servidor
                    c.box(x, b + 1, z, x, b + 3, z, IRON); c.set(x, b + 3, z, 'minecraft:redstone_lamp[lit=true]')
                elif kind == 3 and lx in (3, 10) and lz in (3, 10):          # pilares
                    c.box(x, b + 1, z, x, b + 6, z, ac)
    # escadas deste nivel para o de baixo
    if f in STAIRS: stairs(c, STAIRS[f][0], STAIRS[f][1], b)

def mob(c, eid, x, y, z, extra=None):
    nbt = {'id': eid, 'PersistenceRequired': B(1)}
    if extra: nbt.update(extra)
    c.ents.append({'pos': [D(x + 0.5), D(y), D(z + 0.5)], 'blockPos': [I(x), I(y), I(z)], 'nbt': nbt})

def free_spot(c, x, z, b):
    for r in range(0, 6):
        for dx in range(-r, r + 1):
            for dz in range(-r, r + 1):
                xx, zz = x + dx, z + dz
                if c.air(xx, b + 1, zz) and c.air(xx, b + 2, zz) and c.air(xx, b + 3, zz) and c.get(xx, b, zz) != AIR:
                    return xx, zz
    return x, z

def chest(c, x, z, f):
    b = BASE[f]
    x, z = free_spot(c, x, z, b)
    c.set(x, b + 1, z, 'minecraft:chest[facing=south,type=single,waterlogged=false]',
          {'id': 'minecraft:chest', 'LootTable': LOOTS[f]})

BOMB, NBOT, RBOT = 'netheritesupremo:netherite_bomb_robot', 'netheritesupremo:netherbot', 'netheritesupremo:netherite_robot_rainbow_boss'

def populate(c):
    for f in (1, 2, 3, 4):
        b = BASE[f]
        eid = BOMB if f <= 2 else NBOT
        spots = []
        for (x0, z0) in ROOMS:
            spots += [(x0 + 10, z0 + 11), (x0 + 11, z0 + 3)]
        if f >= 2: spots += [(23, 6), (23, 40), (6, 23), (40, 23)]
        if f == 1: spots = spots[:8]
        if f == 4: spots = [(x0 + 9, z0 + 10) for (x0, z0) in ROOMS] + [(23, 8), (23, 38)]
        for (x, z) in spots:
            x, z = free_spot(c, x, z, b)
            mob(c, eid, x, b + 1, z)
        # baus: duas salas por andar
        for k, (x0, z0) in enumerate(ROOMS):
            if (k + f) % 2 == 0: chest(c, x0 + 12, z0 + 12, f)
    # andar 4: chefe no salao central + cofre extra
    mob(c, RBOT, 23, BASE[4] + 2, 23)
    chest(c, 23, 40, 4); chest(c, 40, 23, 4)

def pavilion(c):
    b = BASE[0]
    c.box(16, b + 1, 16, 30, b + 8, 30, AIR)
    for y in range(b + 1, b + 8):
        for x in range(16, 31):
            for z in (16, 30):
                c.set(x, y, z, GLASS if (x % 3 and 2 <= y - b <= 6) else WHITE)
        for z in range(16, 31):
            for x in (16, 30):
                c.set(x, y, z, GLASS if (z % 3 and 2 <= y - b <= 6) else WHITE)
    for (x, z) in ((16, 16), (16, 30), (30, 16), (30, 30)): c.box(x, b + 1, z, x, b + 8, z, WHITE)
    c.box(16, b + 8, 16, 30, b + 8, 30, BLACK)
    for x in range(18, 30, 3):
        for z in range(18, 30, 3): c.set(x, b + 8, z, LANT)
    c.box(30, b + 1, 22, 30, b + 3, 24, AIR)           # porta (lado leste)
    c.box(16, b, 16, 30, b, 30, QUARTZ)
    for i in range(0, 15, 2): c.set(16 + i, b, 16, CYAN); c.set(16 + i, b, 30, CYAN)
    # entrada da escada para o andar 1
    sx, sz = STAIRS[0]
    c.box(sx + 5, b, sz, sx + 6, b, sz + 2, AIR)
    # faixa de luz no telhado do complexo
    for t in range(2, 45, 4):
        c.set(t, b, 2, LANT); c.set(t, b, 44, LANT); c.set(2, b, t, LANT); c.set(44, b, t, LANT)

def build():
    c = Lab()
    c.box(0, 0, 0, SX - 1, BASE[0], SZ - 1, HULL)
    rnd = random.Random(7)
    for f in (4, 3, 2, 1): floor_build(c, f, rnd)
    # o piso do andar f e o teto de f+1; escadas de cima cortam o piso (feito em stairs())
    c.box(2, BASE[0], 2, 44, BASE[0], 44, LGRAY)
    pavilion(c)
    stairs(c, STAIRS[0][0], STAIRS[0][1], BASE[0])
    populate(c)
    palette, idx, blocks = [], {}, []
    def pal(s):
        if s not in idx:
            m = re.match(r'([^\[]+)(?:\[(.*)\])?$', s)
            e = {'Name': m.group(1)}
            if m.group(2): e['Properties'] = dict(p.split('=') for p in m.group(2).split(','))
            idx[s] = len(palette); palette.append(e)
        return idx[s]
    for (x, y, z), s in sorted(c.b.items()):
        e = {'pos': [I(x), I(y), I(z)], 'state': I(pal(s))}
        if (x, y, z) in c.nbt: e['nbt'] = c.nbt[(x, y, z)]
        blocks.append(e)
    return {'DataVersion': I(3465), 'size': [I(SX), I(SY), I(SZ)], 'palette': palette, 'blocks': blocks, 'entities': c.ents}

if __name__ == '__main__':
    out = os.path.join(os.path.dirname(__file__), '..', 'forge-1.20.1', 'src', 'main', 'resources', 'data', 'netheritesupremo', 'structures')
    t = build()
    write_nbt(os.path.join(out, 'robot_lab.nbt'), t)
    from collections import Counter
    print(len(t['blocks']), 'blocos;', dict(Counter(e['nbt']['id'] for e in t['entities'])))
