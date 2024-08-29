package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowTypeTest {

    @Test
    void testMakeInstance() {
        // Check if the WorkflowInstance' dag matches the WorkflowType's one
        WorkflowType workflowType = new WorkflowType();

        workflowType.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);
        WorkflowInstance workflowInstance = workflowType.makeInstance();

        DirectedAcyclicGraph<ProductType, CustomEdge> dagType = workflowType.cloneDag();
        DirectedAcyclicGraph<ProductInstance, CustomEdge> dagInstance = workflowInstance.cloneDag();
        // Checks the number of nodes and edges
        assertEquals(dagType.vertexSet().size(), dagInstance.vertexSet().size());
        assertEquals(dagType.edgeSet().size(), dagInstance.edgeSet().size());

        // Checks if all nodes matches (same attributes)
        assertTrue(workflowType.equalsNodesAttributes(workflowInstance));

        // Check in workflowInstance if all ProcessedTypes have their subgraph specified
        for (ProductInstance nodeInstance : dagInstance.vertexSet()) {

            if (nodeInstance.isProcessed()) {
                WorkflowInstance subgraphInstance = new WorkflowInstance(createSubgraph(dagInstance, nodeInstance));
                assertTrue(subgraphInstance.equalsNodesAttributes(workflowInstance.getProductWorkflow(nodeInstance)));
                // System.out.println(nodeInstance.getProductWorkflow().equals(subgraphInstance));
                // System.out.println("Instance workflow");
                // System.out.println(nodeInstance.getProductWorkflow());
                // System.out.println("SubgraphGenerated workflow");
                // System.out.println(subgraphInstance);
            }
        }
    }

    private DirectedAcyclicGraph<ProductInstance, CustomEdge> createSubgraph(
            DirectedAcyclicGraph<ProductInstance, CustomEdge> originalDAG, ProductInstance root) {
        DirectedAcyclicGraph<ProductInstance, CustomEdge> subgraphDAG = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<ProductInstance> subgraphVertices = originalDAG.getAncestors(root);
        subgraphVertices.add(root);

        // Add vertices and edges to the subgraph
        for (ProductInstance vertex : subgraphVertices) {
            subgraphDAG.addVertex(vertex);
        }

        for (ProductInstance vertex : subgraphVertices) {
            Set<CustomEdge> edges = originalDAG.outgoingEdgesOf(vertex);
            for (CustomEdge edge : edges) {
                ProductInstance target = originalDAG.getEdgeTarget(edge);
                if (subgraphVertices.contains(target)) {
                    CustomEdge subgraphEdge = new CustomEdge();
                    subgraphEdge.setQuantityRequired(edge.getQuantityRequired());
                    subgraphDAG.addEdge(vertex, target, subgraphEdge);
                }
            }
        }

        return subgraphDAG;
    }

    @Test
    void testReflection() {
        // Create a workflowtype, make an instance, modify the dag of WorkflowType and
        // make another instance.
        // Verify that the two instances are not equals
        DirectedAcyclicGraph<ProductType, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 1, new UniformTime(0, 2));
        ProductType v2 = new ProductType("v2", 1, new UniformTime(0, 2));
        ProductType v3 = new ProductType("v3", 1, new UniformTime(0, 2));
        ProductType v4 = new ProductType("v4");

        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);

        dag.addEdge(v4, v3);
        dag.addEdge(v4, v2);
        dag.addEdge(v3, v2);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);

        WorkflowType workflowType = new WorkflowType(dag);
        WorkflowInstance workflowInstance1 = workflowType.makeInstance();
        WorkflowInstance workflowInstance2 = workflowType.makeInstance();

        assertTrue(workflowInstance1.equalsNodesAttributes(workflowInstance2));

        ProductType v5 = new ProductType("v5");
        workflowType.connectVertex(v5, v3);

        WorkflowInstance workflowInstance3 = workflowType.makeInstance();
        assertFalse(workflowInstance3.equalsNodesAttributes(workflowInstance1));

    }

    @Test
    public void testMakeInstanceOfSubGraph() {
        // DAG:
        // v0
        // v1
        // v2
        // v3
        // v4

        DirectedAcyclicGraph<ProductType, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 1, new UniformTime(0, 2));
        ProductType v2 = new ProductType("v2", 1, new UniformTime(0, 2));
        ProductType v3 = new ProductType("v3", 1, new UniformTime(0, 2));
        ProductType v4 = new ProductType("v4");

        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);

        dag.addEdge(v4, v3);
        dag.addEdge(v3, v2);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);

        WorkflowType workflowType = new WorkflowType(dag);
        WorkflowType workflowTypeV2 = (WorkflowType) workflowType
                .getProductWorkflow(workflowType.findProduct("v2").get());
        WorkflowInstance workflowInstanceV2 = workflowTypeV2.makeInstance();

        assertTrue(workflowTypeV2.equalsNodesAttributes(workflowInstanceV2));

    }
}
