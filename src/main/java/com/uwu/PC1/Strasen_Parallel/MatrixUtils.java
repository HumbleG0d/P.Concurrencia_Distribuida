package com.uwu.PC1.Strasen_Parallel;

public class MatrixUtils {

  public static double[][] standardMultiply(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        for (int k = 0; k < n; k++) {
          C[i][j] += A[i][k] * B[k][j];
        }
      }
    }
    return C;
  }

  public static void splitMatrix(double[][] src, double[][] A11, double[][] A12,
                                 double[][] A21, double[][] A22) {
    int n = src.length / 2;
    for (int i = 0; i < n; i++) {
      System.arraycopy(src[i], 0, A11[i], 0, n);
      System.arraycopy(src[i], n, A12[i], 0, n);
      System.arraycopy(src[i + n], 0, A21[i], 0, n);
      System.arraycopy(src[i + n], n, A22[i], 0, n);
    }
  }

  public static void joinMatrices(double[][] C11, double[][] C12,
                                  double[][] C21, double[][] C22, double[][] dest) {
    int n = C11.length;
    for (int i = 0; i < n; i++) {
      System.arraycopy(C11[i], 0, dest[i], 0, n);
      System.arraycopy(C12[i], 0, dest[i], n, n);
      System.arraycopy(C21[i], 0, dest[i + n], 0, n);
      System.arraycopy(C22[i], 0, dest[i + n], n, n);
    }
  }

  public static double[][] add(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        C[i][j] = A[i][j] + B[i][j];
      }
    }
    return C;
  }

  public static double[][] subtract(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        C[i][j] = A[i][j] - B[i][j];
      }
    }
    return C;
  }
}
