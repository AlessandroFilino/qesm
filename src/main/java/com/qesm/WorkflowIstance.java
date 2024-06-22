package com.qesm;

import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class WorkflowIstance extends AbstractWorkflow<ProductIstance>{

    public WorkflowIstance(){
        super(ProductIstance.class);
        // this.dag = null;
    }

    public WorkflowIstance(DirectedAcyclicGraph<ProductIstance, CustomEdge> dag) {
        super(dag, ProductIstance.class);
    }

    @Override
    public void toUnshared() {
        super.toUnshared();
        updateSubgraphs();
    }

    public void updateSubgraphs(){

        for (ProductIstance productIstance : dag.vertexSet()) {
            if(productIstance.isProcessedType()){
                productIstance.setProductWorkflow(new WorkflowIstance(createSubgraph(dag, productIstance)));
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

}
