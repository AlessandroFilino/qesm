package com.qesm;

import java.util.Set;


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
    protected void buildChangedSubGraphs(Set<ProductIstance> vertexSet) {
        for (ProductIstance productIstance : vertexSet) {
            if(productIstance.isProcessed()){
                productToSubWorkflowMap.put(productIstance, new WorkflowIstance(createSubgraph(dag, productIstance), false));
            }
        }
    }

}
