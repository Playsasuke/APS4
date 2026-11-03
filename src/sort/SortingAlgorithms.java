package sort;

public final class SortingAlgorithms {
    private SortingAlgorithms() {}

    // 1) Insertion Sort
    public static void insertionSort(int[] a) {
        for (int i = 1; i < a.length; i++) {
            int key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j] > key) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    // 2) Merge Sort
    public static void mergeSort(int[] a) {
        if (a.length <= 1) return;
        int[] aux = new int[a.length];
        mergeSortRec(a, aux, 0, a.length - 1);
    }

    private static void mergeSortRec(int[] a, int[] aux, int left, int right) {
        if (left >= right) return;

        int mid = left + (right - left) / 2;
        mergeSortRec(a, aux, left, mid);
        mergeSortRec(a, aux, mid + 1, right);
        merge(a, aux, left, mid, right);
    }

    private static void merge(int[] a, int[] aux, int left, int mid, int right) {
        for (int i = left; i <= right; i++) aux[i] = a[i];

        int i = left;
        int j = mid + 1;
        int k = left;

        while (i <= mid && j <= right) {
            if (aux[i] <= aux[j]) a[k++] = aux[i++];
            else a[k++] = aux[j++];
        }
        while (i <= mid) a[k++] = aux[i++];
        while (j <= right) a[k++] = aux[j++];
    }

    // 3) Quick Sort (Lomuto com pivô no meio)
    public static void quickSort(int[] a) {
        if (a.length <= 1) return;
        quickSortRec(a, 0, a.length - 1);
    }

    private static void quickSortRec(int[] a, int left, int right) {
        if (left >= right) return;

        int pivotIndex = left + (right - left) / 2;
        int pivotValue = a[pivotIndex];

        // move pivô pro final
        swap(a, pivotIndex, right);

        int store = left;
        for (int i = left; i < right; i++) {
            if (a[i] < pivotValue) {
                swap(a, i, store);
                store++;
            }
        }
        // pivô na posição final
        swap(a, store, right);

        quickSortRec(a, left, store - 1);
        quickSortRec(a, store + 1, right);
    }

    private static void swap(int[] a, int i, int j) {
        if (i == j) return;
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }
}
