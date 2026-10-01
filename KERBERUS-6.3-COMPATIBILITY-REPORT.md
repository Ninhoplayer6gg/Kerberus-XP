# Kerberus XP 6.3 — Pokémon Compatibility Expansion
## Relatório de compatibilidade

> **Leia primeiro.** Nenhum jogo foi executado durante o desenvolvimento da 6.3: o ambiente de
> trabalho não tinha os arquivos dos jogos nem um aparelho Android. Todo estado de jogo abaixo é a
> última evidência real conhecida. A 6.3 não declara nenhum jogo compatível; ela passa a **registrar
> automaticamente** as evidências de cada jogo (Boot, Title, Mapa, Batalha, Save, Jogabilidade
> prolongada) para o próximo teste no aparelho.
>
> **6.3.1.** O primeiro teste real no aparelho (Pokémon Uranium na 6.3) mostrou que o jogo abria,
> mas nenhum controle respondia. Causa e correção: bug 15.
>
> **6.3.2.** Com a 6.3.1, o Uranium carregou todas as sections até a 199 e parou em
> `superclass mismatch for class Window_PokemonBag`. Causa e correção: bugs 16 e 17.
>
> **6.3.3.** O Ruby 1.8 passa a ser compilado no CI a partir do código fixado, e a regra de
> superclasse do Ruby 1.8.1 vai para dentro do interpretador (`eval.c`).
>
> **6.4.0.** Atualização grande de compatibilidade e desempenho: 99 funções Win32 comuns que paravam o
> jogo passam a funcionar (entre elas `SetPriorityClass`, presente no Uranium), pulo de quadros do
> RGSS1, coleta de lixo e cache de métodos do Ruby 1.8 ajustados com medição, e relatório `[PERF]`
> de desempenho no log.
>
> **6.4.1.** Com a 6.4.0 o Uranium passou da section 199 (classes) e já tinha controles, pulo de
> quadros e as APIs Win32 resolvidas, mas parou na section 253 (GENERATE WIKI PAGES) com
> `undefined (?...) sequence: /(?<!\\)"/`. Causa e correção: bugs 22 a 25.
>
> **6.4.2.** No aparelho, a 6.4.1 parou na tela de título (`Sprite_Resizer:209`, `ENV["TEMP"]` nil).
> Em vez de mais uma APK por erro, o Uranium passou a rodar **no motor legacy18 real compilado para
> PC** (`tools/game-harness/`), com teclas de verdade, do título até a captura do primeiro Pokémon,
> evolução e save/load, e numa varredura automática por todos os mapas. Tudo o que apareceu está nos
> bugs 26 a 34, corrigidos juntos. Isso é teste real do jogo, mas **no PC**: o aparelho ainda precisa
> confirmar (toque, GPU real, desempenho).
>
> **6.4.3.** No aparelho, a 6.4.2 abriu o Uranium, aplicou as variáveis do Windows e os ports e
> carregou até a section 224, mas a tela de título parou em `undefined method 'unpack' for nil`
> (`AudioUtilities v17:1010`). O harness do PC não via porque o stdio do Linux é diferente do
> Android. Causa e correção: bugs 35 e 36; o harness agora roda com um Ruby 1.8 que usa o stdio do
> Android. Junto: chave de assinatura fixa (sem desinstalar nas próximas versões), backup dos saves do
> Uranium, turbo 2x/3x, coleta de lixo em momentos ociosos e fonte para texto chinês/japonês/coreano.
>
> **6.5.0.** No aparelho, a 6.4.3 abriu o Uranium e o jogo roda, mas com mini travadas ao andar e um
> turbo que "trava muito". O harness do PC achou as duas causas das travadas (coleta de lixo do Ruby
> 1.8 varrendo o código dos scripts; pulo de quadros do RGSS1 sem limite, que deixava a tela sem
> desenhar por segundos quando o aparelho fica um pouco mais lento que o jogo) e a do turbo (1 quadro
> desenhado a cada N atualizações mesmo quando o aparelho não aguenta N). Corrigidas com medição na
> seção 3. Junto: Salvar agora, histórico automático de saves, filtros de imagem, painel de
> desempenho, controle Bluetooth com mapeamento, vibração, captura de tela e modo debug do Essentials.

## 1. Estado por jogo

| Jogo | Runtime | Última evidência real | Estado 6.3 | O que a 6.3 muda para ele |
|---|---|---|---|---|
| Pokémon Uranium | legacy18 (Ruby 1.8.7 real) | Aparelho, 6.4.2: variáveis do Windows e ports aplicados, carrega até a section 224 e para na tela de título (`unpack` para nil, bug 35). PC, motor real, 6.4.2: título, novo jogo, intro, controles, casa, cutscenes, laboratório, inicial, batalha do rival, captura, evolução, save/backup/load e varredura de todos os mapas sem erro, com textura máxima de 4096 | Controles corrigidos na 6.3.1; regra de superclasse do RGSS1 no interpretador desde a 6.3.3; regex do RGSS1 (Oniguruma) na 6.4.1; bugs 26–34 na 6.4.2; bugs 35–36 na 6.4.3 (título confirmado no harness com o stdio do Android); aguarda teste no aparelho | APIs opcionais declaradas não derrubam mais o boot; nomes com sufixo `A`; 307 providers em vez de 62; ports `rgss_linker`/`fmodex_audio` preservados e completados (volume das opções, fade-in real); `klein_bitmap`, `gif_dll`, `text_entry` (teclado Android), `bitmap_memory`; correção genérica do `LargePlane`; caminhos estilo Windows |
| Pokémon Insurgence | legacy18 | Nenhuma | Não testado | HMode7 e Winsock declarados no carregamento não bloqueiam mais; rede responde com erros Winsock documentados; `GetKeyboardState` do módulo `Keys` 9× mais rápido |
| Pokémon Xenoverse | legacy18 | Nenhuma | Não testado | Port do Zeus Video Player (OGV nativo), KleinBitmap, MCI mapeado para o áudio nativo |
| Pokémon Infinite Fusion | modern31 (Ruby 3.1) | Entrava no jogo (6.1.x) | Regressão automatizada OK; aparelho pendente | Nada no comportamento do jogo. Bootstrap moderno byte a byte idêntico (sha256 fixado). No C++ do modern31 entraram só dois registros de diagnóstico (marco BOOT e relatório de exceção). Três proteções independentes na seleção de runtime |

A seleção de runtime da 6.3 foi testada com casos para os quatro jogos, RGSS1 comum, RGSS3,
jogos com `mkxp.json`, com `x64-msvcrt-ruby310.dll`, com scripts externos do Essentials v19+ e
com o override `.kerberus-runtime`.

## 2. Bugs encontrados, causas raízes e soluções

| # | Sintoma | Categoria | Causa raiz | Solução genérica | Teste |
|---|---|---|---|---|---|
| 1 | Jogo morre ao carregar uma section que só **declara** uma API Windows sem provider | Win32API | O legacy18 aceitava 62 nomes fixos; o resto ia para o MiniFFI, cujo `SDL_LoadObject` falha no Android e lança erro no `Win32API.new` | WinBridge com Deferred Binding: declarar sempre funciona, chamar gera `WINBRIDGE_UNAVAILABLE` com dll, função, assinatura, script, section, linha, local da declaração e contagem | `test_deferred.rb` |
| 2 | `GetPrivateProfileString` funcionava e `GetPrivateProfileStringA` não (e vice-versa) | Win32API | Tabela por nome exato; o Win32API do RGSS1 tenta `nome` e depois `nomeA` | Busca exata e depois com `A`, como no Windows; nomes de função continuam sensíveis a maiúsculas | `test_signatures.rb` |
| 3 | Resultado `nil` em APIs de retorno void | Win32API | Kerberus devolvia `nil`; Win32API e MiniFFI devolvem `0` | Void devolve `0` | `test_deferred.rb` |
| 4 | `GetAsyncKeyState` devolvia `32768` e não tinha o bit "pressionada desde a última consulta" | Input | No RGSS1 de 32 bits o SHORT chega com sinal (`-32768`/`-32767`); o bit 0 não existia | Conversão `long` 32 bits para todo retorno inteiro; bit 0 na primeira consulta depois que a tecla desce (6.3.1) | `test_input_memory.rb` |
| 5 | `No instance data for variable (missing call to super?)` em `disposed?`/`dispose` | Gráficos/lifecycle | `getPrivateData` lança antes do `if (!d)` que devia tratar objeto sem nativo; `LargePlane < Plane` do Essentials nunca chama `Plane#initialize` | No legacy18, objeto sem nativo: `dispose` não faz nada, `disposed?` é `true`; demais acessos continuam erro, agora com o nome da classe | `tests/native` (reproduz o erro original com o header antigo) |
| 6 | Assinaturas como `"%w()"`, itens de array longos ou caracteres estranhos eram recusadas; outras formas geravam só "Tipo de assinatura desconhecido" | Win32API | Parser mais estrito que o Win32API real e sem contexto no erro | Regras do `ext/Win32API` do Ruby 1.8 (caractere a caractere, desconhecidos ignorados com aviso); erro de tipo com `WINBRIDGE_SIGNATURE` completo | `test_signatures.rb` |
| 7 | Local de erros do Morph sempre "script nao identificado" no legacy18 | Diagnóstico | `caller_locations` não existe no Ruby 1.8 | Leitura de `caller` no C++ quando `RAPI_MAJOR < 2` | compilação C++ dos dois runtimes |
| 8 | Arquivos com outra caixa ou com `\` não eram encontrados por `File`/`Dir`/`IO` | Filesystem | Android diferencia maiúsculas e não entende `\`; o mkxp-z só resolve isso para Bitmap/Audio/load_data | Camada Paths: caminho exato primeiro; em falha, resolução por componente com índice de diretório; escrita reutiliza o arquivo existente (sem duplicatas) | `test_filesystem.rb` |
| 9 | INI: `#` tratado como comentário, nome sem pasta lia o `Game.ini` do jogo, sem escrita | Win32API/INI | Parser simplificado | Regras do Windows (só `;` comenta, nome sem pasta = pasta Windows virtual, aspas, truncamento, listas com duplo NUL) e escrita preservando o arquivo | `test_ini_registry.rb` |
| 10 | XInput sempre "desconectado"; vibração inexistente | Input | Provider fixo | Controle SDL ao vivo e `SDL_GameControllerRumble` | `test_input_memory.rb` |
| 11 | Volume do menu de opções ignorado e `Audio_bgm_fadein` sem efeito no port FMOD | Áudio | Adaptador incompleto | Ponte única de áudio com volume das opções e fade-in real por frame; FMOD, audio.dll, MCI e PlaySound usam o mesmo dono por canal | `test_audio.rb`, `test_ports.rb` |
| 12 | Idioma sempre en-US, bateria sempre indisponível | Sistema | Valores fixos | LANGID/LCID/GEOID/code page do locale Android; bateria via `SDL_GetPowerInfo` | `test_input_memory.rb` |
| 13 | Os dois textos Ruby eram embutidos nos dois motores | Separação de runtimes | Os dois headers eram incluídos em `binding-mri.cpp` para ambos | Cada motor inclui só o seu; auditoria da APK verifica | workflow v20 |
| 14 | (achados pelos testes da própria 6.3, corrigidos antes da entrega) | Filesystem/IO/áudio | Ruby 1.8 recusa reexecutar `File#initialize` ("reinitializing File"); cache relativo inválido após `chdir`; `flush` em arquivo só leitura; `type mpegvideo` do MCI é usado para MP3/OGG | Resolução antes da abertura; cache limpo no `chdir`; handles sabem se são graváveis; vídeo MCI decidido pela extensão | suítes legacy18 |
| 15 | Uranium abre, mas nenhum controle responde: gamepad de tela, teclado ou controle físico | Input | `GetAsyncKeyState`, `GetKeyState` e `GetKeyboardState` liam `Input.pressex?`, que consulta uma cópia do estado que só o `Input.update` nativo do mkxp-z atualiza. O Essentials v15–v17 substitui `Input.update` pela própria leitura via `GetAsyncKeyState` e nunca chama o nativo, então a cópia ficava vazia. `GetCursorPos` e XInput tinham a mesma dependência. Os testes da 6.3 não pegaram isso porque o dublê do mkxp-z respondia ao vivo | O legacy18 lê o estado ao vivo da thread de eventos SDL (`KerberusNative.key_down?`, `mouse_position`, `pad_buttons`, `pad_axis`), como o Windows lê o teclado real. O controle físico aciona as mesmas teclas do gamepad de tela. O dublê do mkxp-z agora separa estado vivo e cópia, como o original. O modern31 não muda | `test_input_live.rb` (13 falhas na 6.3, passa na 6.3.1) |
| 16 | Uranium para ao carregar a section 199: `superclass mismatch for class Window_PokemonBag` | Ruby 1.8 | O RPG Maker XP usa o Ruby 1.8.1, que ao reabrir uma classe com outra superclasse cria uma classe nova com o mesmo nome (`eval.c`, `goto override_class`). O Ruby 1.8.2 em diante, incluindo o 1.8.7 do Kerberus, lança `TypeError` | Antes de cada `class Nome < Constante` dos scripts, na mesma linha, o legacy18 remove `Nome` quando a superclasse atual difere; o Ruby 1.8.7 então define a classe nova como o 1.8.1. Cada caso vira `[RUBY181_CLASS_OVERRIDE]` com section e linha. Nenhum arquivo do jogo muda. Na 6.3.3 a regra foi para o `NODE_CLASS` do `eval.c` do Ruby 1.8 compilado no CI; o texto dos scripts fica intacto e a versão por texto ficou como fallback | `test_ruby181_classes.rb` (interpretador) e `test_ruby181_transform.rb` (fallback) |
| 18 | Qualquer chamada a `SetPriorityClass` (detectada no Uranium) ou a outras 98 funções Win32 comuns em scripts de RMXP parava o jogo com `WINBRIDGE_UNAVAILABLE` | Win32API | Sem provider: de uma lista de 256 funções comuns, 99 não existiam no WinBridge | `54_extended.rb` implementa as 99 com o comportamento real ou com a falha documentada do Windows; a conferência contra a lista dá zero faltando | `test_extended_apis.rb` (90 verificações) |
| 19 | O diagnóstico listava `xinput1_3`, `xinput9_1_0` e `RGSS Linker` como não classificadas | Diagnóstico | A análise estática procurava o nome da DLL como escrito pelo jogo | Mesmos aliases de DLL e mesmo sufixo `A` do WinBridge; um teste compara as listas de aliases do Java e do Ruby | `CompatibilityStatusHostTest` |
| 20 | Em aparelho lento o jogo roda em câmera lenta em vez de pular quadros | Desempenho | O RGSS1 pula quadros quando atrasa; o mkxp-z vem com isso desligado | O legacy18 liga `Graphics.frameskip` (desligável por jogo) | `test_performance.rb` |
| 21 | Coletas de lixo frequentes (a cada 8 MB alocados) em jogos grandes | Desempenho | Limites do Ruby 1.8 pensados para PCs de 2003 | Limite de 16 MB (medido: 43% menos coletas e 37% menos tempo de GC na carga leve), cache de métodos 8x maior, ajuste por jogo | benchmark no documento técnico |
| 22 | Uranium para ao carregar a section 253 (GENERATE WIKI PAGES): `undefined (?...) sequence: /(?<!\\)"/` | Ruby 1.8 | O RPG Maker XP compila regex com o Oniguruma, que aceita look-behind e grupos nomeados; o motor GNU do Ruby 1.8.7 recusa essa sintaxe ao ler o script, e um único literal derruba a section inteira, mesmo sem ser usado | O Ruby 1.8 do CI (`scripts/patch-ruby18-oniguruma.py`) mantém o GNU para toda regex que ele aceita e passa só as recusadas ao Oniguruma 6.9.10 (BSD 2-Clause, commit fixado), com sintaxe Ruby e o `$KCODE` em uso; se os dois recusam, o erro do GNU continua. Nenhum script do jogo muda | `test_regex_rgss1.rb` (48 verificações, com a regex exata do Uranium; falha no Ruby sem o patch com o mesmo erro do aparelho) |
| 23 | O relatório desse erro dizia `line=369`, mas a regex estava na linha 270 | Diagnóstico | O Ruby 1.8 lança o `SyntaxError` na última linha da section; a linha do erro fica na mensagem | No legacy18 o relatório usa a linha `SectionNNN:linha:` da mensagem (`kerberus/exception-line.h`) | `exception_line_test.cpp` (8 verificações) |
| 24 | `GetDiskFreeSpaceExA` falhava com `ERROR_NOT_SUPPORTED` no aparelho, apesar de a 6.4.0 anunciar espaço real | Win32API | O provider chamava `KerberusNative.disk_space`, que só existia no dublê dos testes | `disk_space` nativo com `statvfs` no legacy18 | checagem C++ e conferência com `df` no host |
| 25 | O diagnóstico mostrava `IMPORTAÇÃO INTERROMPIDA ... 3911/6540` e a pasta parcial ocupava espaço para sempre | Launcher | Com o processo morto no meio da importação, o `finally` que apaga a pasta de preparação (`games/.import-*`) e as cópias do ZIP no cache nunca roda. O jogo já importado não é afetado: a importação só troca a pasta no final | O launcher apaga essas sobras ao abrir, no mesmo worker das importações, antes de qualquer importação nova; jogos importados e outros arquivos ficam intactos | `ImportLeftoversHostTest` |
| 26 | Tela de título do Uranium: `NoMethodError: undefined method '+' for nil` em `Sprite_Resizer:209` (6.4.1 no aparelho) | Ambiente Windows | O Essentials v16/v17 monta caminhos com `ENV["TEMP"]`; o Android não tem as variáveis do Windows. A captura de tela ainda gravava um BMP em `%TEMP%` pela `rubyscreen.dll` | O legacy18 cria só as variáveis que faltam (`TEMP`, `TMP`, `APPDATA`, `LOCALAPPDATA`, `USERPROFILE` em pastas reais `.kerberus/` do jogo; `USERNAME=Player`, `COMPUTERNAME`, `OS`) e espelha no `GetEnvironmentVariableA`; o `snap_to_bitmap` do jogo vira a captura nativa, mantendo o pós-processamento e as linhas da section. Saves continuam onde estavam | `test_uranium_runtime.rb` |
| 27 | `Bitmap.new` de um arquivo gravado durante o jogo: "No such file or directory" | Filesystem | O mkxp-z resolve imagens por um cache de caminhos montado na abertura | Uma nova tentativa por caminho, depois de `System.reload_cache`, só se o arquivo existe (absoluto dentro do jogo vira relativo); ausente continua erro | `test_uranium_runtime.rb` |
| 28 | Mapa com evento sem gráfico parava com `File Graphics/Characters/ not found` | Port `gif_dll` | O `GifBitmap` do Essentials devolve um bitmap vazio para nome vazio ou arquivo ausente; o port lançava o erro | Mesmo comportamento do Essentials; arquivo ausente vira `GIF_MISSING` no log | `test_uranium_runtime.rb` |
| 29 | Tela de controles do Uranium gravava Enter como "Separator" | Input | O mkxp-z liga `VK_SEPARATOR` (0x6C) e `VK_PLAY` (0xFA) a teclas que já têm VK próprio; um teclado Windows nunca envia essas duas | `GetAsyncKeyState`/`GetKeyboardState` nunca as reportam | `test_uranium_runtime.rb` |
| 30 | Jogo fecha (segfault) num diálogo em português | C++ (os dois motores) | Um U+200B sozinho não tem largura; o SDL_ttf devolve `NULL` e o `draw_text` do mkxp-z usava a superfície sem checar | Não desenha nada, como o RGSS; o mesmo para o contorno | reproduzido no harness com gdb |
| 31 | F12 fechava o jogo | Port novo `hard_reset` | A section 0 do Uranium relança o executável e chama `exit`; o mkxp-z reinicia as sections no mesmo processo | Só o relançamento sai (evento `RESET`); CRLF e LF | `test_uranium_runtime.rb` |
| 32 | `Operation not supported for mega surfaces` ao entrar na Comet Cave | Gráficos | O `CustomTilemap` do Essentials v15–v17 (padrão, `MAPVIEWMODE 1`) põe o tileset inteiro num Sprite; tileset maior que a textura da GPU (até 256x21256 no Uranium) não pode ser bitmap de Sprite | Port `mega_tileset`: para esse tileset o tilemap usa o próprio caminho de tiles redimensionados (cópia única por tile para um bitmap 32x32) | `test_uranium_runtime.rb`; varredura de mapas |
| 33 | Tiles de camadas superiores apagariam os de baixo em mapas com tileset gigante | C++ (legacy18) | Cópia de uma mega surface para a GPU sem mistura de transparência | `blt`/`stretch_blt` de mega surface pelo pipeline normal de mistura e opacidade, por páginas na GPU | varredura de mapas (captura de cada mapa) |
| 35 | Tela de título do Uranium no aparelho: `NoMethodError: undefined method 'unpack' for nil` em `AudioUtilities v17:1010` (`getOggPage`, 6.4.2) | Ruby 1.8 no Android | O `FILE` do bionic é opaco: o `config.h` do Android não tem `FILE_READEND`/`FILE_READPTR` e o `io.c` usa `!feof(fp)` como "há dados no buffer". Um `seek` apaga a flag de fim, então `eof?` depois de `pos = tamanho` dava `false` e o `read` seguinte, `nil`. No glibc a resposta é exata (o harness não reproduzia) | `scripts/patch-ruby18-io.py`: sem acesso ao buffer, o `eof?` do interpretador lê um byte e devolve com `ungetc`, o caminho que o Ruby 1.8 já usa com o buffer vazio; `IO.kerberus_eof_probe` informa o modo. Ruby 1.8 sem o patch: `84_io_eof.rb` detecta o `eof?` errado e o troca (evento `RUBY18_IO`) | `ruby18_io_eof.rb` num Ruby 1.8 com o stdio do Android (CI) e no normal; `test_io_eof_fallback.rb`; título do Uranium no harness com esse Ruby |
| 36 | `File.expand_path("~")` sem `HOME` | Ambiente Windows | O Ruby 1.8 do Windows usa `USERPROFILE`; o do Android só `HOME` | `HOME` criado na mesma pasta `.kerberus/profile` quando falta | `test_io_eof_fallback.rb` |
| 34 | Com textura máxima 4096 (celulares comuns) a tela de título e batalhas com Pokémon animados largos paravam: `Texture dimensions [5600, 80] exceed hardware capabilities` | C++ (legacy18) | O EliteBattle copia a faixa de animação inteira (até 15360x80; 184 sprites passam de 4096 e 66 de 8192) para `Bitmap.new(l, a)`; o mkxp-z só aceitava imagem grande carregada de arquivo | `Bitmap.new` acima do limite fica na RAM, como no RGSS1; cópia, limpeza, pixels, `raw_data=` e clone em software; cópia para a GPU por páginas. Sprite/Plane com esse bitmap continua erro preciso | harness com `maxTextureSize` 4096; `host-compile-check.sh` nos dois motores |
| 17 | O erro aparecia como `Script '' line ection199` e o backtrace como `:ection199:302` | Diagnóstico | O parser de mensagem do mkxp-z espera quadros com dois `:` (formato do Ruby 1.9+). No Ruby 1.8 o código de topo de uma section gera `Section199:302`; o parser escrevia `\0` e depois `:` sobre a primeira letra da string do backtrace | No legacy18, o quadro é lido de uma cópia: arquivo antes do último `:`, linha depois; o modern31 fica idêntico | checagem C++ dos dois runtimes; pré-processador do modern31 idêntico |

## 3. Implementado

- **WinBridge** (legacy18): 307 providers em 19 DLLs (`kernel32`, `user32`, `advapi32`, `shell32`,
  `winmm`, `gdi32`, `ole32`, `msvcrt`, `xinput1_1…9_1_0`, `ws2_32`, `ntdll`, `rpg.net`, `rgss1`,
  `rubyscreen` e DLLs de fangame), heap virtual, INI, registro virtual, janela lógica, console virtual.
  Lista completa: `docs/KERBERUS-6.3-WINBRIDGE-APIS.md`.
- **Kerberus Ports**: `rgss_linker`, `fmodex_audio`, `audio_dll`, `gif_dll`, `klein_bitmap`,
  `bitmap_memory`, `font_installer`, `text_entry`, `unicode_pointer`, `zeus_video`.
- **Filesystem estilo Windows**, **ponte única de input** e **ponte única de áudio**.
- **Compatibility Recorder (WINTRACE)**: APIs detectadas no código (`DETECTED_STATIC`), declaradas
  e chamadas (`CALLED_RUNTIME`), top APIs, bloqueios com local, limitadas chamadas.
- **Estado por testes**: marcos gravados pelo runtime e exibidos no diagnóstico do launcher.
- **C++**: correção de ciclo de vida, relógios `CLOCK_BOOTTIME`, vibração, local do Morph no Ruby 1.8,
  relatório completo de exceção Ruby, marco BOOT.
- **Java**: seleção de runtime por evidência, catálogo por runtime na análise estática, estado de
  compatibilidade, `.kerberus-options`, versão 6.3.
- **Build**: workflow v20 com job de testes (Ruby 1.8.7 real compilado da tag oficial, Ruby 3.1,
  C++ nos dois runtimes, teste nativo) antes do build da APK; auditoria de separação dos motores;
  script Termux atualizado.

- **6.4**: 99 funções Win32 novas, análise estática com aliases, pulo de quadros do RGSS1, patch de
  desempenho do Ruby 1.8 (GC e cache de métodos), relatório `[PERF]` com coletas de lixo, opções
  `frameskip` e `gc_malloc_limit`.
- **6.4.1**: Oniguruma no Ruby 1.8 para a sintaxe de regex do RGSS1 (look-behind, grupos nomeados)
  que o GNU recusa; linha certa no relatório de `SyntaxError`; `disk_space` nativo; limpeza das
  sobras de importação interrompida. Licença em `THIRD-PARTY-NOTICES.md` e no APK.
- **6.4.2**: variáveis de ambiente do Windows e arquivos criados em execução (`83_winenv.rb`);
  ports `hard_reset` e `mega_tileset`; captura nativa no `bitmap_memory`; `GifBitmap` vazio como no
  Essentials; teclas que o Windows nunca envia; bitmaps maiores que a GPU no legacy18 (C++);
  `draw_text` sem crash com texto de largura zero; harness de desktop em `tools/game-harness/`.

### 6.4.3

- **Chave de assinatura fixa** (`app/build.gradle`, workflow): com os segredos
  `KERBERUS_KEYSTORE_B64` e `KERBERUS_KEYSTORE_PASSWORD`, toda APK sai com a mesma chave e instala por
  cima da anterior. `apk-signature.txt` mostra o SHA-256 do certificado. Sem os segredos, o CI avisa e
  usa a chave de debug.
- **Saves**: a busca de saves reconhece os nomes do Uranium e de outros fangames, pastas de saves e as
  pastas `.kerberus/appdata` e `.kerberus/profile`; o botão Arquivos da biblioteca abre direto
  Exportar/Restaurar. Backups antigos continuam válidos.
- **Turbo 2x/3x** (`src/display/kerberus-turbo.h`, botão Turbo da barra do jogo): N atualizações do
  jogo por quadro desenhado; `Graphics.frame_rate` do jogo inalterado. Medido: 40/80/120 atualizações
  por segundo no Uranium.
- **Coleta de lixo em momentos ociosos** (`94_idle_gc.rb`, `GC.kerberus_pressure`): com a tela
  congelada de uma transição ou com mensagem na tela há 20 quadros, se já passou da metade do caminho
  até a próxima coleta. Uranium no PC (12 mensagens + caminhada): coletas durante o jogo de 22 (1,21 s)
  para 12 (0,65 s); total de 22 para 24. O Essentials já chama `GC.start` ao trocar de mapa e ao
  começar batalhas; o ganho vem das mensagens.
- **Fonte CJK**: texto com caractere chinês/japonês/coreano que a fonte do jogo não tem usa a
  WenQuanYi Micro Hei (Apache 2.0) do mkxp-z, empacotada uma vez como asset.

### 6.5.0

Medido no motor legacy18 real (harness do PC, Uranium em Nowtoch City, andando; carga simulada =
espera fixa por quadro para imitar um aparelho mais lento). O aparelho ainda precisa confirmar.

| Medida | 6.4.3 | 6.5.0 |
|---|---|---|
| Coletas de lixo andando | 1 a cada ~1,6 s, 55–107 ms | 10 em 32 s, ~11 ms (pior ~27 ms) |
| Objetos vivos no heap do Ruby | 905 mil (790 mil de código) | ~129 mil |
| 1x, lógica ~25 ms/quadro: quadros desenhados por s | 0–5 (até 13 s sem desenhar) | 28–31 |
| 1x, lógica ~35 ms/quadro: quadros/s, maior intervalo | 0 | 19, ~80 ms |
| Turbo 3x sem carga: velocidade, quadros/s | 2,0–2,3x, 28–33 | 2,2x, ~32 |
| Turbo 3x, carga média: velocidade, quadros/s, maior intervalo | 1,4x, 19–20, 86–106 ms | 1,3x, 28–30, ~40 ms |
| Turbo 3x, CPU lenta: quadros/s, maior intervalo | 12–13, 82–130 ms | 28–32, 31–50 ms |
| Turbo 3x, atualizações/s no PC (Win32API e reflexos) | 93 | 104 |

- **Área de código** (`scripts/patch-ruby18-performance.py`, `gc.c`/`parse.y`/`eval.c`): as árvores de
  sintaxe das sections ficam fora do heap coletado; literais viram raízes. Mais: alvo de 400 mil
  vagas livres, varredura em partes, atalhos de Fixnum/String no `eval.c` com guarda de redefinição.
- **Pulo de quadros limitado** e **turbo adaptativo** (`src/display/graphics.cpp`).
- **Núcleos rápidos e Performance Hint** no Android (`src/display/kerberus-cpu.h`).
- **Ports em memória**: `essentials_focus` (pbSameThread) e `essentials_reflections`; cache do
  `Win32API.new`.
- **Menu do jogo**: Salvar agora (legacy18, `93_session.rb`), captura, debug, filtro, histórico de
  saves; canal de pedidos `src/display/kerberus-session.h`; painel em `kerberus-runtime-metrics.h`.
- **Controle físico**: os botões do controle não chegavam ao jogo (o SDL do Android não dá tecla de
  teclado para eles); agora mapeados por jogo. **Escala**: Inteiro/Pixel Perfect/Preencher passam a
  valer no Android (`main.cpp`).

## 4. Testes adicionados

| Suíte | Verificações | Resultado local |
|---|---|---|
| Legacy18 em Ruby 1.8.7 real com os patches RGSS1, de desempenho, Oniguruma e IO (20 arquivos) | 725 | OK |
| `IO#eof?` num Ruby 1.8 com o stdio do Android (sem buffer do `FILE`) | 9 | OK |
| Ciclo de vida nativo (extensão Ruby 1.8 + headers reais) | 7 | OK (e falha com o header antigo) |
| Linha do `SyntaxError` no relatório de erro (C++) | 8 | OK |
| Regressão modern31/Infinite Fusion em Ruby 3.1.6 | 7 + sha256 | OK |
| Java: seleção de runtime | 14 casos | OK |
| Java: estado de compatibilidade e opções | 14 | OK |
| Java: SessionLock e controles (existentes) | – | OK |
| Java: sobras de importação interrompida | 8 | OK |
| Java: backup e restauração dos saves do Uranium | 11 saves + 15 não-saves | OK |
| C++: 7 arquivos × 2 runtimes (clang, C++14), incluindo `bitmap.cpp` | 14 | OK |
| Uranium no motor real (PC, harness de desktop, textura 4096) | jogo do título à evolução + todos os mapas | OK (detalhes na seção 1) |
| Java do app inteiro contra `android.jar` API 33, alvo Java 8 | 58 arquivos (140 classes) | OK |
| Turbo e GC ocioso no motor real (Uranium no PC) | 1x/2x/3x; 12 mensagens + caminhada | OK (números na seção 3) |
| Fonte CJK no motor real (coreano, chinês, japonês, negrito) | antes/depois | OK |
| Sincronia de gerados (header, catálogo, lista de APIs) | 3 | OK |
| 6.5: Salvar agora (`test_session.rb`) e ports de desempenho (`test_performance_ports.rb`) | 20 + 27 | OK |
| 6.5: histórico automático de saves (`SaveHistoryHostTest.java`) | rotação de 10, restaurar e desfazer | OK |
| 6.5: Salvar agora, captura, debug e filtro no motor real (Uranium no PC) | parado/andando/menu; save carregado de novo | OK |
| 6.5: `graphics.cpp` e `binding-mri.cpp` em modo Android, Ruby 1.8 e 3.1 (clang) | 4 | OK |

## 5. Limitações restantes

- Só o Uranium foi aberto no aparelho: na 6.3 até descobrir o bug 15 e na 6.3.1 até o bug 16. Os
  demais estados só mudam com o teste no aparelho.
- **Harness de desktop (6.4.2)**: o Uranium rodou no motor real, mas no PC. A história foi jogada até
  a captura do primeiro Pokémon; o resto do jogo foi coberto pela varredura de mapas (cada mapa aberto
  com seus eventos automáticos, batalhas de treinadores que disparam ao entrar e mensagens), não por
  uma partida completa. Ginásios, Liga e eventos que dependem da ordem da história ainda podem
  mostrar algo novo; o harness fica no projeto para achar esses casos também no PC.
- **Bitmap maior que a GPU** mostrado direto num Sprite/Plane continua erro preciso ("mega surfaces");
  o Uranium não faz isso (tilemap e EliteBattle copiam por partes).
- **Regex**: o Oniguruma só entra para padrões que o GNU recusa. Um padrão que os dois aceitam, mas
  com sentidos diferentes (por exemplo `[[]` ou `&&` dentro de `[...]`), continua com o sentido do
  GNU, como na 6.4.0. Trocar o motor inteiro seria mais fiel ao RGSS1, mas mudaria regex que hoje
  funcionam; só vale a pena se um jogo real mostrar essa diferença.
- **Regra de superclasse do Ruby 1.8.1**: no interpretador da 6.3.3 vale para qualquer classe. Só
  um Ruby 1.8 sem o patch, como o de um build antigo do Termux, cai no fallback por texto, que cobre
  apenas `class Nome < Constante` escritos nos scripts. Outras diferenças entre o 1.8.1 e o 1.8.7 só
  serão tratadas quando um jogo real mostrar o erro.
- **Toques mais curtos que um quadro** (cerca de 16 ms) podem se perder: o estado é lido quando o
  jogo consulta, e o Windows guardaria esse toque no bit 0 do `GetAsyncKeyState`.
- **Roda do mouse** (`InputGetWheelDelta`) ainda depende do `Input.update` nativo.
- **Coleta de lixo**: o limite de 16 MB faz menos coletas, mas cada uma pode demorar um pouco mais
  (no host, pior pausa de 13,5 para 17,2 ms). O relatório `[PERF]` mostra o efeito real no aparelho.
  A coleta em momentos ociosos (6.4.3) muda quando a pausa acontece, não o tamanho dela: com mais de
  um milhão de objetos vivos no Uranium, uma coleta no meio da caminhada continua possível.
- **Turbo**: a música não acelera (os efeitos sonoros, sim, porque o jogo os dispara mais vezes).
  Repetição de teclas nos menus fica 2x/3x mais rápida junto com o jogo.
- **Fonte CJK**: a linha inteira que tem um caractere CJK faltando passa para a fonte reserva (inclusive
  as letras latinas dessa linha). Outros alfabetos (tailandês, árabe…) não têm reserva.
- **MIDI**: a APK não traz o fluidsynth; músicas `.mid` ficam em silêncio, sem travar o jogo.
- **RTP**: jogos clássicos de RMXP que dependem do RTP instalado no Windows não encontram esses
  arquivos; a maioria dos fangames de Pokémon traz tudo na própria pasta.
- **Assinatura da APK**: a 6.4.3 com a chave fixa ainda precisa de uma última desinstalação (as
  anteriores usavam chave de debug aleatória); daí em diante, as APKs instalam por cima. Sem os
  segredos no repositório, o CI volta à chave de debug.
- **HMode7** (mapas 3D do Insurgence): renderizador x86 sem port; a chamada gera erro preciso.
- **Rede** (recursos online do Insurgence): indisponível; o jogo recebe erros Winsock documentados.
- **KleinBitmap** `save_pallete`/`modify_pallete`: formato sem documentação pública; erro preciso.
  `to_retro` e `add_outline` seguem a descrição publicada; limiares exatos podem diferir da DLL.
- **Vídeo**: só OGV/Theora (player nativo do mkxp-z); AVI/MCI de vídeo é pulado com diagnóstico.
- **Pastas especiais do Windows** falham de propósito para os saves continuarem onde estão; se um jogo
  exigir essas pastas para salvar, o WINTRACE mostrará a chamada como limitada.
- **Teclas de trava** (Caps/Num/Scroll): o SDL não expõe o estado de toggle ao mkxp-z.
- `ToUnicode` usa layout US; texto internacional chega pelo port `text_entry` (IME) quando o editor
  do Essentials é reconhecido.
- Caminhos sem diferenciar maiúsculas valem só para ASCII (como o `downcase` do Ruby 1.8).
- `keybd_event`/`SendInput` não injetam teclas.
- Marcos além do BOOT só são gravados automaticamente no legacy18; o modern31 não foi tocado.
- Nenhum backend de DLL (Box64/Hangover/FEX/Winlator) foi integrado; existe só o ponto de extensão.
- Ports com cobertura incompleta aplicam mesmo assim e registram `PORT_COVERAGE` com os nomes que
  faltam; o próximo log real dirá se algum desses nomes é usado.

## 6. Próximo teste no aparelho

1. Instalar a APK da 6.5.0 por cima da 6.4.3 (mesma chave fixa, `apk-signature.txt` com o SHA-256
   `7E:4C:BE:96:…:C7:CD`); jogos e saves continuam. Saves do Uranium ficam na pasta do jogo
   (`Uranium.rxdata`); Arquivos → Exportar backup dos saves guarda uma cópia fora do app.
   Em Performance → Painel detalhado, andar em Nowtoch City em 1x e em turbo 3x e anotar quadros/s,
   "Jogo", "Pior quadro" e a velocidade mostrada no botão do turbo.
2. Em **Logs → Mostrar diagnóstico**, copiar o diagnóstico completo. Ele traz o estado de testes,
   o resumo WINTRACE, os eventos `PORT_*`/`WINBRIDGE`, e `RUBY_EXCEPTION` com script, section, linha,
   classe, mensagem e backtrace.
3. Para depurar um port, criar `.kerberus-options` na pasta do jogo com `wintrace=detail` e
   `dump_scripts=on`.
4. Seguir a ordem Boot → Title → New Game → intro → nome → primeiro mapa → movimento → menus →
   batalha → áudio → save → load → troca de mapas; os marcos aparecem sozinhos no diagnóstico.
5. A cada falha: enviar o bloco `[WINBRIDGE]` ou `[RUBY_EXCEPTION]`. Ele já traz dll, função,
   assinatura, script e linha para decidir entre provider genérico, port ou perfil.
