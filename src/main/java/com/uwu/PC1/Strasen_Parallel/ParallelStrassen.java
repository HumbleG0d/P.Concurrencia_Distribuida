package com.uwu.PC1.Strasen_Parallel;

public class ParallelStrassen {
  private static final int THRESHOLD = 64;

  public static double[][] multiply(double[][] A, double[][] B) throws InterruptedException {
    int n = A.length;

    if (n <= THRESHOLD) {
      return MatrixUtils.standardMultiply(A, B);
    }

    int newSize = n / 2;
    double[][] A11 = new double[newSize][newSize];
    double[][] A12 = new double[newSize][newSize];
    double[][] A21 = new double[newSize][newSize];
    double[][] A22 = new double[newSize][newSize];

    double[][] B11 = new double[newSize][newSize];
    double[][] B12 = new double[newSize][newSize];
    double[][] B21 = new double[newSize][newSize];
    double[][] B22 = new double[newSize][newSize];

    MatrixUtils.splitMatrix(A, A11, A12, A21, A22);
    MatrixUtils.splitMatrix(B, B11, B12, B21, B22);

    double[][] P1 = new double[newSize][newSize];
    double[][] P2 = new double[newSize][newSize];
    double[][] P3 = new double[newSize][newSize];
    double[][] P4 = new double[newSize][newSize];
    double[][] P5 = new double[newSize][newSize];
    double[][] P6 = new double[newSize][newSize];
    double[][] P7 = new double[newSize][newSize];

    Thread[] threads = new Thread[]{
            new Thread(new StrassenTask(MatrixUtils.add(A11, A22), MatrixUtils.add(B11, B22), P1)),
            new Thread(new StrassenTask(MatrixUtils.add(A21, A22), B11, P2)),
            new Thread(new StrassenTask(A11, MatrixUtils.subtract(B12, B22), P3)),
            new Thread(new StrassenTask(A22, MatrixUtils.subtract(B21, B11), P4)),
            new Thread(new StrassenTask(MatrixUtils.add(A11, A12), B22, P5)),
            new Thread(new StrassenTask(MatrixUtils.subtract(A21, A11), MatrixUtils.add(B11, B12), P6)),
            new Thread(new StrassenTask(MatrixUtils.subtract(A12, A22), MatrixUtils.add(B21, B22), P7))
    };

    for (Thread t : threads) t.start();
    for (Thread t : threads) t.join();

    double[][] C11 = MatrixUtils.add(MatrixUtils.subtract(MatrixUtils.add(P1, P4), P5), P7);
    double[][] C12 = MatrixUtils.add(P3, P5);
    double[][] C21 = MatrixUtils.add(P2, P4);
    double[][] C22 = MatrixUtils.add(MatrixUtils.subtract(MatrixUtils.add(P1, P3), P2), P6);

    double[][] C = new double[n][n];
    MatrixUtils.joinMatrices(C11, C12, C21, C22, C);

    return C;
  }

  public static void main(String[] args) throws InterruptedException {
    int n = 1024;
    double[][] A = new double[n][n];
    double[][] B = new double[n][n];

    for (int i = 0; i < n; i++)
      for (int j = 0; j < n; j++) {
        A[i][j] = Math.random();
        B[i][j] = Math.random();
      }

    long startTime = System.currentTimeMillis();
    double[][] C = multiply(A, B);
    long endTime = System.currentTimeMillis();

    System.out.println("Tiempo de ejecución: " + (endTime - startTime) + " ms");
  }
}
