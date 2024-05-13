package com.qesm;

import java.io.File;

import com.qesm.ProductGraph.DagType;

public class Main {
    public static void main(String[] args) {
        ensureFolderExists("media");
        ensureFolderExists("output");

        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(10, 10, 2, 5);
        
        graphTest.exportDagToDotFile("./output/sharedDAG.dot", DagType.SHARED);
        // graphTest.importDagFromDotFile("./output/sharedDAG.dot");
        // Renderer.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        // graphTest.exportDagToDotFile("./output/unsharedDAG.dot", DagType.UNSHARED);
        // graphTest.importDagFromDotFile("./output/unsharedDAG.dot");
        // graphTest.renderDotFile("./output/unsharedDAG.dot", "./media/unshared.png", 3);

        StructuredTree structuredTree = new StructuredTree(graphTest.getSharedDag(), graphTest.getRootNode(DagType.SHARED));
        // StructuredTree structuredTree = new StructuredTree(graphTest.getUnsharedDag(), graphTest.getRootNode(DagType.UNSHARED));

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTree");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTree");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder, 3);

        
        
    }

    public static String mkEmptyDir(String folderPath){
        File folder = new File(folderPath);
        if(folder.isDirectory()){
            for (File file : folder.listFiles()) {
                if(file.getName().endsWith(".dot") || file.getName().endsWith(".png")){
                    file.delete();
                }
            }
        }
        else{
            folder.mkdir();
        }

        return folderPath;
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