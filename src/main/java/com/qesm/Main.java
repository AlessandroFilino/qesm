package com.qesm;

import java.io.File;
import java.math.BigDecimal;

import org.oristool.eulero.modeling.Activity;
import org.oristool.models.stpn.TransientSolution;
import org.oristool.models.stpn.TransientSolutionViewer;
import org.oristool.models.stpn.trans.RegTransient;
import org.oristool.models.stpn.trees.DeterministicEnablingState;
import org.oristool.petrinet.Marking;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;

import com.qesm.ProductGraph.DagType;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");

        ensureFolderExists("media");
        ensureFolderExists("output");

        ProductGraph graphTest = new ProductGraph();
        
        graphTest.generateRandomDAG(3, 3, 2, 5);
        
        graphTest.exportDagToDotFile("./output/sharedDAG.dot", DagType.SHARED);
        // graphTest.importDagFromDotFile("./output/sharedDAG.dot");
        // // Renderer.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);

        // graphTest.exportDagToDotFile("./output/unsharedDAG.dot", DagType.UNSHARED);
        // graphTest.importDagFromDotFile("./output/unsharedDAG.dot");
        // Renderer.renderDotFile("./output/unsharedDAG.dot", "./media/unshared.png", 3);

        // StructuredTree structuredTree = new StructuredTree(graphTest.getSharedDag(), graphTest.getRootNode(DagType.SHARED));
        StructuredTree structuredTree = new StructuredTree(graphTest.getUnsharedDag(), graphTest.getRootNode(DagType.UNSHARED));

        // structuredTree.buildStructuredTree();

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTree");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTree");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder, 3);
        
        StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(structuredTree.getStructuredWorkflow());
        Activity rootActivity = structuredTreeConverter.convertToActivity();

        DAGAnalyzer dagAnalyzer = new DAGAnalyzer(rootActivity);
        // dagAnalyzer.analyze();
        // dagAnalyzer.test2();
        dagAnalyzer.test3();

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