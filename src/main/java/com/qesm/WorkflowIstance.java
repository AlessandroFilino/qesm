package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class WorkflowIstance implements DotFileConverter<ProductIstance>, Serializable{

    private DirectedAcyclicGraph<ProductIstance, CustomEdge> dag;

    public WorkflowIstance(){
        this.dag = null;
    }

    public WorkflowIstance(DirectedAcyclicGraph<ProductIstance, CustomEdge> dag) {
        this.dag = dag;
    }

    @Override
    public DirectedAcyclicGraph<ProductIstance, CustomEdge> getDag() {
        return dag;
    }

    @Override
    public void setDag(DirectedAcyclicGraph<ProductIstance, CustomEdge> dagToSet) {
        dag = dagToSet;
    }

    @Override
    public Class<ProductIstance> getVertexClass() {
        return ProductIstance.class;
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

        WorkflowIstance workflowIstanceToCompare = (WorkflowIstance) obj;

        // Convert HashSets to ArrayLists because hashset.equals() is based on hashCode() and we have only defined equals() for Product
        List<ProductIstance> vertexListToCompare = new ArrayList<>(workflowIstanceToCompare.getDag().vertexSet());
        List<ProductIstance> vertexList = new ArrayList<>(dag.vertexSet());

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

    // TODO: Move method to abstract super class
    public Optional<ProductIstance> findProduct(String productName){
        for(ProductIstance product : dag.vertexSet()){
            if(product.getNameType().equals(productName)){
                return Optional.of(product);
            }
        }
        return Optional.empty();
    } 
}
