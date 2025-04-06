package com.uwu.PC1.AWXXZ_Parallel;

public class ParallelMatrixMultiplier {
  private static final double ALPHA = 0.6;
  private static final double BETA = 0.4;
  private static final int CUTOFF = 64;

  public static double[][] multiply(double[][] A, double[][] B) throws InterruptedException {
    int n = A.length;
    double[][] C = new double[n][n];
    multiplyRecursive(A, B, C, 0, 0, 0, 0, 0, 0, n);
    return C;
  }

  private static void multiplyRecursive(double[][] A, double[][] B, double[][] C,
                                        int aRow, int aCol, int bRow, int bCol,
                                        int cRow, int cCol, int size) throws InterruptedException {
    if (size <= CUTOFF) {
      MatrixUtils.standardMultiply(A, B, C, aRow, aCol, bRow, bCol, cRow, cCol, size);
      return;
    }

    int newSize = size / 2;
    double[][][] products = new double[8][newSize][newSize];
    Thread[] threads = new Thread[8];

    // P1 = (A11 + α*A22) × (B11 + α*B22)
    threads[0] = new Thread(() -> computeProduct(A, B, products[0],
            aRow, aCol, ALPHA, aRow + newSize, aCol + newSize,
            bRow, bCol, ALPHA, bRow + newSize, bCol + newSize, ALPHA, newSize));

    // P2 = (A21 + β*A12) × (B12 - β*B11)
    threads[1] = new Thread(() -> computeProduct(A, B, products[1],
            aRow + newSize, aCol, BETA, aRow, aCol + newSize,
            bRow, bCol + newSize, -BETA, bRow, bCol, -BETA, newSize));

    // P3 = (A11 - A21) × (B22 - B12)
    threads[2] = new Thread(() -> computeProduct(A, B, products[2],
            aRow, aCol, -1.0, aRow + newSize, aCol,
            bRow + newSize, bCol + newSize, -1.0, bRow, bCol + newSize, -1.0, newSize));

    // P4 = (α*A12 + A22) × (β*B21 - B22)
    threads[3] = new Thread(() -> computeProduct(A, B, products[3],
            aRow, aCol + newSize, ALPHA, aRow + newSize, aCol + newSize,
            bRow + newSize, bCol, BETA, bRow + newSize, bCol + newSize, -1.0, newSize));

    // P5 = A11 × B11
    threads[4] = new Thread(() -> MatrixUtils.standardMultiply(A, B, products[4],
            aRow, aCol, bRow, bCol, 0, 0, newSize));

    // P6 = A12 × B21
    threads[5] = new Thread(() -> MatrixUtils.standardMultiply(A, B, products[5],
            aRow, aCol + newSize, bRow + newSize, bCol, 0, 0, newSize));

    // P7 = A21 × B12
    threads[6] = new Thread(() -> MatrixUtils.standardMultiply(A, B, products[6],
            aRow + newSize, aCol, bRow, bCol + newSize, 0, 0, newSize));

    // P8 = A22 × B22
    threads[7] = new Thread(() -> MatrixUtils.standardMultiply(A, B, products[7],
            aRow + newSize, aCol + newSize, bRow + newSize, bCol + newSize, 0, 0, newSize));

    for (Thread thread : threads) {
      thread.start();
    }

    for (Thread thread : threads) {
      thread.join();
    }

    MatrixUtils.combineResults(products, C, cRow, cCol, newSize);
  }

  private static void computeProduct(double[][] A, double[][] B, double[][] result,
                                     int aRow1, int aCol1, double aCoeff1, int aRow2, int aCol2,
                                     int bRow1, int bCol1, double bCoeff1, int bRow2, int bCol2,
                                     double bCoeff2, int size) {
    double[][] tempA = new double[size][size];
    double[][] tempB = new double[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        tempA[i][j] = aCoeff1 * A[aRow1 + i][aCol1 + j];
        tempA[i][j] += A[aRow2 + i][aCol2 + j];

        tempB[i][j] = bCoeff1 * B[bRow1 + i][bCol1 + j];
        tempB[i][j] += bCoeff2 * B[bRow2 + i][bCol2 + j];
      }
    }

    MatrixUtils.standardMultiply(tempA, tempB, result, 0, 0, 0, 0, 0, 0, size);
  }
}
