package com.qesm;

import java.io.File;
import java.math.BigDecimal;

import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;
import org.oristool.petrinet.PetriNet;
import org.oristool.petrinet.Place;

import com.qesm.ProductType.ItemType;
import com.qesm.RandomDAGGenerator.PdfType;

public class Main {
    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");

        ensureFolderExists("media");
        ensureFolderExists("output");

        WorkflowType graphTest = new WorkflowType();
        
        graphTest.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        
        graphTest.exportDagToDotFile("./output/sharedDAG.dot");
       // graphTest.importDagFromDotFile("./output/sharedDAG.dot");
        Renderer.renderDotFile("./output/sharedDAG.dot", "./media/shared.png", 3);
        // graphTest.toUnshared();
        
        
        // graphTest.exportDagToDotFile("./output/unsharedDAG.dot");
        // // graphTest.importDagFromDotFile("./output/unsharedDAG.dot");
        // Renderer.renderDotFile("./output/unsharedDAG.dot", "./media/unshared.png", 3);

        // TODO: add method to export and render all subgraphs
        Workflow workflow = graphTest.makeIstance();
        workflow.exportDagToDotFile("./output/istance.dot");
        Renderer.renderDotFile("./output/istance.dot", "./media/istance.png", 3);
        int i = 2;
        for (Product product : workflow.getDag().vertexSet()) {
            if(product.getItemType() == ItemType.PROCESSED){
                i--;
                if(i == 0){
                    product.getProductWorkflow().exportDagToDotFile("./output/istanceSubgraph.dot");
                    Renderer.renderDotFile("./output/istanceSubgraph.dot", "./media/istanceSubgraph.png", 3);
                } 
            }
            
        }

        // StructuredTree structuredTree = new StructuredTree(graphTest.getDag(), graphTest.getRootNode());

        // structuredTree.buildStructuredTree();

        // String structuredTreeDotFolder = mkEmptyDir("./output/structuredTree");
        // String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTree");

        // structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder);
        // Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder, 3);
        
        // StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(structuredTree.getStructuredWorkflow());
        // Activity rootActivity = structuredTreeConverter.convertToActivity();
        // System.out.println(rootActivity);


        // PetriNet net = new PetriNet();
        // Place pOut = net.addPlace("FINAL_PLACE");
        // Place pIn = net.addPlace("STARTING_PLACE");
        // rootActivity.buildSTPN(net, pIn, pOut, 0);

        
        // System.out.println(net);

        // DAGAnalyzer dagAnalyzer = new DAGAnalyzer();
        // dagAnalyzer.analyzeActivity(rootActivity);
        // dagAnalyzer.analizePetriNet(net, pOut, pIn);

        // dagAnalyzer.testPetriNet1();
        // dagAnalyzer.testPetriNet2();
        

    }

    public static Activity generateTestActivity(){
        StochasticTime pdf = new ExponentialTime(BigDecimal.valueOf(5));
        
        Activity t0 = new Simple("t0", pdf);
        
        
        return ModelFactory.DAG(t0);
    }

    public static Activity generateTestActivity2(){
        StochasticTime pdf = new UniformTime(0, 1);
        
        Activity t0 = new Simple("t0", pdf);
        Activity t1 = new Simple("t1", pdf);

        Activity s0 = ModelFactory.sequence(t0,t1);

        Activity t3 = new Simple("t3", pdf);
        Activity t5 = new Simple("t5", pdf);

        t3.addPrecondition(s0);
        t5.addPrecondition(t3);
        return ModelFactory.DAG(t3, s0, t5);
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