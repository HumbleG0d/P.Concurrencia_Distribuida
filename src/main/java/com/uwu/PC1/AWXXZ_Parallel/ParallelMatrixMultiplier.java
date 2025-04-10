package com.uwu.PC1.AWXXZ_Parallel;

import static com.uwu.PC1.AWXXZ_Parallel.MatrixUtils.tensorContraction;

public class ParallelMatrixMultiplier {
  private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

  public static double[][] multiply(double[][] A, double[][] B) {
    int n = A.length;
    double[][] C = new double[n][n];
    Thread[] threads = new Thread[NUM_THREADS];

    // Asegurar que la matriz sea divisible en bloques 2x2
    if (n % 2 != 0) {
      throw new IllegalArgumentException("El tamaño de la matriz debe ser par");
    }

    // Dividir el trabajo en 4 bloques (2x2) y asignar hilos
    for (int t = 0; t < Math.min(NUM_THREADS, 4); t++) {
      final int block = t;
      threads[t] = new Thread(() -> {
        int i = block / 2;
        int j = block % 2;
        tensorContraction(A, B, C, n, i, j);
      });
      threads[t].start();
    }

    // Esperar a que todos los hilos terminen
    for (int t = 0; t < Math.min(NUM_THREADS, 4); t++) {
      try {
        threads[t].join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    return C;
  }
}
