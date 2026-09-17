#!/usr/bin/env python3
from pathlib import Path
import re
import shutil
import subprocess

ROOT = Path(__file__).resolve().parents[1]
UPSTREAM = ROOT / "upstream" / "mkxp-z-android"
OVERLAY = ROOT / "kerberus-overlay"
OUT = ROOT / "worktree"
PIN = "b668e08cb58edf661d56fd4d396036ac31cd8332"

if not (UPSTREAM / "app" / "build.gradle").is_file():
    raise SystemExit("Upstream ausente em upstream/mkxp-z-android")

try:
    current = subprocess.check_output(["git", "-C", str(UPSTREAM), "rev-parse", "HEAD"], text=True).strip()
    if current != PIN:
        raise SystemExit(f"Commit upstream inesperado: {current}; esperado: {PIN}")
except (subprocess.CalledProcessError, FileNotFoundError):
    raise SystemExit("Não foi possível validar o commit do upstream")

if OUT.exists():
    shutil.rmtree(OUT)
shutil.copytree(UPSTREAM, OUT, ignore=shutil.ignore_patterns(".git"))

for src in OVERLAY.rglob("*"):
    if not src.is_file():
        continue
    rel = src.relative_to(OVERLAY)
    dst = OUT / rel
    dst.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(src, dst)

gradle = OUT / "app" / "build.gradle"
txt = gradle.read_text(encoding="utf-8")
if 'applicationId "com.hatkid.mkxpz"' not in txt:
    raise SystemExit("applicationId upstream mudou; patch não aplicado")
txt = txt.replace('applicationId "com.hatkid.mkxpz"', 'applicationId "com.kerberus.xp"')
txt = re.sub(r'versionCode\s+\d+', 'versionCode 1001', txt)
txt = re.sub(r'versionName\s+"[^"]+"', 'versionName "0.1.0-alpha1"', txt)
txt = txt.replace('archivesBaseName = "mkxp-z-$versionName"', 'archivesBaseName = "Kerberus-XP-$versionName"')
gradle.write_text(txt, encoding="utf-8")

main = OUT / "app" / "src" / "main" / "java" / "com" / "hatkid" / "mkxpz" / "MainActivity.java"
txt = main.read_text(encoding="utf-8")
old_default = 'private static final String GAME_PATH_DEFAULT = Environment.getExternalStorageDirectory() + "/mkxp-z";'
if old_default not in txt:
    raise SystemExit("GAME_PATH_DEFAULT upstream mudou; patch não aplicado")
txt = txt.replace(
    old_default,
    'private static final String GAME_PATH_DEFAULT = "";\n    public static final String EXTRA_GAME_PATH = "kerberus_game_path";'
)

needle = "    protected void onCreate(Bundle savedInstanceState)\n    {\n        super.onCreate(savedInstanceState);"
replacement = "    protected void onCreate(Bundle savedInstanceState)\n    {\n        String requestedGamePath = getIntent().getStringExtra(EXTRA_GAME_PATH);\n        if (requestedGamePath != null) GAME_PATH = requestedGamePath;\n        super.onCreate(savedInstanceState);\n        if (GAME_PATH == null || GAME_PATH.isEmpty()) {\n            android.util.Log.e(\"Kerberus\", \"Runtime started without a game path\");\n            finish();\n            return;\n        }"
if needle not in txt:
    raise SystemExit("onCreate upstream mudou; patch não aplicado")
txt = txt.replace(needle, replacement, 1)

txt = txt.replace(
    'if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {',
    'if (false && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {'
)
main.write_text(txt, encoding="utf-8")

print("KERBERUS_XP_WORKTREE_READY")
print(OUT)
