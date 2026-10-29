# Netherite Supremo (Forge 1.19.2 e 1.20.1)

Mod que deixa a netherite OP e remove o limite de armadura do jogo.

| Recurso | Valor |
|---|---|
| Armadura | capacete 14, peitoral 20, calças 17, botas 13 |
| Teto de armadura | removido (vanilla limita o total a 30; aqui o máximo vira 1024) |
| Queda e explosão | dano zero com o conjunto |
| Qualquer outro dano | reduzido em 97% com o conjunto |
| Voo | voa como no criativo com o conjunto (duplo pulo para ligar), em velocidade ~3x a da aranha do nether montada (~58 blocos/s; correr no ar dobra). Valor em `NETHERITE_FLYING_SPEED` |
| Escudo | flechas quicam e não dão dano, com o som do escudo |
| Efeitos | Resistência ao Fogo e Força II |
| Espada / machado | 80 / 100 de dano total, Fire Aspect II |
| Espada (efeitos no alvo) | Fraqueza II, Wither II e Lentidão III por 10 segundos |
| Picareta | quebra qualquer bloco instantaneamente |
| Inquebrável | armadura, espada, machado, picareta e pá de netherite |
| Mobs | tudo vale também para mobs que usarem o equipamento (exceto o voo) |

Os efeitos da armadura (voo, escudo, imunidades, -97% e efeitos) ativam com o conjunto
completo (4 peças). Para ativar com qualquer peça, mude `requireFullSet` para `false` no
arquivo `config/netheritesupremo-common.toml`.

Com a Força II o dano sobe +6: espada 86 e machado 106 quando o conjunto está vestido.

Limite da picareta: blocos que o jogo marca como indestrutíveis (bedrock, barreira, etc.)
continuam impossíveis de quebrar no modo survival, porque isso é fixo no jogo.

Os valores e durações dos efeitos da espada ficam no topo de `SupremoEvents.java`.

## Mobs novos (somente Forge 1.20.1)

O Forge 1.19.2 continua na versão 1.1.0, sem os mobs.

### Golem de Netherite (neutro)
- **Invocação:** 4 blocos de netherite em T (dois braços, corpo e pé) e uma abóbora talhada ou
  jack-o'-lantern por cima, colocada por último, igual ao golem de ferro. Vale colocando a abóbora à mão.
- **Atributos:** 1800 de vida, 500 de dano e velocidade 0,45 (corre rápido). Valores no topo de `NetheriteGolem.java`.
- **Imunidades:** efeito e dano de Wither, projéteis (flechas, bolas de fogo, caveiras) e explosões.
- **Não ataca cavaleiros de netherite.**
- **Neutro:** só ataca o jogador se for atacado. Protege aldeões e ataca monstros hostis.
- **Aura de fogo:** partículas de fogo e incendeia monstros hostis (e quem ele está atacando) num raio de 4 blocos.
- **Bolas de fogo:** lança salvas de 5 bolas de fogo (120 de dano cada, mais Wither III por 30 s e Lentidão III por 45 s) em alvos entre 6 e 24 blocos.
  As bolas de fogo não acendem blocos, então não queimam aldeias.
- **Cura golens de ferro feridos** que estiverem por perto.
- **Curar o próprio golem:** botão direito com barra de ouro (+150) ou de netherite (+450).
- **Drop:** 4 blocos de netherite.

### Cavaleiro de Netherite
- **Equipamento:** conjunto completo de netherite e espada de netherite. A armadura não aparece no corpo dele,
  só a skin (a espada na mão aparece).
- **Atributos:** 35 de vida e 1 de dano de mão. O dano real vem da espada.
- **Neutro com jogadores:** só revida se for atacado. Ataca qualquer criatura hostil (menos outros cavaleiros).
- **Escala blocos** como uma aranha.
- **Spawn:** natural dentro da área de fortalezas do Nether e bastiões (modificador de bioma do Nether + checagem de estrutura no código). Dentro dos corredores da fortaleza o jogo usa a lista de mobs própria dela, então ele aparece mais no entorno e nos bastiões.
- **Grupos de dez:** nasce em grupo de 10 e os selvagens ficam perto uns dos outros. Se um deles for atacado (por mob ou jogador),
  o grupo inteiro por perto ataca o agressor. Existe no máximo um grupo selvagem por mundo; não somem por distância.
  Quando todos morrem, outro grupo pode nascer. Para nunca mais nascer, mude `knightRespawnsAfterDeath` para `false`
  em `config/netheritesupremo-common.toml`. Ovos de geração e `/summon` não têm essa trava.
- **Domesticar:** botão direito com **bloco de esmeralda ou de ferro** (gasta 1, sempre funciona). Domesticado, nunca ataca jogadores,
  segue o dono, defende o dono de mobs e deixa de contar como parte do grupo selvagem.
- **Rivalidade:** cavaleiros domesticados e selvagens atacam uns aos outros (mas nunca outro do mesmo lado).
- **Parado/seguindo:** dono com a mão vazia clica no cavaleiro domesticado para alternar entre **parado** (não anda nem ataca, como um lobo sentado) e seguindo.
- **Aranhas do nether:** cavaleiros nunca atacam aranhas do nether. Cerca de 1 em cada 4 cavaleiros selvagens já nasce montado
  numa aranha do nether selvagem. Com os dois domesticados, o dono **agachado + mão vazia** clica no cavaleiro (ele monta na aranha
  domesticada mais próxima, ou desmonta se já estiver montado) ou na aranha (o cavaleiro mais próximo monta nela).
  A aranha domesticada que carrega um cavaleiro segue o dono.
- **Skin:** `assets/netheritesupremo/textures/entity/netherite_knight.png` (modelo clássico, braços de 4 pixels).

### Necromante (boss)
- **Atributos:** 195 de vida e 55,5 de dano. Barra de boss roxa.
- **Voa e atravessa paredes**, usando o modelo de esqueleto com a textura `textures/entity/necromancer.png`.
- **Invoca esqueletos** com armadura de diamante encantada e arco ou espada de diamante encantados (somem após 90 s ou quando ele morre).
- **Cabeças de wither** como projétil (55,5 de dano + Wither II); não destroem blocos.
- **Spawn:** por enquanto só pelo ovo de geração ou `/summon netheritesupremo:necromancer`.

### Aranha do Nether
- **Modelo de aranha** com a textura `textures/entity/nether_spider.png`, 30% maior (hitbox e tamanho). Filhote tem metade do tamanho.
- **Atributos:** 65 de vida, dano de 6 a 12 (sorteado a cada ataque), velocidade 0,45 (cavalos chegam a 0,34).
- **Imune a fogo e explosões.** Escala paredes. Solta bolas de fogo (6 a 12 de dano, não acendem blocos) entre 5 e 18 blocos.
- **Hostil:** ataca jogadores, piglins e zoglins. Spawna **só na Floresta Carmesim**, em bandos de 3 ou 4 (modificador de bioma `add_nether_spider.json`, peso 10).
- **Montada:** solta bolas de fogo sozinha no mob que o dono atacou por último, ou no hostil/piglin/zoglin mais próximo à vista, e escala paredes com o dono em cima.
- **Domar:** botão direito com barra de ouro (1 chance em 3 por barra). Domada, não ataca mais jogadores (continua atacando piglins). Barra de ouro também cura 10 de vida.
- **Montaria:** o dono clica com a mão vazia para montar (agachar desmonta). Anda e escala paredes montada.
- **Reprodução:** o dono dá fungo carmesim ou distorcido a uma aranha domada adulta; ela procura outra aranha do nether apaixonada ou qualquer outra aranha (comum, das cavernas...) e gera um **filhote passivo** (já domado, cresce em 20 min). Intervalo de 5 min.
- **Drops:** linha e pepitas de ouro.

### Zumbi Dourado
- Modelo de zumbi com a textura `textures/entity/golden_zombie.png`. Mesma IA do zumbi comum (queima ao sol, chama reforços, infecta aldeões).
- **Atributos:** 27 de vida, 8 de dano e velocidade 0,30 (zumbi comum: 0,23). Não vira afogado na água.
- **Drop:** 1 a 2 blocos de ouro.
- **Spawn:** por enquanto só pelo ovo de geração ou `/summon netheritesupremo:golden_zombie`.

Os cinco mobs têm ovo de geração na aba de ovos do modo criativo.

## Proteção contra golpe fatal (jogadores)
Com a armadura de netherite completa, um único golpe que mataria o jogador de uma vez (dano final, já depois da armadura,
maior ou igual à vida atual **e** à metade da vida máxima) é **anulado**, com som de escudo e aviso na barra de ação.
Não vale para mobs, nem para vazio e `/kill`. Desligue com `lethalHitProtection = false` em `config/netheritesupremo-common.toml`.

## Encantamentos multiplicadores (somente Forge 1.20.1)
- **Multiplicador 2x / 4x / 8x / 16x** (`netheritesupremo:multiplier_2x`, `_4x`, `_8x`, `_16x`): multiplicam os atributos do item
  (armadura, resistência da armadura, dano de ataque, resistência a repulsão...) e a velocidade de mineração.
  Valem em armaduras, ferramentas e armas. A velocidade de ataque não é multiplicada (ficaria negativa).
- **Como obter:** o **Necromante** dropa um livro com o 2x. Na bigorna, **2x + 2x = 4x** e **4x + 4x = 8x** e **8x + 8x = 16x** (livro com livro,
  ou item encantado + livro; custa 10, 20 e 39 níveis). Não aparecem na mesa de encantamento nem em baús.
- Só um multiplicador por item. Para pegar pelo criativo: `/give @s enchanted_book{StoredEnchantments:[{id:"netheritesupremo:multiplier_8x",lvl:1s}]}`.
- Os tetos de armadura e de resistência da armadura foram removidos para os valores multiplicados valerem.

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

## Estruturas: Vila Umbral e Castelo Umbral (1.20.1)

Estruturas do datapack "Vila Umbral" agora geram naturalmente (vila + castelo juntos, lado a lado (castelo a 8 blocos da vila)):
- **Planícies** (`plains`, `sunflower_plains`): muito raras (spacing 100 / separation 50 chunks).
- **Nether** (qualquer bioma do Nether): raras (spacing 60 / separation 30 chunks).
- Teste manual: `/place structure netheritesupremo:umbral_plains` ou `netheritesupremo:umbral_nether`.

### Moradores da Vila Umbral e mercadores (1.20.1)
- A estrutura nasce com **3 golens de netherite**, **10 cavaleiros de netherite** e **2 Mercadores Umbral**.
- **Mercador Umbral**: mesma aparência do mercador viajante, não desaparece, fica perto de onde nasceu e vende as melhores trocas (livros e equipamentos de diamante totalmente encantados, maçã dourada encantada, totem, elytra, sinalizador...). Único item de netherite à venda: o **peitoral** (encantado). Ovo de geração na aba de ovos.
- **Mercador viajante melhorado**: preços das trocas normais pela metade, trocas extras boas e o **Mapa Umbral** (64 esmeraldas), que aponta para a Vila Umbral mais próxima (a busca é feita ao aparecer o mercador).
- Planícies agora usam spacing 64 / separation 32 (antes 100/50) para o mapa conseguir achar uma vila.

## Robô (1.20.1)
- 150 de vida, ataque 25 (golem de ferro: 15), imune a projéteis e a dano de queda, **voa** e dispara **lasers** (raio que atravessa e fere inimigos na linha, 12 de dano, alcance 28).
- Neutro como o golem de ferro: protege quem o construiu, ataca monstros hostis (menos creepers e cavaleiros de netherite) e revida se for atacado.
- **Invocação:** bloco de ferro no chão, sinalizador em cima, dois blocos de diamante nos lados do sinalizador (em T) e uma abóbora talhada por último na cabeça.
- Três skins (sorteadas ao nascer). Dropa ferro e diamante. **Só nasce pela construção** (sem ovo de geração e sem spawn natural).

## Netherite Bomb-Robot e Netherbot (1.20.1, hostis, Nether)
Ambos são **imunes a fogo, explosões e projéteis** (e a dano de queda).
- **Netherite Bomb-Robot**: 45 de vida, 15 de dano, muito rápido, voa (chama azul nas costas ao voar), solta projéteis explosivos e, **ao morrer, explode com força 30** (10x um creeper). Configurável em `bombRobotDestroysBlocks` (false = só dano, sem destruir blocos). Spawn natural no Nether.
- **Netherbot (mini boss)**: 666 de vida, 66 de dano, lasers de 45, voa (aura de fogo normal), invoca bomb-robots durante a luta e tem um **totem da imortalidade**: na primeira morte recupera toda a vida e fica mais forte (+50% dano, +20% velocidade, lasers 67) e mais resistente (armadura e Resistência II). Muito raro no Nether; dropa netherite, diamantes, estrela do Nether e totem.
- Ambos têm ovo de geração na aba de ovos.

## Netherite Robot Rainbow Boss e Joia Colorida (1.20.1)
- **Boss** (`netherite_robot_rainbow_boss`, ovo de geração na aba de ovos; sem spawn natural): 4500 de vida, 150 de dano, **imune a projéteis, fogo e explosões**, voa (aura de fogo), lasers de 150, bolas de fogo explosivas, **explosão de força 225 (75x um creeper)** carregada por 3 s a cada ~30 s, quebra blocos, **agarra quem ataca e desfere vários golpes** antes de arremessar, invoca **2 netherbots + 10 bomb-robots** (uma onda no início e outra depois do totem) e tem um **totem da imortalidade** (cura total + Força III + Resistência II).
- Drops: 2 estrelas do Nether, 4 totens, 7 maçãs douradas encantadas e a **Joia Colorida**.
- **Joia Colorida**: segurando dá Resistência 5 e Visão Noturna; botão direito solta uma explosão 75x creeper onde você mira (até 100 blocos, recarga 30 s). Com Resistência 5 o usuário fica imune à própria explosão.
- Config: `rainbowExplosionPower` (padrão 225) e `rainbowDestroysBlocks`. Explosões desse tamanho destroem uma área enorme e podem travar o jogo; diminua a força se precisar.

## Precursor (boss, 1.20.1)
- 20.000 de vida, 1.500 de dano, **voa, nada e atravessa paredes**, quebra qualquer bloco (até bedrock, sem drops), **imune a explosões e projéteis** (também a queda e sufocamento; fogo o fere).
- Ataques: salva de lasers (500 de dano cada, um no alvo, vários espalhados e um em cada outro jogador), 3 explosões fortes (força 10) sobre o alvo, **maldição dos guardiões** (Fraqueza II + Fadiga de Mineração III e o efeito do guardião ancião) e invocação de guardiões e guardiões anciões.
- **Cinco totens da imortalidade**: cada um recupera toda a vida (6 "vidas" no total).
- Dropa 4 estrelas do Nether, 5 totens e 16 lingotes de netherite. Só nasce por ovo de geração ou `/summon netheritesupremo:precursor`.
- Config: `precursorDestroysBlocks` (false = só dano, sem destruir blocos).

## Núcleo do Precursor (1.20.1)
- Só é obtido matando o **Precursor** (drop garantido). Item `netheritesupremo:precursor_core`.
- **Segurando** (qualquer mão): Força VI e Health Boost 40 (+160 de vida extra).
- **Tecla K** (configurável em Controles > Netherite Supremo) com o item na mão: liga/desliga **atravessar paredes** (sem colisão e voando; ao desligar dentro de um bloco você continua sem colisão até sair, para não sufocar).
- O boss Rainbow dropa a Joia Colorida (já estava no loot; agora coberto por teste).

## Lula Brilhante Gigante (boss, 1.20.1)
- Lula brilhante enorme (modelo e textura da lula brilhante do jogo, ~4,5x), 700 de vida. Lança **tinta brilhante** (projétil de 10,7 de dano, Brilho + Cegueira rápida) e, a cada 6 tiros, uma rajada de 5. De perto bate com os tentáculos.
- **Spawn:** só em oceano fundo e cavernas subaquáticas grandes (bolsão de água ~7x7x7, bem abaixo do nível do mar), bem raro. **Só existe um por vez** (não nasce outro enquanto houver um carregado na dimensão). Também por ovo de geração ou `/summon netheritesupremo:giant_glow_squid`.
- **Drops:** 8–16 blocos de diamante, 64–128 tinta brilhante e o **Ovo de Lula Brilhante Gigante**.
- **Ovo:** coloque dentro d'água; amadurece (3 etapas) e choca um **filhote domesticado** do jogador mais próximo (até 32 blocos). Fora da água não amadurece.
- **Filhote:** triplo das estatísticas do boss (2100 de vida, 32,1 de dano de tinta, 60 de dano corpo a corpo), segue o dono, ataca monstros e é **montaria** (clique direito; W/S para nadar para onde você olha; Shift desce). Filhote sem dono pode ser domesticado com tinta brilhante.

- **Robô:** além da construção (bloco de ferro + sinalizador + diamante + abóbora), voltou o ovo de geração (aba de ovos).
