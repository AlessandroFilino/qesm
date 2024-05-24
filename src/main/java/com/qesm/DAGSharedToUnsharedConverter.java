package com.qesm;

import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class DAGSharedToUnsharedConverter {
    
    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;
    private DirectedAcyclicGraph<ProductType, CustomEdge> unsharedDag;
    private HashMap<ProductType, Integer> idCounter = new HashMap<ProductType, Integer>();
    private ProductType rootNode;


    public DAGSharedToUnsharedConverter(DirectedAcyclicGraph<ProductType, CustomEdge> dag, ProductType rootNode) {
        this.dag = dag;
        this.rootNode = rootNode;
        this.unsharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> makeConversion(){
        recursiveConversion(rootNode);

        return unsharedDag;
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
                newNode = new ProcessedType(node.getNameType(), node.getQuantityProduced(), node.getPdf());
            }
            else{
                newNode = new ProcessedType(node.getNameType()+"_"+id, node.getQuantityProduced(), node.getPdf());
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

        if(dag.inDegreeOf(node) == 0){
            return newNode;
        } else { 
            for(CustomEdge edge: dag.incomingEdgesOf(node)) {
                ProductType child = dag.getEdgeSource(edge);
                ProductType newChild = recursiveConversion(child);
                CustomEdge newEdge = new CustomEdge(edge);
                unsharedDag.addEdge(newChild, newNode, newEdge);
                newChild.setQuantityProduced(newEdge.getQuantityRequired());
            }
        }
        return newNode;
    }
}
