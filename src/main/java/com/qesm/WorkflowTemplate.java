package com.qesm;

import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.RandomDAGGenerator.PdfType;

public class WorkflowTemplate extends AbstractWorkflow<ProductTemplate> {

    public WorkflowTemplate() {
        super(ProductTemplate.class, true);
    }

    public WorkflowTemplate(DirectedAcyclicGraph<ProductTemplate, CustomEdge> dagToImport) {
        super(dagToImport, ProductTemplate.class, true);
    }

    private WorkflowTemplate(DirectedAcyclicGraph<ProductTemplate, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductTemplate.class, isTopTierGraph);
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

        HashMap<ProductTemplate, ProductInstance> productTemplateToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (ProductTemplate vertex : dag.vertexSet()) {
            ProductInstance product = new ProductInstance(vertex);
            dagInstance.addVertex(product);
            productTemplateToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy
        for (CustomEdge edge : dag.edgeSet()) {
            ProductTemplate source = dag.getEdgeSource(edge);
            ProductTemplate target = dag.getEdgeTarget(edge);
            dagInstance.addEdge(productTemplateToProductMap.get(source), productTemplateToProductMap.get(target),
                    new CustomEdge(edge));
        }

        WorkflowInstance workflowInstance = new WorkflowInstance(dagInstance);

        return workflowInstance;
    }

    @Override
    protected WorkflowTemplate buildWorkflow(DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag) {
        return new WorkflowTemplate(dag, false);
    }

}
