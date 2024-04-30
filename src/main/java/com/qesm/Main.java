package com.qesm;

public class Main {
    public static void main(String[] args) {
        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(10, 10, 3, 3);
        graphTest.sharedToUnsharedGraph(graphTest.getRootNode());
        graphTest.printDAG(graphTest.getSharedDag());
        
        graphTest.exportDAGDotLanguage("./output/sharedDAG.dot", graphTest.getSharedDag());
        //graphTest.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        graphTest.exportDAGDotLanguage("./output/unSharedDAG.dot", graphTest.getUnsharedDag());
        //graphTest.renderDotFile("./output/unSharedDAG.dot", "./media/unShared.png", 3);

        
        // graphTest.importDagDotLanguage("./output/DAG.dot");
        // graphTest.exportDAGDotLanguage("./output/DAG_Test_Copy.dot");
        // System.out.println(graphTest.isDagConnected());

    }
}