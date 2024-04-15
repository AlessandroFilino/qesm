package com.qesm;

public class Main {
    public static void main(String[] args) {
        int maxDepth = 4;
        int maxWidth = 4;
        int branchingFactor = 4;

        ProductType workflow;
        GraphAdapter randomGraph = new GraphAdapter();
        workflow = randomGraph.generateRandomWorkflow(maxDepth, maxWidth, branchingFactor);

        randomGraph.printWorkflow(workflow);

    }
}