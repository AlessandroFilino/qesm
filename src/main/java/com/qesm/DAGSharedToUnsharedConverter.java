package com.qesm;

import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.ProductType.ItemType;

public class DAGSharedToUnsharedConverter<V extends ProductType> {
    
    private DirectedAcyclicGraph<V, CustomEdge> dag;
    private DirectedAcyclicGraph<V, CustomEdge> unsharedDag;
    private HashMap<V, Integer> idCounter = new HashMap<V, Integer>();
    private V rootNode;
    private Class<V> vertexClass;

    public DAGSharedToUnsharedConverter(DirectedAcyclicGraph<V, CustomEdge> dag, V rootNode, Class<V> vertexClass) {
        this.dag = dag;
        this.rootNode = rootNode;
        this.unsharedDag = new DirectedAcyclicGraph<V, CustomEdge>(CustomEdge.class);
        this.vertexClass = vertexClass;
    }

    public DirectedAcyclicGraph<V, CustomEdge> makeConversion(){
        recursiveConversion(rootNode);

        return unsharedDag;
    }

    private V recursiveConversion(V node) {
        int id = 0;
        if(!idCounter.containsKey(node)){
            idCounter.put(node, 0);
        }
        else{
            id = idCounter.get(node);
            id++;
            idCounter.put(node, id);
        }
        V newNode;
        if (node.getItemType() == ItemType.PROCESSED) {
            if(id == 0){
                newNode = generateNode(node.getNameType(), ItemType.PROCESSED);
                newNode.setQuantityProduced(node.getQuantityProduced());
                newNode.setPdf(node.getPdf());
            }
            else{
                newNode = generateNode(node.getNameType()+"_"+id, ItemType.PROCESSED);
                newNode.setQuantityProduced(node.getQuantityProduced());
                newNode.setPdf(node.getPdf());
            }
        }
        else {
            if(id == 0){
                newNode = generateNode(node.getNameType(), ItemType.RAW_MATERIAL);
            }
            else{
                newNode = generateNode(node.getNameType()+"_"+id, ItemType.RAW_MATERIAL);
            }
        }
        unsharedDag.addVertex(newNode);

        if(dag.inDegreeOf(node) == 0){
            return newNode;
        } else { 
            for(CustomEdge edge: dag.incomingEdgesOf(node)) {
                V child = dag.getEdgeSource(edge);
                V newChild = recursiveConversion(child);
                CustomEdge newEdge = new CustomEdge(edge);
                unsharedDag.addEdge(newChild, newNode, newEdge);
                newChild.setQuantityProduced(newEdge.getQuantityRequired());
            }
        }
        return newNode;
    }

    private V generateNode(String nameType, ItemType itemType){
        try {
            Constructor<V> constructor = vertexClass.getConstructor(String.class, ItemType.class);
            V newNode = constructor.newInstance(nameType, itemType);
            return newNode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate the object", e);
        }

        
    }

}
