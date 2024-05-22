package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class Workflow {

    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;

    public Workflow() {
        this.dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
    }

}
