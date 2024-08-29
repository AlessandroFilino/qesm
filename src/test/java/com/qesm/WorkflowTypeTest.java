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
    void testMakeIstance() {
        // Check if the WorkflowIstance' dag matches the WorkflowType's one
        WorkflowType workflowType = new WorkflowType();

        workflowType.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workflowType.makeIstance();

        DirectedAcyclicGraph<ProductType, CustomEdge> dagType = workflowType.CloneDag();
        DirectedAcyclicGraph<ProductIstance, CustomEdge> dagInstance = workflowIstance.CloneDag();
        // Checks the number of nodes and edges
        assertEquals(dagType.vertexSet().size(), dagInstance.vertexSet().size());
        assertEquals(dagType.edgeSet().size(), dagInstance.edgeSet().size());

        // Checks if all nodes matches (same attributes)
        assertTrue(workflowType.equalsNodesAttributes(workflowIstance));

        // Check in workflowIstance if all ProcessedTypes have their subgraph specified
        for (ProductIstance nodeInstance : dagInstance.vertexSet()) {

            if (nodeInstance.isProcessed()) {
                WorkflowIstance subgraphIstance = new WorkflowIstance(createSubgraph(dagInstance, nodeInstance));
                assertTrue(subgraphIstance.equalsNodesAttributes(workflowIstance.getProductWorkflow(nodeInstance)));
                // System.out.println(nodeIstance.getProductWorkflow().equals(subgraphIstance));
                // System.out.println("Istance workflow");
                // System.out.println(nodeIstance.getProductWorkflow());
                // System.out.println("SubgraphGenerated workflow");
                // System.out.println(subgraphIstance);
            }
        }
    }

    private DirectedAcyclicGraph<ProductIstance, CustomEdge> createSubgraph(
            DirectedAcyclicGraph<ProductIstance, CustomEdge> originalDAG, ProductIstance root) {
        DirectedAcyclicGraph<ProductIstance, CustomEdge> subgraphDAG = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<ProductIstance> subgraphVertices = originalDAG.getAncestors(root);
        subgraphVertices.add(root);

        // Add vertices and edges to the subgraph
        for (ProductIstance vertex : subgraphVertices) {
            subgraphDAG.addVertex(vertex);
        }

        for (ProductIstance vertex : subgraphVertices) {
            Set<CustomEdge> edges = originalDAG.outgoingEdgesOf(vertex);
            for (CustomEdge edge : edges) {
                ProductIstance target = originalDAG.getEdgeTarget(edge);
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
        // Create a workflowtype, make an istance, modify the dag of WorkflowType and
        // make another istance.
        // Verify that the two istances are not equals
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
        WorkflowIstance workflowIstance1 = workflowType.makeIstance();
        WorkflowIstance workflowIstance2 = workflowType.makeIstance();

        assertTrue(workflowIstance1.equalsNodesAttributes(workflowIstance2));

        ProductType v5 = new ProductType("v5");
        workflowType.connectVertex(v5, v3);

        WorkflowIstance workflowIstance3 = workflowType.makeIstance();
        assertFalse(workflowIstance3.equalsNodesAttributes(workflowIstance1));

    }

    @Test
    public void testMakeIstanceOfSubGraph() {
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
        WorkflowIstance workflowIstanceV2 = workflowTypeV2.makeIstance();

        assertTrue(workflowTypeV2.equalsNodesAttributes(workflowIstanceV2));

    }
}
