#!/usr/bin/env python3
"""Gera as ruinas abandonadas e quebradas da Terra Negra (ruin_house, ruin_tower, ruin_hall).
Uso: python3 tools/gerar_ruinas.py  (escreve em forge-1.20.1/src/main/resources/data/netheritesupremo/structures)"""
import os, re, random
from gerar_cidade import write_nbt, I

AIR = 'minecraft:air'
BRICK = 'minecraft:blackstone_bricks'; CRACK = 'minecraft:cracked_polished_blackstone_bricks'
POLISH = 'minecraft:polished_blackstone'; COBBLE = 'minecraft:cobbled_deepslate'; PLANK = 'minecraft:dark_oak_planks'
LOG = 'minecraft:dark_oak_log[axis=y]'; LOGX = 'minecraft:dark_oak_log[axis=x]'; SLAB = 'minecraft:dark_oak_slab[type=bottom]'
WEB = 'minecraft:cobweb'; BARS = 'minecraft:iron_bars'; FENCE = 'minecraft:dark_oak_fence'; BONE = 'minecraft:bone_block[axis=y]'
SOIL = 'netheritesupremo:black_soil'; COAL = 'minecraft:coal_block'
LOOT = 'netheritesupremo:chests/black_realm_ruins'

class T:
    def __init__(self, sx, sy, sz, seed):
        self.sx, self.sy, self.sz = sx, sy, sz; self.b = {}; self.nbt = {}; self.r = random.Random(seed)
    def set(self, x, y, z, s, nbt=None):
        if 0 <= x < self.sx and 0 <= y < self.sy and 0 <= z < self.sz:
            self.b[(x, y, z)] = s
            if nbt: self.nbt[(x, y, z)] = nbt
    def get(self, x, y, z): return self.b.get((x, y, z), AIR)
    def box(self, x1, y1, z1, x2, y2, z2, s):
        for x in range(x1, x2 + 1):
            for y in range(y1, y2 + 1):
                for z in range(z1, z2 + 1): self.set(x, y, z, s)
    def wall_block(self):  # parede envelhecida
        return self.r.choice([BRICK, BRICK, BRICK, CRACK, CRACK, COBBLE, POLISH])
    def shell(self, x1, y1, z1, x2, y2, z2, dmg):
        """paredes com buracos: quanto mais alto, mais quebrado"""
        for y in range(y1, y2 + 1):
            h = (y - y1) / max(1, (y2 - y1))
            for x in range(x1, x2 + 1):
                for z in range(z1, z2 + 1):
                    if x in (x1, x2) or z in (z1, z2):
                        if self.r.random() < dmg * (0.15 + h * 1.1): continue
                        self.set(x, y, z, self.wall_block())
    def rubble(self, x1, z1, x2, z2, y, n):
        for _ in range(n):
            x, z = self.r.randint(x1, x2), self.r.randint(z1, z2)
            self.set(x, y, z, self.r.choice([COBBLE, BRICK, CRACK, BONE if self.r.random() < .15 else COBBLE]))
    def webs(self, x1, y1, z1, x2, y2, z2, n):
        for _ in range(n):
            x, y, z = self.r.randint(x1, x2), self.r.randint(y1, y2), self.r.randint(z1, z2)
            if self.get(x, y, z) == AIR: self.set(x, y, z, WEB)
    def chest(self, x, y, z, f='south'):
        self.set(x, y, z, 'minecraft:chest[facing=%s,type=single,waterlogged=false]' % f, {'id': 'minecraft:chest', 'LootTable': LOOT})
    def out(self):
        palette, idx, blocks = [], {}, []
        def pal(s):
            if s not in idx:
                m = re.match(r'([^\[]+)(?:\[(.*)\])?$', s)
                e = {'Name': m.group(1)}
                if m.group(2): e['Properties'] = dict(p.split('=') for p in m.group(2).split(','))
                idx[s] = len(palette); palette.append(e)
            return idx[s]
        for (x, y, z), s in sorted(self.b.items()):
            e = {'pos': [I(x), I(y), I(z)], 'state': I(pal(s))}
            if (x, y, z) in self.nbt: e['nbt'] = self.nbt[(x, y, z)]
            blocks.append(e)
        return {'DataVersion': I(3465), 'size': [I(self.sx), I(self.sy), I(self.sz)], 'palette': palette, 'blocks': blocks, 'entities': []}

def foundation(t, x1, z1, x2, z2):
    for x in range(x1, x2 + 1):
        for z in range(z1, z2 + 1):
            t.set(x, 0, z, SOIL if t.r.random() < .35 else COBBLE)
            t.set(x, 1, z, t.r.choice([BRICK, CRACK, PLANK, PLANK, AIR if t.r.random() < .12 else COBBLE]))

def house():
    t = T(13, 12, 11, 101); foundation(t, 1, 1, 11, 9)
    t.shell(1, 2, 1, 11, 6, 9, 0.9)
    for y in range(2, 5): t.set(6, y, 1, AIR)                       # porta arrombada
    for z in (3, 7):                                                # janelas
        for x in (1, 11): t.set(x, 4, z, BARS if t.r.random() < .5 else AIR)
    for x in range(1, 12):                                          # telhado quase todo caido
        for z in range(1, 10):
            if t.r.random() < 0.28: t.set(x, 7, z, SLAB)
    for x in (1, 11):
        for z in (1, 9): t.box(x, 2, z, x, 7 if t.r.random() < .6 else 5, z, LOG)
    t.box(5, 2, 4, 6, 2, 4, PLANK); t.set(8, 2, 6, FENCE); t.set(8, 3, 6, FENCE)  # mesa quebrada
    t.set(3, 2, 7, 'minecraft:soul_campfire[facing=north,lit=false,signal_fire=false,waterlogged=false]')
    t.chest(10, 2, 8, 'west')
    t.rubble(0, 0, 12, 10, 2, 22); t.webs(2, 2, 2, 10, 6, 8, 12)
    return t.out()

def tower():
    t = T(11, 24, 11, 202); foundation(t, 2, 2, 8, 8)
    t.shell(2, 2, 2, 8, 19, 8, 0.7)
    for y in (6, 10, 14):                                           # andares caidos
        for x in range(3, 8):
            for z in range(3, 8):
                if t.r.random() < 0.45: t.set(x, y, z, PLANK)
    for y in range(2, 5): t.set(5, y, 2, AIR)
    for y in range(3, 19, 1):
        if t.r.random() < .6: t.set(7, y, 7, 'minecraft:ladder[facing=west]')
    t.chest(3, 2, 3, 'east'); t.webs(3, 2, 3, 7, 18, 7, 16); t.rubble(0, 0, 10, 10, 2, 30)
    return t.out()

def hall():
    t = T(21, 10, 15, 303); foundation(t, 1, 1, 19, 13)
    for z in (3, 7, 11):                                            # colunas quebradas
        for x in (3, 17):
            top = t.r.randint(3, 8)
            t.box(x, 2, z, x, top, z, LOG)
    for x in (5, 9, 13):
        for z in (3, 11):
            t.box(x, 2, z, x, t.r.randint(2, 7), z, BRICK if t.r.random() < .5 else CRACK)
    t.shell(1, 2, 1, 19, 5, 13, 1.1)
    for x in range(3, 18):                                          # pilares caidos
        if t.r.random() < .25: t.set(x, 2, 7, LOGX)
    t.chest(10, 2, 12, 'north'); t.chest(2, 2, 2, 'south')
    t.rubble(0, 0, 20, 14, 2, 45); t.webs(2, 2, 2, 18, 6, 12, 18)
    return t.out()

if __name__ == '__main__':
    out = os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', 'forge-1.20.1', 'src', 'main', 'resources', 'data', 'netheritesupremo', 'structures')
    for name, fn in (('ruin_house', house), ('ruin_tower', tower), ('ruin_hall', hall)):
        t = fn(); write_nbt(os.path.join(out, name + '.nbt'), t); print(name, len(t['blocks']), 'blocos')
