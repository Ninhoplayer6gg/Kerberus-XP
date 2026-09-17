package com.kerberus.launcher;import android.content.Context;import android.content.SharedPreferences;public class ProjectLibrary {private final SharedPreferences prefs;public ProjectLibrary(Context context) {
    prefs = context.getSharedPreferences("kerberus_library", Context.MODE_PRIVATE);
}

public void saveLastGame(String path, String report) {
    prefs.edit()
         .putString("last_path", path)
         .putString("last_report", report)
         .apply();
}

public String lastGamePath() {
    return prefs.getString("last_path", null);
}

public String lastReport() {
    return prefs.getString("last_report", "Nenhum relatório.");
}
                                                                                                                                  }
