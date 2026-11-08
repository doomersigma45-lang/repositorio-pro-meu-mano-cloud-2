#!/usr/bin/env python3
"""Gera 5 sons estranhos/assustadores da Terra Negra (assets/netheritesupremo/sounds/ambient/strange_N.ogg).
Uso: python3 tools/gerar_sons_estranhos.py   (precisa de numpy e ffmpeg)"""
import os, subprocess, wave
import numpy as np
SR = 32000
rng = np.random.default_rng(1313)

def t_(d): return np.arange(int(SR * d)) / SR
def env(n, a, r):
    e = np.ones(n); na, nr = int(a * SR), int(r * SR)
    e[:na] = np.linspace(0, 1, na); e[-nr:] = np.linspace(1, 0, nr); return e
def bandpass(x, lo, hi):
    X = np.fft.rfft(x); f = np.fft.rfftfreq(len(x), 1 / SR); X[(f < lo) | (f > hi)] = 0; return np.fft.irfft(X, len(x))
def reverb(x, tail=2.5, mix=0.35):
    ir_t = t_(tail); ir = rng.standard_normal(len(ir_t)) * np.exp(-ir_t * 2.0); ir[0] = 1
    w = np.fft.irfft(np.fft.rfft(x, len(x) + len(ir)) * np.fft.rfft(ir, len(x) + len(ir)))
    out = np.concatenate([x, np.zeros(len(ir))]) * (1 - mix) + w * mix * 0.15
    return out
def norm(x, peak=0.85): return x / (np.max(np.abs(x)) + 1e-9) * peak

def groan():     # gemido grave com vibrato e ruido
    t = t_(4.5); f = 62 + 14 * np.sin(2 * np.pi * 0.35 * t) + 8 * np.sin(2 * np.pi * 5.3 * t)
    ph = 2 * np.pi * np.cumsum(f) / SR
    x = np.sin(ph) + 0.6 * np.sin(2 * ph) + 0.4 * np.sin(3 * ph + 1) + 0.2 * bandpass(rng.standard_normal(len(t)), 60, 400)
    return x * env(len(t), 1.2, 2.0)
def whisper():   # sussurro invertido (ruido modulado em formantes)
    t = t_(3.8); n = rng.standard_normal(len(t))
    x = bandpass(n, 1800, 4200) * (0.5 + 0.5 * np.sin(2 * np.pi * 3.1 * t + np.sin(2 * np.pi * 0.6 * t) * 4)) \
        + 0.7 * bandpass(n, 500, 1100) * (0.5 + 0.5 * np.sin(2 * np.pi * 2.3 * t))
    return (x * env(len(t), 0.2, 1.5))[::-1].copy()
def scrape():    # arranhao metalico
    t = t_(3.0); x = bandpass(rng.standard_normal(len(t)), 2500, 9000) * np.abs(np.sin(2 * np.pi * 1.7 * t) ** 3)
    for fr in (523, 831, 1244, 1789):
        x += 0.25 * np.sin(2 * np.pi * fr * t) * np.exp(-t * 1.4)
    return x * env(len(t), 0.05, 1.3)
def heartbeat(): # batidas surdas, lentas e irregulares
    x = np.zeros(int(SR * 6.0))
    for k, at in enumerate((0.3, 0.75, 1.9, 2.35, 3.6, 4.05, 5.0)):
        tt = t_(0.5); th = np.sin(2 * np.pi * (48 - 14 * tt) * tt) * np.exp(-tt * 9)
        i = int(at * SR); x[i:i + len(th)] += th * (1.0 if k % 2 == 0 else 0.7)
    return x
def wail():      # lamento distante
    t = t_(5.0); f = 380 + 260 * np.sin(2 * np.pi * 0.25 * t) + 90 * np.sin(2 * np.pi * 0.9 * t)
    ph = 2 * np.pi * np.cumsum(f) / SR
    x = (np.sin(ph) + 0.35 * np.sin(2 * ph)) * (0.7 + 0.3 * np.sin(2 * np.pi * 6 * t))
    return x * env(len(t), 1.5, 2.5)

here = os.path.dirname(os.path.abspath(__file__))
dest = os.path.join(here, '..', 'forge-1.20.1', 'src', 'main', 'resources', 'assets', 'netheritesupremo', 'sounds', 'ambient')
os.makedirs(dest, exist_ok=True)
for i, fn in enumerate((groan, whisper, scrape, heartbeat, wail), 1):
    x = norm(reverb(fn()))
    n = len(x); x[:int(.02 * SR)] *= np.linspace(0, 1, int(.02 * SR)); x[-int(.3 * SR):] *= np.linspace(1, 0, int(.3 * SR))
    wav = os.path.join(dest, '_t.wav')
    with wave.open(wav, 'wb') as w:
        w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes((x * 32767).astype('<i2').tobytes())
    subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', wav, '-c:a', 'libvorbis', '-q:a', '3', os.path.join(dest, 'strange_%d.ogg' % i)], check=True)
    os.remove(wav)
print(sorted(os.listdir(dest)))
