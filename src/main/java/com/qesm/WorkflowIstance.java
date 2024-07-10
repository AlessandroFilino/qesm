package com.qesm;


public class WorkflowIstance extends AbstractWorkflow<ProductIstance, WorkflowIstance>{
    
    protected WorkflowIstance(){
        super(ProductIstance.class, true);
    }

    protected WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport) {
        super(dagToImport, ProductIstance.class, true);
    }

    private WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport, Boolean isTopTierGraph) {
        super(dagToImport, ProductIstance.class, isTopTierGraph);
    }

    @Override
    protected WorkflowIstance buildWorkflow(ListenableDAG<ProductIstance, CustomEdge> dag) {
        return new WorkflowIstance(dag, false);
    }

}
