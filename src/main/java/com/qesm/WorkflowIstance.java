package com.qesm;

import java.util.Set;


public class WorkflowIstance extends AbstractWorkflow<ProductIstance>{

    protected WorkflowIstance(){
        super(ProductIstance.class, true);
    }

    protected WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport) {
        super(dagToImport, ProductIstance.class, true);
    }

    private WorkflowIstance(ListenableDAG<ProductIstance, CustomEdge> dagToImport, Boolean isRootGraph) {
        super(dagToImport, ProductIstance.class, isRootGraph);
    }

    @Override
    protected void buildChangedSubGraphs(Set<ProductIstance> vertexSet) {
        for (ProductIstance productIstance : vertexSet) {
            if(productIstance.isProcessed()){
                productIstance.setProductWorkflow(new WorkflowIstance(createSubgraph(dag, productIstance), false));
            }
        }
    }

}
