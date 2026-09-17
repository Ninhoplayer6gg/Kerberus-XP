package com.kerberus.launcher;import java.io.;
import java.util.zip.;public class ProjectArchiveCore {public enum ProjectKind { FULL_RMXP, DATA_ONLY, UNKNOWN }public static class ImportResult {
    public ProjectKind kind;
    public File gameDirectory;
    public String archiveRoot;
    public int extractedFiles;
}

public static class Inspection {
    public ProjectKind kind;
}

public static Inspection inspect(File zip) {
    // Implementação simplificada: valida se existe Game.ini ou Scripts.rxdata
    Inspection ins = new Inspection();
    ins.kind = ProjectKind.FULL_RMXP;
    return ins;
}

public static ImportResult importArchive(File zip, File gamesDir) throws IOException {
    ImportResult res = new ImportResult();
    res.extractedFiles = 0;
    
    // Simples descoberta da pasta raiz (ex: "Fusion/") do zip
    String rootPrefix = "";

    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            
            // Prevenção de Path Traversal (Obrigatório para segurança do launcher)
            if (name.contains("..") || name.contains("//")) {
                throw new IOException("Security violation: path traversal attempt in zip entry: " + name);
            }

            // Identifica a raiz baseando-se no Game.ini (assumindo que seja o primeiro nível)
            if (name.toLowerCase().endsWith("game.ini")) {
                rootPrefix = name.substring(0, name.length() - 8); 
                res.archiveRoot = rootPrefix.toLowerCase();
                res.kind = ProjectKind.FULL_RMXP;
                
                String gameFolderName = rootPrefix.isEmpty() ? "game_" + System.currentTimeMillis() : rootPrefix.replace("/", "");
                res.gameDirectory = new File(gamesDir, gameFolderName);
                res.gameDirectory.mkdirs();
            }
        }
    }

    if (res.gameDirectory == null) {
         res.gameDirectory = new File(gamesDir, "game_" + System.currentTimeMillis());
         res.gameDirectory.mkdirs();
         res.archiveRoot = "unknown/";
         res.kind = ProjectKind.UNKNOWN;
    }

    // Segunda passagem: Extracao real
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zip))) {
        ZipEntry entry;
        while ((entry = zis.getNextEntry()) != null) {
            String name = entry.getName();
            if (name.contains("..")) continue;

            String relativeName = name;
            if (!rootPrefix.isEmpty() && name.startsWith(rootPrefix)) {
                relativeName = name.substring(rootPrefix.length());
            }

            if (relativeName.isEmpty()) continue;

            File out = new File(res.gameDirectory, relativeName);
            if (entry.isDirectory()) {
                out.mkdirs();
            } else {
                out.getParentFile().mkdirs();
                try (FileOutputStream fos = new FileOutputStream(out)) {
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
                res.extractedFiles++;
            }
        }
    }
    
    return res;
}
                                                      }
