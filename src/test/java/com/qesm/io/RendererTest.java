package com.qesm.io;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.io.Renderer;
import com.qesm.tree.StructuredTree;
import com.qesm.workflow.CustomEdge;
import com.qesm.workflow.ProductInstance;
import com.qesm.workflow.ProductTemplate;
import com.qesm.workflow.WorkflowInstance;
import com.qesm.workflow.WorkflowTemplate;
import com.qesm.workflow.RandomDAGGenerator.PdfType;

public class RendererTest {

    @BeforeAll
    private static void setupFolders() {
        ensureFolderExists("output");
        ensureFolderExists("media");
    }

    @Test
    public void testRenderWorkflowTemplate() {

        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        renderworkflowTemplate(workflowTemplate);
    }

    @Test
    public void testRenderSharedAndUsharedworkflowTemplate() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderworkflowTemplate(workflowTemplate);
        workflowTemplate.toUnshared();
        workflowTemplate.exportDotFileNoSerialization("./output/workflowTemplateUnsharedRendererTest.dot");

        Renderer.renderDotFile("./output/workflowTemplateUnsharedRendererTest.dot",
                "./media/workflowTemplateUnsharedRendererTest.svg");
        // rmDotFile("./output/workflowTemplateUnsharedRendererTest.dot");

    }

    @Test
    public void testRenderSharedAndUsharedWorkflowInstance() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowInstance workflowInstance = workflowTemplate.makeInstance();

        workflowInstance.exportDotFileNoSerialization("./output/workflowInstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowInstanceRendererTest.dot", "./media/workflowInstanceRendererTest.svg");

        workflowInstance.toUnshared();
        workflowInstance.exportDotFileNoSerialization("./output/workflowInstanceUnsharedRendererTest.dot");
        Renderer.renderDotFile("./output/workflowInstanceUnsharedRendererTest.dot",
                "./media/workflowInstanceUnsharedRendererTest.svg");

        // rmDotFile("./output/workflowInstanceUnsharedRendererTest.dot");
        // rmDotFile("./output/workflowInstanceRendererTest.dot");

    }

    @Test
    public void testRenderRandomStructureTree() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        StructuredTree<ProductTemplate> structuredTree = new StructuredTree<>(workflowTemplate.cloneDag(),
                ProductTemplate.class);
        structuredTree.buildStructuredTree();

        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.svg");

        // rmDotFile("./output/structuredTreeRenderTest.dot");
    }

    @Test
    public void testRenderFixedStructureTree() {

        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductTemplate v0 = new ProductTemplate("v0", 1, new UniformTime(0, 2));
        ProductTemplate v1 = new ProductTemplate("v1", 2, new UniformTime(2, 4));
        ProductTemplate v2 = new ProductTemplate("v2", 3, new UniformTime(4, 6));
        ProductTemplate v3 = new ProductTemplate("v3", 4, new UniformTime(6, 8));
        ProductTemplate v4 = new ProductTemplate("v4");
        ProductTemplate v5 = new ProductTemplate("v5");
        ProductTemplate v6 = new ProductTemplate("v6");
        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);
        dag.addVertex(v5);
        dag.addVertex(v6);
        dag.addEdge(v3, v1);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);
        dag.addEdge(v4, v1);
        dag.addEdge(v5, v2);
        dag.addEdge(v6, v3);
        WorkflowTemplate wf1 = new WorkflowTemplate(dag);

        StructuredTree<ProductTemplate> structuredTree = new StructuredTree<>(wf1.getProductWorkflow(v0).cloneDag(),
                ProductTemplate.class);
        structuredTree.buildStructuredTree();
        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.svg");

    }

    @Test
    public void testRenderStructureTreeWithAllSteps() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderworkflowTemplate(workflowTemplate);

        StructuredTree<ProductTemplate> structuredTree = new StructuredTree<>(workflowTemplate.cloneDag(),
                ProductTemplate.class);

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTreeRenderTest");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTreeRenderTest");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder, false);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder);

        rmDotFileFolder(structuredTreeDotFolder);
    }

    @Test
    public void testRenderWorkflowInstance() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();

        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderworkflowTemplate(workflowTemplate);

        WorkflowInstance workflowInstance = workflowTemplate.makeInstance();

        workflowInstance.exportDotFileNoSerialization("./output/workflowInstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowInstanceRendererTest.dot", "./media/workflowInstanceRendererTest.svg");

        // rmDotFile("./output/workflowInstanceRendererTest.dot");
    }

    @Test
    public void testRenderWorkflowInstanceSubgraphs() {
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowInstance workflowInstance = workflowTemplate.makeInstance();

        String subgraphsDotFolder = mkEmptyDir("./output/subgraphsRenderTest");
        String subgraphsMediaFolder = mkEmptyDir("./media/subgraphsRenderTest");

        for (ProductInstance nodeInstance : workflowInstance.cloneDag().vertexSet()) {
            if (nodeInstance.isProcessed()) {
                workflowInstance.getProductWorkflow(nodeInstance).exportDotFileNoSerialization(
                        subgraphsDotFolder + "/subgraph" + nodeInstance.getName() + ".dot");
            }
        }

        Renderer.renderAllDotFile(subgraphsDotFolder, subgraphsMediaFolder);

        // rmDotFile("./output/workflowInstanceRendererTest.dot");
        // rmDotFileFolder("./output/subgraphsRenderTest");
    }

    @Test
    public void testFileError() {
        assertThrows(RuntimeException.class, () -> {
            Renderer.renderDotFile("test", "test");
        });

        assertThrows(RuntimeException.class, () -> {
            Renderer.renderAllDotFile("testtest", "testtest");
        });
        assertThrows(RuntimeException.class, () -> {
            Renderer.renderAllDotFile("test", "pom.xml");
        });
    }

    private static void renderworkflowTemplate(WorkflowTemplate workflowTemplateToRender) {
        workflowTemplateToRender.exportDotFileNoSerialization("./output/workflowTemplateRenderTest.dot");
        Renderer.renderDotFile("./output/workflowTemplateRenderTest.dot", "./media/workflowTemplateRenderTest.svg");

        // rmDotFile("./output/workflowTemplateRenderTest.dot");
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

    // private static void rmDotFile(String dotFilePath){
    // File dotFile = new File(dotFilePath);
    // if(dotFile.getName().endsWith(".dot")){
    // dotFile.delete();
    // }
    // }

    private static void rmDotFileFolder(String dotFileFolderPath) {
        File dotFileFolder = new File(dotFileFolderPath);
        if (dotFileFolder.isDirectory()) {
            for (File dotFile : dotFileFolder.listFiles()) {
                if (dotFile.getName().endsWith(".dot")) {
                    dotFile.delete();
                }
            }
            if (dotFileFolder.listFiles().length == 0) {
                dotFileFolder.delete();
            }
        }

    }

    public static String mkEmptyDir(String folderPath) {
        File folder = new File(folderPath);
        if (folder.isDirectory()) {
            for (File file : folder.listFiles()) {
                if (file.getName().endsWith(".dot") || file.getName().endsWith(".svg")) {
                    file.delete();
                }
            }
        } else {
            folder.mkdir();
        }

        return folderPath;
    }

}