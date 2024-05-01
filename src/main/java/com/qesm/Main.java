package com.qesm;

import com.qesm.ProductGraph.DagType;

public class Main {
    public static void main(String[] args) {
        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(3, 3, 2, 2);
        graphTest.sharedToUnsharedGraph(graphTest.getRootNode());
        
        graphTest.printDAG(DagType.SHARED);
        
        graphTest.exportDAGDotLanguage("./output/sharedDAG.dot", DagType.SHARED);
        graphTest.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        graphTest.exportDAGDotLanguage("./output/unSharedDAG.dot", DagType.UNSHARED);
        graphTest.renderDotFile("./output/unSharedDAG.dot", "./media/unShared.png", 3);

        
        // graphTest.importDagDotLanguage("./output/DAG.dot");
        // graphTest.exportDAGDotLanguage("./output/DAG_Test_Copy.dot");
        // System.out.println(graphTest.isDagConnected());

    }
}