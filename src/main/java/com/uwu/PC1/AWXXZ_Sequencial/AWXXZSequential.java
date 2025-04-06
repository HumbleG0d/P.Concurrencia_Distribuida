package com.uwu.PC1.AWXXZ_Sequencial;

public class AWXXZSequential {

  // Coeficientes optimizados basados en el trabajo teórico
  private static final double ALPHA = 0.6;
  private static final double BETA = 0.4;

  // Tamaño mínimo para dejar de dividir y usar multiplicación estándar
  private static final int CUTOFF = 64;

  public static double[][] multiply(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    multiplyRecursive(A, B, C, 0, 0, 0, 0, 0, 0, n);
    return C;
  }

  private static void multiplyRecursive(double[][] A, double[][] B, double[][] C,
                                        int aRow, int aCol, int bRow, int bCol,
                                        int cRow, int cCol, int size) {
    // Caso base: usar multiplicación estándar para bloques pequeños
    if (size <= CUTOFF) {
      standardMultiply(A, B, C, aRow, aCol, bRow, bCol, cRow, cCol, size);
      return;
    }

    int newSize = size / 2;

    // Submatrices temporales para los productos intermedios
    double[][] P1 = new double[newSize][newSize];
    double[][] P2 = new double[newSize][newSize];
    double[][] P3 = new double[newSize][newSize];
    double[][] P4 = new double[newSize][newSize];
    double[][] P5 = new double[newSize][newSize];
    double[][] P6 = new double[newSize][newSize];
    double[][] P7 = new double[newSize][newSize];
    double[][] P8 = new double[newSize][newSize];

    // Calcular los 8 productos intermedios (paso clave del algoritmo)
    // P1 = (A11 + α*A22) × (B11 + α*B22)
    addAndMultiply(A, B, P1,
            aRow, aCol, ALPHA, aRow + newSize, aCol + newSize,
            bRow, bCol, ALPHA, bRow + newSize, bCol + newSize,
            newSize);

    // P2 = (A21 + β*A12) × (B12 - β*B11)
    addAndMultiply(A, B, P2,
            aRow + newSize, aCol, BETA, aRow, aCol + newSize,
            bRow, bCol + newSize, -BETA, bRow, bCol,
            newSize);

    // P3 = (A11 - A21) × (B22 - B12)
    subtractAndMultiply(A, B, P3,
            aRow, aCol, aRow + newSize, aCol,
            bRow + newSize, bCol + newSize, bRow, bCol + newSize,
            newSize);

    // P4 = (α*A12 + A22) × (β*B21 - B22)
    linearCombineAndMultiply(A, B, P4,
            ALPHA, aRow, aCol + newSize, 1.0, aRow + newSize, aCol + newSize,
            BETA, bRow + newSize, bCol, -1.0, bRow + newSize, bCol + newSize,
            newSize);

    // P5 = A11 × B11 (producto directo)
    multiplyRecursive(A, B, P5,
            aRow, aCol, bRow, bCol,
            0, 0, newSize);

    // P6 = A12 × B21 (producto directo)
    multiplyRecursive(A, B, P6,
            aRow, aCol + newSize, bRow + newSize, bCol,
            0, 0, newSize);

    // P7 = A21 × B12 (producto directo)
    multiplyRecursive(A, B, P7,
            aRow + newSize, aCol, bRow, bCol + newSize,
            0, 0, newSize);

    // P8 = A22 × B22 (producto directo)
    multiplyRecursive(A, B, P8,
            aRow + newSize, aCol + newSize, bRow + newSize, bCol + newSize,
            0, 0, newSize);

    // Combinar los resultados para obtener los bloques de C
    combineResults(P1, P2, P3, P4, P5, P6, P7, P8, C, cRow, cCol, newSize);
  }

  // Método para A±B × C±D
  private static void addAndMultiply(double[][] A, double[][] B, double[][] result,
                                     int aRow1, int aCol1, double aCoeff1, int aRow2, int aCol2,
                                     int bRow1, int bCol1, double bCoeff1, int bRow2, int bCol2,
                                     int size) {
    double[][] tempA = new double[size][size];
    double[][] tempB = new double[size][size];

    // Calcular combinación lineal para A
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        tempA[i][j] = A[aRow1 + i][aCol1 + j] + aCoeff1 * A[aRow2 + i][aCol2 + j];
        tempB[i][j] = B[bRow1 + i][bCol1 + j] + bCoeff1 * B[bRow2 + i][bCol2 + j];
      }
    }

    standardMultiply(tempA, tempB, result, 0, 0, 0, 0, 0, 0, size);
  }

  // Método para (A-B) × (C-D)
  private static void subtractAndMultiply(double[][] A, double[][] B, double[][] result,
                                          int aRow, int aCol, int bRow, int bCol,
                                          int cRow, int cCol, int dRow, int dCol,
                                          int size) {
    double[][] tempA = new double[size][size];
    double[][] tempB = new double[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        tempA[i][j] = A[aRow + i][aCol + j] - B[bRow + i][bCol + j];
        tempB[i][j] = B[cRow + i][cCol + j] - B[dRow + i][dCol + j];
      }
    }

    standardMultiply(tempA, tempB, result, 0, 0, 0, 0, 0, 0, size);
  }

  // Método para (aA + bB) × (cC + dD)
  private static void linearCombineAndMultiply(double[][] A, double[][] B, double[][] result,
                                               double aCoeff, int aRow, int aCol,
                                               double bCoeff, int bRow, int bCol,
                                               double cCoeff, int cRow, int cCol,
                                               double dCoeff, int dRow, int dCol,
                                               int size) {
    double[][] tempA = new double[size][size];
    double[][] tempB = new double[size][size];

    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        tempA[i][j] = aCoeff * A[aRow + i][aCol + j] + bCoeff * A[bRow + i][bCol + j];
        tempB[i][j] = cCoeff * B[cRow + i][cCol + j] + dCoeff * B[dRow + i][dCol + j];
      }
    }

    standardMultiply(tempA, tempB, result, 0, 0, 0, 0, 0, 0, size);
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

  private static void combineResults(double[][] P1, double[][] P2, double[][] P3, double[][] P4,
                                     double[][] P5, double[][] P6, double[][] P7, double[][] P8,
                                     double[][] C, int cRow, int cCol, int size) {
    // Calcular los bloques de C según el esquema AWXXZ
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
