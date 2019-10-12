package bigsort.util;

import java.util.Arrays;

import bigsort.util.api.Sorter;

/**
 * FastSorter will provide a sorting algorithm to sort splited input data.
 * QuickSort algorithm(provided by Arrays.sort()) is used to sort the string
 * array.
 */
public class FastSorter implements Sorter {
    @Override
    public void sort(String[] data) {
        Arrays.sort(data);
    }
}