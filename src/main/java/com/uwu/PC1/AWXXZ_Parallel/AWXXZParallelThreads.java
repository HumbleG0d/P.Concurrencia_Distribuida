package com.uwu.PC1.AWXXZ_Parallel;

public class AWXXZParallelThreads {
  public static void main(String[] args) {
    int n = 1024; // Tamaño de la matriz (potencia de 2)
    double[][] A = MatrixUtils.createRandomMatrix(n);
    double[][] B = MatrixUtils.createRandomMatrix(n);

    try {
      long startTime = System.currentTimeMillis();
      double[][] C = ParallelMatrixMultiplier.multiply(A, B);
      long endTime = System.currentTimeMillis();

      System.out.println("Multiplicación paralela completada en " + (endTime - startTime) + " ms");
    } catch (InterruptedException e) {
      System.err.println("Error en la ejecución paralela: " + e.getMessage());
      Thread.currentThread().interrupt();
    }
  }
}