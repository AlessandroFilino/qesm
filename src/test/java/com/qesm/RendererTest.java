package com.qesm;

import java.io.File;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class RendererTest {

    @BeforeAll
    private static void setupFolders(){
        ensureFolderExists("output");
        ensureFolderExists("media");
    }

    @Test
    public void testRenderWorkflowType(){

        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        renderWorkflowType(workflowType);
    }

    @Test
    public void testRenderSharedAndUsharedWorkflowType(){
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);
        workflowType.toUnshared();
        workflowType.exportDotFileNoSerialization("./output/workflowTypeUnsharedRendererTest.dot");

        Renderer.renderDotFile("./output/workflowTypeUnsharedRendererTest.dot", "./media/workflowTypeUnsharedRendererTest.png", 3);
        rmDotFile("./output/workflowTypeUnsharedRendererTest.dot");
        
    }

    @Test
    public void testRenderSharedAndUsharedWorkflowIstance(){
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workflowType.makeIstance();
        
        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceRendererTest.dot", "./media/workflowIstanceRendererTest.png", 3);

        workflowIstance.toUnshared();
        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceUnsharedRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceUnsharedRendererTest.dot", "./media/workflowIstanceUnsharedRendererTest.png", 3);

        rmDotFile("./output/workflowIstanceUnsharedRendererTest.dot");
        rmDotFile("./output/workflowIstanceRendererTest.dot");
        
    }

    @Test
    public void testRenderStructureTree(){
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        
        StructuredTree structuredTree = new StructuredTree(workflowType.getDag());
        structuredTree.buildStructuredTree();

        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.png", 3);
        

        rmDotFile("./output/structuredTreeRenderTest.dot");
    }

    @Test
    public void testRenderStructureTreeWithAllSteps(){
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);

        StructuredTree structuredTree = new StructuredTree(workflowType.getDag());

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTreeRenderTest");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTreeRenderTest");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder, false);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder, 3);

        rmDotFileFolder(structuredTreeDotFolder);
    }

    @Test
    public void testRenderWorkflowIstance(){
        WorkflowType workflowType = new WorkflowType();

        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workflowType);

        WorkflowIstance workflowIstance = workflowType.makeIstance();

        workflowIstance.exportDotFileNoSerialization("./output/workflowIstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workflowIstanceRendererTest.dot", "./media/workflowIstanceRendererTest.png", 3);

        rmDotFile("./output/workflowIstanceRendererTest.dot");
    }

    @Test
    public void testRenderWorkflowIstanceSubgraphs(){
        WorkflowType workflowType = new WorkflowType();
        workflowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workflowType.makeIstance();

        String subgraphsDotFolder = mkEmptyDir("./output/subgraphsRenderTest");
        String subgraphsMediaFolder = mkEmptyDir("./media/subgraphsRenderTest");

        for (ProductIstance nodeIstance : workflowIstance.getDag().vertexSet()) {
            if(nodeIstance.isProcessedType()){
                nodeIstance.getProductWorkflow().exportDotFileNoSerialization(subgraphsDotFolder + "/subgraph" + nodeIstance.getNameType() + ".dot");
            }
        }

        Renderer.renderAllDotFile(subgraphsDotFolder, subgraphsMediaFolder, 3);

        rmDotFile("./output/workflowIstanceRendererTest.dot");
        rmDotFileFolder("./output/subgraphsRenderTest");
    }

    private static void renderWorkflowType(WorkflowType workflowTypeToRender){
        workflowTypeToRender.exportDotFileNoSerialization("./output/workflowTypeRenderTest.dot");
        Renderer.renderDotFile("./output/workflowTypeRenderTest.dot", "./media/workflowTypeRenderTest.png", 3);
        
        rmDotFile("./output/workflowTypeRenderTest.dot");
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

    private static void rmDotFileFolder(String dotFileFolderPath){
        File dotFileFolder = new File(dotFileFolderPath);
        if(dotFileFolder.isDirectory()){
            for (File dotFile : dotFileFolder.listFiles()) {
                if(dotFile.getName().endsWith(".dot")){
                    dotFile.delete();
                }
            }
            if(dotFileFolder.listFiles().length == 0){
                dotFileFolder.delete();
            }
        }
        
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

}
