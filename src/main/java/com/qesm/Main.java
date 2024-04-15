package com.qesm;

public class Main {
    public static void main(String[] args) {
        //System.out.println("Hello world!");
        int maxDepth = 4;
        int maxWidth = 4;
        int branchingFactor = 4;

        GraphAdapter randomWorkflow = new GraphAdapter();
        randomWorkflow.generateRandomWorkflow(maxDepth, maxWidth, branchingFactor);

    }
}