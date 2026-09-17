package com.kerberus.launcher;import android.app.Activity;import android.content.Intent;import android.graphics.Color;import android.net.Uri;import android.os.Bundle;import android.view.Gravity;import android.view.View;import android.widget.Button;import android.widget.LinearLayout;import android.widget.ScrollView;import android.widget.TextView;import android.widget.Toast;import java.io.File;import java.io.FileOutputStream;import java.io.IOException;import java.io.InputStream;import java.util.concurrent.ExecutorService;import java.util.concurrent.Executors;/ Kerberus shell. mkxp-z stays isolated in com.hatkid.mkxpz.MainActivity (:runtime). */public final class KerberusLauncherActivity extends Activity {private static final int PICK_PROJECT = 4101;private static final String RUNTIME_CLASS = "com.hatkid.mkxpz.MainActivity";private static final String EXTRA_GAME_PATH = "kerberus_game_path";private final ExecutorService worker = Executors.newSingleThreadExecutor();
private ProjectLibrary library;
private TextView status;
private Button playButton;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    library = new ProjectLibrary(this);
    setTitle("Kerberus XP");
    setContentView(buildUi());
    refreshLibraryState();
}

@Override
protected void onDestroy() {
    worker.shutdownNow();
    super.onDestroy();
}

private View buildUi() {
    ScrollView scroll = new ScrollView(this);
    scroll.setBackgroundColor(Color.rgb(9, 7, 6));

    LinearLayout root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(22), dp(28), dp(22), dp(28));
    scroll.addView(root);

    TextView title = label("KERBERUS XP", 30, true, Color.rgb(255, 130, 32));
    title.setGravity(Gravity.CENTER_HORIZONTAL);
    root.addView(title);

    TextView subtitle = label("MKXP engine • RPG Maker XP / Pokémon Essentials", 13, false, Color.LTGRAY);
    subtitle.setGravity(Gravity.CENTER_HORIZONTAL);
    subtitle.setPadding(0, dp(5), 0, dp(24));
    root.addView(subtitle);

    root.addView(action("Importar jogo (.ZIP / .MXP)", v -> chooseArchive()));
    playButton = action("Jogar último projeto", v -> launchLastGame());
    root.addView(playButton);
    root.addView(action("Mostrar diagnóstico", v -> status.setText(library.lastReport())));

    TextView info = label(
            "O launcher importa o jogo para a área privada do Android. O runtime mkxp-z roda em um processo separado, sem pedir acesso total aos seus arquivos.",
            13, false, Color.rgb(255, 194, 120));
    info.setPadding(0, dp(18), 0, dp(12));
    root.addView(info);

    status = label("Inicializando biblioteca...", 14, false, Color.WHITE);
    status.setPadding(dp(16), dp(16), dp(16), dp(16));
    status.setBackgroundColor(Color.rgb(28, 22, 18));
    root.addView(status, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    return scroll;
}

private void refreshLibraryState() {
    String path = library.lastGamePath();
    boolean valid = path != null && !path.isEmpty() && new File(path).isDirectory();
    playButton.setEnabled(valid);
    status.setText(valid ? library.lastReport() : "Nenhum jogo importado ainda.");
}

private void chooseArchive() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("*/*");
    intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{
            "application/zip", "application/octet-stream", "application/x-zip-compressed"
    });
    startActivityForResult(intent, PICK_PROJECT);
}

@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode != PICK_PROJECT || resultCode != RESULT_OK || data == null || data.getData() == null) return;
    importArchive(data.getData());
}

private void importArchive(Uri uri) {
    status.setText("Importando e verificando o jogo...");
    playButton.setEnabled(false);
    worker.execute(() -> {
        File temp = null;
        try {
            // Aqui o script original havia cortado. Reconstruindo a lógica:
            temp = new File(getCacheDir(), "kerberus-import-" + System.currentTimeMillis() + ".zip");
            copyUri(uri, temp);

            ProjectArchiveCore.Inspection inspection = ProjectArchiveCore.inspect(temp);
            if (inspection.kind == ProjectArchiveCore.ProjectKind.DATA_ONLY) {
                throw new IOException("Esse pacote contém só Data/. Para jogar, importe o pacote completo com Game.ini, Graphics e Audio.");
            }

            File gamesRoot = new File(getFilesDir(), "games");
            ProjectArchiveCore.ImportResult imported = ProjectArchiveCore.importArchive(temp, gamesRoot);
            
            String report = "IMPORTAÇÃO CONCLUÍDA\n\n" +
                            "Tipo: " + imported.kind + "\n" +
                            "Arquivos: " + imported.extractedFiles + "\n" +
                            "Raiz: " + imported.archiveRoot;
            
            library.saveLastGame(imported.gameDirectory.getAbsolutePath(), report);
            
            runOnUiThread(() -> {
                Toast.makeText(this, "Importado com sucesso!", Toast.LENGTH_SHORT).show();
                refreshLibraryState();
            });
        } catch (Exception e) {
            final String err = "Erro na importação:\n" + e.getMessage();
            runOnUiThread(() -> {
                status.setText(err);
                refreshLibraryState();
            });
        } finally {
            if (temp != null && temp.exists()) temp.delete();
        }
    });
}

private void copyUri(Uri uri, File dest) throws IOException {
    try (InputStream in = getContentResolver().openInputStream(uri);
         FileOutputStream out = new FileOutputStream(dest)) {
        if (in == null) throw new IOException("Não foi possível ler o arquivo");
        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) > 0) out.write(buf, 0, len);
    }
}

private void launchLastGame() {
    String path = library.lastGamePath();
    if (path == null) return;
    Intent intent = new Intent();
    intent.setClassName(this, RUNTIME_CLASS);
    intent.putExtra(EXTRA_GAME_PATH, path);
    startActivity(intent);
}

// Helpers de UI Simplificados
private int dp(int px) {
    return (int) (px * getResources().getDisplayMetrics().density);
}

private TextView label(String text, int sp, boolean bold, int color) {
    TextView tv = new TextView(this);
    tv.setText(text);
    tv.setTextSize(sp);
    tv.setTextColor(color);
    if (bold) tv.setTypeface(null, android.graphics.Typeface.BOLD);
    return tv;
}

private Button action(String text, View.OnClickListener listener) {
    Button b = new Button(this);
    b.setText(text);
    b.setOnClickListener(listener);
    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    lp.setMargins(0, 0, 0, dp(12));
    b.setLayoutParams(lp);
    return b;
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        }
