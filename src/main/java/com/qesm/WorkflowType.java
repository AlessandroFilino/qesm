package com.qesm;

import java.util.HashMap;
import java.util.Set;

import com.qesm.RandomDAGGenerator.PdfType;


public class WorkflowType extends AbstractWorkflow<ProductType>{


    public WorkflowType() {
        super(ProductType.class, true);
    }

    public WorkflowType(ListenableDAG<ProductType, CustomEdge> dagToImport) {
        super(dagToImport, ProductType.class, true);
    }

    private WorkflowType(ListenableDAG<ProductType, CustomEdge> dagToImport, Boolean isRootGraph) {
        super(dagToImport, ProductType.class, isRootGraph);
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, int branchingUpProbability, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, pdfType);
        dag = randDAGGenerator.generateGraph();
        setGraphListener();
        updateAllSubgraphs();
    }

    public WorkflowIstance makeIstance() {
        ListenableDAG<ProductIstance, CustomEdge> dagIstance = new ListenableDAG<>(CustomEdge.class);

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

        return workflowIstance;
    }

    @Override
    protected void buildChangedSubGraphs(Set<ProductType> vertexSet) {
        for (ProductType productType : vertexSet) {
            if(productType.isProcessed()){
                productType.setProductWorkflow(new WorkflowType(createSubgraph(dag, productType), false));
            }
        }
    }

}
