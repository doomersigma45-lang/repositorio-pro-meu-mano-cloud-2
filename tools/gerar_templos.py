#!/usr/bin/env python3
"""Gera o Templo Ancestral (subterraneo, com o Guardiao Ancestral dormindo) e o Santuario do Abismo (Terra Negra).
Uso: python3 tools/gerar_templos.py"""
import os, re, random
from gerar_cidade import write_nbt, I, D, B
OUT = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources', 'data', 'netheritesupremo', 'structures')
AIR = 'minecraft:air'

class Tpl:
    def __init__(self, sx, sy, sz, seed):
        self.sx, self.sy, self.sz = sx, sy, sz; self.b = {}; self.nbt = {}; self.ents = []; self.r = random.Random(seed)
    def put(self, x, y, z, s, n=None):
        if 0 <= x < self.sx and 0 <= y < self.sy and 0 <= z < self.sz:
            self.b[(x, y, z)] = s
            if n: self.nbt[(x, y, z)] = n
    def box(self, x1, y1, z1, x2, y2, z2, s):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1): self.put(x, y, z, s)
    def chest(self, x, y, z, f, loot):
        self.put(x, y, z, 'minecraft:chest[facing=%s,type=single,waterlogged=false]' % f, {'id': 'minecraft:chest', 'LootTable': loot})
    def ent(self, eid, x, y, z, extra=None):
        n = {'id': eid, 'PersistenceRequired': B(1)}
        if extra: n.update(extra)
        self.ents.append({'pos': [D(x), D(y), D(z)], 'blockPos': [I(int(x)), I(int(y)), I(int(z))], 'nbt': n})
    def save(self, name):
        palette, idx, blocks = [], {}, []
        def pal(s):
            if s not in idx:
                m = re.match(r'([^\[]+)(?:\[(.*)\])?$', s); e = {'Name': m.group(1)}
                if m.group(2): e['Properties'] = dict(p.split('=') for p in m.group(2).split(','))
                idx[s] = len(palette); palette.append(e)
            return idx[s]
        for (x, y, z), s in sorted(self.b.items()):
            e = {'pos': [I(x), I(y), I(z)], 'state': I(pal(s))}
            if (x, y, z) in self.nbt: e['nbt'] = self.nbt[(x, y, z)]
            blocks.append(e)
        tpl = {'DataVersion': I(3465), 'size': [I(self.sx), I(self.sy), I(self.sz)], 'palette': palette, 'blocks': blocks, 'entities': self.ents}
        write_nbt(os.path.join(OUT, name + '.nbt'), tpl)
        print(name, len(blocks), 'blocos', len(self.ents), 'entidades')

# ============ Templo Ancestral: salao 25x11x25 com poco de escada
t = Tpl(29, 40, 29, 11); r = t.r
def brick(): return r.choice(['minecraft:stone_bricks'] * 5 + ['minecraft:mossy_stone_bricks', 'minecraft:cracked_stone_bricks'])
for x in range(29):
    for y in range(0, 16):
        for z in range(29): t.put(x, y, z, brick())
t.box(2, 1, 2, 26, 11, 26, AIR)
for x in range(2, 27):                       # piso quadriculado
    for z in range(2, 27): t.put(x, 0, z, 'minecraft:polished_andesite' if (x + z) % 2 else 'minecraft:stone_bricks')
for x in range(9, 20):                       # plataforma central
    for z in range(9, 20):
        if (x - 14) ** 2 + (z - 14) ** 2 <= 30: t.put(x, 1, z, 'minecraft:chiseled_stone_bricks' if (x + z) % 2 else 'minecraft:smooth_stone')
for (x, z) in [(5, 5), (5, 23), (23, 5), (23, 23), (5, 14), (23, 14), (14, 5), (14, 23)]:   # pilares com lanternas
    t.box(x, 1, z, x, 10, z, 'minecraft:stone_bricks'); t.put(x, 11, z, 'minecraft:chiseled_stone_bricks')
    for dx, dz in ((1, 0), (-1, 0), (0, 1), (0, -1)): t.put(x + dx, 10, z + dz, 'minecraft:stone_brick_slab[type=top,waterlogged=false]')
    t.put(x, 9, z + 1, 'minecraft:lantern[hanging=true,waterlogged=false]')
for _ in range(60):                          # runas douradas nas paredes
    y = r.randint(2, 10); s = r.randint(2, 26); side = r.choice('xXzZ')
    pos = {'x': (1, y, s), 'X': (27, y, s), 'z': (s, y, 1), 'Z': (s, y, 27)}[side]
    t.put(*pos, 'minecraft:gilded_blackstone')
t.chest(3, 1, 3, 'east', 'netheritesupremo:chests/ancestral_temple'); t.chest(25, 1, 25, 'west', 'netheritesupremo:chests/ancestral_temple')
t.chest(3, 1, 25, 'east', 'netheritesupremo:chests/ancestral_temple'); t.chest(25, 1, 3, 'west', 'netheritesupremo:chests/ancestral_temple')
t.box(1, 12, 1, 5, 39, 5, 'minecraft:stone_bricks'); t.box(2, 12, 2, 4, 39, 4, AIR); t.box(2, 12, 2, 4, 15, 4, AIR)   # poco 3x3
for y in range(1, 40): t.put(2, y, 3, 'minecraft:ladder[facing=east,waterlogged=false]')
t.box(2, 12, 2, 4, 12, 4, AIR)
t.ent('netheritesupremo:ancestral_guardian', 14.5, 2.0, 14.5, {'Dormant': B(1)})
for (x, z) in [(10, 10), (18, 10), (10, 18), (18, 18)]:      # guardioes de pedra de guarda
    t.ent('netheritesupremo:stone_guardian', x + .5, 2.0, z + .5)
t.save('ancestral_temple')

# ============ Santuario do Abismo (Terra Negra): plataforma circular 43x20x43
a = Tpl(43, 20, 43, 5); r = a.r
OB = 'minecraft:obsidian'; BS = 'minecraft:polished_blackstone_bricks'; CR = 'minecraft:crying_obsidian'
for x in range(43):
    for z in range(43):
        d2 = (x - 21) ** 2 + (z - 21) ** 2
        if d2 <= 20 ** 2:
            for y in range(0, 3): a.put(x, y, z, r.choice([BS, BS, 'minecraft:cracked_polished_blackstone_bricks', OB]))
            if d2 <= 6 ** 2: a.put(x, 2, z, CR if (x + z) % 3 == 0 else 'minecraft:black_concrete')
            elif 13 ** 2 <= d2 <= 14 ** 2: a.put(x, 2, z, 'minecraft:amethyst_block')
for i in range(8):                       # 8 obeliscos ao redor
    import math
    px = 21 + round(16 * math.cos(i * math.pi / 4)); pz = 21 + round(16 * math.sin(i * math.pi / 4))
    h = r.randint(5, 9); a.box(px, 3, pz, px, 2 + h, pz, OB); a.put(px, 3 + h, pz, 'minecraft:purple_stained_glass'); a.put(px, 2 + h, pz, CR)
for _ in range(25):
    x, z = r.randint(2, 40), r.randint(2, 40)
    if (x - 21) ** 2 + (z - 21) ** 2 <= 19 ** 2: a.put(x, 3, z, 'minecraft:amethyst_cluster[facing=up,waterlogged=false]')
a.chest(21, 3, 31, 'north', 'netheritesupremo:chests/abyss_sanctum')
a.ent('netheritesupremo:the_abyss', 21.5, 3.0, 21.5, {'Dormant': B(1)})
a.save('abyss_sanctum')
