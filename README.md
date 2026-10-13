# Netherite Supremo (Forge 1.19.2 e 1.20.1)

Mod que deixa a netherite OP e remove o limite de armadura do jogo.

| Recurso | Valor |
|---|---|
| Armadura | capacete 14, peitoral 20, calças 17, botas 13 |
| Teto de armadura | removido (vanilla limita o total a 30; aqui o máximo vira 1024) |
| Queda e explosão | dano zero com o conjunto |
| Qualquer outro dano | reduzido em 90% com o conjunto |
| Voo | voa como no criativo com o conjunto (duplo pulo para ligar) |
| Escudo | flechas quicam e não dão dano, com o som do escudo |
| Efeitos | Resistência ao Fogo e Força II |
| Espada / machado | 80 / 100 de dano total, Fire Aspect II |
| Espada (efeitos no alvo) | Fraqueza II, Wither II e Lentidão III por 10 segundos |
| Picareta | quebra qualquer bloco instantaneamente |
| Inquebrável | armadura, espada, machado, picareta e pá de netherite |
| Mobs | tudo vale também para mobs que usarem o equipamento (exceto o voo) |

Os efeitos da armadura (voo, escudo, imunidades, -90% e efeitos) ativam com o conjunto
completo (4 peças). Para ativar com qualquer peça, mude `requireFullSet` para `false` no
arquivo `config/netheritesupremo-common.toml`.

Com a Força II o dano sobe +6: espada 86 e machado 106 quando o conjunto está vestido.

Limite da picareta: blocos que o jogo marca como indestrutíveis (bedrock, barreira, etc.)
continuam impossíveis de quebrar no modo survival, porque isso é fixo no jogo.

Os valores e durações dos efeitos da espada ficam no topo de `SupremoEvents.java`.

## Compilar

### Opção 1: GitHub Actions (sem instalar nada)
1. A cada push na branch `main`, o workflow **Build mods** compila as duas versões.
2. Quando termina, ele publica uma **Release** (aba Releases) com os dois `.jar`:
   `netherite-supremo-forge-1.19.2-<versão>.jar` e `netherite-supremo-forge-1.20.1-<versão>.jar`.

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
