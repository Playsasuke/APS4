import bench.Benchmark;
import io.DataLoader;
import io.ResultsSaver;
import sort.SortingAlgorithms;
import util.ArrayUtils;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {

    private static final Scanner SC = new Scanner(System.in);

    private enum DataSourceType { FILE, RANDOM }
    private enum ScenarioType { RANDOM, SORTED, REVERSED, PARTIAL }

    public static void main(String[] args) {
        System.out.println("=== APS - Comparacao de Algoritmos de Ordenacao (Java) ===");

        Map<String, Consumer<int[]>> algs = new LinkedHashMap<>();
        algs.put("InsertionSort", SortingAlgorithms::insertionSort);
        algs.put("MergeSort", SortingAlgorithms::mergeSort);
        algs.put("QuickSort", SortingAlgorithms::quickSort);

        while (true) {
            try {
                // 1) Dados
                DataChoice dataChoice = chooseDataSource();
                int[] base = dataChoice.data;

                // 2) Cenário
                ScenarioChoice scChoice = chooseScenario(base);
                int[] scenarioArray = scChoice.data;

                int warmup = readInt("Warmup (ex: 3): ", 0, 1000);
                int runs = readInt("Runs medidos (ex: 10): ", 1, 10000);

                System.out.println("\nAmostra ANTES: " + ArrayUtils.preview(scenarioArray, 30));

                var results = Benchmark.runAll(scenarioArray, algs, warmup, runs);

                System.out.println("\n=== Resultados (tempo apenas da ordenacao) ===");
                results.values().stream()
                        .sorted(Comparator.comparingDouble(r -> r.avgNanos))
                        .forEach(r -> System.out.println("- " + r.algorithm
                                + " | best: " + Benchmark.formatNanos(r.bestNanos)
                                + " | avg: " + Benchmark.formatNanos((long) r.avgNanos)
                                + " | runs: " + r.runs));

                // Mostra "depois" usando o mais rápido (pela média)
                var fastest = results.values().stream()
                        .min(Comparator.comparingDouble(r -> r.avgNanos))
                        .orElse(null);

                if (fastest != null) {
                    int[] after = ArrayUtils.copyOf(scenarioArray);
                    algs.get(fastest.algorithm).accept(after);
                    System.out.println("\nAmostra DEPOIS (usando " + fastest.algorithm + "): " + ArrayUtils.preview(after, 30));
                }

                // 3) Salvar CSV
                System.out.print("\nDeseja salvar os resultados em CSV? (s/n): ");
                String ans = SC.nextLine().trim().toLowerCase();
                if (ans.equals("s") || ans.equals("sim")) {
                    saveResultsCsv(dataChoice, scChoice, results);
                }

            } catch (Exception e) {
                System.out.println("\n[ERRO] " + e.getMessage());
            }

            System.out.println("\n1) Rodar novamente");
            System.out.println("0) Sair");
            int op = readInt("Opcao: ", 0, 1);
            if (op == 0) break;
            System.out.println();
        }

        System.out.println("Encerrado.");
    }

    // ===== CSV =====

    private static void saveResultsCsv(DataChoice dataChoice, ScenarioChoice scChoice,
                                       Map<String, Benchmark.Result> results) {
        try {
            Path csv = Path.of("results", "resultados.csv");
            String header = "data_hora,origem,cenario,n,algoritmo,best_nanos,avg_nanos,runs";

            String dt = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String origem = dataChoice.sourceType == DataSourceType.FILE ? "arquivo" : "aleatorio";
            String cenario = switch (scChoice.scenarioType) {
                case RANDOM -> "aleatorio";
                case SORTED -> "ordenado";
                case REVERSED -> "reverso";
                case PARTIAL -> "parcial_70";
            };

            int n = scChoice.data.length;

            for (Benchmark.Result r : results.values()) {
                String row = ResultsSaver.csvField(dt) + ","
                        + ResultsSaver.csvField(origem) + ","
                        + ResultsSaver.csvField(cenario) + ","
                        + n + ","
                        + ResultsSaver.csvField(r.algorithm) + ","
                        + r.bestNanos + ","
                        + (long) r.avgNanos + ","
                        + r.runs;

                ResultsSaver.appendCsvRow(csv, header, row);
            }

            System.out.println("✅ Salvo em: " + csv.toAbsolutePath());

        } catch (Exception ex) {
            System.out.println("[ERRO ao salvar CSV] " + ex.getMessage());
        }
    }

    // ===== Escolhas =====

    private static final class DataChoice {
        final DataSourceType sourceType;
        final int[] data;
        DataChoice(DataSourceType sourceType, int[] data) {
            this.sourceType = sourceType;
            this.data = data;
        }
    }

    private static final class ScenarioChoice {
        final ScenarioType scenarioType;
        final int[] data;
        ScenarioChoice(ScenarioType scenarioType, int[] data) {
            this.scenarioType = scenarioType;
            this.data = data;
        }
    }

    private static DataChoice chooseDataSource() throws Exception {
        System.out.println("\nEscolha a origem dos dados:");
        System.out.println("1) Arquivo (data/dados.txt)");
        System.out.println("2) Gerar aleatorio");
        int op = readInt("Opcao: ", 1, 2);

        if (op == 1) {
            Path p = Path.of("data", "dados.txt");
            int[] data = DataLoader.loadInts(p);
            if (data.length == 0) throw new IllegalStateException("Arquivo vazio: " + p.toAbsolutePath());
            System.out.println("Lidos " + data.length + " valores de " + p.toAbsolutePath());
            return new DataChoice(DataSourceType.FILE, data);
        } else {
            int n = readInt("Tamanho N (ex: 10000): ", 1, 5_000_000);
            int min = readInt("Valor minimo (ex: 0): ", Integer.MIN_VALUE, Integer.MAX_VALUE);
            int max = readInt("Valor maximo (ex: 100000): ", Integer.MIN_VALUE, Integer.MAX_VALUE);
            int[] data = ArrayUtils.generateRandom(n, min, max);
            System.out.println("Gerados " + data.length + " valores aleatorios.");
            return new DataChoice(DataSourceType.RANDOM, data);
        }
    }

    private static ScenarioChoice chooseScenario(int[] base) {
        System.out.println("\nEscolha o cenário:");
        System.out.println("1) Aleatorio (como veio)");
        System.out.println("2) Ja ordenado (crescente)");
        System.out.println("3) Reverso (decrescente)");
        System.out.println("4) Parcialmente ordenado (70%)");
        int op = readInt("Opcao: ", 1, 4);

        return switch (op) {
            case 1 -> new ScenarioChoice(ScenarioType.RANDOM, base);
            case 2 -> new ScenarioChoice(ScenarioType.SORTED, ArrayUtils.sortedCopy(base));
            case 3 -> new ScenarioChoice(ScenarioType.REVERSED, ArrayUtils.reverseCopy(base));
            case 4 -> new ScenarioChoice(ScenarioType.PARTIAL, ArrayUtils.partiallySortedCopy(base, 0.70));
            default -> new ScenarioChoice(ScenarioType.RANDOM, base);
        };
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String s = SC.nextLine();
            try {
                int v = Integer.parseInt(s.trim());
                if (v < min || v > max) {
                    System.out.println("Digite um inteiro entre " + min + " e " + max + ".");
                    continue;
                }
                return v;
            } catch (NumberFormatException e) {
                System.out.println("Entrada invalida. Digite um numero inteiro.");
            }
        }
    }
}
