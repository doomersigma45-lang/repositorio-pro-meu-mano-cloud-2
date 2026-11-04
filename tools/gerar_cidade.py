#!/usr/bin/env python3
"""Gera as estruturas da cidade futurista de Plutao (pluto_city.nbt e pluto_city_boss.nbt).
Uso: python3 tools/gerar_cidade.py   (escreve em forge-1.20.1/src/main/resources/data/netheritesupremo/structures)"""
import gzip, os, struct, re

# ---------------------------------------------------------------- NBT minimo
class B(int): pass
class I(int): pass
class D(float): pass
def _s(x): b = x.encode(); return struct.pack('>H', len(b)) + b
def _tid(v):
    if isinstance(v, B): return 1
    if isinstance(v, I): return 3
    if isinstance(v, D): return 6
    if isinstance(v, str): return 8
    if isinstance(v, list): return 9
    if isinstance(v, dict): return 10
    raise TypeError(v)
def _pay(v):
    if isinstance(v, B): return struct.pack('>b', v)
    if isinstance(v, I): return struct.pack('>i', v)
    if isinstance(v, D): return struct.pack('>d', v)
    if isinstance(v, str): return _s(v)
    if isinstance(v, list):
        t = _tid(v[0]) if v else 1
        return struct.pack('>bi', t, len(v)) + b''.join(_pay(x) for x in v)
    if isinstance(v, dict):
        out = b''
        for k, x in v.items(): out += struct.pack('>b', _tid(x)) + _s(k) + _pay(x)
        return out + b'\0'
def write_nbt(path, root):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with gzip.GzipFile(path, 'wb', mtime=0) as f:
        f.write(struct.pack('>b', 10) + _s('') + _pay(root))

# ---------------------------------------------------------------- construtor
SX = SZ = 61
SY = 70
FLOOR = 5      # camada do piso (nivel da superficie); caminha-se em y=6
class City:
    def __init__(self): self.b = {}; self.nbt = {}
    def set(self, x, y, z, s, nbt=None):
        if 0 <= x < SX and 0 <= z < SZ and 0 <= y < SY:
            self.b[(x, y, z)] = s
            if nbt: self.nbt[(x, y, z)] = nbt
    def free(self, x, y, z): return self.b.get((x, y, z), 'minecraft:air') == 'minecraft:air'
    def box(self, x1, y1, z1, x2, y2, z2, s):
        for x in range(min(x1, x2), max(x1, x2) + 1):
            for y in range(min(y1, y2), max(y1, y2) + 1):
                for z in range(min(z1, z2), max(z1, z2) + 1): self.set(x, y, z, s)
    def shell(self, x1, y1, z1, x2, y2, z2, s):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1):
                    if x in (x1, x2) or z in (z1, z2): self.set(x, y, z, s)

AIR = 'minecraft:air'
WHITE = 'minecraft:white_concrete'; LGRAY = 'minecraft:light_gray_concrete'; GRAY = 'minecraft:gray_concrete'
BLACK = 'minecraft:black_concrete'; CYAN = 'minecraft:cyan_concrete'; GLASS = 'minecraft:cyan_stained_glass'
LANT = 'minecraft:sea_lantern'; QUARTZ = 'minecraft:smooth_quartz'; ROD = 'minecraft:end_rod[facing=up]'
BSTONE = 'minecraft:polished_blackstone'
CHEST = 'minecraft:chest[facing=south,type=single,waterlogged=false]'
LOOT = {'id': 'minecraft:chest', 'LootTable': 'netheritesupremo:chests/pluto_city'}

def chest(c, x, y, z): c.set(x, y, z, CHEST, LOOT)

def skyscraper(c, cx, cz, h, side_x, side_z):
    x1, x2, z1, z2 = cx - 5, cx + 5, cz - 5, cz + 5
    top = 6 + h
    c.box(x1, 6, z1, x2, top, z2, AIR)
    c.shell(x1, 6, z1, x2, top, z2, GLASS)
    for y in range(6, top + 1):                       # colunas de canto + faixas
        for (x, z) in ((x1, z1), (x1, z2), (x2, z1), (x2, z2)): c.set(x, y, z, WHITE)
        if (y - 6) % 6 == 0:
            c.shell(x1, y, z1, x2, y, z2, WHITE)
    for y in range(6, top + 1):
        c.set(cx, y, z1, LGRAY)                       # coluna da escada
    for y in range(7, top + 2): c.set(cx, y, z1 + 1, 'minecraft:ladder[facing=south]')
    for k in range(1, h // 6 + 1):                    # pisos
        y = 6 + 6 * k
        c.box(x1 + 1, y, z1 + 1, x2 - 1, y, z2 - 1, QUARTZ)
        c.set(cx, y, z1 + 1, AIR)
        c.set(cx + 3, y - 1, cz + 3, LANT)
        c.set(cx - 3, y - 1, cz - 3, LANT)
    for y in range(6, top, 6):                        # luzes no teto de cada andar
        c.set(cx, y + 5 if y + 5 < top else y, cz + 2, LANT)
    c.box(x1 + 1, top, z1 + 1, x2 - 1, top, z2 - 1, QUARTZ)       # teto/terraco
    c.set(cx, top, z1 + 1, AIR)
    # entrada voltada para o centro (3 de largura x 4 de altura)
    if side_x:
        ex = x2 if side_x > 0 else x1
        c.box(ex, 6, cz - 1, ex, 9, cz + 1, AIR)
    if side_z:
        ez = z2 if side_z > 0 else z1
        c.box(cx - 1 + 3, 6, ez, cx + 1 + 3, 9, ez, AIR)
    chest(c, cx + 3, top - 5 if top - 5 > 6 else 7, cz + 3)
    # coroa luminosa e antena
    c.shell(x1, top + 1, z1, x2, top + 1, z2, LANT)
    for y in range(top + 2, top + 8): c.set(cx, y, cz, ROD)
    c.set(cx, top + 8, cz, LANT)
    c.box(cx - 2, top + 1, cz - 2, cx + 2, top + 1, cz + 2, AIR)

def lowrise(c, cx, cz, door):
    x1, x2, z1, z2 = cx - 4, cx + 4, cz - 4, cz + 4
    top = 6 + 9
    c.box(x1, 6, z1, x2, top, z2, AIR)
    c.shell(x1, 6, z1, x2, top, z2, LGRAY)
    for y in (8, 9, 12, 13):
        for x in range(x1 + 1, x2):
            if x % 2: c.set(x, y, z1, GLASS); c.set(x, y, z2, GLASS)
        for z in range(z1 + 1, z2):
            if z % 2: c.set(x1, y, z, GLASS); c.set(x2, y, z, GLASS)
    c.shell(x1, 10, z1, x2, 10, z2, CYAN)
    c.shell(x1, top, z1, x2, top, z2, WHITE)
    c.box(x1 + 1, top, z1 + 1, x2 - 1, top, z2 - 1, BLACK)
    c.box(cx - 1, top, cz - 1, cx + 1, top, cz + 1, LANT)
    c.box(x1 + 1, 6 + 4, z1 + 1, x2 - 1, 6 + 4, z2 - 1, QUARTZ) if False else None
    c.set(cx, top - 1, cz, LANT)
    c.set(cx - 2, 6, cz - 2, LANT)
    dx, dz = door
    if dx: c.box(cx + dx * 4, 6, cz - 1, cx + dx * 4, 9, cz + 1, AIR)
    if dz: c.box(cx - 1, 6, cz + dz * 4, cx + 1, 9, cz + dz * 4, AIR)
    chest(c, cx - 2, 6, cz + 2)

def core_tower(c):
    x1, x2, z1, z2 = 22, 38, 22, 38
    H1 = 27
    c.box(x1, 6, z1, x2, H1, z2, AIR)
    c.shell(x1, 6, z1, x2, H1, z2, BLACK)
    for y in range(6, H1 + 1):
        for x in range(x1, x2 + 1):
            for z in (z1, z2):
                if (x - x1) % 4 == 0: c.set(x, y, z, LANT if y % 4 == 1 else QUARTZ)
                elif 12 <= y <= 24: c.set(x, y, z, GLASS)
        for z in range(z1, z2 + 1):
            for x in (x1, x2):
                if (z - z1) % 4 == 0: c.set(x, y, z, LANT if y % 4 == 1 else QUARTZ)
                elif 12 <= y <= 24: c.set(x, y, z, GLASS)
    # entradas largas nos 4 lados
    c.box(28, 6, z1, 32, 13, z1, AIR); c.box(28, 6, z2, 32, 13, z2, AIR)
    c.box(x1, 6, 28, x1, 13, 32, AIR); c.box(x2, 6, 28, x2, 13, 32, AIR)
    # piso do salao e plataforma central
    c.box(x1 + 1, FLOOR, z1 + 1, x2 - 1, FLOOR, z2 - 1, BSTONE)
    for x in range(x1 + 1, x2):
        for z in range(z1 + 1, z2):
            d = max(abs(x - 30), abs(z - 30))
            if d in (4, 8): c.set(x, FLOOR, z, CYAN)
    c.box(27, 6, 27, 33, 6, 33, BSTONE)
    c.box(28, 6, 28, 32, 6, 32, CYAN)
    c.box(29, 6, 29, 31, 6, 31, LANT)
    # pilares de energia
    for (px, pz) in ((25, 25), (35, 25), (25, 35), (35, 35)):
        c.box(px, 6, pz, px + 1, H1 - 1, pz + 1, QUARTZ)
        for y in range(8, H1 - 1, 5): c.box(px, y, pz, px + 1, y, pz + 1, LANT)
    # luzes do teto
    c.box(x1 + 1, H1, z1 + 1, x2 - 1, H1, z2 - 1, BLACK)
    for x in range(24, 37, 4):
        for z in range(24, 37, 4): c.set(x, H1, z, LANT)
    # baus nas esquinas
    for (bx, bz) in ((24, 24), (36, 24), (24, 36), (36, 36)):
        chest(c, bx, 6, bz)
    # camada superior 1 (vitrine de vidro)
    for tier, (a, b, y0, y1) in enumerate(((25, 35, H1 + 1, 42), (27, 33, 43, 54))):
        c.box(a, y0, a, b, y1, b, AIR)
        c.shell(a, y0, a, b, y1, b, GLASS)
        for y in range(y0, y1 + 1):
            for (x, z) in ((a, a), (a, b), (b, a), (b, b)): c.set(x, y, z, WHITE)
            if (y - y0) % 5 == 0: c.shell(a, y, a, b, y, b, WHITE)
        c.box(a + 1, y1, a + 1, b - 1, y1, b - 1, BLACK)
        c.box(a + 1, y0, a + 1, b - 1, y0, b - 1, BLACK)
        for y in range(y0 + 1, y1):
            c.set(30, y, 30, LANT if (y - y0) % 3 == 0 else ROD)
    # espiral final
    c.shell(26, 55, 26, 34, 55, 34, LANT) if False else None
    for y in range(55, 68): c.set(30, y, 30, ROD)
    c.set(30, 68, 30, LANT)
    # base degradada ao redor
    c.box(20, FLOOR + 1, 20, 40, FLOOR + 1, 20, AIR) if False else None

def ring_monorail(c):
    Y = 18
    for t in range(17, 44):
        for off in (-1, 0, 1):
            for (x, z) in ((t, 18 + off), (t, 42 + off), (18 + off, t), (42 + off, t)):
                c.set(x, Y, z, CYAN if off == 0 else BLACK)
        for (x, z) in ((t, 18), (t, 42), (18, t), (42, t)):
            if t % 4 == 0: c.set(x, Y - 1, z, LANT)
    for (px, pz) in ((18, 18), (42, 18), (18, 42), (42, 42), (24, 18), (36, 18), (24, 42), (36, 42),
                     (18, 24), (18, 36), (42, 24), (42, 36)):
        c.box(px, 6, pz, px, Y - 1, pz, WHITE)
        c.set(px, Y - 1, pz, LANT)

def build(boss):
    c = City()
    # fundacao e piso
    c.box(0, 0, 0, SX - 1, FLOOR - 1, SZ - 1, GRAY)
    c.box(0, FLOOR, 0, SX - 1, FLOOR, SZ - 1, LGRAY)
    c.box(0, 6, 0, SX - 1, 14, SZ - 1, AIR)            # limpa o volume baixo
    for t in range(SX):
        for off in range(-3, 4):
            for (x, z) in ((30 + off, t), (t, 30 + off)): c.set(x, FLOOR, z, GRAY)
        if t % 4 < 2:
            c.set(30, FLOOR, t, WHITE); c.set(t, FLOOR, 30, WHITE)
        for (x, z) in ((t, 0), (t, SZ - 1), (0, t), (SX - 1, t)):
            c.set(x, FLOOR, z, BLACK)
            if t % 6 == 3: c.set(x, FLOOR, z, LANT)
    c.box(18, FLOOR, 18, 42, FLOOR, 42, QUARTZ)
    for t in range(18, 43):                              # a praca fica atras das ruas
        for off in range(-3, 4):
            for (x, z) in ((30 + off, t), (t, 30 + off)): c.set(x, FLOOR, z, GRAY)
    ring_monorail(c)
    core_tower(c)
    for (cx, cz, h) in ((10, 10, 30), (50, 10, 38), (10, 50, 24), (50, 50, 34)):
        skyscraper(c, cx, cz, h, 1 if cx < 30 else -1, 0)
    for (cx, cz, door) in ((22, 10, (0, 1)), (38, 10, (0, 1)), (22, 50, (0, -1)), (38, 50, (0, -1)),
                           (10, 22, (1, 0)), (10, 38, (1, 0)), (50, 22, (-1, 0)), (50, 38, (-1, 0))):
        lowrise(c, cx, cz, door)
    # postes de luz nas ruas
    for t in range(4, 58, 6):
        for (x, z) in ((26, t), (34, t), (t, 26), (t, 34)):
            if all(c.free(x, 6 + k, z) for k in range(5)) and c.b.get((x, FLOOR, z)) in (GRAY, LGRAY, QUARTZ):
                for k in range(4): c.set(x, 6 + k, z, ROD)
                c.set(x, 10, z, LANT)
    entities = []
    def ent(eid, x, y, z):
        entities.append({'pos': [D(x), D(y), D(z)], 'blockPos': [I(int(x)), I(int(y)), I(int(z))],
                         'nbt': {'id': eid, 'PersistenceRequired': B(1)}})
    for (x, z) in ((20.5, 28.5), (40.5, 31.5), (28.5, 17.5), (31.5, 44.5)):
        ent('netheritesupremo:precursor_villager', x, 6.0, z)
    for (x, z) in ((30.5, 12.5), (30.5, 48.5)):
        ent('netheritesupremo:robot', x, 6.0, z)
    if boss: ent('netheritesupremo:precursor', 30.5, 7.0, 30.5)
    # monta o template
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
    return {'DataVersion': I(3465), 'size': [I(SX), I(SY), I(SZ)], 'palette': palette, 'blocks': blocks, 'entities': entities}

if __name__ == '__main__':
    out = os.path.join(os.path.dirname(__file__), '..', 'forge-1.20.1', 'src', 'main', 'resources', 'data', 'netheritesupremo', 'structures')
    for name, boss in (('pluto_city', False), ('pluto_city_boss', True)):
        t = build(boss)
        write_nbt(os.path.join(out, name + '.nbt'), t)
        print(name, len(t['blocks']), 'blocos', len(t['palette']), 'estados', len(t['entities']), 'entidades')
