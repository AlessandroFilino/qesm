package com.qesm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowTypeTest {


    @Test
    void importExportTest(){
        WorkflowType workFlow1 = new WorkflowType();
        WorkflowType workFlow2 = new WorkflowType();
        
        ensureFolderExists("output");

        workFlow1.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);
        workFlow1.exportDagToDotFile("./output/workFlow1.dot");

        workFlow2.importDagFromDotFile("./output/workFlow1.dot");
        
        // System.out.println(workFlow1.equals(workFlow2));

        // System.out.println("WorkFlow1");
        // for (ProductType vertex : workFlow1.getDag().vertexSet()) {
        //     System.out.println(vertex);
        // }

        // for (CustomEdge edge : workFlow1.getDag().edgeSet()) {
        //     System.out.println(edge);
        // }

        // System.out.println("\nWorkFlow2\n");
        // for (ProductType vertex : workFlow2.getDag().vertexSet()) {
        //     System.out.println(vertex);
        // }

        // for (CustomEdge edge : workFlow2.getDag().edgeSet()) {
        //     System.out.println(edge);
        // }

        assertTrue(workFlow1.equals(workFlow2));

        rmDotFile("./output/workFlow1.dot");
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
