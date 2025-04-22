package com.uwu.PC1.Strasen_Parallel;

public class StrassenTask implements Runnable {
  private final double[][] A;
  private final double[][] B;
  private final double[][] result;

  public StrassenTask(double[][] A, double[][] B, double[][] result) {
    this.A = A;
    this.B = B;
    this.result = result;
  }

  @Override
  public void run() {
    try {
      double[][] product = ParallelStrassen.multiply(A, B);
      for (int i = 0; i < product.length; i++) {
        System.arraycopy(product[i], 0, result[i], 0, product[i].length);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
