package com.qesm;

public class Main {
    public static void main(String[] args) {
        ProductGraph graphTest = new ProductGraph();
        graphTest.generateRandomWorkflow(5, 10, 10);
        graphTest.drawGraph();

    }
}