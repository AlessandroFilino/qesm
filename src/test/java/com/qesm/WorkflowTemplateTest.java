package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowTemplateTest {

    @Test
    void testMakeInstance() {
        // Check if the WorkflowInstance' dag matches the workflowTemplate's one
        WorkflowTemplate workflowTemplate = new WorkflowTemplate();

        workflowTemplate.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);
        WorkflowInstance workflowInstance = workflowTemplate.makeInstance();

        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dagTemplate = workflowTemplate.cloneDag();
        DirectedAcyclicGraph<ProductInstance, CustomEdge> dagInstance = workflowInstance.cloneDag();
        // Checks the number of nodes and edges
        assertEquals(dagTemplate.vertexSet().size(), dagInstance.vertexSet().size());
        assertEquals(dagTemplate.edgeSet().size(), dagInstance.edgeSet().size());

        // Checks if all nodes matches (same attributes)
        assertTrue(workflowTemplate.equalsNodesAttributes(workflowInstance));

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
        // Create a workflowTemplate, make an instance, modify the dag of
        // workflowTemplate and
        // make another instance.
        // Verify that the two instances are not equals
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductTemplate v0 = new ProductTemplate("v0", 1, new UniformTime(0, 2));
        ProductTemplate v1 = new ProductTemplate("v1", 1, new UniformTime(0, 2));
        ProductTemplate v2 = new ProductTemplate("v2", 1, new UniformTime(0, 2));
        ProductTemplate v3 = new ProductTemplate("v3", 1, new UniformTime(0, 2));
        ProductTemplate v4 = new ProductTemplate("v4");

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

        WorkflowTemplate workflowTemplate = new WorkflowTemplate(dag);
        WorkflowInstance workflowInstance1 = workflowTemplate.makeInstance();
        WorkflowInstance workflowInstance2 = workflowTemplate.makeInstance();

        assertTrue(workflowInstance1.equalsNodesAttributes(workflowInstance2));

        ProductTemplate v5 = new ProductTemplate("v5");
        workflowTemplate.connectVertex(v5, v3);

        WorkflowInstance workflowInstance3 = workflowTemplate.makeInstance();
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

        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductTemplate v0 = new ProductTemplate("v0", 1, new UniformTime(0, 2));
        ProductTemplate v1 = new ProductTemplate("v1", 1, new UniformTime(0, 2));
        ProductTemplate v2 = new ProductTemplate("v2", 1, new UniformTime(0, 2));
        ProductTemplate v3 = new ProductTemplate("v3", 1, new UniformTime(0, 2));
        ProductTemplate v4 = new ProductTemplate("v4");

        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);

        dag.addEdge(v4, v3);
        dag.addEdge(v3, v2);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);

        WorkflowTemplate workflowTemplate = new WorkflowTemplate(dag);
        WorkflowTemplate workflowTemplateV2 = (WorkflowTemplate) workflowTemplate
                .getProductWorkflow(workflowTemplate.findProduct("v2").get());
        WorkflowInstance workflowInstanceV2 = workflowTemplateV2.makeInstance();

        assertTrue(workflowTemplateV2.equalsNodesAttributes(workflowInstanceV2));

    }
}
