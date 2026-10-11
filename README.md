# Netherite Supremo (Forge 1.19.2 e 1.20.1)

Mod que deixa a netherite OP e remove o limite de armadura do jogo.

| Recurso | Valor |
|---|---|
| Armadura | capacete 14, peitoral 20, calças 17, botas 13 |
| Teto de armadura | removido (vanilla limita o total a 30; aqui o máximo vira 1024) |
| Espada / machado | 80 / 100 de dano total |
| Fire Aspect | nível II em espada e machado (qualquer atacante) |
| Inquebrável | armadura, espada e machado de netherite |
| Escudo | flechas quicam e não dão dano, com o som do escudo |
| Efeitos | Resistência ao Fogo e Força II |
| Mobs | tudo vale para jogador e mobs que usarem o equipamento |

Os efeitos e o escudo ativam com o conjunto completo (4 peças). Para ativar com qualquer
peça, mude `requireFullSet` para `false` no arquivo `config/netheritesupremo-common.toml`.

Com a Força II o dano sobe +6: espada 86 e machado 106 quando o conjunto está vestido.

## Compilar

### Opção 1: GitHub Actions (sem instalar nada)
1. Crie um repositório no GitHub e envie esta pasta inteira.
2. Abra a aba **Actions**, escolha **Build mods** e aguarde terminar.
3. Em cada execução, baixe os artefatos `NetheriteSupremo-forge-1.19.2` e
   `NetheriteSupremo-forge-1.20.1`. Dentro de cada um está o `.jar`.

### Opção 2: no seu computador
Precisa do JDK 17. Dentro de `forge-1.20.1` ou `forge-1.19.2`:

```
./gradlew build        (Windows: gradlew.bat build)
```

O `.jar` sai em `build/libs/`.

## Instalar
Coloque o `.jar` na pasta `mods` do Forge, no cliente e no servidor. Se você estava usando o
datapack anterior, remova-o: o mod faz o mesmo trabalho e mais.

## Versões usadas
- 1.20.1: Forge 47.1.0, ForgeGradle 6, Gradle 8.1.1
- 1.19.2: Forge 43.2.0, ForgeGradle 5, Gradle 7.6.4
