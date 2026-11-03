package io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;

public final class ResultsSaver {
    private ResultsSaver() {}

    public static void appendCsvRow(Path csvPath, String headerLine, String rowLine) throws IOException {
        if (csvPath == null) throw new IllegalArgumentException("csvPath null");

        Path parent = csvPath.getParent();
        if (parent != null) Files.createDirectories(parent);

        boolean fileExists = Files.exists(csvPath);

        if (!fileExists) {
            Files.write(csvPath, List.of(headerLine), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }

        Files.write(csvPath, List.of(rowLine), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);
    }

    // Escapa campo para CSV (se tiver vírgula/aspas/quebra de linha)
    public static String csvField(String s) {
        if (s == null) return "";
        boolean mustQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return mustQuote ? "\"" + escaped + "\"" : escaped;
    }
}
