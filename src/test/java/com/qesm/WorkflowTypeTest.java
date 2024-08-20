package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

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

        ListenableDAG<ProductType, CustomEdge> dagType = workflowType.getDag();
        ListenableDAG<ProductIstance, CustomEdge> dagIstance = workflowIstance.getDag();
        // Checks the number of nodes and edges
        assertEquals(dagType.vertexSet().size(), dagIstance.vertexSet().size());
        assertEquals(dagType.edgeSet().size(), dagIstance.edgeSet().size());

        // Checks if all nodes matches (same attributes)
        assertTrue(workflowType.equalsNodesAttributes(workflowIstance));

        // Check in workflowIstance if all ProcessedTypes have their subgraph specified
        for (ProductIstance nodeIstance : dagIstance.vertexSet()) {

            if (nodeIstance.isProcessed()) {
                WorkflowIstance subgraphIstance = new WorkflowIstance(createSubgraph(dagIstance, nodeIstance));
                assertEquals(subgraphIstance, workflowIstance.getProductWorkflow(nodeIstance));
                // System.out.println(nodeIstance.getProductWorkflow().equals(subgraphIstance));
                // System.out.println("Istance workflow");
                // System.out.println(nodeIstance.getProductWorkflow());
                // System.out.println("SubgraphGenerated workflow");
                // System.out.println(subgraphIstance);
            }
        }

    }

    private ListenableDAG<ProductIstance, CustomEdge> createSubgraph(
            ListenableDAG<ProductIstance, CustomEdge> originalDAG, ProductIstance root) {
        ListenableDAG<ProductIstance, CustomEdge> subgraphDAG = new ListenableDAG<>(CustomEdge.class);

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
        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
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

        assertEquals(workflowIstance1, workflowIstance2);

        ProductType v5 = new ProductType("v5");
        workflowType.getDag().addVertex(v5);
        workflowType.getDag().addEdge(v5, v3);

        WorkflowIstance workflowIstance3 = workflowType.makeIstance();

        assertNotEquals(workflowIstance3, workflowIstance1);

    }

    @Test
    public void testDAGUpdating() {
        // DAG:
        // v0
        // v1
        // v2
        // v3
        // v4

        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 1, new UniformTime(0, 2));
        ProductType v2 = new ProductType("v2", 1, new UniformTime(0, 2));
        ProductType v3 = new ProductType("v3", 1, new UniformTime(0, 2));
        ProductType v4 = new ProductType("v4");
        ProductType v5 = new ProductType("v5");

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

        dag.addVertex(v5);
        dag.addEdge(v5, v2);

        // DAG:
        // v0
        // v1
        // v2
        // v3 v5
        // v4

        assertTrue(workflowType.getProductWorkflow(workflowType.findProduct("v2").get()).getDag().containsVertex(v5));
        assertTrue(workflowType.getProductWorkflow(workflowType.findProduct("v1").get()).getDag().containsVertex(v5));
        assertTrue(workflowType.getProductWorkflow(workflowType.findProduct("v0").get()).getDag().containsVertex(v5));

    }

    @Test
    public void testMakeIstanceOfSubGraph() {
        // DAG:
        // v0
        // v1
        // v2
        // v3
        // v4

        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);
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
