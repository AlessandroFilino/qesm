package com.qesm;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class Workflow {

    private DirectedAcyclicGraph<Product, CustomEdge> dag;

    public Workflow(DirectedAcyclicGraph<Product, CustomEdge> dag) {
        this.dag = dag;
    }

    public DirectedAcyclicGraph<Product, CustomEdge> getDag() {
        return dag;
    }

    public void exportDagToDotFile(String filePath) {
        ProductTypeCustomEdgeIO<Product> exporter = new ProductTypeCustomEdgeIO<>(Product.class);
        exporter.writeDotFile(filePath, dag);
    }

}
