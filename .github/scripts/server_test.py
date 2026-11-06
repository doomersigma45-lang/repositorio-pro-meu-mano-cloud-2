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
            # ---- nado rapido dos guerreiros/chefe
            sw = []
            for t in ["attribute @e[type=netheritesupremo:guerreiro_villager,limit=1] forge:swim_speed get",
                      "attribute @e[type=netheritesupremo:chefe_villager,limit=1] forge:swim_speed get"]:
                sw.append(f"> {t}\n  {r.cmd(t)}")
            annotate("notice", "nado", sw)
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
