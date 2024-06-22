package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;


public abstract class AbstractWorkflow <T extends ProductType> implements DotFileConverter<T>, Serializable{

    protected DirectedAcyclicGraph<T, CustomEdge> dag;
    protected transient final Class<T> vertexClass;

    public AbstractWorkflow(Class<T> vertexClass) {
        this.vertexClass = vertexClass;
        this.dag = new DirectedAcyclicGraph<T, CustomEdge>(CustomEdge.class);
    }

    public AbstractWorkflow(DirectedAcyclicGraph<T, CustomEdge> dagToImport, Class<T> vertexClass) {
        this.vertexClass = vertexClass;
        this.dag = new DirectedAcyclicGraph<T, CustomEdge>(CustomEdge.class);

        // import all verteces
        for (T vertex : dagToImport.vertexSet()) {
            dag.addVertex(vertex);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dagToImport.edgeSet()) {
            T source = dagToImport.getEdgeSource(edge);
            T target = dagToImport.getEdgeTarget(edge);
            dag.addEdge(source, target, edge);
        }
    }

    // TODO: Add metric to measure the paralellization/balance of the dag

    @Override
    public DirectedAcyclicGraph<T, CustomEdge> getDag() {
        return dag;
    }

    @Override
    public void setDag(DirectedAcyclicGraph<T, CustomEdge> dagToSet) {
        dag = dagToSet;
    }

    @Override
    public Class<T> getVertexClass() {
        return this.vertexClass;
    }

    public T getRootNode() {
        for (T node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                return node;
            }
        }
        System.err.println("ERROR: there isn't a root node");
        return null;
    }

    public String toString() {
        String dagInfo = "";
        Iterator<T> iter = new DepthFirstIterator<T, CustomEdge>(dag);
        while (iter.hasNext()) {
            T vertex = iter.next();
            dagInfo += vertex.toString() + " is connected to: \n";
            for (CustomEdge connectedEdge : dag.outgoingEdgesOf(vertex)) {
                dagInfo += "\t" + connectedEdge.toString() + "\n";
            }
        }
        return dagInfo;
    }

    public boolean isDagConnected() {
        ConnectivityInspector<T, CustomEdge> connInspector = new ConnectivityInspector<T, CustomEdge>(
                dag);
        return connInspector.isConnected();
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

        AbstractWorkflow<T> workflowToCompare = uncheckedCast(obj);

        // Convert HashSets to ArrayLists because hashset.equals() is based on hashCode() and we have only overloaded equals() (In all our classes) not hashCode()
        
        List<T> vertexListToCompare = new ArrayList<>(workflowToCompare.getDag().vertexSet());
        List<T> vertexList = new ArrayList<>(dag.vertexSet());

        // Check if all element of a list are contained in the other and vice versa 
        // (very inneficent, need to implement custum hashcode if it will be developed further)
        for (T vertex : vertexList) {
            Boolean isContained = false;
            for (T vertexToCompare : vertexListToCompare) {
                if(vertex.equals(vertexToCompare)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        for (T vertexToCompare : vertexListToCompare) {
            Boolean isContained = false;
            for (T vertex : vertexList) {
                if(vertexToCompare.equals(vertex)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        List<CustomEdge> edgeListToCompare = new ArrayList<>(workflowToCompare.getDag().edgeSet());
        List<CustomEdge> edgeList = new ArrayList<>(dag.edgeSet());

        
        for (CustomEdge customEdge : edgeList) {
            Boolean isContained = false;
            for (CustomEdge customEdgeToCompare : edgeListToCompare) {
                if(customEdge.equals(customEdgeToCompare)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        for (CustomEdge customEdgeToCompare : edgeListToCompare) {
            Boolean isContained = false;
            for (CustomEdge customEdge : edgeList) {
                if(customEdgeToCompare.equals(customEdge)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        return true;
    }

    public void toUnshared() {
        DAGSharedToUnsharedConverter<T> dagConverter = new DAGSharedToUnsharedConverter<T>(dag, getRootNode(), vertexClass);
        dag = dagConverter.makeConversion();
    } 

    public Optional<T> findProduct(String productName){
        for(T product : dag.vertexSet()){
            if(product.getNameType().equals(productName)){
                return Optional.of(product);
            }
        }
        return Optional.empty();
    } 

    // Jgrapht does the same
    @SuppressWarnings("unchecked")
    private AbstractWorkflow<T> uncheckedCast(Object o)
    {
        return (AbstractWorkflow<T>) o;
    }
}
