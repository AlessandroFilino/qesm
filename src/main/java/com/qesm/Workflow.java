package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class Workflow {

    private DirectedAcyclicGraph<Product, CustomEdge> dag;

    public Workflow(DirectedAcyclicGraph<Product, CustomEdge> dag) {
        this.dag = dag;
    }

}
