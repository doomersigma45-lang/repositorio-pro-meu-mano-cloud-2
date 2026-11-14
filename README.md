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
| Efeitos | Resistência ao Fogo, Força II e Resistência IV |
| Espada / machado | 100 / 120 de dano total, Fire Aspect II |
| Espada (efeitos no alvo) | Fraqueza II, Wither II e Lentidão III por 10 segundos |
| Picareta | quebra qualquer bloco instantaneamente |
| Inquebrável | armadura, espada, machado, picareta e pá de netherite |
| Mobs | tudo vale também para mobs que usarem o equipamento (exceto o voo) |

Os efeitos da armadura (voo, escudo, imunidades, -97% e efeitos) ativam com o conjunto
completo (4 peças). Para ativar com qualquer peça, mude `requireFullSet` para `false` no
arquivo `config/netheritesupremo-common.toml`.

Com a Força II o dano sobe +6: espada 106 e machado 126 quando o conjunto está vestido.

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

## Instalar (para amigos)
- Use **somente** o jar `netherite-supremo-forge-1.20.1-<versão>.jar` (o do 1.19.2 está congelado e quase vazio).
- Precisa de **Minecraft 1.20.1**, **Forge 47.1.0 ou mais novo** e **Java 17**. Coloque o jar na pasta `mods`.
- Se o jogo fechar ou der erro, envie `logs/latest.log` (ou o arquivo em `crash-reports`) para o autor.

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

## Katana, Shuriken e Kunai (1.20.1)
- **Katana**: 17 de dano, velocidade de ataque 3,0, durabilidade 2500 (diamante: 1561), **dobro de dano em bosses** (tag `forge:bosses`, que inclui os bosses do mod, Warden e Ancião Guardião).
- **Shuriken**: 6,5 de dano na mão; botão direito **arremessa** (consome uma; dano de 1x a 3x, ou seja 6,5 a 19,5). Se cair num bloco, 60% de chance de poder ser recolhida. Empilha até 16.
- **Kunai**: 8 de dano, ataque rápido; na mão principal dá **velocidade 1,75x** e, com ele em qualquer mão, **pulo duplo** (aperte espaço de novo no ar).
- Receitas: katana = 2 lingotes de netherite empilhados sobre um graveto, shuriken = ferro em cruz com fragmento de netherite no centro (4), kunai = lingote de netherite + graveto. Todos na aba Combate do criativo.

## Ninja (1.20.1)
- Mob **neutro** que spawna em **Cherry Grove** (bandos de 1 a 3). 50 de vida, **7 ou 12 de dano** (sorteado por ninja, fixo mesmo segurando a arma) e **armadura de ferro invisível** com Proteção III, Inquebrável III (e Peso Pena IV nas botas). Cada ninja nasce com **katana**, **kunai** (fica mais rápido, como o item) ou **shurikens** (arremessa de 4 a 16 blocos; cada shuriken dá o mesmo dano do ninja).
- Só ataca se for atacado; ao ser ferido, chama os outros ninjas por perto.
- Drops: 1–3 shurikens, 1–4 pepitas de ferro, 6% de chance de kunai e 4% de katana. Ovo de geração na aba de ovos.

## Samurai (boss, 1.20.1)
- Spawna em **Cherry Grove**, **um só por vez** (não nasce outro enquanto houver um carregado na dimensão). 250 de vida, **15 de dano que aumenta com a vida perdida (até 45 quase morto)**, katana, **armadura de diamante completa invisível**.
- **Chama ninjas** (2, ou 3 abaixo de metade da vida; até 6 por perto) e arremessa **salvas de 5 shurikens** (10 de dano cada) de 5 a 20 blocos.
- Drops: katana, kunai, 16–32 shurikens e 6–12 diamantes. Ovo de geração na aba de ovos; barra de boss.

## Ladrão (1.20.1)
- Mob hostil que **só aparece à noite**, ao ar livre no Overworld, **em bandos de 3 a 5**; de dia, sem ter roubado nada, ele some. 15 de vida, velocidade 0,32 (mais rápido que o jogador), **espada de madeira ou de pedra**; 3 skins (sorteada por ladrão). Ataca jogadores e aldeões e chama os outros ladrões quando é ferido.
- **Rouba:** ao acertar, 50% de chance de roubar **metade de uma pilha** do inventário de um jogador (ou do inventário de aldeões, ou o equipamento de outro mob, como a arma de um esqueleto) e foge por ~10 s. **O que ele roubou cai quando ele morre.**
- Drops: esmeraldas, lingotes ou pepitas de ouro e, raramente (4%), 1 diamante. Ovo de geração na aba de ovos.

## Aldeão Precursor (1.20.1)
- Variante **neutra e mais fraca do Precursor**, com 3 skins sorteadas. 750 de vida, 25 de dano, **vive na água (nada em 3D) e na terra**. Nasce em rios (grupos de 2 a 4) e pelo ovo de geração.
- **Defesa:** ao ver uma ameaça (monstro hostil) ou ser atacado, **chama 2 robôs** (até 6 por perto, a cada 15 s) que atacam o alvo; se for atacado, **todos da espécie num raio de 48 blocos** também vão atrás do agressor.
- **Trocas** (como um aldeão; reabastecem a cada ~5 min): a moeda é **ferro e diamante**. Ferro: comida, maçã dourada, flechas, **shurikens**, **kunai**, escudo, peitoral e espada de ferro encantados, pérolas. Diamante: garrafas de XP, livros (Inquebrável III, Remendo), espada, picareta e peitoral de diamante encantados, **katana**, totem, élitro e **ovo de geração do Robô**.
- Drops: 4–10 esmeraldas, 1–3 diamantes e 4–12 ferros.

## Obsidiana (1.20.1)
- **Barra de obsidiana** (4 obsidianas em quadrado) e, com ela, **espada, machado, picareta, pá, enxada, capacete, peitoral, calças e botas** (receitas iguais às vanilla).
- Ferramentas: durabilidade 2400 (diamante 1561), nível de diamante. **Espada 32 de dano** e **machado 34** (mais lento); picareta 10, pá 9,5, enxada 6.
- Armadura: 46 de armadura no conjunto (9/16/13/8, melhor peça a peça que a Armadura de Fusão: 5/14/11/5) + 5 de resistência da armadura + resistência a repulsão; durabilidade 660/960/900/780 (a da fusão: 528/768/720/624), encantabilidade 20. **Conjunto completo = imunidade a explosões.** Mais forte que o diamante, mais fraca que o netherite do mod.
- Texturas dos itens: as que você enviou. A folha de armadura veio em layout de skin 64x64; as peças foram remapeadas para as duas camadas de armadura do jogo (capacete, peitoral, braços e botas na camada 1; calças e cinto na camada 2).

## Armadura Ninja (1.20.1)
- Capuz, roupa, calças e botas ninja. **Receitas:** couro + pepitas e lingotes de ferro (capuz: LLL/LNL; roupa: LNL/LIL/LLL; calças: LIL/N N/L L; botas: L L/N N).
- Defesa um pouco acima do couro (2/4/3/2 = 11, couro = 7) e durabilidade 12x (couro = 5x).
- **Conjunto completo:** Velocidade III e Pulo IV, e **furtividade**: os mobs quase nunca escolhem você como alvo (só se estiver a menos de 3 blocos ou se você os atacou).
- **Ninjas** largam uma peça aleatória da armadura com 50% de chance. Texturas da armadura: as que você enviou (camada 1 da folha para capuz/roupa/botas, camada 2 montada a partir dela para as calças).

## Armadura de Fusão (ferro + diamante, 1.20.1)
- **Como fazer:** na bancada, coloque **uma peça de ferro e a peça de diamante do mesmo tipo** (ex.: capacete de ferro + capacete de diamante) -> peça de fusão.
- **Atributos = soma das duas:** armadura 5/14/11/5 (= 35; ferro 15 + diamante 20), resistência 2 (a do diamante), durabilidade ferro + diamante (528/768/720/624), encantabilidade 19.
- **Textura:** em cada face, **metade ferro e metade diamante**; as texturas são geradas na hora a partir das armaduras de ferro e de diamante do jogo (acompanha resource packs), e o ícone usa as duas metades dos ícones vanilla.

## Nova receita do lingote de netherite (1.20.1)
- A receita padrão (4 fragmentos + 4 barras de ouro) foi **substituída**: agora é **4 fragmentos de netherite + 3 barras de obsidiana + 1 estrela do Nether** (sem formato fixo; o da imagem funciona). O resto (bloco <-> lingote, melhoria na bancada de ferraria) não muda.

## Árvore Cristalina (1.20.1)
- Novo tipo de árvore com as suas texturas (tronco e tábuas). **Ainda não nasce naturalmente** (não há bioma/spawn configurado), só plantando a **muda** (cresce a feature `netheritesupremo:crystal_tree`) ou com `/place feature netheritesupremo:crystal_tree`.
- Blocos: tronco, madeira, tronco e madeira descascados (machado), tábuas, escada, laje, cerca, portão, botão, placa de pressão, folhas e muda. Receitas das tábuas/escadas etc. **não foram adicionadas** (só pegar no criativo/dropando da árvore); porta, alçapão, placa e barco ficaram de fora por falta de textura.
- Texturas das folhas, da muda e do tronco descascado foram geradas por mim nas cores da árvore.

## Rubi e Olho do Dragão (1.20.1)
- **Rubi** (dropado só pelo Samurai, garantido): botão direito solta um leque de 9 projéteis de rubi (12 de dano cada, recarga de 1 s, não gasta o item).
- **Olho do Dragão** (dropado só pelo Ender Dragon, cai perto do portal ao morrer): segurando dá voo rápido (~26 blocos/s, mais que cavalo), +15 corações e dobra a força (dano de ataque x2). O voo é o voo de criativo acelerado (duplo pulo), não a pose de elytra.
- **Olho do Dragão, portais:** botão direito abre um portal (2x3, dura 30 s) para a dimensão escolhida; agachado + botão direito troca o destino, percorrendo todas as dimensões carregadas no servidor (inclusive de outros mods). No destino aparece um portal de volta. Fora da dimensão do mod só funciona o que o servidor tiver carregado.

## Manopla Suprema (1.20.1)
- Receita sem formato: Olho do Dragão + Rubi + Núcleo do Precursor + Joia Colorida.
- 5000 de dano. Segurando junta os poderes x10: Núcleo (+18 dano, +160 vida) e Olho (+30 vida, força x20, voo 10x mais rápido), Joia (Resistência V, visão noturna, imune a explosões), tecla K atravessa paredes. Botão direito: rajada de 90 projéteis de rubi (120 de dano cada). Agachado: explosão da Joia Colorida onde olha (recarga 30 s). Agachado olhando para baixo: liga/desliga projéteis explosivos (poder 4 cada). Correndo + botão direito: abre portal; agachado olhando para cima: troca a dimensão.
- Cada mob/jogador morto com ela na mão conta um abate; com 100 (`GauntletItem.INFINITE_KILLS`) o dano vira infinito.
- A armadura de netherite completa (a do bônus de conjunto) sofre menos com o dano infinito da Manopla: quem usa ainda toma dano, mas reduzido: 25% da vida máxima por golpe infinito.

## Portais pela tecla R e compatibilidade com Avaritia (1.20.1)
- Com o Olho do Dragão ou a Manopla Suprema na mão: **R** abre o portal; **segurar R e girar o scroll** troca a dimensão (o scroll não troca o item da hotbar enquanto R está apertada). O botão direito não abre mais portais.
- **Avaritia / Re-Avaritia** (detectados pelo nome registrado `avaritia`, sem dependência): a armadura de netherite completa anula o dano das espadas do mod (inclusive o tipo de dano `avaritia:infinity`); a espada de netherite e a Manopla Suprema ferem quem veste armadura do mod, passando a imunidade e a redução dela. A armadura do mod não tem durabilidade, então ela em si não "quebra".

## Plutão com oceano, árvores de cristal e spawns (1.20.1)
- O mod **Sistema Solar** (repositório `mod-de-planetas`) agora tem Plutão com crosta de gelo e **oceanos por baixo**. Árvores de cristal e os spawns de **Aldeão Precursor** e **Robô** ficam neste mod, ligados aos biomas de Plutão por tags opcionais (`netheritesupremo:pluto_biomes`): sem o Sistema Solar instalado, não fazem nada.
- O Robô passou a ser criatura (antes era categoria "misc") para poder nascer em biomas; os Robôs construídos por jogadores continuam iguais.
- O boss de Plutão ainda não existe (futuro).

## Datapack do Sistema Solar e Satélite (1.20.1)
- Os dados do mod **Sistema Solar** (dimensões dos 14 corpos, inclusive Plutão com oceano sob o gelo) agora vêm **dentro deste mod** (`data/sistemasolar`), então não precisa instalar o outro mod/datapack para as dimensões existirem. Se instalar os dois juntos, o conteúdo é o mesmo.
- **Satélite:** estrutura rara que aparece no **oceano** (sobre a água, com pernas de ferro) e nas **ilhas externas do End**. Dentro há um baú com loot e um **portal para Plutão** (bloco `satellite_portal`). Ao entrar, uma plataforma com portal de volta é criada em Plutão; o portal de lá leva de volta ao lugar de onde a pessoa veio (ou ao Overworld).

## Alienígenas aquáticos e animais do Cambriano (1.20.1)
- **Alienígena Aquático** (40 vida, 6 de dano): humanoide que nada e ataca como um afogado, sem tridente; dropa cristais/lascas de prismarinho e raramente um Coração do Mar.
- **Anomalocaris** (160 vida, 16 de dano desde a 1.59.0): predador; nada muito rápido em oceanos profundos, caça peixes, lulas e axolotes, evita jogadores mas ataca se provocado. **Trilobita** (6 vida) e **Opabínia** (14 vida): pacíficos, fogem do jogador.
- Todos nascem **só nos oceanos de Plutão** (dentro d'água, qualquer luz). Os modelos dos animais são feitos em código (corpo segmentado) e as texturas foram geradas por mim, então dá para trocar por texturas suas. Cada um tem ovo de geração.

## Cidades futuristas de Plutão (1.20.1)
- Estrutura **só em Plutão** (biomas Coração de Tombaugh e Terra Escura de Cthulhu), gerada pelo mod em `netheritesupremo:pluto_city`: praça com torre central de vidro e antena, 4 arranha-céus nos cantos com escadas, 8 prédios baixos, ruas com postes de luz, monotrilho elevado em anel e baús com loot (`chests/pluto_city`).
- Moradores fixos: 4 Aldeões Precursores e 2 Robôs em todas as cidades.
- **Boss Precursor:** em cerca de 1 a cada 3 cidades ele já nasce dentro do salão da torre central (não some sozinho).
- As estruturas são geradas por `tools/gerar_cidade.py` (sem texturas novas, só blocos do jogo).

## Spectrite (porte do mod 1.12.2 para 1.20.1)
- O mod original (Spectrite 1.7.1, de Samuel Harbord) é do Minecraft 1.12.2 e não roda no 1.20.1, então portei o essencial **para dentro do nosso mod**: **Gema**, **Minério** (Overworld, y -64 a 16, um pouco mais raro que o diamante; 5 tentativas de veia de até 5 blocos por chunk, metade descartada se exposta ao ar; brilha), **Bloco**, **Armadura** (4 peças) e **Ferramentas** em duas linhas como no original: *Diamond Spectrite* (cabo de graveto; 2400 de durabilidade, eficiência 10, dano +6) e *Spectrite* "special" (cabo de Bastão de Spectrite, o mais forte; 3600 de durabilidade, eficiência 12, dano +10), com as texturas animadas do mod original (a armadura vestida usa um quadro fixo, o jogo não anima armadura).
- **Armadura de Spectrite:** levemente melhor que a de Fusão: defesa 6/15/12/6 (Fusão 5/14/11/5), dureza 3 (Fusão 2), durabilidade x50 (Fusão x48), encantabilidade 22 (Fusão 19). Craft: gemas no formato de armadura normal.
- Não portados (por enquanto): chefes, estruturas, baús, bigorna, fogo/fluido de Spectrite, arcos, flechas e demais itens do mod original.

### Efeitos do Spectrite (1.42.0)
- **Armadura:** cada peça aumenta a vida máxima, como no mod original (capacete +4, peitoral +8, calça +6, botas +2 = **+20 de vida, 10 corações**). Com o **set completo** e a fome não zerada: Velocidade I, Força II, Resistência II e Resistência ao Fogo (renovados sozinhos, sem partículas).
- **Espadas de Spectrite** (as duas): ao acertar, soltam uma **explosão colorida** (esfera de partículas em ciclo do arco-íris + fagulhas) que fere os outros seres em volta sem destruir blocos nem ferir você: 12 de dano em 5 blocos na espada Spectrite (special) e 8 em 4 blocos na de Diamante-Spectrite.
- No mod original esses efeitos são ligados a uma poção própria de Resistência de Spectrite e a orbes; não portei as orbes, então usei efeitos do próprio jogo.

## Laboratório de Robôs (1.20.1)
- Estrutura grande (47x47) que aparece no **Overworld** (florestas, planícies, montanhas, desertos, etc.; não em oceano) e no **Nether**: um pavilhão na superfície com escada para **4 andares subterrâneos** de dungeon, gerada por `tools/gerar_laboratorio.py`.
- **Andar 1 e 2 (fáceis):** só Robôs Bomba (8 e 12). **Andar 3 (médio):** 12 Netherbots. **Andar 4 (difícil):** o **Rainbow Bot** (chefe) no salão central + 6 Netherbots. Cada andar tem 4 salas, um salão em cruz e escadas em cantos opostos, então é preciso atravessar o andar.
- Baús com loot por dificuldade (`chests/robot_lab_easy`, `_medium`, `_hard`). No Nether a entrada (pavilhão) pode estar dentro de rocha: é preciso cavar ou achar uma caverna.

## Invasão alienígena (1.20.1)
- **Gatilhos** (chance de começar uma invasão): matar um **Guardião ou Guardião Ancião 10%**, **entrar em Plutão 30%**, matar um **Aldeão Precursor 60%**, matar um **boss Precursor 100%**. Ao sortear, o jogador recebe um aviso e a invasão começa em 30 segundos (se ele estiver no Overworld; senão espera ele voltar).
- **A invasão** tem 6 ondas de **robôs hostis reforçados** (Robôs Bomba e Netherbots com o dobro de vida e +50% de dano), **Soldados Precursores** (infantaria que atira laser) e **Naves Espaciais** (até 3, orbitam o alvo e disparam lasers explosivos que destroem casas, respeitando `mobGriefing`). Ao fim das ondas surge o **boss Precursor**.
- **Alienígenas vencem** se matarem **todos os aldeões da vila** (os que estavam num raio de 80 blocos quando a invasão começou) **e** matarem o jogador **3 vezes** durante a invasão. Aí passam a surgir **Aldeões Precursores nas vilas** e **cidades futuristas** (até 5, uma por vez, a 180-280 blocos do jogador).
- **Se perderem** (boss morto ou tempo esgotado): os invasores somem e deixam um **baú de oferta de paz** (netherite, maçãs douradas encantadas, totens, estrelas do Nether, armadura/espada/picareta de netherite encantadas, etc.) perto da casa do jogador (cama, ou onde a invasão começou).
- Soldados e naves têm ovo de geração. O boss da invasão não dispara um novo sorteio enquanto ela estiver ativa. Desligue tudo com `invasionEnabled=false` na config.
- Comandos de operador: `/invasion start|stop|win|lose|reset|status|city x z|peace x y z|test x z`.

### Aliados alienígenas, soldados e híbridos (1.45.0)
- **Aliados:** Robôs (todas as variantes), Robôs Bomba, Netherbots, Rainbow Bot, Precursor, Soldados Precursores, Naves e Aldeões Precursores **não se atacam** nem se ferem (lasers, explosões e golpes), e nenhum escolhe o outro como alvo.
- **Aldeão Precursor** ficou mais fraco que o Soldado: 100 de vida, 10 de dano, armadura 4 (Soldado: 150 de vida, 22 de dano, armadura 12).
- **Soldados Precursores** atacam **primeiro os bosses** (tag `forge:bosses`, exceto aliados), depois **saqueadores (pillagers, vindicators, evokers, ravagers) e piglins**, depois jogadores e aldeões normais.
- **Aldeão Híbrido:** um Aldeão Precursor e um aldeão normal adultos lado a lado (5 blocos) têm 50% de chance a cada 5 s de gerar um filhote híbrido (recarga de 5 min). O híbrido tem 50 de vida e 5 de dano, as mesmas trocas e textura própria (gerada por mim).
- **Depois que os alienígenas vencem:** perto de cada jogador a população de Precursores (híbridos incluídos) é mantida em cerca do **dobro** da de aldeões normais (+2, mínimo 4, máximo 40).

### Mods incorporados (1.46.x)
Os mods **Claude Content** e **Vila de Ferro** agora fazem parte deste mod (mesmo jar, namespace `netheritesupremo`; os IDs mudaram, ex.: `/summon netheritesupremo:mossling`).
- **Claude Content:** Ceifador de Cinzas (boss, invocado com o Selo de Cinzas), Musgolim, Lumiléu, Colosso de Musgo (guardião do Altar das Raízes), Espreitador de Brasa, armadura de Brasa (bônus de conjunto), Núcleo Enraizado, Broto Antigo, Altar das Raízes, Santuário das Raízes (estrutura em florestas) e uma aba própria no criativo.
- **Vila de Ferro:** Villager Guerreiro (neutro, espada ou arco, nada 4x mais rápido), Chefe Villager (invoca guerreiros e golems, totem, equipamento de diamante) e o Filhote do Chefe (pet leal ao jogador, cresce com o tempo). Vilas ganham um Chefe e guerreiros quando o jogador chega perto; ao derrotar o Chefe sai a conquista "Fim do Chefe da Vila".

### Invocador do Necromante e Rei Esqueleto (1.48.0)
- **Invocador do Necromante:** clique em um bloco para invocar o Necromante (consome o item). Receita (3x3): `carvão | estrela do Nether | carvão` / `obsidiana | Selo de Cinzas | obsidiana` / `osso | Núcleo Enraizado | osso`.
- **Rei Esqueleto (boss, só por ovo de geração):** esqueleto 60% maior que um jogador, de coroa dourada, **7500 de vida e 100 de dano**, imune a fogo e a Wither. Invoca as três variantes (esqueleto, stray e wither) com **armadura e espada de Spectrite** (até 9 vivos, somem em 2 min, sem drops) e arremessa **bolas de fogo** e **cabeças de wither** (50% do dano dele, sem quebrar blocos).
- **Fase 2 (metade da vida):** invoca **dois Necromantes** (sem drops), causa **3x mais dano** (300) e recebe **1/3 do dano**. A barra de boss fica vermelha.
- Drops: 2–3 estrelas do Nether, 6–12 gemas de Spectrite, 3–6 blocos de diamante, 2–4 maçãs douradas encantadas, 1–2 totens e blocos de osso. Está na tag `forge:bosses`.

### Steve Vermelho e a Terra Negra (1.49.0)
- **Steve Vermelho:** modelo de jogador com a skin que você enviou. **20 de vida e 550 de dano**, **neutro** (só revida se atacado). É **muito raro** de achar: tem o menor peso de spawn possível no Overworld, só 1 em 4 das tentativas passa e nasce sozinho. Também tem ovo de geração.
- **Quem o matar** (jogador ou mob) é **teletransportado para a Terra Negra** (`netheritesupremo:black_realm`), nas mesmas coordenadas x/z (procura um ponto seguro perto).
- **Terra Negra:** sempre noite, céu preto, terra preta (`Terra Preta`), subsolo de blackstone, **árvores sem folhas** bem separadas (troncos de carvalho-escuro bifurcados, ~1 a cada 2 chunks), **rios e lagos de Líquido Negro** e **ruínas abandonadas e quebradas** (casa, torre e salão em ruínas, com baús de pouca coisa). **Não há mobs** na dimensão (nada nasce lá; só quem foi levado para dentro). **Sons estranhos** aleatórios (gemidos graves, sussurros invertidos, arranhões metálicos, batidas de coração, lamentos distantes, feitos por mim em `tools/gerar_sons_estranhos.py`) somados ao zumbido do Soul Sand Valley e aos ruídos de caverna; sem luz ambiente.
- **Líquido Negro:** causa 8 de dano a cada meio segundo, Lentidão IV, **puxa para o fundo** (não dá para nadar para cima) e afoga. Não é água: não apaga fogo, não serve para barcos.
- **Música invertida:** faixa sombria **sintetizada por mim e tocada de trás para frente** (`tools/gerar_musica.py`), que toca na dimensão.
- **Sair:** morrendo você volta ao seu ponto de renascimento. Operadores têm `/blackrealm enter` e `/blackrealm leave`.

### Old Gun, esqueletos neutros, Esqueleto Villager, vilas e guerra (1.50.0)
- **Esqueletos neutros:** esqueleto, stray, wither esqueleto, Esqueleto Villager e o Rei só reagem se alguém atacar um deles; aí os esqueletos num raio de 40 blocos (e o Rei) o defendem. Usam a **Old Gun** no lugar do arco: atiram de longe e, de perto, atacam corpo a corpo. Esqueletos invocados pelo Rei ou pelo Necromante continuam hostis, e esqueletos não se ferem entre si.
- **Old Gun:** projétil de **50 a 150 de dano** (aleatório), recarga de **5 segundos**, 250 usos, conserto com barra de ferro. Receita: 3 barras de ferro em cima, pólvora + tábua embaixo à esquerda, tábua no meio (`III / GW. / .W.`). Textura enviada por você.
- **Esqueleto Villager:** esqueleto de terno (textura enviada por você). Pacífico, 30 de vida, revida com a Old Gun. Troca por **pepitas de ferro ou de ouro**, trocas boas (ossos, flechas, pólvora, comida, XP, maçã dourada, pérola, diamante) e vende a **Old Gun por 4 barras de ferro**. Reabastece as trocas a cada ~5 min.
- **Vila de esqueletos:** 9 casas simples e modernas (concreto branco, vidro, telhado liso), **9 esqueletos villagers** (um por casa), **estradas de asfalto** (concreto preto com faixa amarela e calçadas) e uma praça do trono com o **Rei Esqueleto**, calmo enquanto ninguém o atacar. Surge em planícies, savana, deserto, floresta, taiga e tundra (raro: espaçamento de 44 chunks).
- **Guerra:** se um jogador matar o Rei, os esqueletos declaram guerra a até 2 vilas e 1 outpost de pilhagem próximos. Chegam em 4 ondas (esqueleto, stray, wither) com armadura de ferro e a Old Gun. **Só vencem se matarem todos os moradores** (aldeões, golems de ferro, saqueadores); se perderem todos, a guerra acaba e eles somem. Se vencem, o lugar fica **conquistado** e esqueletos e esqueletos villagers passam a surgir lá. Operadores: `/skeletonwar declare|status|stop|reset`.
- Gerador da vila: `tools/gerar_vila_esqueletos.py`.

### Esqueletos mais fortes, guerra montada e Old Gun corpo a corpo (1.51.0)
- **Esqueletos normais:** 45 de vida, armadura de ferro completa (inquebrável, então o sol não os queima nem quebra o capacete) e **vivem em grupos**: nascem em grupos de 3 a 6 e quem fica longe dos outros anda até o esqueleto mais próximo. Strays e wither esqueletos continuam como eram.
- **Guerra:** os atacantes agora vêm montados em **cavalos esqueleto** (só durante a guerra; o cavalo some quando ela termina). Corrigido: em lugar **conquistado** aldeões, golems de ferro, saqueadores, vindicators e evokers **não nascem nem carregam mais** num raio de ~64 blocos, e os que sobrarem são removidos. Os esqueletos que surgem nos lugares conquistados vêm em grupos de 2 a 4 (limite de 12).
- **Old Gun:** a arma em si dá **6 de dano** corpo a corpo; os tiros continuam dando 50 a 150.

### Tanque de guerra (1.52.0)
- **Tanque de Guerra:** 270 de vida, armadura 12, resistente a empurrão e a fogo. Dispara **obuses explosivos de 750 de dano** (raio de 3,5 blocos, a cada ~3,5 s, alcance até 36 blocos) que **não destroem blocos**; não ferem esqueletos, cavalos esqueleto nem outros tanques. Ataca jogadores (fora do criativo) e os moradores das vilas.
- **Nas guerras:** a partir da segunda onda um tanque chega com um esqueleto de tripulante em cima (no máximo 2 ao mesmo tempo). O ataque só termina quando os tanques também forem destruídos. Se os esqueletos vencem, o tanque some e o tripulante fica como morador; se perdem, os dois morrem. Dropa ferro, pólvora e às vezes um bloco de ferro. Também tem ovo de geração.
- **Visual:** modelo próprio (casco, duas esteiras e torre que gira) com **textura que eu pintei por código** (`tools/gerar_tanque.py`), não é arte sua: se quiser outra, é só me mandar.

### Marca de Guerra (1.53.0)
- Quem **matar o Rei Esqueleto** recebe o efeito **Marca de Guerra** (duração infinita, ícone de caveira vermelha). Ela **não some** com leite, `/effect clear` nem morte.
- A guerra **começa quando o jogador marcado chegar a uma vila ou a um outpost de pilhagem**; nesse momento a marca some e os esqueletos atacam aquele lugar (ondas, cavalos e tanques, como antes). Se o lugar já foi atacado ou conquistado, a marca some sem guerra nova.
- Ops: `/skeletonwar mark <jogador>`, `/skeletonwar unmark <jogador>`, `/skeletonwar arrive <x y z>` (simula a chegada); chegar = ficar a até 48 blocos de uma vila ou outpost. O `/skeletonwar declare` antigo continua existindo.
- O ícone do efeito foi pintado por mim por código (`tools/gerar_icone_marca.py`).

### Esqueletos abrem portas e escalam (1.54.0)
- Todos os esqueletos (comum, stray, wither, atacantes de guerra) **abrem portas** e **escalam paredes** como aranhas quando vão atrás de um alvo e batem num muro.

### Tanques atacam moradores (1.54.1)
- Os tanques de guerra miram **aldeões, saqueadores (pillager, vindicator, evoker) e golems de ferro** sem precisar enxergá-los, e a guerra escolhe sempre o mais próximo como alvo.

### Maçãs de netherite, bolo de netherite e armadura adaptativa (1.55.0)
- **Maçã de Netherite:** `NNN / NAN / NNN` (N = lingote de netherite, A = maçã). Efeitos: Regeneração IV 30 s, Absorção X 2 min, Resistência III 5 min, Resistência ao Fogo 5 min, Força II 3 min, Aumento de Vida III 5 min. Sempre dá para comer.
- **Maçã de Netherite Encantada:** `BSB / SAS / BSB` (B = bloco de netherite, S = estrela do Nether, A = maçã de netherite). Brilha. Regeneração V 1 min, Absorção XX 3 min, Resistência IV 10 min, Resistência ao Fogo 10 min, Força III 5 min, Aumento de Vida X 10 min, Velocidade II 5 min.
- **Bolo de Netherite:** `MMM / SAS / NNN` (M = balde de leite, S = açúcar, A = maçã de netherite, N = lingote de netherite). Bolo cinza com **cobertura vermelha**, 7 fatias como o bolo comum; cada fatia dá Regeneração II 15 s, Absorção IV 1 min e Resistência I 1 min (como o bolo comum, só dá para comer com fome).
- **Armadura de netherite adaptativa:** com o conjunto de netherite vestido (jogador, mob ou qualquer um), se por perto (24 blocos) houver um jogador ou mob **que não use o conjunto de netherite** com armadura **superior**, as peças se adaptam: ganham armadura e resistência da armadura para passar a dele, e Resistência/Força sobem acima das dele (Resistência até V, Força até VII). **Só melhora o que a outra armadura tem de superior; contra armaduras iguais ou mais fracas nada muda.** A adaptação fica gravada em cada peça (NBT `NetheriteAdapt`), nunca diminui, aparece na dica do item e tem uma pausa de 5 s entre melhorias. Dois conjuntos de netherite não se adaptam um ao outro (evita uma corrida infinita).
- Texturas de maçãs e bolo **pintadas por mim por código** (`tools/gerar_comidas.py`), não são arte sua.

### Adaptação e encantamentos multiplicadores (1.55.1)
- A adaptação fica gravada nas peças (NBT `NetheriteAdapt`): **continua mesmo depois que o mob/jogador com a armadura superior morre** (ou que você se afasta).
- **Encantamentos 2x, 4x, 8x, 16x** agora também valem para a adaptação: o multiplicador multiplica a armadura e a resistência da armadura adaptadas, e **os efeitos do conjunto escalam**: nível de Força/Resistência × multiplicador (Resistência até V, Força até o limite do jogo). Ao se comparar com outra armadura, o sistema já conta os valores multiplicados.

### Ganso, Galinha de Netherite e Aranha Colossal (1.56.0)
**Não testado no jogo** (só compilação/servidor de teste no CI). Texturas e modelos feitos por mim (IA), não enviados por você.
- **Ganso**: neutro, 10 HP, 3,5 de dano, rápido, planeia ao cair (sem dano de queda). Spawna perto de água (rios, planícies, pântanos, florestas). Sementes domesticam (1/3 de chance; domesticado segue o dono, clique com a mão vazia senta/levanta); pão reproduz. Drop: 3 carnes de frango e 2 penas.
- **Galinha de Netherite**: clique com uma maçã de netherite (ou encantada) numa galinha adulta. 250 HP, 25 de dano, revida. Drop: 25 frangos assados e 1 maçã de netherite. Ovo de spawn no criativo.
- **Aranha Colossal** (mini boss, barra de boss roxa): 450 HP, 50 de dano. Spawna raramente em taigas/pinheiros. Coloca teias no chão e em árvores; quem encosta numa teia dela vira alvo. Tem medo de fogo, tochas, lanternas, fogueiras, lava e de quem segura tocha/isqueiro. Em combate atira teias (lentidão + teia) e agarra o alvo de perto dando 5 golpes. As teias somem quando ela morre.

### Mosquito Gigante e Strongest Zombie (1.57.0)
**Não testado no jogo.** Texturas/modelos feitos por mim (IA).
- **Mosquito Gigante** (do tamanho de um cavalo, voa): 120 HP, 5 de dano na mordida. Ataca qualquer mob (jogadores só se o atacarem) e **gruda** no alvo, chupando 7 de dano por segundo até o alvo morrer. Domestica com carne podre (1/3 de chance). O dono monta (clique direito, voo rápido seguindo para onde olha) e abre o inventário próprio de 27 espaços com Shift + clique direito. Spawna em pântanos.
- **Strongest Zombie**: zumbi grande, 150 HP, 10 de dano, corre e nada rápido, dá grandes saltos, arremessa quem acerta e invoca zumbis para defendê-lo. Spawna raramente (qualquer lugar do overworld, à noite) ou quando um zumbi comum é atingido por um raio. Drop: **Cabeça do Strongest Zombie**.
- **Cabeça**: usável como capacete (clique direito ou no slot). Quando quem a usa é atacado, invoca 3 zumbis defensores (dura 60 s, recarga 15 s) que atacam o agressor e nunca o dono.

### Perseguidor, Besouro Colossal e Fungus (1.58.0)
**Não testado no jogo.** Ideias do GPT; texturas/modelos feitos por mim (IA). Os drops foram escolha minha.
- **Perseguidor** (3 blocos, humanoide magro, braços longos de 3 pontas, pernas invertidas, olhos brancos brilhantes): 120 HP, 12 de dano. Aparece à noite em florestas/taigas e persegue jogadores de longe (alcance 64). Mais rápido que um zumbi. Fica invisível enquanto o alvo está a mais de 24 blocos. Não atravessa água. Ao amanhecer foge e some. Drop: ossos e, às vezes, pérola do fim.
- **Besouro Colossal** (estilo besouro-rinoceronte, chifre grande, 6 patas): 250 HP, 18 de dano. Spawna em cavernas profundas (abaixo de y=0). A carapaça reduz 50% do dano recebido. Ataca qualquer criatura que entre em 18 blocos de onde nasceu e dá investidas que empurram. Drop: ferro e, às vezes, diamante.
- **Fungus** (tronco apodrecido com chapéu de cogumelo, 4 raízes, cogumelos nas costas): 90 HP, 7 de dano. Spawna em pântanos e florestas escuras, ataca principalmente à noite. Esporos causam Lentidão II e Fraqueza (na mordida e em explosão de esporos num raio de 6). Regenera perto de cogumelos gigantes. Drop: cogumelos e farinha de ossos.

### Anomalocaris reformulado e Guardião Ancestral (1.59.0)
**Não testado no jogo.** Ideia do GPT; texturas/modelos feitos por mim (IA).
- **Anomalocaris**: 160 HP, 16 de dano, velocidade de natação bem maior. Spawna também em oceanos profundos. Caça peixes, lulas e axolotes; foge de jogadores, mas se for atacado revida e não foge mais.
- **Guardião Ancestral** (boss, 5 blocos, 4 braços, 2 chifres, rachaduras de magma brilhantes): 1.500 HP, 35 de dano. Nasce adormecido nas profundezas (spawn raríssimo no bioma Deep Dark, abaixo de y=-10; ovo de spawn o cria acordado) e acorda quando um jogador chega a 10 blocos ou ele leva dano. Ataque de impacto em área (raio 7, 21 de dano e arremesso) a cada 5 s, invoca 3 **Guardiões de Pedra** (40 HP, 8 de dano, até 8) a cada 20 s e recebe só 25% do dano de flechas/projéteis. Drops (escolha minha): sucata de netherite, diamantes, obsidiana e bloco de magma.

### Escavador, Autômato e Tartaruga Ancestral (1.60.0)
**Não testado no jogo.** Ideias do GPT; texturas/modelos feitos por mim (IA). Drops e detalhes não especificados foram escolha minha.
- **Escavador** (quadrúpede magro de costas arqueadas, braços compridos, mandíbula que abre muito): 180 HP, 15 de dano. Vive em cavernas (abaixo de y=50, sem céu). Fica parado quando não percebe ninguém; sente jogadores a até 5 blocos e escuta quem se move (andando/correndo, não agachado) até 24 blocos; persegue e esquece após 5 s sem ouvir. Escala paredes. Drop: ossos e ferro bruto.
- **Autômato** (humanoide de placas metálicas, visor azul e núcleo luminoso no peito, 2,5 blocos): neutro, 200 HP, 14 de dano. Aparece em minas abandonadas e portais em ruínas. Domestica com barra de ferro (1/3 de chance; ferro cura 25 quando domesticado). Domesticado segue e protege o dono, atacando esqueletos, zumbis e outros monstros; clique com a mão vazia senta/levanta. Drop: ferro e redstone.
- **Tartaruga Ancestral** (casco enorme escuro com placas ósseas, fósseis e plantas): neutra, 300 HP, 20 de dano (cabeçada que empurra). Vive em rios e oceanos, raro. Nunca ataca primeiro; ao ser ferida se esconde no casco por 3 s (recebe 80% menos dano) e depois revida. Drop: escudos de tartaruga (scute) e ossos. Altura em torno de 1,8 bloco (você pediu 2).

### Escavador mais assustador e Devorador do Nether (1.61.0)
**Não testado no jogo.** Texturas/modelos feitos por mim (IA).
- **Escavador**: pernas reposicionadas (estavam soltas do corpo); agora tem espinhos nas costas, dentes na boca, garras nas mãos, mandíbula maior e pele com veias/costelas.
- **Devorador do Nether** (antigo Guardião Ancestral; só o nome de exibição mudou, o id interno continua `ancient_guardian`): 2.500 HP, 45 de dano, impacto em área de 27, **solta bolas de fogo** (explosão 2, a cada 3 s) e continua invocando Guardiões de Pedra. Com **metade da vida fica mais agressivo**: mais rápido, ataques mais frequentes e 3 bolas de fogo por vez (a barra de vida fica roxa). **Spawna em fortalezas do Nether** (raro, um por vez; o ovo de spawn cria em qualquer lugar). Substitui o spawn antigo no Deep Dark.

### Armadura de obsidiana: efeitos (1.61.1)
**Não testado no jogo.** O conjunto completo de obsidiana agora dá Resistência IV e Força I (além da imunidade a explosões).

### Guardião Ancestral (verdadeiro) e O Abismo (1.62.0)
**Não testado no jogo.** Texturas, modelos e estruturas feitos por mim (IA). Drops, loot e detalhes não especificados foram escolha minha.
- **Guardião Ancestral** (boss, golem de pedra de 7 blocos de altura, cabeça colada ao tronco, braços enormes quase no chão, pernas de camadas de rocha, blocos que flutuam levemente, olhos de fenda dourada e runas brilhantes): 1.500 HP, 35 de dano. Fica adormecido e acorda quando um jogador chega a 10 blocos ou ele leva dano. Impacto em área (raio 7, 21 de dano, arremesso) a cada 5 s, invoca 3 **Guardiões de Pedra** a cada 20 s (até 8) e recebe só 25% do dano de flechas/projéteis. Drops: pedra, blocos de ouro, diamantes, às vezes estrela do Nether e sucata de netherite. Não tem spawn natural solto: vive no **Templo Ancestral**.
- **Templo Ancestral** (estrutura nova, **subterrânea**, rara, em qualquer bioma do overworld, por volta de y=-24): salão com pilares, runas de blackstone dourado, 4 baús, 4 guardiões de pedra de guarda e o Guardião dormindo no centro. Há um poço de 3x3 com escada subindo, mas a entrada **não chega à superfície** (você precisa cavar até ele ou achar por caverna). Use `/locate structure netheritesupremo:ancestral_temple`.
- **O Abismo** (chefe secreto, 8 blocos, massa escura em gaiola aberta com **núcleo roxo que pulsa**, vários olhinhos, 6 tentáculos de tamanhos diferentes): 5.000 HP, 60 de dano. Só existe na **Terra Negra**: nasce adormecido no **Santuário do Abismo** (estrutura rara de lá) e, em qualquer outra dimensão, ele não entra no mundo (nem por ovo/comando). Acorda a 24 blocos.
  - **Tentáculos**: cada um tem alcance e recarga próprios (11,5 a 7 blocos) e escolhe o próprio alvo, 24 de dano e arremesso.
  - **Invoca Crias do Abismo** (endermites roxos de 20 HP, até 12).
  - **3 fases** pela vida: >66% normal; ≤66% mais rápido, tentáculos mais frequentes, invoca mais e dá um **pulso** que puxa todos e escurece a visão; ≤33% ainda mais rápido e o pulso também causa dano. O núcleo pulsa mais rápido a cada fase.
  - Drops: estrelas do Nether, netherite, cacos de eco, ametista, maçã dourada encantada.
- Ovos de spawn: Guardião Ancestral, O Abismo (só funciona na Terra Negra) e Cria do Abismo.

### Wood Stalker (1.63.0)
**Não testado no jogo.** Textura/modelo feitos por mim (IA). Drops (gravetos, tronco de carvalho escuro, 15% diamante) e detalhes não especificados foram escolha minha.
- **Wood Stalker** (magro, 3 blocos, madeira escura com galhos e olhos verde-pálido brilhantes): 125 HP, 25 de dano. Hostil; spawna em florestas e taigas e ataca qualquer mob e jogador.
- **Agarrar e sugar**: ao acertar, 1/3 de chance de agarrar a vítima por até 5 s (recarga 8 s): ela fica presa à frente dele e leva 12 de dano por segundo, que ele recupera como vida.
- **Quebra blocos** (dureza até 5, respeita a regra mobGriefing) quando trava no caminho até o alvo, **escala paredes** e **se abaixa** sob tetos baixos (altura 1,6; automático, mas o pathfinding dele ainda trata túneis de 2 blocos como difíceis).
- **Medo de fogo e água**: foge de fogo, lava, fogueira e água num raio de 3 blocos (e solta quem agarrou).
- **Regenera** perto de árvores, folhas, tábuas, mudas, flores e plantações (até 6 HP por segundo com 6+ blocos num raio de 4).
- **Resistências**: recebe 25% do dano de flechas/projéteis, 25% do dano de queda e **não morre afogado** (mesmo assim ele tem medo de água e foge dela).
- **Domesticável com diamante** (1/3 de chance). Domesticado segue e protege o dono, ataca só monstros; clique vazio senta/levanta; diamante cura 30.
