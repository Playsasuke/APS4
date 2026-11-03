package io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class DataLoader {
    private DataLoader() {}

    /**
     * Lê inteiros de um arquivo.
     * Aceita:
     * - um número por linha
     * - ou separados por vírgula, ponto e vírgula, espaço, tab
     */
    public static int[] loadInts(Path path) throws IOException {
        if (path == null) throw new IllegalArgumentException("path não pode ser null");
        if (!Files.exists(path)) throw new IOException("Arquivo não encontrado: " + path.toAbsolutePath());

        List<String> lines = Files.readAllLines(path);
        List<Integer> values = new ArrayList<>();

        for (String line : lines) {
            if (line == null) continue;
            String trimmed = line.trim();
            if (trimmed.isEmpty()) continue;

            // quebra por , ; espaço tab
            String[] parts = trimmed.split("[,;\\s]+");
            for (String p : parts) {
                if (p == null) continue;
                String t = p.trim();
                if (t.isEmpty()) continue;
                try {
                    values.add(Integer.parseInt(t));
                } catch (NumberFormatException e) {
                    throw new IOException("Valor inválido no arquivo: '" + t + "' em " + path.toAbsolutePath(), e);
                }
            }
        }

        int[] arr = new int[values.size()];
        for (int i = 0; i < values.size(); i++) {
            arr[i] = values.get(i);
        }
        return arr;
    }
}
