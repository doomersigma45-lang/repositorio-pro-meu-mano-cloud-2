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

## Mobs novos

### Golem de Netherite (neutro)
- **Invocação:** 4 blocos de netherite em T (dois braços, corpo e pé) e uma abóbora talhada ou
  jack-o'-lantern por cima, colocada por último, igual ao golem de ferro. Vale colocando a abóbora à mão.
- **Atributos:** 1800 de vida e 500 de dano (valores no topo de `NetheriteGolem.java`).
- **Neutro:** só ataca o jogador se for atacado. Protege aldeões e ataca monstros hostis.
- **Aura de fogo:** partículas de fogo e incendeia monstros hostis (e quem ele está atacando) num raio de 4 blocos.
- **Bolas de fogo:** lança salvas de 5 bolas de fogo (40 de dano cada) em alvos entre 6 e 24 blocos.
  As bolas de fogo não acendem blocos, então não queimam aldeias.
- **Cura golens de ferro feridos** que estiverem por perto.
- **Curar o próprio golem:** botão direito com barra de ouro (+150) ou de netherite (+450).
- **Drop:** 4 blocos de netherite.

### Cavaleiro de Netherite
- **Equipamento:** conjunto completo de netherite e espada de netherite. A armadura não aparece no corpo dele,
  só a skin (a espada na mão aparece).
- **Atributos:** 20 de vida e 1 de dano de mão, como um jogador. O dano real vem da espada.
- **Ataca** o jogador e qualquer criatura hostil (menos outros cavaleiros).
- **Spawn:** natural em fortalezas do Nether e bastiões.
- **Único:** existe no máximo um cavaleiro de netherite por mundo, que não some por distância.
  Quando ele morre, outro pode nascer de novo. Para nunca mais nascer, mude
  `knightRespawnsAfterDeath` para `false` no `config/netheritesupremo-common.toml`.
  Ovos de geração e `/summon` não têm essa trava.
- **Skin:** `assets/netheritesupremo/textures/entity/netherite_knight.png` (modelo clássico, braços de 4 pixels).

Os dois mobs têm ovo de geração na aba de ovos do modo criativo.

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
