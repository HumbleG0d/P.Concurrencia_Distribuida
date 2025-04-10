package com.uwu.PC1.AWXXZ_Sequencial;

import java.util.Arrays;

public class AWXXZSequential {

  private static final int BLOCK_SIZE = 32; // Tamaño de bloque para contracción

  public static double[][] multiply(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    tensorMultiply(A, B, C, n);
    return C;
  }

  private static void tensorMultiply(double[][] A, double[][] B, double[][] C, int n) {
    if (n <= BLOCK_SIZE) {
      standardMultiply(A, B, C, 0, 0, 0, 0, 0, 0, n);
      return;
    }

    int newSize = n / 2;
    // Dividir en bloques (representación tensorial)
    double[][][][] A_blocks = splitMatrix(A, newSize);
    double[][][][] B_blocks = splitMatrix(B, newSize);
    double[][][][] temp = new double[2][2][newSize][newSize];

    // Contracción tensorial: Σ (A_bloques[i][k] * B_bloques[k][j])
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int x = 0; x < newSize; x++) {
          Arrays.fill(temp[i][j][x], 0.0);
        }
        for (int k = 0; k < 2; k++) {
          double[][] product = new double[newSize][newSize];
          standardMultiply(A_blocks[i][k], B_blocks[k][j], product,
                  0, 0, 0, 0, 0, 0, newSize);
          for (int x = 0; x < newSize; x++) {
            for (int y = 0; y < newSize; y++) {
              temp[i][j][x][y] += product[x][y];
            }
          }
        }
      }
    }

    // Ensamblar resultado
    assembleMatrix(C, temp, newSize);
  }

  private static double[][][][] splitMatrix(double[][] M, int blockSize) {
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

  private static void assembleMatrix(double[][] C, double[][][][] blocks, int blockSize) {
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++) {
        for (int x = 0; x < blockSize; x++) {
          for (int y = 0; y < blockSize; y++) {
            C[i * blockSize + x][j * blockSize + y] = blocks[i][j][x][y];
          }
        }
      }
    }
  }

  private static void standardMultiply(double[][] A, double[][] B, double[][] C,
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

  public static void main(String[] args) {
    int n = 1024; // Tamaño de la matriz (preferiblemente potencia de 2)
    double[][] A = new double[n][n];
    double[][] B = new double[n][n];

    // Inicializar matrices con valores de prueba
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        A[i][j] = Math.random();
        B[i][j] = Math.random();
      }
    }

    long startTime = System.currentTimeMillis();
    double[][] C = multiply(A, B);
    long endTime = System.currentTimeMillis();

    System.out.println("Multiplicación completada en " + (endTime - startTime) + " ms");

    // Verificación opcional (solo para tamaños pequeños)
    if (n <= 8) {
      System.out.println("\nMatriz A:");
      printMatrix(A);
      System.out.println("\nMatriz B:");
      printMatrix(B);
      System.out.println("\nResultado C:");
      printMatrix(C);
    }
  }

  private static void printMatrix(double[][] matrix) {
    for (double[] row : matrix) {
      for (double val : row) {
        System.out.printf("%8.2f ", val);
      }
      System.out.println();
    }
  }
}