package com.qesm;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class RendererTest {

    @BeforeAll
    private static void setupFolders() {
        ensureFolderExists("output");
        ensureFolderExists("media");
    }

    @Test
    public void testRenderWorkflowType() {

        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        renderWorkflowType(workflowType);
    }

    @Test
    public void testRenderSharedAndUsharedWorkflowType() {
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);
        workflowType.toUnshared();
        workflowType.exportDotFileNoSerialization("./output/workflowTypeUnsharedRendererTest.dot");

        Renderer.renderDotFile("./output/workflowTypeUnsharedRendererTest.dot",
                "./media/workflowTypeUnsharedRendererTest.svg");
        // rmDotFile("./output/workflowTypeUnsharedRendererTest.dot");

    }

    @Test
    public void testRenderSharedAndUsharedWorkflowIstance() {
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workflowType.makeIstance();

        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceRendererTest.dot", "./media/workflowIstanceRendererTest.svg");

        workflowIstance.toUnshared();
        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceUnsharedRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceUnsharedRendererTest.dot",
                "./media/workflowIstanceUnsharedRendererTest.svg");

        // rmDotFile("./output/workflowIstanceUnsharedRendererTest.dot");
        // rmDotFile("./output/workflowIstanceRendererTest.dot");

    }

    @Test
    public void testRenderRandomStructureTree() {
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        StructuredTree<ProductType> structuredTree = new StructuredTree<>(workflowType.getDagCopy(), ProductType.class);
        structuredTree.buildStructuredTree();

        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.svg");

        // rmDotFile("./output/structuredTreeRenderTest.dot");
    }

    @Test
    public void testRenderFixedStructureTree() {

        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 2, new UniformTime(2, 4));
        ProductType v2 = new ProductType("v2", 3, new UniformTime(4, 6));
        ProductType v3 = new ProductType("v3", 4, new UniformTime(6, 8));
        ProductType v4 = new ProductType("v4");
        ProductType v5 = new ProductType("v5");
        ProductType v6 = new ProductType("v6");
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
        WorkflowType wf1 = new WorkflowType(dag);

        StructuredTree<ProductType> structuredTree = new StructuredTree<>(wf1.getProductWorkflow(v0).getDagCopy(),
                ProductType.class);
        structuredTree.buildStructuredTree();
        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.svg");

    }

    @Test
    public void testRenderStructureTreeWithAllSteps() {
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);

        StructuredTree<ProductType> structuredTree = new StructuredTree<>(workflowType.getDagCopy(), ProductType.class);

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTreeRenderTest");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTreeRenderTest");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder, false);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder);

        rmDotFileFolder(structuredTreeDotFolder);
    }

    @Test
    public void testRenderWorkflowIstance() {
        WorkflowType workflowType = new WorkflowType();

        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);

        WorkflowIstance workflowIstance = workflowType.makeIstance();

        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceRendererTest.dot", "./media/workflowIstanceRendererTest.svg");

        // rmDotFile("./output/workflowIstanceRendererTest.dot");
    }

    @Test
    public void testRenderWorkflowIstanceSubgraphs() {
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workflowType.makeIstance();

        String subgraphsDotFolder = mkEmptyDir("./output/subgraphsRenderTest");
        String subgraphsMediaFolder = mkEmptyDir("./media/subgraphsRenderTest");

        for (ProductIstance nodeIstance : workflowIstance.getDagCopy().vertexSet()) {
            if (nodeIstance.isProcessed()) {
                workflowIstance.getProductWorkflow(nodeIstance).exportDotFileNoSerialization(
                        subgraphsDotFolder + "/subgraph" + nodeIstance.getName() + ".dot");
            }
        }

        Renderer.renderAllDotFile(subgraphsDotFolder, subgraphsMediaFolder);

        // rmDotFile("./output/workflowIstanceRendererTest.dot");
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

    private static void renderWorkflowType(WorkflowType workflowTypeToRender) {
        workflowTypeToRender.exportDotFileNoSerialization("./output/workflowTypeRenderTest.dot");
        Renderer.renderDotFile("./output/workflowTypeRenderTest.dot", "./media/workflowTypeRenderTest.svg");

        // rmDotFile("./output/workflowTypeRenderTest.dot");
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
