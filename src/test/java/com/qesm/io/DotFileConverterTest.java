package com.qesm.io;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.qesm.tree.StructuredTree;
import com.qesm.workflow.ProductTemplate;
import com.qesm.workflow.WorkflowInstance;
import com.qesm.workflow.WorkflowTemplate;
import com.qesm.workflow.RandomDAGGenerator.PdfType;

public class DotFileConverterTest {

    private PdfType pdfType1 = PdfType.UNIFORM;
    private PdfType pdfType2 = PdfType.EXPONENTIAL;
    private PdfType pdfType3 = PdfType.ERLANG;
    private PdfType pdfType4 = PdfType.DETERMINISTIC;

    @Test
    void workflowTemplateIOTest() {
        workflowTemplateIO(pdfType1);
        workflowTemplateIO(pdfType2);
        workflowTemplateIO(pdfType3);
        workflowTemplateIO(pdfType4);
    }

    private void workflowTemplateIO(PdfType pdfType) {
        WorkflowTemplate workflowTemplate1 = new WorkflowTemplate();
        WorkflowTemplate workflowTemplate2 = new WorkflowTemplate();

        ensureFolderExists("output");

        workflowTemplate1.generateRandomDAG(3, 3, 2, 5, 60, pdfType);
        workflowTemplate1.exportDotFile("./output/workflowTemplate1.dot");

        workflowTemplate2.importDotFile("./output/workflowTemplate1.dot");
        workflowTemplate2.exportDotFile("./output/workflowTemplate2.dot");

        // Renderer.renderDotFile("./output/workflowTemplate1.dot",
        // "./media/workflowTemplate1.svg");
        // Renderer.renderDotFile("./output/workflowTemplate2.dot",
        // "./media/workflowTemplate2.svg");

        // System.out.println(workflowTemplate1);
        // System.out.println(workflowTemplate2);

        assertTrue(workflowTemplate1.equalsNodesAttributes(workflowTemplate2));

        rmDotFile("./output/workflowTemplate1.dot");
        rmDotFile("./output/workflowTemplate2.dot");
    }

    @Test
    void structuredTreeIOTest() {
        WorkflowTemplate workflowTemplate1 = new WorkflowTemplate();

        ensureFolderExists("output");

        workflowTemplate1.generateRandomDAG(5, 5, 2, 5, 60, pdfType1);
        // workflowTemplate1.exportDotFile("./output/workflowTemplate1.dot");
        // Renderer.renderDotFile("./output/workflowTemplate1.dot",
        // "./media/workflowTemplate1.svg", 3);

        StructuredTree<ProductTemplate> structuredTree1 = new StructuredTree<>(workflowTemplate1.cloneDag(),
                ProductTemplate.class);
        structuredTree1.buildStructuredTree();
        structuredTree1.exportDotFile("./output/structuredTree1.dot");

        StructuredTree<ProductTemplate> structuredTree2 = new StructuredTree<>(ProductTemplate.class);
        structuredTree2.importDotFile("./output/structuredTree1.dot");
        structuredTree2.exportDotFile("./output/structuredTree2.dot");

        assertTrue(structuredTree1.equals(structuredTree2));

        // Renderer.renderDotFile("./output/structuredTree1.dot",
        // "./media/structuredTree1.svg", 3);
        // Renderer.renderDotFile("./output/structuredTree2.dot",
        // "./media/structuredTree2.svg", 3);

        // rmDotFile("./output/workflowTemplate1.dot");
        rmDotFile("./output/structuredTree1.dot");
        rmDotFile("./output/structuredTree2.dot");
    }

    @Test
    void workflowInstanceIOTest() {
        WorkflowTemplate workflowTemplate1 = new WorkflowTemplate();

        ensureFolderExists("output");

        workflowTemplate1.generateRandomDAG(5, 5, 2, 5, 60, pdfType1);
        // workflowTemplate1.exportDotFile("./output/workflowTemplate1.dot");
        // Renderer.renderDotFile("./output/workflowTemplate1.dot",
        // "./media/workflowTemplate1.svg", 3);

        WorkflowInstance workFlowInstance1 = workflowTemplate1.makeInstance();
        workFlowInstance1.exportDotFile("./output/workFlowInstance1.dot");

        WorkflowInstance workFlowInstance2 = new WorkflowInstance();
        workFlowInstance2.importDotFile("./output/workFlowInstance1.dot");

        assertTrue(workFlowInstance1.equalsNodesAttributes(workFlowInstance2));

        rmDotFile("./output/workFlowInstance1.dot");

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
