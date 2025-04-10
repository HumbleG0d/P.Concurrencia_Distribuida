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

  public static void tensorContraction(double[][] A, double[][] B, double[][] C,
                                        int n, int blockRow, int blockCol) {
    int newSize = n / 2;
    double[][][][] A_blocks = splitMatrix(A, newSize);
    double[][][][] B_blocks = splitMatrix(B, newSize);
    double[][] temp = new double[newSize][newSize];

    // Contracción tensorial para el bloque (blockRow, blockCol)
    for (int k = 0; k < 2; k++) {
      double[][] product = new double[newSize][newSize];
      standardMultiply(A_blocks[blockRow][k], B_blocks[k][blockCol],
              product, 0, 0, 0, 0, 0, 0, newSize);
      for (int x = 0; x < newSize; x++) {
        for (int y = 0; y < newSize; y++) {
          temp[x][y] += product[x][y];
        }
      }
    }

    // Copiar el resultado a la matriz C
    for (int x = 0; x < newSize; x++) {
      for (int y = 0; y < newSize; y++) {
        C[blockRow * newSize + x][blockCol * newSize + y] = temp[x][y];
      }
    }
  }

  public static double[][][][] splitMatrix(double[][] M, int blockSize) {
    double[][][][] blocks = new double[2][2][blockSize][blockSize];
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int x = 0; x < blockSize; x++) {
          for (int y = 0; y < blockSize; y++) {
            blocks[i][j][x][y] = M[i * blockSize + x][j * blockSize + y];
          }
        }
      }
    }
    return blocks;
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
}