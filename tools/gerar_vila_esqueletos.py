#!/usr/bin/env python3
"""Gera a vila dos esqueletos: 9 casas modernas e simples, 9 esqueletos villagers, estradas de asfalto e a
praca do trono com o Rei Esqueleto. Uso: python3 tools/gerar_vila_esqueletos.py
(escreve em forge-1.20.1/src/main/resources/data/netheritesupremo/structures/skeleton_village.nbt)"""
import os, re, random
from gerar_cidade import write_nbt, I, D, B

SX, SY, SZ = 43, 14, 63
FLOOR = 5          # camada da superficie; anda-se em y=6
AIR = 'minecraft:air'
ASPHALT = 'minecraft:black_concrete'; SIDEWALK = 'minecraft:light_gray_concrete'; LINE = 'minecraft:yellow_concrete'
GRASS = 'minecraft:grass_block[snowy=false]'; DIRT = 'minecraft:dirt'; COBBLE = 'minecraft:cobblestone'
WALL = 'minecraft:white_concrete'; PILLAR = 'minecraft:gray_concrete'; BASE = 'minecraft:light_gray_concrete'
GLASS = 'minecraft:glass'; ROOF = 'minecraft:smooth_stone'; PARAPET = 'minecraft:smooth_stone_slab[type=bottom]'
LANTERN = 'minecraft:sea_lantern'; BARS = 'minecraft:iron_bars'
LOOT = 'netheritesupremo:chests/skeleton_village'

b = {}; nbt = {}; ents = []; r = random.Random(77)
def put(x, y, z, s, n=None):
    if 0 <= x < SX and 0 <= y < SY and 0 <= z < SZ:
        b[(x, y, z)] = s
        if n: nbt[(x, y, z)] = n
def box(x1, y1, z1, x2, y2, z2, s):
    for x in range(x1, x2 + 1):
        for y in range(y1, y2 + 1):
            for z in range(z1, z2 + 1): put(x, y, z, s)
def ent(eid, x, y, z, extra=None):
    n = {'id': eid, 'PersistenceRequired': B(1)}
    if extra: n.update(extra)
    ents.append({'pos': [D(x), D(y), D(z)], 'blockPos': [I(int(x)), I(int(y)), I(int(z))], 'nbt': n})

VROADS = (12, 26); HROADS = (12, 26, 40)
def band(v, starts):
    for s in starts:
        if s <= v <= s + 4: return v - s
    return None

# ---- terreno: fundacao, gramado, estradas de asfalto
for x in range(SX):
    for z in range(SZ):
        box(x, 0, z, x, 3, z, COBBLE); put(x, 4, z, DIRT)
        u = band(x, VROADS); v = band(z, HROADS) if z <= 44 else None
        if z > 44: top = SIDEWALK if (x + z) % 2 == 0 else 'minecraft:polished_andesite'   # praca do trono
        elif u is not None and v is not None: top = ASPHALT
        elif u is not None: top = ASPHALT if 1 <= u <= 3 else SIDEWALK
        elif v is not None: top = ASPHALT if 1 <= v <= 3 else SIDEWALK
        else: top = GRASS
        if top == ASPHALT:
            if u == 2 and v is None and z % 4 < 2: top = LINE
            if v == 2 and u is None and x % 4 < 2: top = LINE
        put(x, FLOOR, z, top)
# postes de luz nas calcadas
for x in range(SX):
    for z in range(45):
        if b[(x, FLOOR, z)] == SIDEWALK and (x * 3 + z * 5) % 17 == 0 and not any(a <= x <= a + 2 for a in (6, 20, 34)):
            box(x, 6, z, x, 8, z, BARS); put(x, 9, z, LANTERN)

# ---- casas 9x9
COLS = (3, 17, 31); ROWS = (3, 17, 31)
FACE_S = {0: True, 1: False, 2: True}      # linha 0 e 2 viram para o sul; a do meio para o norte
OPP = {'north': 'south', 'south': 'north', 'east': 'west', 'west': 'east'}

def house(hx, hz, south, seed):
    def g(lx, y, lz):                          # local -> global (z espelhado quando a porta e' ao norte)
        return hx + lx, y, hz + (lz if south else 8 - lz)
    def p(lx, y, lz, s, n=None):
        x, yy, z = g(lx, y, lz); put(x, yy, z, s, n)
    def fz(f):                                 # espelha direcoes norte/sul
        return f if south else OPP.get(f, f) if f in ('north', 'south') else f
    for lx in range(9):
        for lz in range(9):
            p(lx, FLOOR, lz, 'minecraft:polished_andesite')
            edge = lx in (0, 8) or lz in (0, 8)
            for y in range(6, 10):
                if edge:
                    corner = lx in (0, 8) and lz in (0, 8)
                    p(lx, y, lz, PILLAR if corner else (BASE if y == 6 else WALL))
                else: p(lx, y, lz, AIR)
            p(lx, 10, lz, ROOF)
            if edge: p(lx, 11, lz, PARAPET)
    door_z = 8
    # porta de entrada no meio da parede voltada para a estrada
    p(4, 6, door_z, 'minecraft:dark_oak_door[facing=%s,half=lower,hinge=left,open=false,powered=false]' % fz('north'))
    p(4, 7, door_z, 'minecraft:dark_oak_door[facing=%s,half=upper,hinge=left,open=false,powered=false]' % fz('north'))
    p(4, 9, door_z, LANTERN)
    # janelas grandes
    for lx in (1, 2, 6, 7):
        for y in (7, 8): p(lx, y, door_z, GLASS)
    for lz in (3, 4, 5):
        for y in (7, 8): p(0, y, lz, GLASS); p(8, y, lz, GLASS)
    for lx in (3, 4, 5):
        for y in (7, 8): p(lx, y, 0, GLASS)
    # interior: cama, mesa, bau, estante, tapete e luz
    p(1, 6, 2, 'minecraft:red_bed[facing=%s,part=foot,occupied=false]' % fz('north'))
    p(1, 6, 1, 'minecraft:red_bed[facing=%s,part=head,occupied=false]' % fz('north'))
    p(6, 6, 2, 'minecraft:spruce_fence'); p(6, 7, 2, 'minecraft:spruce_pressure_plate')
    p(7, 6, 1, 'minecraft:chest[facing=%s,type=single,waterlogged=false]' % fz('south'), {'id': 'minecraft:chest', 'LootTable': LOOT})
    p(7, 6, 4, 'minecraft:bookshelf'); p(7, 7, 4, 'minecraft:bookshelf')
    for lx in range(3, 6):
        for lz in range(3, 6): p(lx, 6, lz, 'minecraft:gray_carpet')
    p(4, 9, 4, LANTERN)
    x, y, z = g(4.5, 6.0, 4.5)
    ent('netheritesupremo:skeleton_villager', hx + 4.5, 6.0, hz + (4.5 if south else 8 - 4.5))

n = 0
for ri, hz in enumerate(ROWS):
    for ci, hx in enumerate(COLS):
        house(hx, hz, FACE_S[ri], n); n += 1

# ---- praca do trono e dais do Rei Esqueleto
box(14, 6, 50, 28, 6, 60, 'minecraft:polished_blackstone_bricks')
box(16, 7, 52, 26, 7, 58, 'minecraft:quartz_block')
box(19, 8, 52, 23, 8, 56, 'minecraft:red_carpet')
put(21, 8, 57, 'minecraft:quartz_stairs[facing=south,half=bottom,shape=straight,waterlogged=false]')
box(20, 8, 58, 22, 11, 58, 'minecraft:black_concrete')
put(20, 12, 58, 'minecraft:gold_block'); put(22, 12, 58, 'minecraft:gold_block'); put(21, 12, 58, LANTERN)
for (x, z) in ((16, 52), (26, 52), (16, 58), (26, 58)):
    box(x, 8, z, x, 10, z, 'minecraft:bone_block[axis=y]')
    put(x, 11, z, 'minecraft:skeleton_skull[rotation=0]')
for (x, z) in ((2, 47), (40, 47), (2, 60), (40, 60)):
    box(x, 6, z, x, 9, z, 'minecraft:bone_block[axis=y]'); put(x, 10, z, LANTERN)
ent('netheritesupremo:skeleton_king', 21.5, 8.07, 55.5, {'VillageKing': B(1)})

# ---- monta o template
palette, idx, blocks = [], {}, []
def pal(s):
    if s not in idx:
        m = re.match(r'([^\[]+)(?:\[(.*)\])?$', s)
        e = {'Name': m.group(1)}
        if m.group(2): e['Properties'] = dict(p.split('=') for p in m.group(2).split(','))
        idx[s] = len(palette); palette.append(e)
    return idx[s]
for (x, y, z), s in sorted(b.items()):
    e = {'pos': [I(x), I(y), I(z)], 'state': I(pal(s))}
    if (x, y, z) in nbt: e['nbt'] = nbt[(x, y, z)]
    blocks.append(e)
tpl = {'DataVersion': I(3465), 'size': [I(SX), I(SY), I(SZ)], 'palette': palette, 'blocks': blocks, 'entities': ents}
if __name__ == '__main__':
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources',
                       'data', 'netheritesupremo', 'structures', 'skeleton_village.nbt')
    write_nbt(out, tpl)
    print('skeleton_village', len(blocks), 'blocos', len(palette), 'estados', len(ents), 'entidades')
