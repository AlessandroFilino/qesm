package com.qesm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class DotFileConverterTest {

    private PdfType pdfType = PdfType.EXPONENTIAL;

    @Test
    void workflowTypeIOTest() {
        WorkflowType workFlowType1 = new WorkflowType();
        WorkflowType workFlowType2 = new WorkflowType();

        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        workFlowType1.exportDotFile("./output/workFlowType1.dot");

        workFlowType2.importDotFile("./output/workFlowType1.dot");
        workFlowType2.exportDotFile("./output/workFlowType2.dot");

        // Renderer.renderDotFile("./output/workFlowType1.dot",
        // "./media/workFlowType1.svg", 3);
        // Renderer.renderDotFile("./output/workFlowType2.dot",
        // "./media/workFlowType2.svg", 3);

        assertTrue(workFlowType1.equals(workFlowType2));

        rmDotFile("./output/workFlowType1.dot");
        rmDotFile("./output/workFlowType2.dot");

    }

    @Test
    void structuredTreeIOTest() {
        WorkflowType workFlowType1 = new WorkflowType();

        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        // workFlowType1.exportDotFile("./output/workFlowType1.dot");
        // Renderer.renderDotFile("./output/workFlowType1.dot",
        // "./media/workFlowType1.svg", 3);

        StructuredTree<ProductType> structuredTree1 = new StructuredTree<>(workFlowType1.CloneDag(),
                ProductType.class);
        structuredTree1.buildStructuredTree();
        structuredTree1.exportDotFile("./output/structuredTree1.dot");

        StructuredTree<ProductType> structuredTree2 = new StructuredTree<>(ProductType.class);
        structuredTree2.importDotFile("./output/structuredTree1.dot");
        structuredTree2.exportDotFile("./output/structuredTree2.dot");

        assertTrue(structuredTree1.equals(structuredTree2));

        // Renderer.renderDotFile("./output/structuredTree1.dot",
        // "./media/structuredTree1.svg", 3);
        // Renderer.renderDotFile("./output/structuredTree2.dot",
        // "./media/structuredTree2.svg", 3);

        // rmDotFile("./output/workFlowType1.dot");
        rmDotFile("./output/structuredTree1.dot");
        rmDotFile("./output/structuredTree2.dot");
    }

    @Test
    void workflowIstanceIOTest() {
        WorkflowType workFlowType1 = new WorkflowType();

        ensureFolderExists("output");

        workFlowType1.generateRandomDAG(5, 5, 2, 5, 60, pdfType);
        // workFlowType1.exportDotFile("./output/workFlowType1.dot");
        // Renderer.renderDotFile("./output/workFlowType1.dot",
        // "./media/workFlowType1.svg", 3);

        WorkflowIstance workFlowIstance1 = workFlowType1.makeIstance();
        workFlowIstance1.exportDotFile("./output/workFlowIstance1.dot");

        WorkflowIstance workFlowIstance2 = new WorkflowIstance();
        workFlowIstance2.importDotFile("./output/workFlowIstance1.dot");

        assertTrue(workFlowIstance1.equals(workFlowIstance2));

        rmDotFile("./output/workFlowIstance1.dot");

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

    private static void rmDotFile(String dotFilePath) {
        File dotFile = new File(dotFilePath);
        if (dotFile.getName().endsWith(".dot")) {
            dotFile.delete();
        }
    }

}
