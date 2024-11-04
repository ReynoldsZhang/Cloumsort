import java.io.*;
import java.util.*;

/**
 * Prog3 - Implements Column Sort algorithm to sort data from a file in 2D matrix form.
 *
 * This program reads a list of integers from a file, chooses optimal values of R (rows) and S (columns)
 * based on specific conditions, and then sorts the data using a column-sort algorithm. The sorted data
 * is then printed in a 1D array format.
 *
 * Author: Zehang Zhang
 */
public class Prog3 {
    // R represents the number of rows, S the number of columns for the 2D matrix
    private static int R;
    private static int S;

    public static void main(String[] args) {
        Integer[] data = parseFile(args[0]);  // Reads data from file
        if (data == null) return;

        int[] rs = chooseRS(data.length);  // Choose appropriate R and S
        assert rs != null;
        R = rs[0];
        S = rs[1];
        System.out.println("Rows (R): " + R + ", Columns (S): " + S);
        int[][] array2D = new int[R][S];
        int index = 0;

        // Step 1: Convert 1D data array to a 2D array (R x S matrix)
        for (int j = 0; j < S; j++) {
            for (int i = 0; i < R; i++) {
                array2D[i][j] = data[index++];
            }
        }

        // Step 2: Perform column sort and measure time taken
        long startTime = System.nanoTime();
        columnSort(array2D);
        long elapsedTime = System.nanoTime() - startTime;
        System.out.printf("Elapsed time = %.3f seconds.\n", elapsedTime / 1000000000.0);

        // Step 3: Output sorted 1D array
        int[] sortedArray = to1DArray(array2D);
        for (int value : sortedArray) {
            System.out.println(value);
        }
    }

    /**
     * Reads integers from a file and stores them in an Integer array.
     *
     * @param filename The path to the file containing integers.
     * @return Integer array containing numbers from the file, or null if an error occurs.
     */
    private static Integer[] parseFile(String filename) {
        List<Integer> numbers = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File(filename))) {
            while (scanner.hasNextInt()) {
                numbers.add(scanner.nextInt());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Unable to find input file!");
            return null;
        }
        return numbers.toArray(new Integer[0]);
    }

    /**
     * Chooses values of R and S based on the total count n.
     * Ensures that R and S satisfy: R * S = n, R % S = 0, and R >= 2 * (S - 1)^2.
     *
     * @param n Total number of elements.
     * @return int array with two elements [R, S] if conditions are met, else null.
     */
    private static int[] chooseRS(int n) {
        for (int s = 1; s <= n; s++) {
            if (n % s == 0) {  // Check R * S = n
                int r = n / s;
                if (r % s == 0 && r >= 2 * Math.pow(s - 1, 2)) {  // Check R mod S = 0 and R >= 2(S-1)^2
                    return new int[]{r, s};
                }
            }
        }
        return null;
    }

    /**
     * Sorts the 2D matrix using column sort algorithm.
     * Steps:
     * 1. Sorts each column.
     * 2. Transposes the matrix.
     * 3. Sorts each column of the transposed matrix.
     * 4. Transposes back to original shape.
     * 5. Performs a final column-wise sort.
     *
     * @param array 2D matrix to be sorted.
     */
    private static void columnSort(int[][] array) {
        for (int j = 0; j < S; j++) {
            heapSort(array, R, j);  // Step 1: Sort each column
        }

        int[][] transposed = transpose(array, R, S);  // Step 2: Transpose matrix

        for (int j = 0; j < R; j++) {
            heapSort(transposed, S, j);  // Step 3: Sort columns in transposed matrix
        }

        int[][] reshaped = transpose(transposed, S, R);  // Step 4: Transpose back

        for (int j = 0; j < S; j++) {
            heapSort(reshaped, R, j);  // Step 5: Final column sort
        }

        // Copy sorted data back into original array
        for (int i = 0; i < R; i++) {
            System.arraycopy(reshaped[i], 0, array[i], 0, S);
        }
    }

    /**
     * Transposes a matrix, swapping rows and columns.
     *
     * @param matrix Original matrix to transpose.
     * @param rows Number of rows in the matrix.
     * @param cols Number of columns in the matrix.
     * @return Transposed matrix.
     */
    private static int[][] transpose(int[][] matrix, int rows, int cols) {
        int[][] transposed = new int[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    /**
     * Sorts a specified column of a 2D array using heap sort.
     *
     * @param array 2D array containing the column to be sorted.
     * @param rowCount Number of rows in the array.
     * @param col The column index to sort.
     */
    private static void heapSort(int[][] array, int rowCount, int col) {
        int[] columnData = new int[rowCount];
        for (int i = 0; i < rowCount; i++) {
            columnData[i] = array[i][col];
        }

        // Build max heap
        for (int i = rowCount / 2 - 1; i >= 0; i--) {
            maxHeapify(columnData, rowCount, i);
        }
        // Extract elements from the heap
        for (int i = rowCount - 1; i > 0; i--) {
            int temp = columnData[0];
            columnData[0] = columnData[i];
            columnData[i] = temp;
            maxHeapify(columnData, i, 0);
        }

        // Place sorted column back into the 2D array
        for (int i = 0; i < rowCount; i++) {
            array[i][col] = columnData[i];
        }
    }

    /**
     * Maintains the max-heap property for heap sort.
     *
     * @param array Array to maintain as a max heap.
     * @param n Size of the heap.
     * @param i Index to perform max-heapify.
     */
    private static void maxHeapify(int[] array, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        if (left < n && array[left] > array[largest]) {
            largest = left;
        }
        if (right < n && array[right] > array[largest]) {
            largest = right;
        }
        if (largest != i) {
            int temp = array[i];
            array[i] = array[largest];
            array[largest] = temp;
            maxHeapify(array, n, largest);
        }
    }

    /**
     * Converts a 2D matrix into a 1D array for output.
     *
     * @param matrix The 2D matrix to convert.
     * @return 1D array containing all elements of the matrix.
     */
    private static int[] to1DArray(int[][] matrix) {
        int[] result = new int[R * S];
        int index = 0;
        for (int j = 0; j < S; j++) {
            for (int i = 0; i < R; i++) {
                result[index++] = matrix[i][j];
            }
        }
        return result;
    }
}
