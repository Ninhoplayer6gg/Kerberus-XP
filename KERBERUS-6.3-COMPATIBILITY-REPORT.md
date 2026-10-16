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
>
> **6.6.1.** No aparelho, a 6.6.0 abriu o Pokémon Insurgence e parou no boot com
> `Zlib::DataError: invalid distance too far back` ao descompactar `Data/Constants.rxdata`. Os mesmos
> dados descompactam no PC e num emulador arm64 com o zlib do Android; a causa no aparelho não foi
> reproduzida fora dele. Correção e diagnóstico: bug 37; junto, dois defeitos latentes do legacy18
> achados na investigação (bugs 38 e 39).
>
> **6.6.2.** Com a 6.6.1 o Insurgence passou do boot no aparelho, e o bloco `[ZLIB_INFLATE]` mostrou a
> causa do bug 37: a `libSDL2_ttf.so` exportava funções da cópia do zlib embutida no FreeType. O jogo
> fechou ao começar a história sem deixar log (bug 40): a 6.6.2 registra crash nativo e o motivo da
> saída do processo.
>
> **6.6.3.** A 6.6.2 confirmou o zlib no aparelho e o relatório `[CRASH]` mostrou o fechamento: o
> `Tone#to_s` do motor legacy18 passava do buffer e o FORTIFY do Android encerrava o jogo (bug 41).
>
> **6.6.4.** A 6.6.3 levou o Insurgence até a história no aparelho. A 6.6.4 acelera o ciclo de cada
> jogo. O diagnóstico começa por um resumo, e um botão copia só o essencial. O `[CRASH]` já sai com o
> nome das funções no celular. O harness do PC ganha AddressSanitizer, e o CI mostra qual biblioteca
> cada função importada usa no Android. Essa auditoria achou na primeira APK um defeito latente: duas
> cópias da libc++ no mesmo processo (bug 42).
>
> **6.7.0.** Música MIDI e RTP, as duas falhas da lista de limitações que barravam os jogos clássicos
> de RPG Maker XP. MIDI: FluidLite (LGPL) compilado do código oficial e o soundfont TimGM6mb (GPL-2)
> no APK, com a opção de usar o seu; um defeito que deixaria reverb e chorus sempre ligados no FluidLite
> foi achado e corrigido antes de chegar ao aparelho (bug 43). RTP: o usuário importa a pasta ou o ZIP
> do RTP e o Kerberus liga cada jogo ao RTP pelo `Game.ini`. Jogos sem MIDI (a maioria dos fangames
> de Pokémon) não carregam o sintetizador no boot. Testado no PC; o aparelho ainda precisa confirmar.
>
> **6.8.0.** Compatibilidade e desempenho máximo. Jogos de **RPG Maker VX** (RGSS2, no legacy18 com
> o Ruby 1.8 do player original) e **VX Ace** (RGSS3, no modern31) passam a ser importados, soltos ou
> empacotados (`.rgss2a`/`.rgss3a`). O WinBridge do legacy18 foi portado para rodar igual no Ruby
> 1.8.7 e no 3.1, e os jogos VX/VX Ace do modern31 ganham as mesmas APIs Win32: antes, a primeira DLL
> do Windows num script de VX Ace pararia o jogo. Todas as bibliotecas do APK ficam alinhadas a
> 16 KB (Android 15+), com a libc++ estática e oculta em cada uma. O cache de caminhos do mkxp-z,
> montado no início de cada jogo, deixa de ser refeito por RTP e o PhysFS deixa de conferir links
> simbólicos componente por componente: 7 a 9 vezes mais rápido no PC com RTP (bugs 44 a 46). Testado
> no PC; nenhum jogo VX/VX Ace foi aberto no aparelho.

## 1. Estado por jogo

| Jogo | Runtime | Última evidência real | Estado 6.3 | O que a 6.3 muda para ele |
|---|---|---|---|---|
| Pokémon Uranium | legacy18 (Ruby 1.8.7 real) | Aparelho, 6.4.2: variáveis do Windows e ports aplicados, carrega até a section 224 e para na tela de título (`unpack` para nil, bug 35). PC, motor real, 6.4.2: título, novo jogo, intro, controles, casa, cutscenes, laboratório, inicial, batalha do rival, captura, evolução, save/backup/load e varredura de todos os mapas sem erro, com textura máxima de 4096 | Controles corrigidos na 6.3.1; regra de superclasse do RGSS1 no interpretador desde a 6.3.3; regex do RGSS1 (Oniguruma) na 6.4.1; bugs 26–34 na 6.4.2; bugs 35–36 na 6.4.3 (título confirmado no harness com o stdio do Android); aguarda teste no aparelho | APIs opcionais declaradas não derrubam mais o boot; nomes com sufixo `A`; 307 providers em vez de 62; ports `rgss_linker`/`fmodex_audio` preservados e completados (volume das opções, fade-in real); `klein_bitmap`, `gif_dll`, `text_entry` (teclado Android), `bitmap_memory`; correção genérica do `LargePlane`; caminhos estilo Windows |
| Pokémon Insurgence | legacy18 | Aparelho, 6.6.2: zlib correto (`[ZLIB] ... ok`), tela de título; ao começar a história, SIGABRT do FORTIFY em `Tone#to_s` (bug 41, corrigido na 6.6.3) | Motor real: 798/832 mapas, batalhas (captura, Mega, Armored, Delta), evolução, salvar/carregar, créditos | H-Mode7 (mapas 3D) nativo e idêntico à DLL; recursos online indisponíveis (erro de rede como no Windows sem conexão); história completa do começo ao fim não jogada |
| Pokémon Xenoverse | legacy18 | Nenhuma | Não testado | Port do Zeus Video Player (OGV nativo), KleinBitmap, MCI mapeado para o áudio nativo |
| Pokémon Infinite Fusion | modern31 (Ruby 3.1) | Entrava no jogo (6.1.x) | Regressão automatizada OK; aparelho pendente | Nada no comportamento do jogo. Bootstrap moderno byte a byte idêntico (sha256 fixado). No C++ do modern31 entraram só dois registros de diagnóstico (marco BOOT e relatório de exceção). Três proteções independentes na seleção de runtime |

A seleção de runtime da 6.3 foi testada com casos para os quatro jogos, RGSS1 comum, RGSS3,
jogos com `mkxp.json`, com `x64-msvcrt-ruby310.dll`, com scripts externos do Essentials v19+ e
com o override `.kerberus-runtime`.

Desde a 6.8.0, jogos de RPG Maker VX (legacy18) e VX Ace (modern31 com o WinBridge) são importados e
têm runtime escolhido pelos mesmos testes (`RuntimeSelectorHostTest`, `VxImportHostTest`). Nenhum
jogo VX ou VX Ace foi aberto no aparelho ainda.

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
| 37 | Insurgence no aparelho (6.6.0): `Script 'PokemonSystem' line 131: Zlib::DataError (invalid distance too far back)` no boot | zlib / bibliotecas do APK | A `libSDL2_ttf.so` exporta `inflateInit_`, `zlibVersion`, `zError`, `crc32_z`, `get_crc_table` e outras da cópia do zlib 1.2.12 embutida no FreeType (compilada com `Z_SOLO`). O motor carrega essa biblioteca antes do `libz.so`, então o `inflateInit_` vinha da cópia e o `inflate` do zlib do sistema: duas implementações no mesmo fluxo. Confirmado pelo log da 6.6.1 (entrada com o CRC certo; `inflateInit` do C com `Z_STREAM_ERROR`, o retorno do `Z_SOLO`) e compilando o mesmo `ftgzip.c` | 6.6.1: depois de um `Zlib::DataError`, inflate em C numa chamada só, entregue só com o Adler-32 conferido (proteção mantida). 6.6.2: FreeType com o zlib do sistema (`FT_CONFIG_OPTION_SYSTEM_ZLIB`, `scripts/patch-sdl-ttf-system-zlib.py` no `get_deps.sh`); auditoria do APK proíbe funções do zlib nas bibliotecas que o motor carrega; linha `[ZLIB]` no boot | `test_zlib_guard.rb` (29), `zlib_inflate_test.cpp` (28), auditoria testada com a biblioteca antiga e a corrigida |
| 38 | (latente) `load_data` podia entregar ao `Marshal` um buffer só parcialmente lido | Filesystem | `fileIntRead` do legacy18 ignorava o retorno de `SDL_RWread` | Lê até completar; leitura do arquivo inteiro que ainda falte vira `IOError` "leitura incompleta"; leitura com tamanho pedido devolve o que foi lido | checagem C++ dos dois runtimes |
| 39 | (latente) Variáveis da função que inicia o Ruby e roda os scripts não eram vistas pelo coletor de lixo do Ruby 1.8 | Ruby 1.8 | O legacy18 chamava `ruby_init()` sem `RUBY_INIT_STACK`; a base da pilha do coletor virava uma variável de dentro do `ruby_init` | `RUBY_INIT_STACK` antes do `ruby_init()`, como no motor moderno | checagem C++; boot do Insurgence no harness |
| 40 | Insurgence no aparelho (6.6.1): o jogo fechou ao começar a história e voltou ao Kerberus, sem nenhuma linha no log | Diagnóstico | Nenhum registro de crash nativo nem do motivo com que o Android encerra o processo | Bloco `[CRASH]` no legacy18 (sinal, endereço, thread, backtrace, linha Ruby, memória; o sinal segue para o tratamento anterior) e linha `[SAIDA]` no launcher com o motivo do Android 11+ (`LOW_MEMORY`, `CRASH_NATIVE`…). A causa em si ainda depende do próximo log | `crash_report_test.cpp` (15, x86 e arm64), `RuntimeExitHostTest` (15), SIGSEGV real no harness |
| 41 | Insurgence no aparelho (6.6.2): `[CRASH] sinal 6 (SIGABRT)` vindo de `__vsprintf_chk` ao começar a história | C++ (legacy18) / FORTIFY | `Color`, `Tone` e `Rect#to_s`/`inspect` escreviam `"(%f, %f, %f, %f)"` num `char[50]`; `Tone.new(-255,-255,-255,255)` precisa de 52 bytes. O bionic confere `sprintf` com o tamanho do buffer e aborta; o glibc, em C++ com clang, não confere, e o harness não via. Endereços simbolizados no CI: `ToneStringify` (`etc-binding.cpp:184`) chamado por `rb_call0` | Buffer de 128 com `snprintf`, mesmo texto do RGSS. Harness com `_FORTIFY_SOURCE=2` e a conferência do bionic para `sprintf`/`snprintf` em C++ (`tools/game-harness/fortify-bionic.h`); build-id no `[CRASH]` | `etc_stringify_test.rb` (12; aborta com o código antigo), reprodução e correção no harness |
| 42 | (latente) O motor usava duas cópias da libc++ ao mesmo tempo | Bibliotecas do APK | A `libopenal.so` levava uma cópia própria da libc++ e a exportava. O Makefile das dependências pedia `CMAKE_ANDROID_STL_TYPE=c++_shared`, mas o toolchain do NDK lê `ANDROID_STL` (padrão `c++_static`). Como vem antes da `libc++_shared.so` na ordem de busca, o `libmkxp-z18.so` e o `libmkxp-z31.so` ligavam parte das funções da libc++ (strings, streams, exceções) a essa cópia e o resto à `libc++_shared.so`. Achado pela auditoria de ligação de símbolos na primeira APK real (165 DUPLICADO; nenhuma SOMBRA) | A `libopenal.so` era uma cópia pré-compilada guardada no projeto, que o CI nunca recompilava. O CI passa a compilar a OpenAL Soft 1.23.0 do código-fonte com `-DANDROID_STL=c++_shared`. Sobra 1 DUPLICADO inofensivo (`__emutls_get_address`, auxiliar do compilador, na `libruby.so` e na `libc++_shared.so`). A auditoria da APK confere que a `libopenal.so` usa a `libc++_shared.so` e reprova SOMBRA, AUSENTE e SEM_DEFINICAO | `symbol_binding_test.sh` (16); auditoria da APK |
| 44 | Início de cada jogo lento em jogos grandes, pior com RTP: o cache de caminhos do mkxp-z levava 5,3 s no PC para 155 mil arquivos com um RTP e 7,9 s com dois | Desempenho / filesystem | O `PHYSFS_enumerate` entrega um nome uma vez por montagem que o tem, e listar uma pasta já junta todas. O jogo, o pacote RGSSAD e cada RTP têm `Graphics/` e `Audio/`, e o `cacheEnumCB` do mkxp-z descia na árvore inteira de novo para cada uma. Somava-se a isso a verificação de links simbólicos do PhysFS: cerca de 6 `stat` por arquivo | O `cacheEnumCB` pula o mesmo caminho exato já visto, antes do `stat` (o cache sai idêntico). No Android o PhysFS permite links (`KERBERUS_PHYSFS_SYMLINKS=0` religa a verificação): os jogos e RTPs são extraídos pelo launcher, que não cria links, e um script já lê qualquer arquivo do app pelo Ruby. Medido no PC: 5,3 s → 0,73 s (1 RTP), 7,9 s → 0,85 s (2 RTPs); cada arquivo aberto no jogo também deixa de pagar a verificação | `pathcache_test.cpp` (o `filesystem.cpp` real; o código antigo faz 4 vezes mais `PHYSFS_stat` e é reprovado) |
| 45 | (latente) Num aparelho com páginas de 16 KB (Android 15+) o motor não carregaria | Bibliotecas do APK | A `libc++_shared.so` pré-compilada do NDK r23 é alinhada a 4 KB, e todas as bibliotecas do jogo dependiam dela. A `libopenal.so` também saía a 4 KB: no CMake, o toolchain do NDK define `CMAKE_SHARED_LINKER_FLAGS` antes de o CMake ler o `LDFLAGS` | Cada biblioteca leva a libc++ que usa, estática e oculta (`--exclude-libs`); nenhum objeto ou exceção C++ cruza bibliotecas. Flags de 16 KB explícitas no CMake da OpenAL. A auditoria da APK reprova biblioteca abaixo de 16 KB, dependência de libc++ compartilhada e runtime C++ exportado | auditoria da APK no CI; mecanismo do `--exclude-libs` testado no host |
| 46 | (latente) Um jogo de VX cujo arquivo de scripts tinha outra extensão (`Scripts.dat`) rodaria como XP | Seleção de engine | O mkxp-z deduz a versão RGSS só pela extensão do `Scripts=` do `Game.ini` | O launcher passa a engine que leu do `Game.ini` (extensão, senão a DLL do `Library=`) em `KERBERUS_RGSS_VERSION`; o `mkxp.json` do jogo continua vencendo | `VxImportHostTest`; `fluidlite_midi_test.cpp` (`kerberusEnvRgss`) |
| 43 | (latente, achado antes do aparelho) Com o FluidLite, reverb e chorus ficariam sempre ligados, qualquer que fosse `midiReverb`/`midiChorus` | MIDI | O mkxp-z grava `synth.reverb.active` e `synth.chorus.active` com `settings_setint` (API do FluidSynth 2.x). O FluidLite segue a API 1.x: registra essas opções como texto (`"yes"`/`"no"`), recusa o inteiro e fica no padrão `"yes"`. A opção do jogo e a do Kerberus não teriam efeito | `kerberusMidiFlag` (`src/audio/kerberus-midi.h`) grava o inteiro e o texto; a chamada do tipo errado falha sem efeito, nas duas bibliotecas | `fluidlite_midi_test.cpp`: só `setint` deixa `"yes"` e o som muda; o `SharedMidiState` corrigido soa igual a efeitos desligados |

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

### 6.6.0

- **H-Mode7 nativo** (`binding/kerberus/hm7-core.h`, `hm7-native.h`): as 8 funções do `MGC_Hmode7.dll`
  reescritas e conferidas byte a byte contra a DLL original num emulador x86, em casos gerados e em
  dados reais do Insurgence (preparação do mapa 689 e quadros da introdução). O quadro é desenhado em
  até 4 núcleos, com o mesmo resultado de 1 núcleo.
- Ports em memória `hm7_surface` (poses dos personagens copiadas na CPU) e `hm7_rows` (cor de fade
  aplicada também na cópia da CPU). Sem eles, a GPU era lida a cada quadro.
- Ferramenta local `tools/hm7-reference` (emulador da DLL; a DLL nunca entra no repositório).
- Achados da varredura de mapas do Insurgence no motor real:
  - **Segunda batalha fechava o jogo** (Essentials com a transição do SpriteResizer, que abre
    `ENV["TEMP"] + "\tempscreen.bmp"`): a nova tentativa com o caminho relativo só acontecia na
    primeira vez. Agora acontece sempre; o cache de caminhos é recarregado uma vez por arquivo.
  - **Créditos em GPUs de 4096**: `draw_text` em bitmap maior que a textura máxima (software, mesma
    fórmula do shader) e Sprite desse bitmap pelas páginas de GPU. Comparado no harness com o
    caminho normal da GPU.
  - **Teclado do Android na tela de nome**: o port `text_entry` não era aplicado (scripts com CRLF).
  - **Enter/Apagar do teclado do Android**: tecla apertada e solta entre dois quadros conta como
    apertada até o fim do quadro seguinte (só legacy18).
  - **Compilação**: includes dos headers do Kerberus como no NDK; `tools/check-native-includes.py`
    no preflight e a checagem de C++ do PC com os diretórios do NDK.
  - **Erros de rede** (port `winsock_errno`): o script Sockets do Essentials converte o código
    Winsock em `Errno` com a numeração do Ruby do Windows; sem rede o jogo recebe
    `Errno::ENETDOWN` em vez de `TypeError`.
- Insurgence no motor real (harness, textura 4096): 798 de 832 mapas sem erro por teleporte; os 34
  restantes dependem do progresso da história ou do servidor online e falham igual no Windows.
  Captura com apelido, Mega Evolução, Armored, Delta, evolução, salvar e carregar conferidos;
  batalhas de líder (Orion), Taen e Campeão (Reukra) sem erro do motor.

### 6.6.1

- **Zlib no aparelho** (bug 37): `86_zlib.rb` envolve `Zlib::Inflate.inflate`; depois de um
  `Zlib::DataError`, os bytes são descompactados de novo em C (`KerberusNative.inflate`,
  `binding/kerberus/zlib-inflate.h`) em pedaços de 1024 bytes e numa chamada só. Só a chamada
  única, terminada com o Adler-32 conferido pelo zlib, é entregue ao jogo; senão o erro original
  continua. O bloco `[ZLIB_INFLATE]` do log traz o necessário para fechar a causa (CRC-32 da
  entrada, versão do zlib do aparelho, resultado de cada tentativa).
- **Leitura completa no `load_data`** (bug 38) e **base da pilha do coletor do Ruby 1.8** (bug 39).
- Referência do `Data/Constants.rxdata` da cópia testada: 22619 bytes, CRC-32 `7980f5c0`;
  PBTypes 273 `e9493363`, PBAbilities 2026 `3ca3b77d`, PBMoves 5518 `29514d56`, PBItems 6000
  `a914e047`, PBSpecies 6428 `d7eafd8b`, PBTrainers 2235 `06205795` (tamanho comprimido, CRC-32).

### 6.6.2

- **Causa do bug 37**: FreeType do SDL2_ttf com o zlib do sistema; a `libSDL2_ttf.so` não exporta
  mais funções do zlib, e o CI reprova o APK se alguma biblioteca carregada pelo motor exportar.
  Linha `[ZLIB]` no boot do legacy18 com o zlib em uso e um inflate em partes (C e Ruby).
- **Relatório de crash** (bug 40): `binding/kerberus/crash-report.h` no legacy18 e
  `RuntimeExit`/`RuntimeReport.describeExit` no launcher.
- Harness: novo jogo no Insurgence até o mapa 689 (introdução em H-Mode7); memória de ~260 MB para
  ~570 MB nesse momento.

### 6.6.3

- **Bug 41**: `Color`/`Tone`/`Rect#to_s` do legacy18 com buffer de 128 e `snprintf`.
- Harness compilado como o ndk-build (`-D_FORTIFY_SOURCE=2`) e com a conferência de `sprintf`/
  `snprintf` do bionic em C++; o `Tone#to_s` antigo aborta no PC como no celular.
- `[CRASH]` com o build-id do `libmkxp-z18.so`; passo do CI que simboliza `crash-offsets.txt`; APK e
  artefato com a versão no nome.

### 6.6.4

- **Resumo no topo do diagnóstico** (`ReportSummary.java`): `LEGACY18_READY`, `[ZLIB]`, seções
  carregadas, último `[CRASH]`, `[SAIDA]`, último `[RUBY_EXCEPTION]`/`[Mensagem]`,
  `WINBRIDGE_UNAVAILABLE` e `[ZLIB_INFLATE]`. As linhas `[Script]` seguidas aparecem agrupadas.
- **"Copiar resumo para enviar"**: copia o resumo e as 60 últimas linhas do relatório.
- **Símbolos no celular**: o CI grava `assets/kerberus-symbols/libmkxp-z18.txt` com os símbolos
  do mesmo build (compila primeiro as bibliotecas, depois a APK). O launcher dá nome aos frames do
  `libmkxp-z18.so` de um `[CRASH]` quando o build-id confere. A auditoria da APK confere que o mapa
  é do `libmkxp-z18.so` da APK.
- **Harness com AddressSanitizer** (`SANITIZE=address`): leitura ou escrita fora de buffer no
  mkxp-z/Kerberus para o jogo no PC, com o lugar exato. Até este build, sob ASan:
  Insurgence com 493 de 832 mapas visitados e Uranium com os 198 mapas, sem nenhum relatório do ASan.
- **Ligação de símbolos no Android** (`scripts/audit-symbol-binding.py`, passo de auditoria do CI):
  para cada função importada que mais de uma biblioteca define, mostra qual o linker do Android
  escolhe. Segue a ordem de `MainActivity.getLibraries` e as bibliotecas do sistema da API 23 do NDK.
  Na primeira APK real: nenhuma SOMBRA, AUSENTE ou SEM_DEFINICAO, que agora reprovam o build. Os
  165 DUPLICADO eram a libc++ embutida na OpenAL (bug 42, corrigido).

### 6.7.0

- **Música MIDI** (os dois motores): o carregador do mkxp-z (`fluid-fun.cpp`) procurava no Android
  uma `libfluidsynth.so.3` que não existe, e o MIDI ficava desligado. Agora carrega a
  `libfluidlite.so`: FluidLite 1.2.2 (LGPL-2.1), commit fixado `4a01cf1`, compilado pelo `ndk-build`
  (`app/jni/fluidlite.mk`) como biblioteca própria, com SF2 e SF3. O caminho absoluto vem do launcher
  (`KERBERUS_FLUID_LIB`).
- **Soundfont**: TimGM6mb.sf2 (GPL-2, 5,7 MB) no APK, conferido por sha256 no preflight e na auditoria
  da APK. O FluidLite abre o soundfont por caminho, então o launcher o copia uma vez para `files/midi`
  (cópia atômica, refeita se o arquivo estiver incompleto). Configurações → **Música MIDI**: soundfont
  padrão ou um seu (.sf2/.sf3), reverb e chorus (ligados por padrão, como o MIDI do Windows). Medido no
  PC (12 vozes, melhor de 5): o reverb soma cerca de 30% à CPU do sintetizador e o chorus mais 7%,
  cerca de 0,7% de um núcleo ao todo; dá para desligar num celular lento. Um soundfont do `mkxp.json`
  do jogo continua tendo prioridade.
- **Sintetizador só quando o jogo tem MIDI**: no RGSS1/2 o mkxp-z cria dois sintetizadores ao abrir o
  jogo e cada um carrega o soundfont inteiro. O launcher procura `.mid` em `Audio/` e vê se o jogo usa
  um RTP instalado (`KERBERUS_MIDI_PRELOAD`). Sem MIDI, nada é carregado no boot; uma música MIDI que
  apareça depois cria o sintetizador na hora, como no RGSS3.
- **RTP**: Configurações → **RTP · jogos clássicos** importa a pasta do RTP (copiada do PC:
  `Common Files\Enterbrain\RGSS\Standard`) ou um ZIP dela. O Kerberus acha a pasta que tem
  `Graphics` e `Audio` (também dentro de `RGSS/Standard/`) e instala em `files/rtp/<Nome>` numa
  transação: um RTP com o mesmo nome só é trocado quando o novo está completo. Os jogos são ligados
  pelo `Game.ini` (`RTP1`–`RTP3` no XP, `RTP` no VX/VX Ace), sem diferenciar maiúsculas. O motor monta
  os RTPs depois da pasta do jogo (`KERBERUS_RTP_PATHS`), como o RGSS: um arquivo do jogo vence o do
  RTP. Nenhum RTP vem com o Kerberus.
- **Diagnóstico**: linhas `[BOOT] RTP: ...` (instalado/faltando) e `[BOOT] MIDI: ...` no início de
  cada jogo; `[MIDI]` do motor (biblioteca carregada, soundfont, reverb/chorus e falhas). O resumo do
  diagnóstico mostra um RTP faltando e um MIDI que ficou mudo.
- As variáveis do launcher para o motor são sempre definidas (vazias quando não há valor), para um jogo
  não herdar o RTP ou o soundfont de outro no mesmo processo.

### 6.8.0

- **RPG Maker VX (RGSS2)**, motor legacy18: importação com `Data/Scripts.rvdata` solto ou `.rgss2a`
  (RGSSAD v1), cabeçalho conferido antes de extrair; um pacote de outra engine que a do `Game.ini` é
  recusado. Roda com o Ruby 1.8 do player original do VX e recebe tudo do legacy18 (WinBridge, ports,
  regras do Ruby 1.8.1, pulo de quadros). A DLL do player (`RGSS202E.dll`) responde como a do XP. Um
  VX com `mkxp.json` vai para o modern31.
- **RPG Maker VX Ace (RGSS3)**, motor modern31: importação com `Scripts.rvdata2` solto ou `.rgss3a`
  (RGSSAD v3, lido como o `rgssad.cpp` do mkxp-z). A análise de compatibilidade lê os scripts dos
  dois formatos.
- **WinBridge no Ruby 3.1**: os arquivos do legacy18 que formam o WinBridge (núcleo, assinaturas,
  memória, gravador WINTRACE, os provedores de `kernel32`, `user32`, `advapi32`, `shell32`, `winmm`,
  `gdi32`, `msvcrt`, XInput, Winsock, INI, registro virtual, entrada ao vivo, áudio, caminhos e
  variáveis do Windows, gancho de quadro e `[PERF]`) viram também o pacote
  `kerberus-winbridge31-compat.h` (`tools/gen-legacy18-header.py`), avaliado pelo `binding-mri.cpp`
  do modern31 para RGSS2/RGSS3, depois do bootstrap Pokémon. A ativação própria
  (`kerberus/winbridge31/99_activate.rb`) liga WinBridge, caminhos, variáveis, gravador e gancho de
  quadro; Ruby 1.8.1, IO, zlib, ports e "salvar agora" continuam só no legacy18. Se o pacote falhar
  ao carregar, o erro vai para o relatório e o jogo segue sem a ponte. As funções nativas que o
  WinBridge usa (`KerberusNative.key_down?`, `mouse_position`, `pad_buttons`, `pad_axis`,
  `controller_rumble`, `uptime_ms`, `monotonic_ns`, `disk_space`) passam a existir nos dois motores.
- **Portabilidade 1.8/3.1**: no Ruby 3.1, `String#[]` e `length` contam caracteres e cada texto tem
  codificação. A camada `Bytes` (`00_core.rb`) trata os buffers Win32 como bytes nas duas versões; os
  textos entram como bytes (argumentos do jogo, nomes de arquivos, título, ambiente) e saem no buffer
  do jogo com a codificação dele, ou como UTF-8 válido em logs e mensagens. Os wrappers de
  `File`/`Dir`/`IO` repassam argumentos nomeados (`ruby2_keywords`), e o `Dir.glob` sem diferenciar
  maiúsculas usa classes `[aA]`, que valem nas duas versões (o Ruby 3.1 ignora `FNM_CASEFOLD` no
  Linux). No modern31 os nomes com acento também são comparados sem diferenciar maiúsculas.
- **Ruby 1.9 do RGSS3**: `Hash#index` e `Thread.exclusive`, que o Ruby 3 removeu, são adicionados só
  quando faltam. `MessageBox` usa o `msgbox` do RGSS3; os marcos leem a cena do `SceneManager`.
- **Versão RGSS para o motor**: `KERBERUS_RGSS_VERSION` (bug 46).
- **16 KB**: bug 45.
- **Desempenho**: bug 44. Também medidos e descartados por não ganharem nada: ajuste do GC do Ruby
  3.1 por variáveis `RUBY_GC_*` (14 coletas viram 6 no boot de dados no estilo Essentials, mas o tempo
  de coleta fica nos mesmos ~100 ms) e libpng no lugar do `stb_image` do SDL_image (37,3 ms contra
  37,6 ms num tileset de 256×8192).
- **Interface**: textos de importação e do RTP citam XP, VX e VX Ace (RTP em `RGSS\Standard`,
  `RGSS2\RPGVX`, `RGSS3\RPGVXAce`); preset de controles "RPG Maker XP / VX / VX Ace".

## 4. Testes adicionados

| Suíte | Verificações | Resultado local |
|---|---|---|
| Legacy18 em Ruby 1.8.7 real com os patches RGSS1, de desempenho, Oniguruma e IO (24 arquivos) | 832 | OK |
| `IO#eof?` num Ruby 1.8 com o stdio do Android (sem buffer do `FILE`) | 9 | OK |
| Ciclo de vida nativo (extensão Ruby 1.8 + headers reais) | 7 | OK (e falha com o header antigo) |
| Linha do `SyntaxError` no relatório de erro (C++) | 8 | OK |
| Regressão modern31/Infinite Fusion em Ruby 3.1.6 | 7 + sha256 | OK |
| Java: seleção de runtime | 14 casos | OK |
| Java: estado de compatibilidade e opções | 14 | OK |
| Java: SessionLock e controles (existentes) | – | OK |
| Java: sobras de importação interrompida | 8 | OK |
| Java: backup e restauração dos saves do Uranium | 11 saves + 15 não-saves | OK |
| C++: 16 arquivos × 2 runtimes (clang, C++14, diretórios de include do NDK), incluindo `filesystem-binding.cpp`, `bitmap.cpp`, `sprite.cpp`, `eventthread.cpp`, `input.cpp`, `graphics.cpp` e, na 6.7.0, `config.cpp`, `sharedstate.cpp`, `fluid-fun.cpp`, `alstream.cpp` | 32 | OK |
| Includes dos headers nativos do Kerberus contra os diretórios do NDK (`check-native-includes.py`) | 17 | OK (e falha com o include que quebrou o APK) |
| Uranium no motor real (PC, harness de desktop, textura 4096) | jogo do título à evolução + todos os mapas | OK (detalhes na seção 1) |
| Java do app inteiro contra `android.jar` API 33, alvo Java 8 | 58 arquivos (140 classes) | OK |
| Turbo e GC ocioso no motor real (Uranium no PC) | 1x/2x/3x; 12 mensagens + caminhada | OK (números na seção 3) |
| Fonte CJK no motor real (coreano, chinês, japonês, negrito) | antes/depois | OK |
| Sincronia de gerados (header, catálogo, lista de APIs) | 3 | OK |
| 6.5: Salvar agora (`test_session.rb`) e ports de desempenho (`test_performance_ports.rb`) | 20 + 27 | OK |
| 6.5: histórico automático de saves (`SaveHistoryHostTest.java`) | rotação de 10, restaurar e desfazer | OK |
| 6.5: Salvar agora, captura, debug e filtro no motor real (Uranium no PC) | parado/andando/menu; save carregado de novo | OK |
| 6.5: `graphics.cpp` e `binding-mri.cpp` em modo Android, Ruby 1.8 e 3.1 (clang) | 4 | OK |
| 6.6.1: proteção do `Zlib::Inflate.inflate` (`test_zlib_guard.rb`) | 24 | OK |
| 6.6.1: inflate em C em pedaços e em chamada única (`zlib_inflate_test.cpp`) | 28 | OK |
| 6.6.1: Insurgence no motor real: 6 blocos do `Constants.rxdata` iguais na extensão e no C; falha do aparelho simulada | 6 + 1 | OK |
| 6.6.2: relatório de crash nativo (`crash_report_test.cpp`; SIGSEGV e SIGABRT reais, x86 e arm64) | 15 | OK |
| 6.6.2: motivo da saída do processo (`RuntimeExitHostTest.java`) | 15 | OK |
| 6.6.2: auditoria de símbolos do zlib (biblioteca antiga reprova, corrigida passa) | 2 | OK |
| 6.6.2: motor real (harness): linha `[ZLIB]` no boot, novo jogo até o mapa 689, SIGSEGV na thread do Ruby gera `[CRASH]` | 3 | OK |
| 6.6.3: `Color`/`Tone`/`Rect#to_s` com FORTIFY (`etc_stringify_test.rb`; o código antigo aborta) | 12 | OK |
| 6.6.3: harness com FORTIFY: `Tone#to_s` antigo aborta, corrigido devolve o texto; novo jogo até o mapa 79; créditos | 4 | OK |
| 6.6.4: resumo do diagnóstico e nomes do `[CRASH]` pelo mapa de símbolos (`ReportSummaryHostTest.java`; outro build ou sem mapa: sem nomes) | 18 | OK |
| 6.6.4: ligação de símbolos no Android (`symbol_binding_test.sh`; o caso do zlib da 6.6.2 aparece como SOMBRA e reprova no modo estrito) | 16 | OK |
| 6.6.4: motor real com AddressSanitizer (Insurgence e Uranium, varredura de mapas) | Uranium: 198 de 198 mapas; Insurgence: 493 de 832 (varredura em andamento) | OK (sem relatório do ASan) |
| 6.7.0: MIDI de ponta a ponta no host (`fluidlite_midi_test.cpp`): carregador do motor, `SharedMidiState`, TimGM6mb, defeito do reverb/chorus reproduzido e corrigido pelo som, bateria, soundfont inválido, variáveis do launcher, biblioteca ausente | 47 | OK |
| 6.7.0: `libfluidlite.so` do host, mesma lista de fontes e headers do `fluidlite.mk`, exporta as 16 funções do mkxp-z | 16 | OK |
| 6.7.0: RTP (`RtpLibraryHostTest.java`): nomes do `Game.ini`, ZIP com `RGSS/Standard`, pasta no topo, troca transacional, ZIP inválido, path traversal, dois RTPs, sobras de instalação | 38 | OK |
| 6.7.0: soundfont, opções e pré-carga (`MidiSupportHostTest.java`) | 33 | OK |
| 6.7.0: resumo com RTP faltando e MIDI mudo (`ReportSummaryHostTest.java`) | 2 | OK |
| 6.7.0: classes Android novas (`MidiSettings`, `RtpSettings`) com stubs da API usada, alvo Java 8, `-Xlint:all` (o `android.jar` não pôde ser baixado neste ambiente; o Gradle do CI compila o app inteiro) | 9 arquivos | OK |
| 6.8.0: importação de VX e VX Ace (`VxImportHostTest.java`): pacotes RGSSAD v1/v3 montados como o RPG Maker grava, scripts soltos e empacotados, scripts renomeados (bug 46), runtime escolhido, pacotes recusados | 31 | OK |
| 6.8.0: seleção de runtime com VX/VX Ace (`RuntimeSelectorHostTest.java`) | +5 casos | OK |
| 6.8.0: WinBridge no Ruby 3.1 contra o pacote do modern31: 9 testes do legacy18 (áudio, chamadas adiadas, APIs comuns, caminhos, INI/registro, entrada ao vivo e memória, gravador, assinaturas) | 434 | OK |
| 6.8.0: VX Ace no modern31 (`tests/winbridge31/test_vxace.rb`): ativação, Ruby 1.9, acentos em INI/buffers/pastas, UTF-16, `msgbox`, argumentos nomeados em `File`/`Dir`, marcos pelo `SceneManager`, `[PERF]` | 38 | OK |
| 6.8.0: ordem do motor (`test_engine_order.rb`): bootstrap Pokémon inalterado inativo no VX Ace, depois o WinBridge e os scripts do jogo | 7 | OK |
| 6.8.0: cache de caminhos (`pathcache_test.cpp`): `filesystem.cpp`, `filesystemImpl.cpp` e `rgssad.cpp` reais sobre o PhysFS, jogo + RGSSAD + 2 RTPs, um `PHYSFS_stat` por caminho, nomes do jogo antes do RTP, links simbólicos | 16 (o código antigo reprova) | OK |
| 6.8.0: legacy18 em Ruby 1.8.7 depois da portabilidade (24 arquivos; mais 3 aliases de DLL e a versão do bootstrap) | 836 | OK |
| 6.8.0: variáveis do launcher (`kerberusEnvRgss`) no teste nativo de MIDI | +4 | OK |

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
- **MIDI** (6.7.0): toca com o FluidLite e o TimGM6mb, que soam diferente do sintetizador do Windows
  (Microsoft GS Wavetable/DirectMusic). Para um som mais próximo, importe outro soundfont em
  Configurações → Música MIDI. Síntese, efeitos e bateria foram testados no PC, com a biblioteca
  compilada do mesmo código; a primeira música MIDI no aparelho ainda precisa ser ouvida. O ganho
  1.0 do mkxp-z foi mantido: num arranjo típico, 0,016% das amostras chegam ao limite.
- **RTP** (6.7.0): precisa ser importado pelo usuário (pasta copiada do PC ou ZIP); o instalador
  oficial (.exe) ainda não é lido direto no celular.
- **Assinatura da APK**: a 6.4.3 com a chave fixa ainda precisa de uma última desinstalação (as
  anteriores usavam chave de debug aleatória); daí em diante, as APKs instalam por cima. Sem os
  segredos no repositório, o CI volta à chave de debug.
- **HMode7** (mapas 3D do Insurgence): nativo desde a 6.6.0 (idêntico à DLL). O jogo guarda os dados
  do último mapa HM7 (até 245 MB) enquanto não entra em outro mapa HM7, como no Windows.
- **Rede** (recursos online do Insurgence): indisponível; o jogo recebe erros Winsock documentados.
- **Zlib no aparelho** (bug 37): a causa foi corrigida na 6.6.2; a recuperação da 6.6.1 continua só
  em `Zlib::Inflate.inflate`. A linha `[ZLIB]` do próximo log confirma a correção no aparelho.
- **Relatório de crash** (bug 40): só no motor legacy18. Crash por estouro da pilha de uma thread
  sem pilha alternativa pode não chegar ao log; o motivo `[SAIDA]` depende do Android 11 ou mais novo.
- **KleinBitmap** `save_pallete`/`modify_pallete`: formato sem documentação pública; erro preciso.
  `to_retro` e `add_outline` seguem a descrição publicada; limiares exatos podem diferir da DLL.
- **Vídeo**: só OGV/Theora (player nativo do mkxp-z); AVI/MCI de vídeo é pulado com diagnóstico.
- **Pastas especiais do Windows** falham de propósito para os saves continuarem onde estão; se um jogo
  exigir essas pastas para salvar, o WINTRACE mostrará a chamada como limitada.
- **Teclas de trava** (Caps/Num/Scroll): o SDL não expõe o estado de toggle ao mkxp-z.
- `ToUnicode` usa layout US; texto internacional chega pelo port `text_entry` (IME) quando o editor
  do Essentials é reconhecido.
- Caminhos sem diferenciar maiúsculas valem só para ASCII no legacy18 (como o `downcase` do Ruby 1.8);
  no modern31 (VX/VX Ace) as letras acentuadas também. O `Dir.glob` com letra acentuada em outra
  caixa no padrão (por exemplo `CANÇÃO`) ainda não acha `canção`.
- `keybd_event`/`SendInput` não injetam teclas.
- Marcos além do BOOT são gravados no legacy18 e, desde a 6.8.0, nos jogos VX/VX Ace do modern31;
  jogos de XP no modern31 (Infinite Fusion) seguem só com o BOOT (bootstrap inalterado).
- **VX e VX Ace** (6.8.0): nenhum jogo foi aberto no aparelho. No VX Ace, recursos do Ruby 1.9 que o
  Ruby 3 removeu e que não dá para devolver de fora (por exemplo `Proc.new` sem bloco) ainda param o
  script com o erro do Ruby; `Hash#index` e `Thread.exclusive` foram devolvidos. DLLs próprias de um
  jogo (Steam, plugins) seguem o mesmo caminho do XP: declarar funciona, chamar dá
  `WINBRIDGE_UNAVAILABLE` com o nome a portar.
- **16 KB** (6.8.0): alinhamento e libc++ conferidos pela auditoria da APK no CI, não num aparelho com
  páginas de 16 KB. A libc++ estática em cada biblioteca aumenta um pouco o tamanho da APK.
- **Cache de caminhos** (6.8.0): ganho medido no PC; no aparelho o `[BOOT]` e o `[PERF]` mostram o
  tempo real. Com links simbólicos permitidos, um link dentro da pasta do jogo passa a ser seguido
  (o launcher nunca cria links).
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
6. **Insurgence e próximos jogos (6.6.4)**: instalar o artefato `Kerberus-XP-6.6.4-arm64-debug` por
   cima e conferir "KERBERUS XP 6.6.4" no topo do diagnóstico. Se algo der errado, tocar em
   **Copiar resumo para enviar** e mandar o texto inteiro. Ele traz o `[CRASH]` com o nome das
   funções, a `[SAIDA]` e o último erro Ruby. O diagnóstico completo só é necessário se for pedido.
7. **MIDI e RTP (6.7.0)**: instalar `Kerberus-XP-6.7.0-arm64-debug` por cima. Num jogo com música
   `.mid` (ou num RMXP clássico, com o RTP importado em Configurações → RTP · jogos clássicos),
   confirmar que a música toca. No diagnóstico devem aparecer `[MIDI] sintetizador carregado`,
   `[MIDI] soundfont: ...` e `RTP: Standard (instalado)`. Num fangame só com OGG, conferir
   `[BOOT] MIDI: ... sem .mid no jogo` (o sintetizador não é criado). Se a música ficar muda, o
   resumo mostra a linha `[MIDI]` com o motivo.
8. **VX, VX Ace, 16 KB e boot (6.8.0)**: instalar `Kerberus-XP-6.8.0-arm64-debug` por cima.
   Importar um jogo de RPG Maker VX e um de VX Ace (ZIP do jogo completo; o RTP `RPGVX`/`RPGVXAce`
   em Configurações → RTP, se o jogo usar). No diagnóstico: `Native loader: legacy18` para o VX e
   `modern31` para o VX Ace; no VX Ace, `[COMPAT] WinBridge31 ativo` e `[WINBRIDGE31_READY]`. Se algo
   parar, **Copiar resumo para enviar**. Num jogo grande com RTP, comparar o tempo até o título com a
   6.7.0. Quem tiver um aparelho com Android 15+ e páginas de 16 KB (ou a opção de desenvolvedor de
   16 KB do Pixel) pode confirmar que o Kerberus abre.
