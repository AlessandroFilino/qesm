package com.qesm;

import com.qesm.ProductGraph.DagType;

public class Main {
    public static void main(String[] args) {
        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(10, 10, 3, 3);
        // graphTest.printDAG(DagType.SHARED);
        
        // graphTest.exportDAGDotLanguage("./output/sharedDAG.dot", DagType.SHARED);
        // graphTest.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        // graphTest.exportDAGDotLanguage("./output/unSharedDAG.dot", DagType.UNSHARED);
        // graphTest.renderDotFile("./output/unSharedDAG.dot", "./media/unShared.png", 3);

        
        // graphTest.importDagDotLanguage("./output/sharedDAGTest.dot");
        // graphTest.exportDAGDotLanguage("./output/unsharedDAGTest.dot", DagType.UNSHARED);
        // graphTest.renderDotFile("./output/unsharedDAGTest.dot", "./media/unsharedDAGTest.png", 3);
        // graphTest.renderDotFile("./output/sharedDAGTest.dot", "./media/sharedDAGTest.png", 3);
        

        // StructuredTree structuredTree = new StructuredTree(graphTest.getSharedDag(), graphTest.getRootNode(DagType.SHARED));
        StructuredTree structuredTree = new StructuredTree(graphTest.getUnsharedDag(), graphTest.getRootNode(DagType.UNSHARED));
        structuredTree.exportDAGDotLanguage("./output/structuredTree.dot");
        structuredTree.renderDotFile("./output/structuredTree.dot", "./media/structuredTree.png", 3);
        structuredTree.buildStructuredTree();
    }
}