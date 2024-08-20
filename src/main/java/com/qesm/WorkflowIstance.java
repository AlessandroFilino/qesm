package com.qesm;

public class WorkflowIstance extends AbstractWorkflow<ProductIstance> {

    protected WorkflowIstance() {
        super(ProductIstance.class, true);
    }

    public WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport) {
        super(dagToImport, ProductIstance.class, true);
    }

    private WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductIstance.class, isTopTierGraph);
    }

    @Override
    public WorkflowIstance buildWorkflow(ListenableDAG<ProductIstance, CustomEdge> dag) {
        return new WorkflowIstance(dag, false);
    }

}
