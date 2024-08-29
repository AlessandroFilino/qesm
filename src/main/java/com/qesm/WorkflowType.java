package com.qesm;

import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowType extends AbstractWorkflow<ProductType> {

    public WorkflowType() {
        super(ProductType.class, true);
    }

    public WorkflowType(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport) {
        super(dagToImport, ProductType.class, true);
    }

    private WorkflowType(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductType.class, isTopTierGraph);
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor,
            int branchingUpProbability, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, pdfType);
        dag = randDAGGenerator.generateGraph();
        updateAllSubgraphs();
    }

    public WorkflowInstance makeInstance() {
        DirectedAcyclicGraph<ProductInstance, CustomEdge> dagInstance = new DirectedAcyclicGraph<>(CustomEdge.class);

        HashMap<ProductType, ProductInstance> productTypeToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (ProductType vertex : dag.vertexSet()) {
            ProductInstance product = new ProductInstance(vertex);
            dagInstance.addVertex(product);
            productTypeToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy
        for (CustomEdge edge : dag.edgeSet()) {
            ProductType sourceType = dag.getEdgeSource(edge);
            ProductType targetType = dag.getEdgeTarget(edge);
            dagInstance.addEdge(productTypeToProductMap.get(sourceType), productTypeToProductMap.get(targetType),
                    new CustomEdge(edge));
        }

        WorkflowInstance workflowInstance = new WorkflowInstance(dagInstance);

        return workflowInstance;
    }

    @Override
    protected WorkflowType buildWorkflow(DirectedAcyclicGraph<ProductType, CustomEdge> dag) {
        return new WorkflowType(dag, false);
    }

}
