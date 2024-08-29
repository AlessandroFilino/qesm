package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class WorkflowInstance extends AbstractWorkflow<ProductInstance> {

    protected WorkflowInstance() {
        super(ProductInstance.class, true);
    }

    public WorkflowInstance(DirectedAcyclicGraph<ProductInstance, CustomEdge> dagToImport) {
        super(dagToImport, ProductInstance.class, true);
    }

    private WorkflowInstance(DirectedAcyclicGraph<ProductInstance, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductInstance.class, isTopTierGraph);
    }

    @Override
    public WorkflowInstance buildWorkflow(DirectedAcyclicGraph<ProductInstance, CustomEdge> dag) {
        return new WorkflowInstance(dag, false);
    }

}
