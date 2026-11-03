package util;

import java.util.Arrays;
import java.util.Random;

public final class ArrayUtils {
    private static final Random RNG = new Random();

    private ArrayUtils() {}

    public static int[] copyOf(int[] arr) {
        return Arrays.copyOf(arr, arr.length);
    }

    public static int[] generateRandom(int n, int minInclusive, int maxInclusive) {
        if (n < 0) throw new IllegalArgumentException("n deve ser >= 0");
        if (minInclusive > maxInclusive) throw new IllegalArgumentException("min > max");

        int[] a = new int[n];
        int bound = (maxInclusive - minInclusive) + 1;
        for (int i = 0; i < n; i++) {
            a[i] = minInclusive + RNG.nextInt(bound);
        }
        return a;
    }

    public static int[] sortedCopy(int[] arr) {
        int[] a = copyOf(arr);
        Arrays.sort(a);
        return a;
    }

    public static int[] reverseCopy(int[] arr) {
        int[] a = sortedCopy(arr);
        reverseInPlace(a);
        return a;
    }

    public static void reverseInPlace(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
    }

    // Deixa uma parte já ordenada e outra parte bagunçada (aprox.)
    public static int[] partiallySortedCopy(int[] arr, double sortedPortion) {
        if (sortedPortion < 0 || sortedPortion > 1) {
            throw new IllegalArgumentException("sortedPortion deve estar entre 0 e 1");
        }

        int[] a = copyOf(arr);
        int k = (int) Math.round(a.length * sortedPortion);

        // Ordena prefixo
        int[] prefix = Arrays.copyOfRange(a, 0, k);
        Arrays.sort(prefix);
        System.arraycopy(prefix, 0, a, 0, k);

        // Bagunça o restante com swaps aleatórios
        for (int i = k; i < a.length; i++) {
            int j = k + RNG.nextInt(a.length - k);
            int tmp = a[i];
            a[i] = a[j];
            a[j] = tmp;
        }
        return a;
    }

    public static boolean isSortedAscending(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) return false;
        }
        return true;
    }

    public static String preview(int[] a, int maxItems) {
        if (a == null) return "null";
        if (a.length <= maxItems) return Arrays.toString(a);

        int show = Math.max(2, maxItems / 2);
        int[] head = Arrays.copyOfRange(a, 0, show);
        int[] tail = Arrays.copyOfRange(a, a.length - show, a.length);

        return Arrays.toString(head) + " ... " + Arrays.toString(tail) + " (n=" + a.length + ")";
    }
}
