package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class DAGSharedToUnsharedConverterTest {

    //TODO Change the test method. Form shared, count the expected edges and nodes
    //To test this method we generate a shared graph, then we create a list of nodes end remove the duplicate. The final result is the edges number fo shared graph 
    @Test
    public void testCheckEdgeNumberFromUnsharedToShared(){
        WorkflowType graphTest = new WorkflowType();
        graphTest.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        int sharedEdgeNumber = graphTest.getDag().edgeSet().size();
        
        graphTest.toUnshared();
        Set<String> connectedNodesUnshared = new HashSet<>();
        for (CustomEdge edge : graphTest.getDag().edgeSet()) {
            String source = graphTest.getDag().getEdgeSource(edge).getNameType();
            String target = graphTest.getDag().getEdgeTarget(edge).getNameType();
            String nodePair = source.replaceAll("_.*", "") + "," + target.replaceAll("_.*", "");
            connectedNodesUnshared.add(nodePair);
        }

        int test_sharedEdgeNumber = connectedNodesUnshared.size();

        assertEquals(test_sharedEdgeNumber, sharedEdgeNumber);
    }

    @Test
    public void testGenerateUsharedGraph(){
        WorkflowType graphTest = new WorkflowType();
        graphTest.generateRandomDAG(10, 10, 5, 5, 60, PdfType.UNIFORM);
        graphTest.toUnshared();
        graphTest.exportDotFileNoSerialization("./output/WorkflowTypeTest.dot");
        Renderer.renderDotFile("./output/WorkflowTypeTest.dot", "./media/WorkflowTypeTest.png", 3);
    }
}
