package com.qesm.workflow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import com.qesm.workflow.CustomEdge;
import com.qesm.workflow.ProductTemplate;
import com.qesm.workflow.WorkflowTemplate;
import com.qesm.workflow.RandomDAGGenerator.PdfType;

public class DAGSharedToUnsharedConverterTest {

    // To test this method we generate a shared graph, then we create a list of
    // nodes end remove the duplicate. The final result is the edges number fo
    // shared graph
    @Test
    public void testCheckEdgeNumberFromUnsharedToShared() {
        WorkflowTemplate graphTest = new WorkflowTemplate();
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
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();
        workflowTemplate.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);

        HashMap<ProductTemplate, Integer> nodeNumberAfterConvMap = new HashMap<ProductTemplate, Integer>();
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> workflowDag = workflowTemplate.cloneDag();

        Integer totalNodeNumberAfterConv = 0;

        for (ProductTemplate node : workflowDag.vertexSet()) {
            totalNodeNumberAfterConv += getNodeNumberAfterConversion(node, workflowDag, nodeNumberAfterConvMap);
        }

        // System.out.println("Test ripetizione : " +
        // repetitionInfo.getCurrentRepetition());
        // System.out.println(workflowTemplate);
        workflowTemplate.toUnshared();
        workflowDag = workflowTemplate.cloneDag();
        // System.out.println(workflowTemplate);

        // Check if node number after conversion is as expected
        assertEquals(totalNodeNumberAfterConv, workflowDag.vertexSet().size());

        // Check that after conversion all nodes has not more of one outGoingEdge
        for (ProductTemplate node : workflowDag) {
            assertTrue(workflowDag.outDegreeOf(node) <= 1);
        }

    }

    private Integer getNodeNumberAfterConversion(ProductTemplate node,
            DirectedAcyclicGraph<ProductTemplate, CustomEdge> workflowDag,
            HashMap<ProductTemplate, Integer> nodeNumberAfterConvMap) {
        if (nodeNumberAfterConvMap.containsKey(node)) {
            return nodeNumberAfterConvMap.get(node);
        } else {
            Integer nodeNumberAfterConv = 0;
            for (CustomEdge descendantEdge : workflowDag.outgoingEdgesOf(node)) {
                ProductTemplate descendantNode = workflowDag.getEdgeTarget(descendantEdge);
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
