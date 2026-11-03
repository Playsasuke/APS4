package bench;

import util.ArrayUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class Benchmark {

    public static final class Result {
        public final String algorithm;
        public final long bestNanos;
        public final double avgNanos;
        public final int runs;

        public Result(String algorithm, long bestNanos, double avgNanos, int runs) {
            this.algorithm = algorithm;
            this.bestNanos = bestNanos;
            this.avgNanos = avgNanos;
            this.runs = runs;
        }
    }

    private Benchmark() {}

    /**
     * Mede apenas o tempo de ordenação.
     * - baseArray: array base (não será modificado)
     * - sortFn: função que ordena IN-PLACE
     * - warmup: execuções de aquecimento (JIT)
     * - runs: execuções medidas
     */
    public static Result measure(String name, int[] baseArray, Consumer<int[]> sortFn, int warmup, int runs) {
        if (baseArray == null) throw new IllegalArgumentException("baseArray null");
        if (sortFn == null) throw new IllegalArgumentException("sortFn null");
        if (warmup < 0 || runs <= 0) throw new IllegalArgumentException("warmup>=0 e runs>0");

        // Warmup
        for (int i = 0; i < warmup; i++) {
            int[] a = ArrayUtils.copyOf(baseArray);
            sortFn.accept(a);
        }

        long best = Long.MAX_VALUE;
        long sum = 0;

        for (int i = 0; i < runs; i++) {
            int[] a = ArrayUtils.copyOf(baseArray);

            long start = System.nanoTime();
            sortFn.accept(a);
            long end = System.nanoTime();

            long elapsed = end - start;
            best = Math.min(best, elapsed);
            sum += elapsed;

            if (!ArrayUtils.isSortedAscending(a)) {
                throw new IllegalStateException("Algoritmo '" + name + "' NÃO ordenou corretamente.");
            }
        }

        double avg = sum / (double) runs;
        return new Result(name, best, avg, runs);
    }

    public static Map<String, Result> runAll(int[] baseArray,
                                             Map<String, Consumer<int[]>> algorithms,
                                             int warmup,
                                             int runs) {
        Map<String, Result> results = new LinkedHashMap<>();
        for (var entry : algorithms.entrySet()) {
            results.put(entry.getKey(), measure(entry.getKey(), baseArray, entry.getValue(), warmup, runs));
        }
        return results;
    }

    public static String formatNanos(long nanos) {
        if (nanos < 1_000) return nanos + " ns";
        if (nanos < 1_000_000) return (nanos / 1_000.0) + " µs";
        if (nanos < 1_000_000_000) return (nanos / 1_000_000.0) + " ms";
        return (nanos / 1_000_000_000.0) + " s";
    }
}
