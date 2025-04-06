package com.uwu.PC1.AWXXZ_Parallel;

public class MatrixUtils {

  public static double[][] createRandomMatrix(int size) {
    double[][] matrix = new double[size][size];
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        matrix[i][j] = Math.random();
      }
    }
    return matrix;
  }

  public static void standardMultiply(double[][] A, double[][] B, double[][] C,
                                      int aRow, int aCol, int bRow, int bCol,
                                      int cRow, int cCol, int size) {
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        double sum = 0.0;
        for (int k = 0; k < size; k++) {
          sum += A[aRow + i][aCol + k] * B[bRow + k][bCol + j];
        }
        C[cRow + i][cCol + j] = sum;
      }
    }
  }

  public static void combineResults(double[][][] products, double[][] C,
                                    int cRow, int cCol, int size) {
    double[][] P1 = products[0], P2 = products[1], P3 = products[2], P4 = products[3],
            P5 = products[4], P6 = products[5], P7 = products[6], P8 = products[7];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        // C11 = P1 + P2 - P4 + P8
        C[cRow + i][cCol + j] = P1[i][j] + P2[i][j] - P4[i][j] + P8[i][j];

        // C12 = P5 + P6
        C[cRow + i][cCol + j + size] = P5[i][j] + P6[i][j];

        // C21 = P7 + P8
        C[cRow + i + size][cCol + j] = P7[i][j] + P8[i][j];

        // C22 = P1 - P3 - P5 + P7
        C[cRow + i + size][cCol + j + size] = P1[i][j] - P3[i][j] - P5[i][j] + P7[i][j];
      }
    }
  }
}