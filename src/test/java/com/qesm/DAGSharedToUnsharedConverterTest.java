package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class DAGSharedToUnsharedConverterTest {

    // To test this method we generate a shared graph, then we create a list of
    // nodes end remove the duplicate. The final result is the edges number fo
    // shared graph
    @Test
    public void testCheckEdgeNumberFromUnsharedToShared() {
        WorkflowType graphTest = new WorkflowType();
        graphTest.generateRandomDAG(5, 5, 2, 5, 60, PdfType.UNIFORM);
        int sharedEdgeNumber = graphTest.cloneDag().edgeSet().size();

        graphTest.toUnshared();
        Set<String> connectedNodesUnshared = new HashSet<>();
        for (CustomEdge edge : graphTest.cloneDag().edgeSet()) {
            String source = graphTest.cloneDag().getEdgeSource(edge).getName();
            String target = graphTest.cloneDag().getEdgeTarget(edge).getName();
            String nodePair = source.replaceAll("_.*", "") + "," + target.replaceAll("_.*", "");
            connectedNodesUnshared.add(nodePair);
        }

        int test_sharedEdgeNumber = connectedNodesUnshared.size();

        assertEquals(test_sharedEdgeNumber, sharedEdgeNumber);
    }

    @RepeatedTest(100)
    public void testCheckConversion(RepetitionInfo repetitionInfo) {
        WorkflowType workflowTypeTest = new WorkflowType();
        workflowTypeTest.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);

        HashMap<ProductType, Integer> nodeNumberAfterConvMap = new HashMap<ProductType, Integer>();
        DirectedAcyclicGraph<ProductType, CustomEdge> workflowDag = workflowTypeTest.cloneDag();

        Integer totalNodeNumberAfterConv = 0;

        for (ProductType node : workflowDag.vertexSet()) {
            totalNodeNumberAfterConv += getNodeNumberAfterConversion(node, workflowDag, nodeNumberAfterConvMap);
        }

        // System.out.println("Test ripetizione : " +
        // repetitionInfo.getCurrentRepetition());
        // System.out.println(workflowTypeTest);
        workflowTypeTest.toUnshared();
        workflowDag = workflowTypeTest.cloneDag();
        // System.out.println(workflowTypeTest);

        // Check if node number after conversion is as expected
        assertEquals(totalNodeNumberAfterConv, workflowDag.vertexSet().size());

        // Check that after conversion all nodes has not more of one outGoingEdge
        for (ProductType node : workflowDag) {
            assertTrue(workflowDag.outDegreeOf(node) <= 1);
        }

    }

    private Integer getNodeNumberAfterConversion(ProductType node,
            DirectedAcyclicGraph<ProductType, CustomEdge> workflowDag,
            HashMap<ProductType, Integer> nodeNumberAfterConvMap) {
        if (nodeNumberAfterConvMap.containsKey(node)) {
            return nodeNumberAfterConvMap.get(node);
        } else {
            Integer nodeNumberAfterConv = 0;
            for (CustomEdge descendantEdge : workflowDag.outgoingEdgesOf(node)) {
                ProductType descendantNode = workflowDag.getEdgeTarget(descendantEdge);
                nodeNumberAfterConv += getNodeNumberAfterConversion(descendantNode, workflowDag,
                        nodeNumberAfterConvMap);
            }
            // RootNode
            if (nodeNumberAfterConv == 0) {
                nodeNumberAfterConv = 1;
            }
            nodeNumberAfterConvMap.put(node, nodeNumberAfterConv);
            return nodeNumberAfterConv;
        }
    }
}
