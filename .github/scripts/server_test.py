#!/usr/bin/env python3
"""Sobe um servidor Forge 1.20.1 com o mod (mundo novo), testa os mobs por RCON e resume o log.

Resultado em anotacoes do GitHub (::error / ::notice), que podem ser lidas pela API de check-runs.
"""
import os
import re
import signal
import socket
import struct
import shutil
import subprocess
import sys
import time

PROJECT = sys.argv[1] if len(sys.argv) > 1 else "forge-1.20.1"
RUN = os.path.join(PROJECT, "run")
LOG = os.path.join(RUN, "logs", "latest.log")
PORT, PASSWORD = 25575, "test"


def annotate(level, title, lines, limit=3500):
    text = "\n".join(lines)[:limit]
    text = text.replace("%", "%25").replace("\r", "").replace("\n", "%0A")
    print(f"::{level} title={title}::{text}", flush=True)


class Rcon:
    def __init__(self):
        self.sock = socket.create_connection(("127.0.0.1", PORT), timeout=20)
        self.n = 0
        self._send(3, PASSWORD)

    def _read(self, size):
        buf = b""
        while len(buf) < size:
            chunk = self.sock.recv(size - len(buf))
            if not chunk:
                raise ConnectionError("RCON fechou")
            buf += chunk
        return buf

    def _send(self, kind, body):
        self.n += 1
        data = struct.pack("<ii", self.n, kind) + body.encode() + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(data)) + data)
        (size,) = struct.unpack("<i", self._read(4))
        payload = self._read(size)
        return payload[8:-2].decode(errors="replace")

    def cmd(self, text):
        return self._send(2, text)


def read_log():
    try:
        with open(LOG, encoding="utf-8", errors="replace") as f:
            return f.read()
    except FileNotFoundError:
        return ""


def problem_lines(log):
    keep = []
    for line in log.splitlines():
        if re.search(r"\[(ERROR|FATAL)\]|Exception|Caused by|\tat com\.netheritesupremo|Failed to|crash", line, re.I):
            keep.append(line.strip())
    return keep


def main():
    os.makedirs(RUN, exist_ok=True)
    with open(os.path.join(RUN, "eula.txt"), "w") as f:
        f.write("eula=true\n")
    with open(os.path.join(RUN, "server.properties"), "w") as f:
        f.write("online-mode=false\nenable-rcon=true\nrcon.port=%d\nrcon.password=%s\n"
                "level-name=world_teste\nspawn-protection=0\nview-distance=4\nsimulation-distance=4\n"
                "enable-command-block=true\n" % (PORT, PASSWORD))

    proc = subprocess.Popen(["./gradlew", "runServer", "--no-daemon"], cwd=PROJECT,
                            stdout=open("server_gradle.out", "w"), stderr=subprocess.STDOUT,
                            start_new_session=True)
    deadline = time.time() + 20 * 60
    started = False
    while time.time() < deadline:
        time.sleep(5)
        log = read_log()
        if re.search(r"Done \(\d", log):
            started = True
            break
        if proc.poll() is not None:
            break

    results = []
    if started:
        try:
            r = Rcon()
            # Sem jogador, so os chunks forcados ficam carregados: garante a area dos testes
            results.append("> forceload\n  " + r.cmd("forceload add -16 -16 112 112"))
            results.append(r.cmd("forceload add 190 190 400 270"))
            time.sleep(40)
            # ---- integracao com o mod Sistema Solar: dimensao de Plutao
            results.append("> forceload plutao\n  " + r.cmd("execute in sistemasolar:plutao run forceload add 0 0 40 40"))
            time.sleep(45)
            for t in [
                "execute in sistemasolar:plutao positioned 0 70 0 run locate biome sistemasolar:plutao_coracao_tombaugh",
                "execute in sistemasolar:plutao run fill 0 64 0 31 95 31 netheritesupremo:crystal_wood replace netheritesupremo:crystal_log",
                "execute in sistemasolar:plutao run fill 0 20 0 31 50 31 minecraft:ice replace minecraft:water",
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:precursor_villager]",
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:robot]",
            ]:
                results.append(f"> {t}\n  {r.cmd(t)}")
            # ---- cidades futuristas de Plutao (so la): template com e sem boss
            results.append("> forceload plutao (cidade)\n  " + r.cmd("execute in sistemasolar:plutao run forceload add 192 192 270 270"))
            results.append("> forceload plutao (cidade boss)\n  " + r.cmd("execute in sistemasolar:plutao run forceload add 392 192 470 270"))
            time.sleep(15)
            for t in [
                "execute in sistemasolar:plutao run place template netheritesupremo:pluto_city 200 100 200",
                "execute in sistemasolar:plutao run place template netheritesupremo:pluto_city_boss 400 100 200",
                "execute in sistemasolar:plutao run fill 200 100 200 230 130 230 minecraft:air replace minecraft:sea_lantern",
                "execute in sistemasolar:plutao run fill 200 100 200 230 130 230 minecraft:air replace minecraft:cyan_stained_glass",
                "execute in sistemasolar:plutao run fill 225 100 225 245 125 245 minecraft:air replace minecraft:chest",
                "execute in sistemasolar:plutao run data get block 222 106 222 LootTable",
            ]:
                results.append(f"> {t}\n  {r.cmd(t)}")
            loc = r.cmd("execute in sistemasolar:plutao positioned 0 70 0 run locate biome sistemasolar:plutao_coracao_tombaugh")
            m = re.search(r"\[(-?\d+), (?:-?\d+|~), (-?\d+)\]", loc)
            results.append("> locate tombaugh\n  " + loc)
            if m:
                bx, bz = int(m.group(1)), int(m.group(2))
                results.append("> forceload cidade natural\n  " + r.cmd(f"execute in sistemasolar:plutao run forceload add {bx-48} {bz-48} {bx+48} {bz+48}"))
                time.sleep(20)
                for t in [
                    f"execute in sistemasolar:plutao run place structure netheritesupremo:pluto_city {bx} 70 {bz}",
                    f"execute in sistemasolar:plutao run place structure netheritesupremo:pluto_city {bx+300} 70 {bz}",
                    "place structure netheritesupremo:pluto_city 100 70 100",
                ]:
                    results.append(f"> {t}\n  {r.cmd(t)}")
            for t in [
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:precursor,x=430,y=106,z=230,distance=..60]",
                "execute in sistemasolar:plutao run data get entity @e[type=netheritesupremo:precursor,x=430,y=106,z=230,distance=..60,limit=1] Health",
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:precursor,x=230,y=106,z=230,distance=..60]",
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:precursor_villager,x=230,y=106,z=230,distance=..60]",
                "execute in sistemasolar:plutao if entity @e[type=netheritesupremo:robot,x=230,y=106,z=230,distance=..60]",
                "execute in sistemasolar:plutao run kill @e[type=netheritesupremo:precursor]",
            ]:
                results.append(f"> {t}\n  {r.cmd(t)}")
            # ---- satelite: estrutura e viagem pelo portal para Plutao
            results.append("> forceload plutao (chegada)\n  " + r.cmd("execute in sistemasolar:plutao run forceload add 80 70 112 110"))
            for t in [
                "place structure netheritesupremo:satellite_ocean 100 70 100",
                "place structure netheritesupremo:satellite_end 300 70 300",
                "setblock 96 110 90 netheritesupremo:satellite_portal",
                "summon minecraft:cow 96.5 110 90.5 {CustomName:'\"viajante\"'}",
            ]:
                results.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(3)
            for t in [
                "execute in sistemasolar:plutao if entity @e[type=minecraft:cow,name=viajante]",
                "execute in sistemasolar:plutao run data get entity @e[type=minecraft:cow,name=viajante,limit=1] Pos",
                "execute in minecraft:overworld if entity @e[type=minecraft:cow,name=viajante]",
                "execute in minecraft:overworld run data get entity @e[type=minecraft:cow,name=viajante,limit=1] Pos",
                "execute in sistemasolar:plutao run fill 85 60 80 110 100 100 minecraft:air replace netheritesupremo:satellite_portal",
            ]:
                results.append(f"> {t}\n  {r.cmd(t)}")
            # ---- laboratorio de robos: template nas duas dimensoes + estruturas naturais
            lab = []
            lab.append("> forceload lab\n  " + r.cmd("forceload add 300 300 360 360"))
            lab.append("> forceload lab nether\n  " + r.cmd("execute in minecraft:the_nether run forceload add 300 300 360 360"))
            time.sleep(10)
            for t in ["place template netheritesupremo:robot_lab 305 20 305",
                      "execute in minecraft:the_nether run place template netheritesupremo:robot_lab 305 40 305",
                      "execute if entity @e[type=netheritesupremo:netherite_bomb_robot,x=305,y=20,z=305,dx=47,dy=44,dz=47]",
                      "execute if entity @e[type=netheritesupremo:netherbot,x=305,y=20,z=305,dx=47,dy=44,dz=47]",
                      "execute if entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,x=305,y=20,z=305,dx=47,dy=44,dz=47]",
                      "execute in minecraft:the_nether if entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,x=305,y=40,z=305,dx=47,dy=44,dz=47]",
                      "data get block 319 22 319 LootTable",
                      "place structure netheritesupremo:robot_lab_overworld 330 70 330",
                      "execute in minecraft:the_nether run place structure netheritesupremo:robot_lab_nether 330 70 330",
                      "loot spawn 0 100 0 loot netheritesupremo:chests/robot_lab_hard"]:
                lab.append(f"> {t}\n  {r.cmd(t)}")
            annotate("notice", "robot_lab", lab)
            sp = []
            placed = 0
            for x in (30, 50, 70, 90):
                for z in (30, 50, 70):
                    for y in (-50, -30, -10):
                        out = r.cmd(f"place feature netheritesupremo:spectrite_ore {x} {y} {z}")
                        placed += 1 if "Placed" in out else 0
            sp.append(f"minerio: {placed} de 36 tentativas colocaram veio")
            # geracao natural: gera 3x3 chunks novos e conta os minerios (fill replace nao mexe fora dos chunks carregados)
            sp.append("> forceload natural\n  " + r.cmd("forceload add 6000 6000 6095 6095"))
            time.sleep(60)
            total = 0
            for cx in range(6000, 6096, 16):
                for cz in range(6000, 6096, 16):
                    out = r.cmd(f"fill {cx} -64 {cz} {cx + 15} 32 {cz + 15} minecraft:stone replace netheritesupremo:spectrite_ore")
                    import re as _re
                    m = _re.search(r"filled (\d+)", out)
                    total += int(m.group(1)) if m else 0
            sp.append(f"minerio natural: {total} blocos em 36 chunks gerados")
            dia = 0
            for cx in range(6000, 6096, 16):
                for cz in range(6000, 6096, 16):
                    for blk in ("minecraft:diamond_ore", "minecraft:deepslate_diamond_ore"):
                        out = r.cmd(f"fill {cx} -64 {cz} {cx + 15} 32 {cz + 15} minecraft:stone replace {blk}")
                        m = _re.search(r"filled (\d+)", out)
                        dia += int(m.group(1)) if m else 0
            sp.append(f"diamante vanilla (referencia): {dia} blocos nos mesmos 36 chunks")
            rate = []
            for ty in ("netheritesupremo:red_steve", "minecraft:sheep", "minecraft:cow", "minecraft:pig", "minecraft:chicken"):
                rate.append(f"{ty}: " + r.cmd(f"execute if entity @e[type={ty},x=6000,y=-64,z=6000,dx=96,dy=500,dz=96]"))
            sp.append("spawn natural em 36 chunks gerados -> " + " | ".join(rate))
            r.cmd("forceload remove 6000 6000 6095 6095")
            for t in ['summon minecraft:zombie 60 100 60 {CustomName:\'"spectro"\',PersistenceRequired:1b,ArmorItems:[{id:"netheritesupremo:spectrite_boots",Count:1b},{id:"netheritesupremo:spectrite_leggings",Count:1b},{id:"netheritesupremo:spectrite_chestplate",Count:1b},{id:"netheritesupremo:spectrite_helmet",Count:1b}]}',
                      "attribute @e[type=minecraft:zombie,name=spectro,limit=1] minecraft:generic.max_health get",
                      "attribute @e[type=minecraft:zombie,name=spectro,limit=1] minecraft:generic.armor get",
                      "setblock 44 100 44 netheritesupremo:spectrite_ore",
                      "loot spawn 0 100 0 loot netheritesupremo:blocks/spectrite_ore"]:
                sp.append(f"> {t}\n  {r.cmd(t)}")
            annotate("notice", "spectrite", sp)
            tests = [
                "execute in sistemasolar:plutao run summon netheritesupremo:aquatic_alien 96 70 90",
                "execute in sistemasolar:plutao run summon netheritesupremo:anomalocaris 96 40 92",
                "execute in sistemasolar:plutao run summon netheritesupremo:trilobite 98 40 92",
                "execute in sistemasolar:plutao run summon netheritesupremo:opabinia 100 40 92",
                "loot spawn 0 100 0 loot netheritesupremo:entities/anomalocaris",
                "loot spawn 0 100 0 loot netheritesupremo:entities/aquatic_alien",
                "place feature netheritesupremo:crystal_tree_pluto 94 100 94",
                "give @a netheritesupremo:supreme_gauntlet",
                "setblock 92 110 84 netheritesupremo:dimension_portal",
                "loot spawn 0 100 0 loot netheritesupremo:entities/samurai",
                "give @a netheritesupremo:ruby",
                "give @a netheritesupremo:dragon_eye",
                "setblock 84 110 84 netheritesupremo:crystal_log[axis=x]",
                "setblock 85 110 84 netheritesupremo:crystal_planks",
                "setblock 86 110 84 netheritesupremo:crystal_stairs[facing=north,half=top]",
                "setblock 87 110 84 netheritesupremo:crystal_fence_gate[open=true]",
                "setblock 88 110 84 netheritesupremo:crystal_leaves",
                "loot spawn 0 100 0 loot netheritesupremo:blocks/crystal_log",
                "loot spawn 0 100 0 loot netheritesupremo:blocks/crystal_leaves",
                "place feature netheritesupremo:crystal_tree 90 100 90",
                "summon netheritesupremo:precursor_villager 80 110 80",
                "item replace entity @e[type=netheritesupremo:precursor_villager,limit=1] armor.head with netheritesupremo:fusion_helmet",
                "item replace entity @e[type=netheritesupremo:precursor_villager,limit=1] armor.chest with netheritesupremo:fusion_chestplate",
                "item replace entity @e[type=netheritesupremo:precursor_villager,limit=1] armor.legs with netheritesupremo:fusion_leggings",
                "item replace entity @e[type=netheritesupremo:precursor_villager,limit=1] armor.feet with netheritesupremo:fusion_boots",
                "data get entity @e[type=netheritesupremo:precursor_villager,limit=1] ArmorItems[1]",
                "kill @e[type=netheritesupremo:precursor_villager,limit=1]",
                "summon netheritesupremo:thief 72 110 184",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.head with netheritesupremo:ninja_helmet",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.chest with netheritesupremo:ninja_chestplate",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.legs with netheritesupremo:ninja_leggings",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.feet with netheritesupremo:ninja_boots",
                "kill @e[type=netheritesupremo:thief,limit=1]",
                "summon netheritesupremo:thief 72 110 182",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.head with netheritesupremo:obsidian_helmet",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.chest with netheritesupremo:obsidian_chestplate",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.legs with netheritesupremo:obsidian_leggings",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] armor.feet with netheritesupremo:obsidian_boots",
                "attribute @e[type=netheritesupremo:thief,limit=1] minecraft:generic.armor get",
                "damage @e[type=netheritesupremo:thief,limit=1] 20 minecraft:explosion",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] weapon.mainhand with netheritesupremo:obsidian_sword",
                "attribute @e[type=netheritesupremo:thief,limit=1] minecraft:generic.attack_damage get",
                "item replace entity @e[type=netheritesupremo:thief,limit=1] weapon.mainhand with netheritesupremo:obsidian_axe",
                "attribute @e[type=netheritesupremo:thief,limit=1] minecraft:generic.attack_damage get",
                "kill @e[type=netheritesupremo:thief,limit=1]",
                "summon netheritesupremo:precursor_villager 70 110 210",
                "attribute @e[type=netheritesupremo:precursor_villager,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:precursor_villager,limit=1] minecraft:generic.attack_damage get",
                "data get entity @e[type=netheritesupremo:precursor_villager,limit=1] Offers.Recipes[0]",
                "data get entity @e[type=netheritesupremo:precursor_villager,limit=1] Offers.Recipes[16]",
                "loot spawn 0 100 0 loot netheritesupremo:entities/precursor_villager",
                "summon netheritesupremo:thief 70 110 180",
                "attribute @e[type=netheritesupremo:thief,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:thief,limit=1] minecraft:generic.movement_speed get",
                "data get entity @e[type=netheritesupremo:thief,limit=1] HandItems[0]",
                "loot spawn 0 100 0 loot netheritesupremo:entities/thief",
                "summon netheritesupremo:samurai 70 110 150",
                "attribute @e[type=netheritesupremo:samurai,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:samurai,limit=1] minecraft:generic.attack_damage get",
                "data get entity @e[type=netheritesupremo:samurai,limit=1] ArmorItems[3]",
                "data get entity @e[type=netheritesupremo:samurai,limit=1] HandItems[0]",
                "loot spawn 0 100 0 loot netheritesupremo:entities/samurai",
                "summon netheritesupremo:ninja 74 110 150",
                "data get entity @e[type=netheritesupremo:ninja,limit=1,sort=nearest] ArmorItems[3]",
                "data get entity @e[type=netheritesupremo:ninja,limit=1,sort=nearest] HitDamage",
                "summon netheritesupremo:ninja 70 110 120",
                "attribute @e[type=netheritesupremo:ninja,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:ninja,limit=1] minecraft:generic.attack_damage get",
                "data get entity @e[type=netheritesupremo:ninja,limit=1] HandItems[0]",
                "loot spawn 0 100 0 loot netheritesupremo:entities/ninja",
                "locate biome minecraft:cherry_grove",
                "loot spawn 0 100 0 loot netheritesupremo:entities/giant_glow_squid",
                "summon netheritesupremo:shuriken 70 110 90",
                "summon netheritesupremo:netherbot 60 110 90",
                "summon minecraft:armor_stand 60 110 94 {Tags:[\"ktest\"]}",
                "recipe give @a netheritesupremo:katana",
                "summon netheritesupremo:giant_glow_squid 70 110 40",
                "attribute @e[type=netheritesupremo:giant_glow_squid,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:giant_glow_squid,limit=1] minecraft:generic.attack_damage get",
                "loot spawn 0 100 0 loot netheritesupremo:entities/giant_glow_squid",
                "summon netheritesupremo:giant_glow_squid_baby 70 110 44",
                "attribute @e[type=netheritesupremo:giant_glow_squid_baby,limit=1] minecraft:generic.max_health get",
                "setblock 74 110 40 netheritesupremo:giant_glow_squid_egg[age=2,waterlogged=true]",
                "data get block 74 110 40 Items",
                "kill @e[type=netheritesupremo:giant_glow_squid_baby,limit=1]",
                "loot spawn 0 100 0 loot netheritesupremo:entities/precursor",
                "give @a netheritesupremo:precursor_core",
                "give @a minecraft:enchanted_book{StoredEnchantments:[{id:\"netheritesupremo:multiplier_16x\",lvl:1s}]}",
                "loot spawn 0 100 0 loot netheritesupremo:entities/netherite_robot_rainbow_boss",
                "summon netheritesupremo:precursor 70 110 70",
                "attribute @e[type=netheritesupremo:precursor,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:precursor,limit=1] minecraft:generic.attack_damage get",
                "damage @e[type=netheritesupremo:precursor,limit=1] 20 minecraft:arrow",
                "damage @e[type=netheritesupremo:precursor,limit=1] 20 minecraft:explosion",
                "damage @e[type=netheritesupremo:precursor,limit=1] 20 minecraft:on_fire",
                "data get entity @e[type=netheritesupremo:precursor,limit=1] Health",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "data get entity @e[type=netheritesupremo:precursor,limit=1] Health",
                "data get entity @e[type=netheritesupremo:precursor,limit=1] Totems",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "data get entity @e[type=netheritesupremo:precursor,limit=1] Totems",
                "damage @e[type=netheritesupremo:precursor,limit=1] 99999 minecraft:generic",
                "data get entity @e[type=netheritesupremo:precursor,limit=1] Health",
                "loot spawn 0 100 0 loot netheritesupremo:entities/precursor",
                "summon netheritesupremo:netherite_robot_rainbow_boss 60 110 100",
                "attribute @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] minecraft:generic.attack_damage get",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 20 minecraft:arrow",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 20 minecraft:explosion",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 20 minecraft:lava",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 20 minecraft:on_fire",
                "data get entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] Health",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 5000 minecraft:generic",
                "data get entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] Health",
                "data get entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] Revived",
                "data get entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] ActiveEffects",
                "damage @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] 99999 minecraft:generic",
                "data get entity @e[type=netheritesupremo:netherite_robot_rainbow_boss,limit=1] Health",
                "loot spawn 0 100 0 loot netheritesupremo:entities/netherite_robot_rainbow_boss",
                "summon netheritesupremo:netherite_bomb_robot 80 100 80",
                "attribute @e[type=netheritesupremo:netherite_bomb_robot,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:netherite_bomb_robot,limit=1] minecraft:generic.attack_damage get",
                "damage @e[type=netheritesupremo:netherite_bomb_robot,limit=1] 10 minecraft:arrow",
                "damage @e[type=netheritesupremo:netherite_bomb_robot,limit=1] 10 minecraft:explosion",
                "damage @e[type=netheritesupremo:netherite_bomb_robot,limit=1] 10 minecraft:in_fire",
                "data get entity @e[type=netheritesupremo:netherite_bomb_robot,limit=1] Health",
                "summon netheritesupremo:netherbot 90 100 90",
                "attribute @e[type=netheritesupremo:netherbot,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:netherbot,limit=1] minecraft:generic.attack_damage get",
                "damage @e[type=netheritesupremo:netherbot,limit=1] 20 minecraft:arrow",
                "damage @e[type=netheritesupremo:netherbot,limit=1] 20 minecraft:explosion",
                "damage @e[type=netheritesupremo:netherbot,limit=1] 20 minecraft:lava",
                "data get entity @e[type=netheritesupremo:netherbot,limit=1] Health",
                "damage @e[type=netheritesupremo:netherbot,limit=1] 900 minecraft:generic",
                "data get entity @e[type=netheritesupremo:netherbot,limit=1] Health",
                "data get entity @e[type=netheritesupremo:netherbot,limit=1] Revived",
                "damage @e[type=netheritesupremo:netherbot,limit=1] 99999 minecraft:generic",
                "data get entity @e[type=netheritesupremo:netherbot,limit=1] Health",
                "loot spawn 0 100 0 loot netheritesupremo:entities/netherbot",
                "kill @e[type=netheritesupremo:netherite_bomb_robot]",
                "data get entity @e[type=netheritesupremo:netherite_bomb_robot,limit=1] Health",
                "summon netheritesupremo:robot 60 100 60",
                "attribute @e[type=netheritesupremo:robot,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:robot,limit=1] minecraft:generic.attack_damage get",
                "damage @e[type=netheritesupremo:robot,limit=1] 20 minecraft:arrow",
                "data get entity @e[type=netheritesupremo:robot,limit=1] Health",
                "damage @e[type=netheritesupremo:robot,limit=1] 20 minecraft:generic",
                "data get entity @e[type=netheritesupremo:robot,limit=1] Health",
                "data get entity @e[type=netheritesupremo:robot,limit=1] Variant",
                "loot spawn 0 100 0 loot netheritesupremo:entities/robot",
                "summon minecraft:wandering_trader 50 70 50",
                "data get entity @e[type=minecraft:wandering_trader,limit=1] Offers.Recipes[-1].sell",
                "place structure netheritesupremo:umbral_plains 200 70 200",
                "place structure netheritesupremo:umbral_nether 300 70 200",
                "data get entity @e[type=netheritesupremo:netherite_knight,limit=1] Health",
                "summon netheritesupremo:netherite_knight 0 100 0",
                "summon netheritesupremo:netherite_golem 90 100 90",
                "data get entity @e[type=netheritesupremo:netherite_knight,limit=1] ArmorItems",
                "data get entity @e[type=netheritesupremo:netherite_knight,limit=1] HandItems",
                "attribute @e[type=netheritesupremo:netherite_golem,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:netherite_knight,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:netherite_knight,limit=1] minecraft:generic.armor get",
                "attribute @e[type=netheritesupremo:netherite_golem,limit=1] minecraft:generic.attack_damage get",
                "loot spawn 0 100 0 loot netheritesupremo:entities/netherite_golem",
                "summon netheritesupremo:nether_spider 20 100 20",
                "attribute @e[type=netheritesupremo:nether_spider,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:nether_spider,limit=1] minecraft:generic.movement_speed get",
                "damage @e[type=netheritesupremo:nether_spider,limit=1] 20 minecraft:explosion",
                "damage @e[type=netheritesupremo:nether_spider,limit=1] 20 minecraft:in_fire",
                "data get entity @e[type=netheritesupremo:nether_spider,limit=1] Health",
                "summon netheritesupremo:nether_spider 20 100 25 {Baby:1b}",
                "data get entity @e[type=netheritesupremo:nether_spider,nbt={Baby:1b},limit=1] Baby",
                "summon netheritesupremo:netherite_knight 10 70 10 {Tame:1b}",
                "summon netheritesupremo:netherite_knight 12 70 10",
                "summon minecraft:iron_golem 13 70 10",
                "damage @e[type=netheritesupremo:netherite_knight,limit=1,sort=furthest] 1 minecraft:mob_attack by @e[type=netheritesupremo:thief,limit=1]",
                "summon netheritesupremo:umbral_merchant 40 70 40",
                "data get entity @e[type=netheritesupremo:umbral_merchant,limit=1] Offers.Recipes[0].sell",
                "data get entity @e[type=netheritesupremo:umbral_merchant,limit=1] Offers.Recipes[-1].sell",
                "summon netheritesupremo:golden_zombie 30 70 30",
                "attribute @e[type=netheritesupremo:golden_zombie,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:golden_zombie,limit=1] minecraft:generic.attack_damage get",
                "attribute @e[type=netheritesupremo:golden_zombie,limit=1] minecraft:generic.movement_speed get",
                "loot spawn 0 100 0 loot netheritesupremo:entities/golden_zombie",
                "loot spawn 0 100 0 loot netheritesupremo:entities/necromancer",
                "give @e[type=netheritesupremo:netherite_knight,limit=1] minecraft:diamond_pickaxe",
                "item replace entity @e[type=netheritesupremo:netherite_knight,limit=1] weapon.mainhand with minecraft:netherite_sword{Enchantments:[{id:\"netheritesupremo:multiplier_8x\",lvl:1s}]}",
                "data get entity @e[type=netheritesupremo:netherite_knight,limit=1] HandItems",
                "item replace entity @e[type=netheritesupremo:netherite_knight,limit=1] armor.head with minecraft:netherite_helmet{Enchantments:[{id:\"netheritesupremo:multiplier_2x\",lvl:1s}]}",
                "attribute @e[type=netheritesupremo:netherite_knight,limit=1] minecraft:generic.attack_damage get",
                "attribute @e[type=netheritesupremo:netherite_knight,limit=1] minecraft:generic.armor get",
                "summon netheritesupremo:necromancer 0 110 20",
                "attribute @e[type=netheritesupremo:necromancer,limit=1] minecraft:generic.max_health get",
                "attribute @e[type=netheritesupremo:necromancer,limit=1] minecraft:generic.attack_damage get",
                "effect give @e[type=netheritesupremo:netherite_golem,limit=1] minecraft:wither 30 2",
                "damage @e[type=netheritesupremo:netherite_golem,limit=1] 50 minecraft:explosion",
                "damage @e[type=netheritesupremo:netherite_golem,limit=1] 50 minecraft:arrow",
                "data get entity @e[type=netheritesupremo:netherite_golem,limit=1] Health",
                "data get entity @e[type=netheritesupremo:netherite_golem,limit=1] ActiveEffects",
                "damage @e[type=netheritesupremo:necromancer,limit=1] 1 minecraft:mob_attack by @e[type=netheritesupremo:netherite_knight,limit=1]",
            ]
            for t in tests:
                results.append(f"> {t}\n  {r.cmd(t)}")
            # Reproducao: aranha apaixonada perto de uma aranha comum e de outra aranha do nether apaixonada
            for t in ("summon netheritesupremo:nether_spider 60 70 60 {InLove:600}",
                      "summon minecraft:spider 63 70 60",
                      "summon netheritesupremo:nether_spider 70 70 60 {InLove:600}",
                      "summon netheritesupremo:nether_spider 73 70 60 {InLove:600}"):
                results.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(25)
            for t in ("execute if entity @e[type=netheritesupremo:nether_spider,nbt={Baby:1b}]",
                      "data get entity @e[type=netheritesupremo:nether_spider,nbt={Baby:1b},limit=1] BabyAge",
                      "execute as @e[type=netheritesupremo:nether_spider] run data get entity @s InLove"):
                results.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(15)  # deixa os mobs rodarem alguns segundos (IA, bolas de fogo, aura)
            for t in ("data get entity @e[type=netheritesupremo:necromancer,limit=1] Health",
                      "data get entity @e[type=netheritesupremo:netherite_golem,limit=1] Health",
                      "data get entity @e[type=netheritesupremo:netherite_knight,limit=1] Health",
                      "execute if entity @e[type=minecraft:skeleton,tag=netheritesupremo_minion]",
                      "kill @e[type=netheritesupremo:necromancer]",
                      "kill @e[type=netheritesupremo:nether_spider]",
                      "list", "kill @e[type=netheritesupremo:netherite_knight]",
                      "kill @e[type=netheritesupremo:netherite_golem]"):
                results.append(f"> {t}\n  {r.cmd(t)}")
            # ---- mods incorporados (Claude Content + Vila de Ferro)
            im = []
            for t in ["summon netheritesupremo:mossling 40 100 40", "summon netheritesupremo:lumileu 42 100 40",
                      "summon netheritesupremo:moss_colossus 44 100 40", "summon netheritesupremo:ash_reaper 46 100 40",
                      "summon netheritesupremo:cinder_stalker 48 100 40", "summon netheritesupremo:guerreiro_villager 50 100 40",
                      "summon netheritesupremo:chefe_villager 52 100 40",
                      "give @a netheritesupremo:cinder_helmet", "give @a netheritesupremo:root_altar",
                      "locate biome minecraft:forest"]:
                im.append(f"> {t}\n  {r.cmd(t)}")
            for e in ["mossling", "lumileu", "moss_colossus", "ash_reaper", "cinder_stalker", "guerreiro_villager", "chefe_villager"]:
                im.append(f"> existe {e}\n  {r.cmd('execute if entity @e[type=netheritesupremo:' + e + ']')}")
            r.cmd("kill @e[type=netheritesupremo:ash_reaper]"); r.cmd("kill @e[type=netheritesupremo:moss_colossus]")
            annotate("notice", "mods incorporados", im)
            # ---- Steve Vermelho + Terra Negra
            bl = []
            for t in ["fill 74 98 74 90 98 90 minecraft:stone", "fill 74 99 74 90 108 90 minecraft:air",
                      "summon netheritesupremo:red_steve 80 100 80 {CustomName:'\"steve\"',PersistenceRequired:1b}",
                      "attribute @e[type=netheritesupremo:red_steve,limit=1] minecraft:generic.max_health get",
                      "attribute @e[type=netheritesupremo:red_steve,limit=1] minecraft:generic.attack_damage get",
                      "execute in netheritesupremo:black_realm run forceload add 0 0 63 63",
                      "summon minecraft:pig 82 100 80 {CustomName:'\"assassino\"',PersistenceRequired:1b}"]:
                bl.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(40)
            for t in ["execute in netheritesupremo:black_realm run setblock 5 150 5 netheritesupremo:black_liquid",
                      "execute in netheritesupremo:black_realm run fill 0 0 0 15 100 15 minecraft:stone replace netheritesupremo:black_soil",
                      "execute in netheritesupremo:black_realm run fill 0 0 16 15 100 31 minecraft:stone replace netheritesupremo:black_liquid",
                      "execute in netheritesupremo:black_realm run fill 16 0 0 31 100 15 minecraft:stone replace minecraft:dark_oak_log",
                      "execute in netheritesupremo:black_realm run locate structure netheritesupremo:ruin_house",
                      "execute in netheritesupremo:black_realm run locate structure netheritesupremo:ruin_tower",
                      "execute in netheritesupremo:black_realm run locate structure netheritesupremo:ruin_hall",
                      "execute in netheritesupremo:black_realm if entity @e[x=0,y=-64,z=0,dx=64,dy=500,dz=64,type=!minecraft:item]",
                      "damage @e[type=netheritesupremo:red_steve,limit=1] 1000 minecraft:mob_attack by @e[type=minecraft:pig,name=assassino,limit=1]"]:
                bl.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(8)
            liq = 0
            for cx in range(0, 64, 16):
                for cz in range(0, 64, 16):
                    out = r.cmd(f"execute in netheritesupremo:black_realm run fill {cx} 0 {cz} {cx + 15} 100 {cz + 15} minecraft:stone replace netheritesupremo:black_liquid")
                    m = _re.search(r"filled (\d+)", out)
                    liq += int(m.group(1)) if m else 0
            bl.append(f"liquido negro: {liq} blocos em 16 chunks")
            for t in ["execute in netheritesupremo:black_realm if entity @e[type=minecraft:pig,name=assassino]",
                      "execute in minecraft:overworld if entity @e[type=minecraft:pig,name=assassino]",
                      "execute in netheritesupremo:black_realm run data get entity @e[type=minecraft:pig,name=assassino,limit=1] Pos",
                      "execute if entity @e[type=netheritesupremo:red_steve,name=steve]"]:
                bl.append(f"> {t}\n  {r.cmd(t)}")
            r.cmd("execute in netheritesupremo:black_realm run forceload remove 0 0 63 63")
            annotate("notice", "steve vermelho / terra negra", bl)
            # ---- Rei Esqueleto + invocador do necromante
            kg = []
            for t in ["summon netheritesupremo:skeleton_king 70 100 70 {CustomName:'\"rei\"'}",
                      "give @a netheritesupremo:necromancer_summoner"]:
                kg.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(3)
            for t in ["attribute @e[type=netheritesupremo:skeleton_king,limit=1] minecraft:generic.max_health get",
                      "attribute @e[type=netheritesupremo:skeleton_king,limit=1] minecraft:generic.attack_damage get",
                      "execute if entity @e[type=#forge:bosses,type=netheritesupremo:skeleton_king]",
                      "data modify entity @e[type=netheritesupremo:skeleton_king,limit=1] Health set value 3000f",
                      "recipe give @a netheritesupremo:necromancer_summoner"]:
                kg.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(3)
            kg.append("> fase 2 (necromantes)\n  " + r.cmd("execute if entity @e[type=netheritesupremo:necromancer]"))
            kg.append("> dano fase 2\n  " + r.cmd("attribute @e[type=netheritesupremo:skeleton_king,limit=1] minecraft:generic.attack_damage get"))
            r.cmd("kill @e[type=netheritesupremo:skeleton_king]"); r.cmd("kill @e[type=netheritesupremo:necromancer]")
            annotate("notice", "rei esqueleto", kg)
            # ---- nado rapido dos guerreiros/chefe
            sw = []
            for t in ["attribute @e[type=netheritesupremo:guerreiro_villager,limit=1] forge:swim_speed get",
                      "attribute @e[type=netheritesupremo:chefe_villager,limit=1] forge:swim_speed get"]:
                sw.append(f"> {t}\n  {r.cmd(t)}")
            annotate("notice", "nado", sw)
            # ---- Old Gun, esqueletos neutros, esqueleto villager
            sk = []
            def scmd(t):
                out = r.cmd(t)
                if not t.startswith(("summon", "fill", "kill", "time ", "weather")):
                    sk.append(f"> {t}\n  {out}")
                return out
            scmd("fill 96 98 96 110 98 110 minecraft:stone")
            scmd("fill 96 99 96 110 108 110 minecraft:air")
            scmd("time set 6000")
            scmd("weather clear")
            helm = 'ArmorItems:[{},{},{},{id:"minecraft:iron_helmet",Count:1b}]'
            scmd(f"summon minecraft:skeleton 98 100 98 {{PersistenceRequired:1b,Invulnerable:1b,{helm},CustomName:'\"esq\"'}}")
            scmd(f"summon minecraft:skeleton 98 100 100 {{PersistenceRequired:1b,Invulnerable:1b,{helm},CustomName:'\"esq2\"'}}")
            scmd(f"summon minecraft:skeleton 98 100 102 {{PersistenceRequired:1b,Invulnerable:1b,{helm},CustomName:'\"esq3\"'}}")
            scmd("summon minecraft:stray 100 100 98 {PersistenceRequired:1b,Invulnerable:1b,CustomName:'\"gelo\"'}")
            scmd("summon minecraft:wither_skeleton 100 100 100 {PersistenceRequired:1b,Invulnerable:1b,CustomName:'\"wither\"'}")
            scmd("summon netheritesupremo:skeleton_villager 102 100 98 {PersistenceRequired:1b,CustomName:'\"vila\"'}")
            scmd("summon minecraft:item 104 100 98 {Item:{id:\"netheritesupremo:old_gun\",Count:1b}}")
            time.sleep(4)
            for who in ("esq", "gelo", "wither"):
                scmd(f"data get entity @e[name={who},limit=1] HandItems[0].id")
            time.sleep(8)
            scmd("attribute @e[name=esq,limit=1] minecraft:generic.max_health get")
            scmd("data get entity @e[name=esq,limit=1] Health")
            scmd("attribute @e[name=esq,limit=1] minecraft:generic.attack_damage get")
            scmd("data get entity @e[name=esq,limit=1] ArmorItems[0].id")
            scmd("data get entity @e[name=esq,limit=1] ArmorItems[1].id")
            scmd("data get entity @e[name=esq,limit=1] ArmorItems[2].id")
            scmd("data get entity @e[name=esq,limit=1] ArmorItems[3].id")
            scmd("data get entity @e[name=esq,limit=1] ArmorItems[0].tag.Unbreakable")
            scmd("data get entity @e[name=esq,limit=1] Fire")
            scmd("data get entity @e[name=esq,limit=1] Tags")
            scmd("data get entity @e[type=netheritesupremo:skeleton_villager,limit=1] HandItems[0].id")
            scmd("data get entity @e[type=netheritesupremo:skeleton_villager,limit=1] Health")
            scmd("data get entity @e[type=netheritesupremo:skeleton_villager,limit=1] Offers.Recipes[0]")
            scmd("data get entity @e[type=netheritesupremo:skeleton_villager,limit=1] Offers.Recipes[1].buy")
            # dano da bala: cavalo com 1000 de vida, 6 tiros
            scmd("summon minecraft:horse 105 100 108 {NoAI:1b,PersistenceRequired:1b,Attributes:[{Name:\"generic.max_health\",Base:1000d}],Health:1000f,CustomName:'\"alvo\"'}")
            time.sleep(1)
            prev = 1000.0
            dmgs = []
            for _ in range(6):
                r.cmd("summon netheritesupremo:old_gun_bullet 105 100.6 103 {Motion:[0.0d,0.0d,2.0d]}")
                time.sleep(1.6)
                out = r.cmd("data get entity @e[name=alvo,limit=1] Health")
                m = _re.search(r"(-?[\d.]+)f?$", out.strip())
                hp = float(m.group(1)) if m else prev
                dmgs.append(round(prev - hp, 1)); prev = hp
            sk.append(f"dano da bala (6 tiros): {dmgs} min={min(dmgs)} max={max(dmgs)}")
            # obus do tanque: 750 de dano num alvo parado
            scmd("summon minecraft:horse 100 100 112 {NoAI:1b,PersistenceRequired:1b,Attributes:[{Name:\"generic.max_health\",Base:1000d}],Health:1000f,CustomName:'\"alvo2\"'}")
            time.sleep(1)
            scmd("summon netheritesupremo:tank_shell 100 100.9 104 {Motion:[0.0d,0.0d,1.5d]}")
            time.sleep(3)
            scmd("data get entity @e[name=alvo2,limit=1] Health")
            scmd("kill @e[name=alvo2]")
            # esqueletos defendem quando um deles e atacado (o villager leva um golpe de um porco)
            scmd("summon minecraft:pig 108 100 108 {PersistenceRequired:1b,CustomName:'\"agressor\"'}")
            time.sleep(1)
            scmd("data get entity @e[name=vila,limit=1] Health")
            scmd("damage @e[name=vila,limit=1] 4 minecraft:mob_attack by @e[type=minecraft:pig,name=agressor,limit=1]")
            scmd("data get entity @e[name=vila,limit=1] Health")
            time.sleep(12)
            scmd("execute if entity @e[type=minecraft:pig,name=agressor]")
            scmd("kill @e[x=96,y=98,z=96,dx=16,dy=12,dz=16,type=!minecraft:player]")
            # Rei de vila: calmo ate ser atacado
            scmd("summon netheritesupremo:skeleton_king 100 100 106 {VillageKing:1b,PersistenceRequired:1b,CustomName:'\"reivila\"'}")
            time.sleep(2)
            scmd("data get entity @e[name=reivila,limit=1] VillageKing")
            scmd("kill @e[name=reivila]")
            annotate("notice", "old gun / esqueletos", sk)
            # ---- guerra dos esqueletos (modo de teste: sem jogador, 2 ondas de 3)
            wr = []
            def wcmd(t):
                out = r.cmd(t)
                if not t.startswith(("summon", "fill", "kill", "forceload")):
                    wr.append(f"> {t}\n  {out}")
                return out
            wcmd("forceload add 90 90 170 170")
            time.sleep(25)
            wcmd("fill 90 99 90 170 99 170 minecraft:stone")
            wcmd("fill 90 100 90 170 103 170 minecraft:air")
            for (vx, vz) in ((128, 128), (132, 130), (130, 133)):
                wcmd(f"summon minecraft:villager {vx} 100 {vz} {{PersistenceRequired:1b}}")
            wcmd("skeletonwar reset")
            wcmd("skeletonwar test 130 100 130")
            time.sleep(8)
            wcmd("execute if entity @e[type=minecraft:skeleton_horse,x=130,y=100,z=130,distance=..80]")
            wcmd("execute as @e[type=minecraft:skeleton_horse,x=130,y=100,z=130,distance=..80] run data get entity @s Passengers[0].id")
            wcmd("data get entity @e[type=minecraft:skeleton_horse,x=130,y=100,z=130,sort=furthest,limit=1] Pos")
            wcmd("execute if entity @e[type=netheritesupremo:war_tank,x=130,y=100,z=130,distance=..90]")
            wcmd("attribute @e[type=netheritesupremo:war_tank,limit=1] minecraft:generic.max_health get")
            wcmd("data get entity @e[type=netheritesupremo:war_tank,limit=1] Passengers[0].id")
            wcmd("data get entity @e[type=netheritesupremo:war_tank,limit=1] Pos")
            wcmd("execute if entity @e[type=netheritesupremo:tank_shell,x=130,y=100,z=130,distance=..90]")
            time.sleep(3)
            wcmd("data get entity @e[type=minecraft:skeleton_horse,x=130,y=100,z=130,sort=furthest,limit=1] Pos")
            wcmd("data get entity @e[type=netheritesupremo:war_tank,limit=1] Pos")
            wcmd("execute if entity @e[type=minecraft:villager,x=130,y=100,z=130,distance=..50]")
            wcmd("execute if entity @e[type=minecraft:villager,x=130,y=100,z=130,distance=..50]")
            wcmd("execute if entity @e[tag=netheritesupremo_war_skeleton,x=130,y=100,z=130,distance=..80]")
            # os moradores morrem: o lugar tem que ser conquistado
            wcmd("kill @e[type=minecraft:villager,x=130,y=100,z=130,distance=..80]")
            for _ in range(12):
                time.sleep(3)
                state = r.cmd("skeletonwar status")
                if "CONQUERED" in state:
                    break
            wr.append("status apos matar moradores: " + state)
            # lugar conquistado: cavalos somem e aldeoes/saqueadores nao voltam
            wcmd("execute if entity @e[type=minecraft:skeleton_horse,x=130,y=100,z=130,distance=..90]")
            wcmd("execute if entity @e[type=netheritesupremo:war_tank,x=130,y=100,z=130,distance=..90]")
            wcmd("execute if entity @e[tag=netheritesupremo_tank_crew]")
            wcmd("summon minecraft:villager 128 100 128 {PersistenceRequired:1b}")
            wcmd("summon minecraft:pillager 131 100 131 {PersistenceRequired:1b}")
            wcmd("summon minecraft:iron_golem 133 100 128 {PersistenceRequired:1b}")
            time.sleep(6)
            wcmd("execute if entity @e[type=minecraft:villager,x=130,y=100,z=130,distance=..60]")
            wcmd("execute if entity @e[type=minecraft:pillager,x=130,y=100,z=130,distance=..60]")
            wcmd("execute if entity @e[type=minecraft:iron_golem,x=130,y=100,z=130,distance=..60]")
            # lugar conquistado: esqueletos passam a surgir sozinhos
            wcmd("kill @e[type=minecraft:skeleton,x=130,y=100,z=130,distance=..80]")
            wcmd("kill @e[type=minecraft:stray,x=130,y=100,z=130,distance=..80]")
            wcmd("kill @e[type=minecraft:wither_skeleton,x=130,y=100,z=130,distance=..80]")
            time.sleep(60)
            wcmd("execute if entity @e[type=minecraft:skeleton,x=130,y=100,z=130,distance=..80]")
            wcmd("execute if entity @e[type=netheritesupremo:skeleton_villager,x=130,y=100,z=130,distance=..80]")
            wcmd("execute if entity @e[type=minecraft:stray,x=130,y=100,z=130,distance=..80]")
            wcmd("skeletonwar status")
            wcmd("skeletonwar reset")
            wcmd("kill @e[x=90,y=99,z=90,dx=80,dy=12,dz=80,type=!minecraft:player]")
            # tanques isolados (um sem tripulante e um com): tem que andar ate o aldeao parado e mata-lo com o obus
            tk = []
            def tcmd(t):
                out = r.cmd(t)
                if not t.startswith(("summon", "kill")):
                    tk.append(f"> {t}\n  {out}")
                return out
            tcmd("summon minecraft:villager 100 100 140 {NoAI:1b,PersistenceRequired:1b,CustomName:'\"vilaA\"'}")
            tcmd("summon minecraft:villager 115 100 140 {NoAI:1b,PersistenceRequired:1b,CustomName:'\"vilaB\"'}")
            tcmd("summon netheritesupremo:war_tank 100 100 105 {PersistenceRequired:1b,CustomName:'\"tanqueA\"'}")
            tcmd("summon netheritesupremo:war_tank 115 100 105 {PersistenceRequired:1b,CustomName:'\"tanqueB\"',Passengers:[{id:\"minecraft:skeleton\",NoAI:1b,PersistenceRequired:1b}]}")
            for espera in (3, 4, 10):
                time.sleep(espera)
                tk.append(f"--- +{espera}s")
                tcmd("data get entity @e[name=tanqueA,limit=1] Pos")
                tcmd("data get entity @e[name=tanqueB,limit=1] Pos")
                tcmd("execute if entity @e[name=vilaA]")
                tcmd("execute if entity @e[name=vilaB]")
            tcmd("data get entity @e[name=tanqueB,limit=1] Passengers[0].id")
            tcmd("kill @e[type=netheritesupremo:war_tank]")
            tcmd("kill @e[name=vilaA]")
            tcmd("kill @e[name=vilaB]")
            tcmd("kill @e[type=minecraft:skeleton,x=100,y=99,z=100,dx=30,dy=12,dz=30]")
            annotate("notice", "tanque isolado", tk)

            wcmd("forceload remove 90 90 170 170")
            # declaracao real (procura vilas/outposts de verdade) e encerra logo
            wcmd("execute positioned 0 100 0 run skeletonwar declare")
            time.sleep(2)
            wcmd("skeletonwar status")
            wcmd("skeletonwar reset")
            annotate("notice", "guerra dos esqueletos", wr)
            # ---- vila de esqueletos (9 casas, 9 moradores, rei)
            vl = []
            def vcmd(t):
                out = r.cmd(t)
                vl.append(f"> {t}\n  {out}")
                return out
            VBOX = "x=500,y=-64,z=500,dx=500,dy=400,dz=500"
            vcmd("forceload add 700 700 820 840")
            time.sleep(60)
            vcmd("place structure netheritesupremo:skeleton_village 750 70 750")
            for espera in (1, 4, 10):
                time.sleep(espera)
                vl.append(f"--- depois de ~{espera}s")
                vcmd(f"execute if entity @e[type=netheritesupremo:skeleton_villager,{VBOX}]")
                vcmd(f"execute if entity @e[type=netheritesupremo:skeleton_king,{VBOX}]")
            vcmd("execute if entity @e[type=netheritesupremo:skeleton_villager]")
            vcmd("execute if entity @e[type=netheritesupremo:skeleton_king]")
            vcmd("data get entity @e[type=netheritesupremo:skeleton_king,limit=1] Pos")
            vcmd("data get entity @e[type=netheritesupremo:skeleton_king,limit=1] VillageKing")
            vcmd("data get entity @e[type=netheritesupremo:skeleton_king,limit=1] Health")
            # vila natural: acha com locate, carrega a area toda e confere se o rei nasceu la
            lo = vcmd("execute in minecraft:overworld positioned 0 70 0 run locate structure netheritesupremo:skeleton_village")
            ml = _re.search(r"\[(-?\d+), ~, (-?\d+)\]", lo)
            if ml:
                nx, nz = int(ml.group(1)), int(ml.group(2))
                if abs(nx - 750) > 200 or abs(nz - 750) > 200:
                    NBOX = f"x={nx - 70},y=-64,z={nz - 70},dx=140,dy=400,dz=140"
                    vcmd(f"forceload add {nx - 70} {nz - 70} {nx + 70} {nz + 70}")
                    time.sleep(50)
                    vcmd(f"execute if entity @e[type=netheritesupremo:skeleton_villager,{NBOX}]")
                    vcmd(f"execute if entity @e[type=netheritesupremo:skeleton_king,{NBOX}]")
                    vcmd(f"data get entity @e[type=netheritesupremo:skeleton_king,{NBOX},limit=1] Pos")
                    vcmd(f"data get entity @e[type=netheritesupremo:skeleton_king,{NBOX},limit=1] VillageKing")
                    vcmd(f"forceload remove {nx - 70} {nz - 70} {nx + 70} {nz + 70}")
            vcmd("kill @e[type=netheritesupremo:skeleton_king]")
            vcmd("kill @e[type=netheritesupremo:skeleton_villager]")
            vcmd("forceload remove 700 700 820 840")
            annotate("notice", "vila de esqueletos", vl)
            # ---- macas e bolo de netherite + adaptacao da armadura
            ad = []
            def acmd(t):
                out = r.cmd(t)
                if not t.startswith(("summon", "fill", "kill", "time ")):
                    ad.append(f"> {t}\n  {out}")
                return out
            acmd("fill 118 98 94 160 98 112 minecraft:stone")
            acmd("summon minecraft:item 120 100 96 {Item:{id:\"netheritesupremo:netherite_apple\",Count:1b}}")
            acmd("summon minecraft:item 121 100 96 {Item:{id:\"netheritesupremo:enchanted_netherite_apple\",Count:1b}}")
            acmd("summon minecraft:item 122 100 96 {Item:{id:\"netheritesupremo:netherite_cake\",Count:1b}}")
            acmd("setblock 124 99 96 netheritesupremo:netherite_cake[bites=3]")
            acmd("execute if block 124 99 96 netheritesupremo:netherite_cake[bites=3]")
            acmd("execute if entity @e[type=minecraft:item,x=118,y=99,z=94,dx=8,dy=4,dz=4,nbt={Item:{id:\"netheritesupremo:netherite_apple\"}}]")
            acmd("execute if entity @e[type=minecraft:item,x=118,y=99,z=94,dx=8,dy=4,dz=4,nbt={Item:{id:\"netheritesupremo:enchanted_netherite_apple\"}}]")
            acmd("execute if entity @e[type=minecraft:item,x=118,y=99,z=94,dx=8,dy=4,dz=4,nbt={Item:{id:\"netheritesupremo:netherite_cake\"}}]")
            arm = 'ArmorItems:[{id:"minecraft:netherite_boots",Count:1b},{id:"minecraft:netherite_leggings",Count:1b},{id:"minecraft:netherite_chestplate",Count:1b},{id:"minecraft:netherite_helmet",Count:1b}]'
            # A: perto de uma armadura MUITO superior; B: perto de uma armadura mais fraca; C: sozinha
            acmd(f"summon minecraft:armor_stand 130 99 100 {{NoGravity:1b,CustomName:'\"adaptA\"',{arm}}}")
            acmd(f"summon minecraft:armor_stand 150 99 100 {{NoGravity:1b,CustomName:'\"adaptB\"',{arm}}}")
            acmd(f"summon minecraft:armor_stand 140 99 108 {{NoGravity:1b,CustomName:'\"adaptC\"',{arm}}}")
            acmd("summon minecraft:zombie 134 99 100 {NoAI:1b,Invulnerable:1b,PersistenceRequired:1b,CustomName:'\"forte\"',Attributes:[{Name:\"generic.armor\",Base:300d},{Name:\"generic.armor_toughness\",Base:150d}],ActiveEffects:[{Id:11,Amplifier:3b,Duration:100000},{Id:5,Amplifier:3b,Duration:100000}]}")
            acmd("summon minecraft:zombie 154 99 100 {NoAI:1b,Invulnerable:1b,PersistenceRequired:1b,CustomName:'\"fraco\"',Attributes:[{Name:\"generic.armor\",Base:2d}]}")
            time.sleep(6)
            for who in ("adaptA", "adaptB", "adaptC"):
                acmd(f"attribute @e[name={who},limit=1] minecraft:generic.armor get")
                acmd(f"attribute @e[name={who},limit=1] minecraft:generic.armor_toughness get")
                acmd(f"data get entity @e[name={who},limit=1] ArmorItems[2].tag.NetheriteAdapt")
            acmd("attribute @e[name=forte,limit=1] minecraft:generic.armor get")
            # a adaptacao fica depois que a armadura superior morre
            acmd("kill @e[name=forte]")
            time.sleep(4)
            ad.append("--- depois da morte do portador da armadura superior")
            acmd("attribute @e[name=adaptA,limit=1] minecraft:generic.armor get")
            acmd("data get entity @e[name=adaptA,limit=1] ArmorItems[2].tag.NetheriteAdapt")
            # armadura encantada 2x: os valores e os efeitos escalam
            ench = 'tag:{Enchantments:[{id:"netheritesupremo:multiplier_2x",lvl:1s}]}'
            arm2 = f'ArmorItems:[{{id:"minecraft:netherite_boots",Count:1b,{ench}}},{{id:"minecraft:netherite_leggings",Count:1b,{ench}}},{{id:"minecraft:netherite_chestplate",Count:1b,{ench}}},{{id:"minecraft:netherite_helmet",Count:1b,{ench}}}]'
            acmd(f"summon minecraft:zombie 160 99 108 {{NoAI:1b,Invulnerable:1b,PersistenceRequired:1b,CustomName:'\"adaptD\"',{arm2}}}")
            time.sleep(4)
            acmd("attribute @e[name=adaptD,limit=1] minecraft:generic.armor get")
            acmd("data get entity @e[name=adaptD,limit=1] ActiveEffects")
            acmd("kill @e[name=adaptD]")
            acmd("kill @e[name=fraco]")
            acmd("kill @e[type=minecraft:armor_stand,x=118,y=99,z=94,dx=50,dy=6,dz=20]")
            annotate("notice", "macas, bolo e adaptacao", ad)
            # ---- aliados alienigenas, soldados vs saqueadores/piglins, hibridos
            al = []
            for t in ["fill 18 99 18 34 99 34 minecraft:stone",
                      "fill 18 100 18 34 110 34 minecraft:air",
                      "summon netheritesupremo:precursor_soldier 24 100 24 {CustomName:'\"sold\"',PersistenceRequired:1b}",
                      "summon netheritesupremo:netherite_bomb_robot 26 100 24 {CustomName:'\"bomba\"',PersistenceRequired:1b}",
                      "summon netheritesupremo:robot 28 100 24 {PersistenceRequired:1b}",
                      "summon netheritesupremo:alien_ship 24 104 28 {CustomName:'\"nave\"',PersistenceRequired:1b}",
                      "summon minecraft:pillager 30 100 26 {PersistenceRequired:1b}",
                      "summon minecraft:piglin 20 100 26 {PersistenceRequired:1b}",
                      "summon netheritesupremo:precursor_villager 32 100 32 {PersistenceRequired:1b}",
                      "summon minecraft:villager 33 100 32 {PersistenceRequired:1b}"]:
                al.append(f"> {t}\n  {r.cmd(t)}")
            time.sleep(30)
            for t in ["data get entity @e[type=netheritesupremo:precursor_soldier,name=sold,limit=1] Health",
                      "data get entity @e[type=netheritesupremo:netherite_bomb_robot,name=bomba,limit=1] Health",
                      "data get entity @e[type=netheritesupremo:alien_ship,name=nave,limit=1] Health",
                      "execute if entity @e[type=minecraft:pillager,x=30,y=100,z=26,distance=..30]",
                      "execute if entity @e[type=minecraft:piglin,x=20,y=100,z=26,distance=..30]",
                      "execute if entity @e[type=netheritesupremo:hybrid_villager]",
                      "attribute @e[type=netheritesupremo:precursor_villager,limit=1] minecraft:generic.max_health get",
                      "attribute @e[type=netheritesupremo:precursor_soldier,limit=1] minecraft:generic.max_health get"]:
                al.append(f"> {t}\n  {r.cmd(t)}")
            r.cmd("kill @e[type=netheritesupremo:precursor_soldier]"); r.cmd("kill @e[type=netheritesupremo:alien_ship]"); r.cmd("kill @e[type=netheritesupremo:robot]"); r.cmd("kill @e[type=netheritesupremo:netherite_bomb_robot]")
            annotate("notice", "aliados", al)
            # ---- invasao alienigena (modo de teste: sem jogador, ondas rapidas)
            inv = []
            def icmd(t):
                inv.append(f"> {t}\n  {r.cmd(t)}")
            icmd("invasion reset")
            icmd("invasion test 40 40")
            time.sleep(10)
            for ty in ("precursor_soldier", "netherite_bomb_robot", "alien_ship", "netherbot"):
                icmd(f"execute if entity @e[type=netheritesupremo:{ty},tag=netheritesupremo_invader]")
            icmd("invasion status")
            time.sleep(28)
            icmd("execute if entity @e[type=netheritesupremo:precursor,tag=netheritesupremo_invader]")
            icmd("invasion status")
            icmd("invasion lose")
            icmd("execute if entity @e[tag=netheritesupremo_invader]")
            icmd("fill 30 55 30 50 110 50 minecraft:air replace minecraft:chest")
            icmd("invasion win")
            icmd("invasion status")
            icmd("invasion city 400 400")
            icmd("execute if entity @e[type=netheritesupremo:precursor_villager,x=400,y=64,z=400,distance=..70]")
            icmd("invasion reset")
            annotate("notice", "invasion", inv)
            # novos mobs: ganso, galinha de netherite, aranha colossal
            mob = []
            def mcmd(t):
                mob.append(f"> {t}\n  {r.cmd(t)}")
            r.cmd("forceload add 900 900")
            r.cmd("fill 895 59 895 920 59 920 minecraft:grass_block")
            r.cmd("fill 895 60 895 920 75 920 minecraft:air")
            for ty in ("goose", "netherite_chicken", "colossal_spider"):
                r.cmd(f"summon netheritesupremo:{ty} 905 60 905")
            time.sleep(2)
            for ty, at in (("goose", "max_health"), ("goose", "attack_damage"), ("netherite_chicken", "max_health"),
                           ("netherite_chicken", "attack_damage"), ("colossal_spider", "max_health"),
                           ("colossal_spider", "attack_damage")):
                mcmd(f"attribute @e[type=netheritesupremo:{ty},limit=1] minecraft:generic.{at} get")
            mcmd("execute if entity @e[type=netheritesupremo:colossal_spider,x=905,y=60,z=905,distance=..60]")
            r.cmd("fill 896 60 896 903 62 903 minecraft:cobweb")
            mcmd("fill 896 60 896 903 62 903 minecraft:air replace minecraft:cobweb")
            r.cmd("summon minecraft:chicken 910 60 910")
            r.cmd("kill @e[type=netheritesupremo:goose]")
            time.sleep(1)
            mcmd("execute if entity @e[type=minecraft:item,nbt={Item:{id:\"minecraft:feather\"}}]")
            r.cmd("kill @e[type=minecraft:item]")
            annotate("notice", "novos mobs", mob)
            time.sleep(3)
            try:
                r.cmd("stop")
            except Exception:
                pass
        except Exception as e:  # noqa: BLE001
            results.append(f"RCON falhou: {e!r}")

    # Espera o processo encerrar; se nao encerrar, mata o grupo
    try:
        proc.wait(timeout=90)
    except subprocess.TimeoutExpired:
        os.killpg(os.getpgid(proc.pid), signal.SIGTERM)
        time.sleep(5)

    log = read_log()
    problems = problem_lines(log)
    crash_dir = os.path.join(RUN, "crash-reports")
    crash_text = []
    if os.path.isdir(crash_dir):
        for name in sorted(os.listdir(crash_dir)):
            with open(os.path.join(crash_dir, name), encoding="utf-8", errors="replace") as f:
                crash_text.append(f"== {name}\n" + "".join(f.readlines()[:45]))

    results = [l for l in log.splitlines() if ("receita original da netherite" in l or "DIAG netherite_ingot" in l)] + results
    if results:
        annotate("notice", "Testes de mobs (RCON)", results)
    if not started:
        tail = log.splitlines()[-40:] or open("server_gradle.out", errors="replace").read().splitlines()[-40:]
        annotate("error", "Servidor NAO iniciou", ["Ultimas linhas do log:"] + tail)
    if crash_text:
        annotate("error", "Crash report", crash_text)
    if problems:
        annotate("error" if (not started or crash_text) else "warning", "Linhas de problema no log", problems[:70])
    print("\n".join(problems[:200]))
    print("started =", started)
    sys.exit(0 if started and not crash_text else 1)


if __name__ == "__main__":
    main()
