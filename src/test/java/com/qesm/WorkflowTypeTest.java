package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowTypeTest {

    @Test
    void testMakeIstance() {
        // Check if the WorkflowIstance' dag matches the WorkflowType's one
        WorkflowType workFlowType = new WorkflowType();

        workFlowType.generateRandomDAG(3, 3, 2, 5, 60, PdfType.UNIFORM);
        WorkflowIstance workflowIstance = workFlowType.makeIstance();

        DirectedAcyclicGraph<ProductType, CustomEdge> dagType = workFlowType.getDag();
        DirectedAcyclicGraph<ProductIstance, CustomEdge> dagIstance = workflowIstance.getDag();
        // Checks the number of nodes and edges
        assertEquals(dagType.vertexSet().size(), dagIstance.vertexSet().size());
        assertEquals(dagType.edgeSet().size(), dagIstance.edgeSet().size());
        
        // Checks if all nodes matches (same attributes and same number of in/out edges) 
        ArrayList<ProductType> workflowTypeNodes = new ArrayList<>();
        ArrayList<ProductIstance> workflowIstanceNodes = new ArrayList<>();

        Iterator<ProductType> iterWorkflowType = new DepthFirstIterator<ProductType, CustomEdge>(dagType);
        while (iterWorkflowType.hasNext()) {
            workflowTypeNodes.add(iterWorkflowType.next());
        }

        Iterator<ProductIstance> iterWorkflowIstance = new DepthFirstIterator<ProductIstance, CustomEdge>(dagIstance);
        while (iterWorkflowIstance.hasNext()) {
            workflowIstanceNodes.add(iterWorkflowIstance.next());
        }

        Integer totalNodes = dagType.vertexSet().size();
        for (int nodeNumber = 0; nodeNumber < totalNodes; nodeNumber++) {
            ProductType nodeType = workflowTypeNodes.get(nodeNumber);
            ProductIstance nodeIstance = workflowIstanceNodes.get(nodeNumber);
            assertTrue(nodeType.equalsAttributes(nodeIstance));
            assertEquals(dagType.inDegreeOf(nodeType), dagIstance.inDegreeOf(nodeIstance));
            assertEquals(dagType.outDegreeOf(nodeType), dagIstance.outDegreeOf(nodeIstance));
        }

        // Check in workflowIstance if all ProcessedTypes have their subgraph specified 
        for (ProductIstance nodeIstance : dagIstance.vertexSet()) {
            
            if(nodeIstance.isProcessedType()){
                WorkflowIstance subgraphIstance = new WorkflowIstance(createSubgraph(dagIstance, nodeIstance));
                assertEquals(subgraphIstance, nodeIstance.getProductWorkflow());
                // System.out.println(nodeIstance.getProductWorkflow().equals(subgraphIstance));
                // System.out.println("Istance workflow");
                // System.out.println(nodeIstance.getProductWorkflow());
                // System.out.println("SubgraphGenerated workflow");
                // System.out.println(subgraphIstance);
            }
        }

    }

    private  DirectedAcyclicGraph<ProductIstance, CustomEdge> createSubgraph(DirectedAcyclicGraph<ProductIstance, CustomEdge> originalDAG, ProductIstance root) {
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
    void testReflection(){
        // Create a workflowtype, make an istance, modify the dag of WorkflowType and make another istance.
        // Verify that the two istances are not equals
        DirectedAcyclicGraph<ProductType, CustomEdge> dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductType v0 = new ProductType("v0", 1, new UniformTime(0,2));
        ProductType v1 = new ProductType("v1", 1, new UniformTime(0,2));
        ProductType v2 = new ProductType("v2", 1, new UniformTime(0,2));
        ProductType v3 = new ProductType("v3", 1, new UniformTime(0,2));
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

}
