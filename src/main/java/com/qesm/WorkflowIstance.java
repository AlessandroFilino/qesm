package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class WorkflowIstance extends AbstractWorkflow<ProductIstance> {

    protected WorkflowIstance() {
        super(ProductIstance.class, true);
    }

    public WorkflowIstance(DirectedAcyclicGraph<ProductIstance, CustomEdge> dagToImport) {
        super(dagToImport, ProductIstance.class, true);
    }

    private WorkflowIstance(DirectedAcyclicGraph<ProductIstance, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductIstance.class, isTopTierGraph);
    }

    @Override
    public WorkflowIstance buildWorkflow(DirectedAcyclicGraph<ProductIstance, CustomEdge> dag) {
        return new WorkflowIstance(dag, false);
    }

}
