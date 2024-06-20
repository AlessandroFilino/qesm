package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class WorkflowIstance extends AbstractWorkflow<ProductIstance>{

    public WorkflowIstance(){
        super(ProductIstance.class);
        // this.dag = null;
    }

    public WorkflowIstance(DirectedAcyclicGraph<ProductIstance, CustomEdge> dag) {
        super(dag, ProductIstance.class);
    }

}
