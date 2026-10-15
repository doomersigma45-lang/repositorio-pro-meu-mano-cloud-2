#!/usr/bin/env python3
"""Sobe um servidor Forge 1.20.1 com o mod (mundo novo), testa os mobs por RCON e resume o log.

Resultado em anotacoes do GitHub (::error / ::notice), que podem ser lidas pela API de check-runs.
"""
import os
import re
import signal
import socket
import struct
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
            time.sleep(20)
            tests = [
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
                "summon minecraft:zombie 13 70 10",
                "damage @e[type=netheritesupremo:netherite_knight,limit=1,sort=furthest] 1 minecraft:mob_attack by @e[type=minecraft:zombie,limit=1]",
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
