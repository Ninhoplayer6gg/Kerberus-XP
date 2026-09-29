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

## 1. Estado por jogo

| Jogo | Runtime | Última evidência real | Estado 6.3 | O que a 6.3 muda para ele |
|---|---|---|---|---|
| Pokémon Uranium | legacy18 (Ruby 1.8.7 real) | 6.3: abre no aparelho, controles sem resposta (relato do usuário) | Controles corrigidos na 6.3.1; aguarda novo teste | APIs opcionais declaradas não derrubam mais o boot; nomes com sufixo `A`; 307 providers em vez de 62; ports `rgss_linker`/`fmodex_audio` preservados e completados (volume das opções, fade-in real); `klein_bitmap`, `gif_dll`, `text_entry` (teclado Android), `bitmap_memory`; correção genérica do `LargePlane`; caminhos estilo Windows |
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

## 4. Testes adicionados

| Suíte | Verificações | Resultado local |
|---|---|---|
| Legacy18 em Ruby 1.8.7 real (12 arquivos) | 450 | OK |
| Ciclo de vida nativo (extensão Ruby 1.8 + headers reais) | 7 | OK (e falha com o header antigo) |
| Regressão modern31/Infinite Fusion em Ruby 3.1.6 | 7 + sha256 | OK |
| Java: seleção de runtime | 14 casos | OK |
| Java: estado de compatibilidade e opções | 14 | OK |
| Java: SessionLock e controles (existentes) | – | OK |
| C++: 6 arquivos × 2 runtimes (clang, C++14) | 12 | OK |
| Java do app inteiro contra `android.jar` API 33, alvo Java 8 | 146 classes | OK |
| Sincronia de gerados (header, catálogo, lista de APIs) | 3 | OK |

## 5. Limitações restantes

- Só o Uranium foi aberto no aparelho, e só até descobrir o bug 15. Os demais estados só mudam com
  o teste no aparelho.
- **Toques mais curtos que um quadro** (cerca de 16 ms) podem se perder: o estado é lido quando o
  jogo consulta, e o Windows guardaria esse toque no bit 0 do `GetAsyncKeyState`.
- **Roda do mouse** (`InputGetWheelDelta`) ainda depende do `Input.update` nativo.
- **Assinatura da APK**: cada build do CI usa uma chave de debug nova. Instalar uma build nova por
  cima da anterior exige desinstalar, o que apaga os jogos importados e os saves guardados no app.
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

1. Instalar a APK da 6.3.1 e abrir o jogo.
2. Em **Logs → Mostrar diagnóstico**, copiar o diagnóstico completo. Ele traz o estado de testes,
   o resumo WINTRACE, os eventos `PORT_*`/`WINBRIDGE`, e `RUBY_EXCEPTION` com script, section, linha,
   classe, mensagem e backtrace.
3. Para depurar um port, criar `.kerberus-options` na pasta do jogo com `wintrace=detail` e
   `dump_scripts=on`.
4. Seguir a ordem Boot → Title → New Game → intro → nome → primeiro mapa → movimento → menus →
   batalha → áudio → save → load → troca de mapas; os marcos aparecem sozinhos no diagnóstico.
5. A cada falha: enviar o bloco `[WINBRIDGE]` ou `[RUBY_EXCEPTION]`. Ele já traz dll, função,
   assinatura, script e linha para decidir entre provider genérico, port ou perfil.

## 7. Verificação desta entrega

### 6.3.1 (correção dos controles)

Workflow v20, execução 36600226232 (commit `623c862`):
https://github.com/Ninhoplayer6gg/Kerberus-XP/actions/runs/36600226232

| Item | Situação |
|---|---|
| Testes de host: Java (inclui o `ControlsHostTest` ampliado pelo dono do repositório), Ruby 1.8.7 real, Ruby 3.1, C++ nos dois runtimes, ciclo de vida nativo | Passaram localmente, a partir de uma extração limpa do ZIP e no job `tests` do CI |
| `test_input_live.rb` | 13 falhas na 6.3, passa na 6.3.1 |
| Saída do pré-processador do `binding-mri.cpp` no modern31 | Idêntica byte a byte à 6.3 |
| Java do app inteiro contra `android.jar` API 33 | Compila |
| Build da APK ARM64 (NDK r23 + Gradle `assembleDebug`) | **Passou**, incluindo a ligação dos acessores ao vivo com a tabela VK do mkxp-z |
| Auditoria da APK | **Passou** (`KERBERUS_APK_AUDIT_OK`), com a mesma separação de runtimes da 6.3 |
| Teste no aparelho | Pendente: reabrir o Uranium com a 6.3.1 |

APK gerada: artefato `Kerberus-XP-6.3-arm64-debug` da execução acima (retenção de 30 dias). O nome do
artefato continua o do workflow v20; a versão dentro do app é `6.3.1-pokemon-compatibility`.

```
sha256  44d9d0f02ca83e56ef3da2f875e06ff00237892e2d6be69241b1eb83aadb5016  Kerberus-XP-6.3-arm64-debug.apk
```

A APK é assinada com uma chave de debug nova a cada build. Para instalar por cima da 6.3 é preciso
desinstalar a 6.3 antes, e isso apaga os jogos importados e os saves guardados dentro do app.

### 6.3

Workflow v20 no GitHub Actions, execução 36571734953 (commit `f1e1335`):
https://github.com/Ninhoplayer6gg/Kerberus-XP/actions/runs/36571734953

| Item | Situação |
|---|---|
| Testes de host (Java, Ruby 1.8.7 real, Ruby 3.1, C++ nos dois runtimes, ciclo de vida nativo) | Passaram localmente, a partir de uma extração limpa do ZIP e no job `tests` do CI |
| Java do app inteiro contra `android.jar` API 33 | Compila |
| Build da APK ARM64 (NDK r23 + Gradle `assembleDebug`) | **Passou** no job `build` (`BUILD SUCCESSFUL`) |
| Auditoria da APK | **Passou** (`KERBERUS_APK_AUDIT_OK`): só ABI arm64-v8a; libmkxp-z18 sem `libruby.so`; libmkxp-z31 com `libruby.so`; sem caminhos absolutos do host; libmkxp-z18 contém só o bootstrap Legacy18 (WinBridge) e libmkxp-z31 só o bootstrap moderno (Infinite Fusion) |
| Teste dos jogos em aparelho | Não executado (sem jogos e sem aparelho). Nenhum jogo passa de "Não testado" |

APK gerada: artefato `Kerberus-XP-6.3-arm64-debug` da execução acima (retenção de 30 dias). O artefato
contém a APK, o arquivo `.sha256` e o `apk-audit.log`.

```
sha256  93469ddc40cb4997ddf7d7d1130a45ff3ee3ba5851e81438d8ee521ea55f127d  Kerberus-XP-6.3-arm64-debug.apk
```

Observação da auditoria: `libc++_shared.so` e `libopenal.so` têm alinhamento de segmento LOAD de 4 KB; as
demais bibliotecas têm 16 KB. Isso não afeta aparelhos com páginas de 4 KB, mas aparelhos com páginas de
16 KB exigirão essas duas bibliotecas recompiladas com alinhamento de 16 KB. Esse ponto não foi alterado
nesta versão.

Para recompilar: Actions → "Kerberus XP 6.3 - Build APK (v20 pokemon compatibility)" → Run workflow. O
workflow também roda sozinho em push que altere o ZIP ou o próprio workflow nesta branch. O job `tests`
roda antes; o job `build` só compila a APK se todos os testes passarem. O workflow usa apenas actions
criadas pelo GitHub (`actions/*`), porque a política do repositório bloqueia actions de terceiros.
