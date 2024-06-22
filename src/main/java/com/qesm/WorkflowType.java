package com.qesm;

import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.RandomDAGGenerator.PdfType;


public class WorkflowType extends AbstractWorkflow<ProductType>{


    public WorkflowType() {
        super(ProductType.class);
    }

    public WorkflowType(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport) {
        super(dagToImport, ProductType.class);
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, int branchingUpProbability, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, pdfType);
        randDAGGenerator.generateGraph(dag);
    }

    public WorkflowIstance makeIstance() {
        DirectedAcyclicGraph<ProductIstance, CustomEdge> dagIstance = new DirectedAcyclicGraph<>(CustomEdge.class);

        HashMap<ProductType, ProductIstance> productTypeToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (ProductType vertex : dag.vertexSet()) {
            ProductIstance product = new ProductIstance(vertex);
            dagIstance.addVertex(product);
            productTypeToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dag.edgeSet()) {
            ProductType sourceType = dag.getEdgeSource(edge);
            ProductType targetType = dag.getEdgeTarget(edge);
            dagIstance.addEdge(productTypeToProductMap.get(sourceType), productTypeToProductMap.get(targetType), new CustomEdge(edge));
        }

        WorkflowIstance workflowIstance = new WorkflowIstance(dagIstance);
        workflowIstance.updateSubgraphs();

        return workflowIstance;
    }

}
