package com.qesm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class DotFileConverterTest {

    private PdfType pdfType = PdfType.EXPONENTIAL;

    @Test
    void workflowTypeIOTest(){
        WorkflowType workFlowType1 = new WorkflowType();
        WorkflowType workFlowType2 = new WorkflowType();
        
        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        workFlowType1.exportDotFile("./output/workFlowType1.dot");

        workFlowType2.importDotFile("./output/workFlowType1.dot");
        workFlowType2.exportDotFile("./output/workFlowType2.dot");

        // Renderer.renderDotFile("./output/workFlowType1.dot", "./media/workFlowType1.png", 3);
        // Renderer.renderDotFile("./output/workFlowType2.dot", "./media/workFlowType2.png", 3);

        assertTrue(workFlowType1.equals(workFlowType2));
        
        rmDotFile("./output/workFlowType1.dot");
        rmDotFile("./output/workFlowType2.dot");
        
    }

    @Test
    void structuredTreeIOTest(){
        WorkflowType workFlowType1 = new WorkflowType();

        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        // workFlowType1.exportDotFile("./output/workFlowType1.dot");
        // Renderer.renderDotFile("./output/workFlowType1.dot", "./media/workFlowType1.png", 3);

        StructuredTree structuredTree1 = new StructuredTree(workFlowType1.getDag(), workFlowType1.getRootNode());
        structuredTree1.buildStructuredTree();
        structuredTree1.exportDotFile("./output/structuredTree1.dot");

        StructuredTree structuredTree2 = new StructuredTree();
        structuredTree2.importDotFile("./output/structuredTree1.dot");
        structuredTree2.exportDotFile("./output/structuredTree2.dot");

        assertTrue(structuredTree1.equals(structuredTree2));

        // Renderer.renderDotFile("./output/structuredTree1.dot", "./media/structuredTree1.png", 3);
        // Renderer.renderDotFile("./output/structuredTree2.dot", "./media/structuredTree2.png", 3);

        // rmDotFile("./output/workFlowType1.dot");
        rmDotFile("./output/structuredTree1.dot");
        rmDotFile("./output/structuredTree2.dot");
    }

    // NOTE: (serializing workflow object leads to massive string, dot render has a limit of (2^14 chars))  
    @Test
    void workflowIstanceIOTest(){
        WorkflowType workFlowType1 = new WorkflowType();

        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        // workFlowType1.exportDotFile("./output/workFlowType1.dot");
        // Renderer.renderDotFile("./output/workFlowType1.dot", "./media/workFlowType1.png", 3);

        Workflow workFlowIstance1 = workFlowType1.makeIstance();
        workFlowIstance1.exportDotFile("./output/workFlowIstance1.dot");
        // workFlowIstance1.exportDotFileNoSerialization("./output/workFlowIstance1NoSerialization.dot");

        Workflow workFlowIstance2 = new Workflow();
        workFlowIstance2.importDotFile("./output/workFlowIstance1.dot");

        // Renderer.renderDotFile("./output/workFlowIstance1NoSerialization.dot", "./media/workFlowIstance1.png", 3);

        assertTrue(workFlowIstance1.equals(workFlowIstance2));

        rmDotFile("./output/workFlowIstance1.dot");
        rmDotFile("./output/workFlowIstance1NoSerialization.dot");
        
    }

    private static void ensureFolderExists(String folderPath) {
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

    private static void rmDotFile(String dotFilePath){
        File dotFile = new File(dotFilePath);
        if(dotFile.getName().endsWith(".dot")){
            dotFile.delete();
        }
    }

}
