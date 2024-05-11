package com.qesm;

import java.io.File;

import com.qesm.ProductGraph.DagType;

public class Main {
    public static void main(String[] args) {
        ensureFolderExists("media");
        ensureFolderExists("output");

        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(10, 10, 3, 3);

        // graphTest.renderDotFile("./output/test.dot", "./media/test.png", 3);
        // graphTest.printDAG(DagType.SHARED);
        
        // graphTest.exportDagToDotFile("./output/sharedDAG.dot", DagType.SHARED);
        // graphTest.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        // graphTest.exportDotFile("./output/unsharedDAG.dot", DagType.UNSHARED);
        // graphTest.renderDotFile("./output/unsharedDAG.dot", "./media/unshared.png", 3);

        // graphTest.importDagDotLanguage("./output/sharedDAGTest.dot");
        // graphTest.exportDAGDotLanguage("./output/unsharedDAGTest.dot", DagType.UNSHARED);
        // graphTest.renderDotFile("./output/unsharedDAGTest.dot", "./media/unsharedDAGTest.png", 3);
        // graphTest.renderDotFile("./output/sharedDAGTest.dot", "./media/sharedDAGTest.png", 3);

        // StructuredTree structuredTree = new StructuredTree(graphTest.getSharedDag(), graphTest.getRootNode(DagType.SHARED));
        // structuredTree.testPrint();
        StructuredTree structuredTree = new StructuredTree(graphTest.getUnsharedDag(), graphTest.getRootNode(DagType.UNSHARED));
        // structuredTree.exportStructuredTreeToDotFile("./output/structuredTree.dot");
        // structuredTree.renderDotFile("./output/structuredTree.dot", "./media/structuredTree.png", 3);
        structuredTree.buildStructuredTree();
        structuredTree.exportStructuredTreeToDotFile("./output/structuredTree1.dot");
        structuredTree.renderDotFile("./output/structuredTree1.dot", "./media/structuredTree1.png", 3);
    }

    public static void ensureFolderExists(String folderPath) {
        // Create a File object with the folder path
        File folder = new File(folderPath);

        // Check if the folder exists
        if (!folder.exists()) {
            // If the folder doesn't exist, try to create it
            boolean created = folder.mkdir();
            if (created) {
                System.out.println("The folder " + folderPath + " was created successfully.");
            } else {
                System.out.println("Error creating folder " + folderPath);
            }
        }
    }
}