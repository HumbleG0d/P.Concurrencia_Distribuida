package com.uwu.PC1.Strasen_Sequencial;

public class StrassenSequencial {
  private static final int THRESHOLD = 64; // Umbral para cambiar a multiplicación estándar

  public static double[][] multiply(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];

    if (n <= THRESHOLD) {
      return standardMultiply(A, B);
    }

    // Dividir matrices en submatrices
    int newSize = n / 2;
    double[][] A11 = new double[newSize][newSize];
    double[][] A12 = new double[newSize][newSize];
    double[][] A21 = new double[newSize][newSize];
    double[][] A22 = new double[newSize][newSize];

    double[][] B11 = new double[newSize][newSize];
    double[][] B12 = new double[newSize][newSize];
    double[][] B21 = new double[newSize][newSize];
    double[][] B22 = new double[newSize][newSize];

    splitMatrix(A, A11, A12, A21, A22);
    splitMatrix(B, B11, B12, B21, B22);

    // Calcular los 7 productos de Strassen
    double[][] P1 = multiply(add(A11, A22), add(B11, B22));
    double[][] P2 = multiply(add(A21, A22), B11);
    double[][] P3 = multiply(A11, subtract(B12, B22));
    double[][] P4 = multiply(A22, subtract(B21, B11));
    double[][] P5 = multiply(add(A11, A12), B22);
    double[][] P6 = multiply(subtract(A21, A11), add(B11, B12));
    double[][] P7 = multiply(subtract(A12, A22), add(B21, B22));

    // Calcular submatrices del resultado
    double[][] C11 = add(subtract(add(P1, P4), P5), P7);
    double[][] C12 = add(P3, P5);
    double[][] C21 = add(P2, P4);
    double[][] C22 = add(subtract(add(P1, P3), P2), P6);

    // Combinar resultados
    joinMatrices(C11, C12, C21, C22, C);

    return C;
  }

  // Método de multiplicación estándar O(n³)
  private static double[][] standardMultiply(double[][] A, double[][] B) {
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

  // Métodos auxiliares
  private static void splitMatrix(double[][] src, double[][] dest11, double[][] dest12,
                                  double[][] dest21, double[][] dest22) {
    int n = src.length / 2;
    for (int i = 0; i < n; i++) {
      System.arraycopy(src[i], 0, dest11[i], 0, n);
      System.arraycopy(src[i], n, dest12[i], 0, n);
      System.arraycopy(src[i + n], 0, dest21[i], 0, n);
      System.arraycopy(src[i + n], n, dest22[i], 0, n);
    }
  }

  private static void joinMatrices(double[][] src11, double[][] src12,
                                   double[][] src21, double[][] src22, double[][] dest) {
    int n = src11.length;
    for (int i = 0; i < n; i++) {
      System.arraycopy(src11[i], 0, dest[i], 0, n);
      System.arraycopy(src12[i], 0, dest[i], n, n);
      System.arraycopy(src21[i], 0, dest[i + n], 0, n);
      System.arraycopy(src22[i], 0, dest[i + n], n, n);
    }
  }

  private static double[][] add(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        C[i][j] = A[i][j] + B[i][j];
      }
    }
    return C;
  }

  private static double[][] subtract(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        C[i][j] = A[i][j] - B[i][j];
      }
    }
    return C;
  }

  public static void main(String[] args) {
    int n = 1024; // Tamaño debe ser potencia de 2
    double[][] A = new double[n][n];
    double[][] B = new double[n][n];

    // Inicializar matrices con valores aleatorios
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        A[i][j] = Math.random();
        B[i][j] = Math.random();
      }
    }

    long startTime = System.currentTimeMillis();
    double[][] C = multiply(A, B);
    long endTime = System.currentTimeMillis();

    System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
  }
}
