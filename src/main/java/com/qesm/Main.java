package com.qesm;

public class Main {
    public static void main(String[] args) {
        ProductGraph graphTest = new ProductGraph();
        
        
        graphTest.generateRandomDAG(10, 10, 3, 3);
        graphTest.printDAG();
        graphTest.exportDAGDotLanguage("./output/DAG.dot");
        graphTest.renderDotFile("./output/DAG.dot", "./media/test.png", 3);
        // graphTest.importDagDotLanguage("./output/DAG.dot");
        // graphTest.exportDAGDotLanguage("./output/DAG_Test_Copy.dot");
        // System.out.println(graphTest.isDagConnected());

    }
}