#!/usr/bin/env python3
"""Gera a musica da Terra Negra: uma melodia sombria sintetizada e depois TOCADA DE TRAS PARA FRENTE.
Uso: python3 tools/gerar_musica.py  (escreve assets/netheritesupremo/sounds/music/black_realm.ogg; precisa de numpy e ffmpeg)"""
import os, subprocess, wave
import numpy as np

SR = 32000
rng = np.random.default_rng(666)

def note(freq, dur, vel=1.0):
    t = np.arange(int(SR * dur)) / SR
    env = np.exp(-t * 1.6) * (1 - np.exp(-t * 60))
    s = (np.sin(2 * np.pi * freq * t) + 0.5 * np.sin(2 * np.pi * freq * 2 * t) * np.exp(-t * 2.5)
         + 0.25 * np.sin(2 * np.pi * freq * 3.01 * t) * np.exp(-t * 4) + 0.12 * np.sin(2 * np.pi * freq * 5.02 * t) * np.exp(-t * 6))
    return s * env * vel

def pad(freq, dur, vel=0.5):
    t = np.arange(int(SR * dur)) / SR
    att = np.minimum(1, t / 2.0) * np.minimum(1, (dur - t) / 2.0)
    s = sum(np.sin(2 * np.pi * freq * (1 + d) * t) for d in (-0.003, 0, 0.004)) / 3
    return s * att * vel

def hz(n):  # n = semitons a partir de A4
    return 440.0 * 2 ** (n / 12)

total = 96.0
out = np.zeros(int(SR * total))
def put(sig, at):
    i = int(at * SR); j = min(len(out), i + len(sig))
    if i < len(out): out[i:j] += sig[:j - i]

# base: A frigio (A Bb C D E F G); melodia lenta e esparsa
scale = [-12, -11, -9, -7, -5, -4, -2, 0, 1, 3, 5, 7]
t = 0.0
step = 0
while t < total - 6:
    n = scale[int(abs(np.sin(step * 0.9) * 5 + np.cos(step * 0.37) * 4)) % len(scale)]
    d = [1.5, 2.0, 3.0, 1.0][step % 4]
    put(note(hz(n), 6.0, 0.55), t)
    if step % 3 == 0:
        put(note(hz(n - 12), 6.0, 0.4), t + 0.02)
    t += d + (0.5 if step % 5 == 0 else 0.0)
    step += 1
for k, base in enumerate((-24, -29, -26, -31)):   # pad grave (acordes lentos)
    put(pad(hz(base), 26.0, 0.35), k * 22.0)
    put(pad(hz(base + 7), 26.0, 0.18), k * 22.0)

# reverb simples (ruido com decaimento)
ir_t = np.arange(int(SR * 2.2)) / SR
ir = rng.standard_normal(len(ir_t)) * np.exp(-ir_t * 2.2); ir[0] = 1.0
wet = np.fft.irfft(np.fft.rfft(out, len(out) + len(ir)) * np.fft.rfft(ir, len(out) + len(ir)))[:len(out)]
mix = out * 0.6 + wet * 0.08
mix = mix / np.max(np.abs(mix)) * 0.8

mix = mix[::-1].copy()                     # <<< invertida
fade = int(SR * 3)
mix[:fade] *= np.linspace(0, 1, fade); mix[-fade:] *= np.linspace(1, 0, fade)

pcm = (mix * 32767).astype('<i2')
here = os.path.dirname(os.path.abspath(__file__))
dest = os.path.join(here, '..', 'forge-1.20.1', 'src', 'main', 'resources', 'assets', 'netheritesupremo', 'sounds', 'music')
os.makedirs(dest, exist_ok=True)
wav = os.path.join(dest, '_tmp.wav')
with wave.open(wav, 'wb') as w:
    w.setnchannels(1); w.setsampwidth(2); w.setframerate(SR); w.writeframes(pcm.tobytes())
subprocess.run(['ffmpeg', '-y', '-loglevel', 'error', '-i', wav, '-c:a', 'libvorbis', '-q:a', '2', os.path.join(dest, 'black_realm.ogg')], check=True)
os.remove(wav)
print('ok', os.path.getsize(os.path.join(dest, 'black_realm.ogg')), 'bytes')
