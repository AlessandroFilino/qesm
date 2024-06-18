package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jgrapht.graph.DirectedAcyclicGraph;

import guru.nidi.graphviz.engine.GraphvizCmdLineEngine.Option;

public class Workflow implements DotFileConverter<Product>, Serializable{

    private DirectedAcyclicGraph<Product, CustomEdge> dag;

    public Workflow(){
        this.dag = null;
    }

    public Workflow(DirectedAcyclicGraph<Product, CustomEdge> dag) {
        this.dag = dag;
    }

    @Override
    public DirectedAcyclicGraph<Product, CustomEdge> getDag() {
        return dag;
    }

    @Override
    public void setDag(DirectedAcyclicGraph<Product, CustomEdge> dagToSet) {
        dag = dagToSet;
    }

    @Override
    public Class<Product> getVertexClass() {
        return Product.class;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }

        Workflow workflowIstanceToCompare = (Workflow) obj;

        // Convert HashSets to ArrayLists because hashset.equals() is based on hashCode() and we have only defined equals() for Product
        List<Product> vertexListToCompare = new ArrayList<>(workflowIstanceToCompare.getDag().vertexSet());
        List<Product> vertexList = new ArrayList<>(dag.vertexSet());

        if(! vertexList.equals(vertexListToCompare)){
            return false;
        }

        List<CustomEdge> edgeListToCompare = new ArrayList<>(workflowIstanceToCompare.getDag().edgeSet());
        List<CustomEdge> edgeList = new ArrayList<>(dag.edgeSet());

        if(! edgeList.equals(edgeListToCompare)){
            return false;
        }

        return true;
    }

    // TODO: Add a method that allows you to select a specific process type and its subgraph
    public Optional<Product> findProduct(String productName){
        for(Product product : dag.vertexSet()){
            if(product.getNameType().equals(productName)){
                return Optional.of(product);
            }
        }
        return Optional.empty();
    } 
}
