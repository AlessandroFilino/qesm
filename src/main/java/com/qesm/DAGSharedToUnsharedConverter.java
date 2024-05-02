package com.qesm;

import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class DAGSharedToUnsharedConverter {
    
    private DirectedAcyclicGraph<ProductType, CustomEdge> sharedDag;
    private DirectedAcyclicGraph<ProductType, CustomEdge> unsharedDag;
    private HashMap<ProductType, Integer> idCounter = new HashMap<ProductType, Integer>();
    private ProductType sharedDagRootNode;


    public DAGSharedToUnsharedConverter(DirectedAcyclicGraph<ProductType, CustomEdge> sharedDag, DirectedAcyclicGraph<ProductType, CustomEdge> unsharedDag, ProductType sharedDagRootNode) {
        this.sharedDag = sharedDag;
        this.unsharedDag = unsharedDag;
        this.sharedDagRootNode = sharedDagRootNode;
    }

    public void makeConversion(){
        recursiveConversion(sharedDagRootNode);
    }

    private ProductType recursiveConversion(ProductType node) {
        int id = 0;
        if(!idCounter.containsKey(node)){
            idCounter.put(node, 0);
        }
        else{
            id = idCounter.get(node);
            id++;
            idCounter.put(node, id);
        }
        ProductType newNode;
        if (node.getClass() == ProcessedType.class) {
            if(id == 0){
                newNode = new ProcessedType(node.getNameType(), null, node.getQuantityProduced());
            }
            else{
                newNode = new ProcessedType(node.getNameType()+"_"+id, null, node.getQuantityProduced());
            }
            
        }
        else {
            if(id == 0){
                newNode = new RawMaterialType(node.getNameType());
            }
            else{
                newNode = new RawMaterialType(node.getNameType()+"_"+id);
            }
        }
        unsharedDag.addVertex(newNode);

        if(sharedDag.inDegreeOf(node) == 0){
            return newNode;
        } else { 
            for(CustomEdge edge: sharedDag.incomingEdgesOf(node)) {
                ProductType child = sharedDag.getEdgeSource(edge);
                ProductType newChild = recursiveConversion(child);
                CustomEdge newEdge = unsharedDag.addEdge(newChild, newNode);
                newEdge.copyEdge(edge);
                newChild.setQuantityProduced(newEdge.getQuantityRequired());
            }
        }
        return newNode;
    }

    
}
