package com.qesm;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class RendererTest {



    @Test
    public void testRenderWorkflowType(){

        WorkflowType workFlowType = new WorkflowType();
        workFlowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);

        renderWorkflowType(workFlowType);
    }

    @Test
    public void testRenderStructureTree(){
        WorkflowType workFlowType = new WorkflowType();
        workFlowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        
        StructuredTree structuredTree = new StructuredTree(workFlowType.getDag());
        structuredTree.buildStructuredTree();

        ensureFolderExists("output");
        ensureFolderExists("media");

        structuredTree.exportDotFileNoSerialization("./output/structuredTreeRenderTest.dot");
        Renderer.renderDotFile("./output/structuredTreeRenderTest.dot", "./media/structuredTreeRenderTest.png", 3);
        

        rmDotFile("./output/structuredTreeRenderTest.dot");
    }

    @Test
    public void testRenderStructureTreeWithAllSteps(){
        WorkflowType workFlowType = new WorkflowType();
        workFlowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workFlowType);

        StructuredTree structuredTree = new StructuredTree(workFlowType.getDag());

        ensureFolderExists("output");
        ensureFolderExists("media");

        String structuredTreeDotFolder = mkEmptyDir("./output/structuredTreeRenderTest");
        String structuredTreeMediaFolder = mkEmptyDir("./media/structuredTreeRenderTest");

        structuredTree.buildStructuredTreeAndExportSteps(structuredTreeDotFolder, false);
        Renderer.renderAllDotFile(structuredTreeDotFolder, structuredTreeMediaFolder, 3);

        rmDotFileFolder(structuredTreeDotFolder);
    }

    @Test
    public void testRenderWorkflowIstance(){
        WorkflowType workFlowType = new WorkflowType();

        workFlowType.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        renderWorkflowType(workFlowType);

        WorkflowIstance workFlowIstance = workFlowType.makeIstance();

        ensureFolderExists("output");
        ensureFolderExists("media");

        workFlowIstance.exportDotFileNoSerialization("./output/workFlowIstanceRendererTest.dot");
        Renderer.renderDotFile("./output/workFlowIstanceRendererTest.dot", "./media/workFlowIstanceRendererTest.png", 3);

        rmDotFile("./output/workFlowIstanceRendererTest.dot");
    }

    private static void renderWorkflowType(WorkflowType workflowTypeToRender){
        ensureFolderExists("output");
        ensureFolderExists("media");

        workflowTypeToRender.exportDotFileNoSerialization("./output/workFlowTypeRenderTest.dot");
        Renderer.renderDotFile("./output/workFlowTypeRenderTest.dot", "./media/workFlowTypeRenderTest.png", 3);
        
        rmDotFile("./output/workFlowTypeRenderTest.dot");
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
