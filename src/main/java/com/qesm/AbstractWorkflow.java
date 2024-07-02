package com.qesm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.traverse.DepthFirstIterator;



public abstract class AbstractWorkflow <V extends AbstractProduct> implements DotFileConverter<V>, Serializable{

    protected ListenableDAG<V, CustomEdge> dag;
    protected transient final Class<V> vertexClass;
    protected Boolean graphListenerAdded = false;
    protected Boolean isRootGraph = false;

    public AbstractWorkflow(Class<V> vertexClass, Boolean isRootGraph) {
        this.vertexClass = vertexClass;
        this.dag = new ListenableDAG<V, CustomEdge>(CustomEdge.class);
        this.isRootGraph = isRootGraph;

        if(isRootGraph){
            setGraphListener();
            updateAllSubgraphs();
        }
    }

    public AbstractWorkflow(ListenableDAG<V, CustomEdge> dagToImport, Class<V> vertexClass, Boolean isRootGraph) {
        this.vertexClass = vertexClass;
        this.dag = dagToImport;
        this.isRootGraph = isRootGraph;

        if(isRootGraph){
            setGraphListener();
            updateAllSubgraphs();
        }
    }

    // TODO: Add metric to measure the paralellization/balance of the dag

    public int computeParallelismValue(){

        // First convert to unshared DAG 
        DAGSharedToUnsharedConverter<V> dagConverter = new DAGSharedToUnsharedConverter<V>(dag, getRootNode(), vertexClass);
        ListenableDAG<V, CustomEdge> unsharedDag = dagConverter.makeConversion();
        
        // Then compute metrics
        int A = 0;
        // A paramater: sum all incoming edges of Processed node if they have more than 2 incoming edges 
        for(V node : unsharedDag.vertexSet()){
            if(node.getItemGroup() == AbstractProduct.ItemGroup.PROCESSED && unsharedDag.inDegreeOf(node) >= 2){
                A += unsharedDag.inDegreeOf(node);
            }
        };

        // B parameter: Load Balance Factor
        float B = 0;

        // TODO: improve this search and uniform with getRootNode maybe?
        // Get root node of unshared dag, using getRootNode doesn't work because RootNode is not unsharedRootNode
        V unsharedRootNode = null;
        for (V node : unsharedDag.vertexSet()) {
            if (unsharedDag.outDegreeOf(node) == 0) {
                unsharedRootNode = node;
                break;
            }
        }

        Set<V> processedChildNodes = unsharedDag.incomingEdgesOf(unsharedRootNode).stream().map(unsharedDag::getEdgeSource).filter(v -> v.getItemGroup() == AbstractProduct.ItemGroup.PROCESSED).collect(Collectors.toSet());
        if (!processedChildNodes.isEmpty()){
            processedChildNodes.forEach(n -> System.out.println("Node: " + n.getName() + " - Item: " + n.getItemGroup()));
            int[] numNodesInSubgraphs = new int[processedChildNodes.size()];
            int index = 0;
            for(V node : processedChildNodes){
                numNodesInSubgraphs[index] = unsharedDag.getAncestors(node).size();
                System.out.println("Index: " + index + ", Node: " + node.getName() + " -> " + numNodesInSubgraphs[index]);
                index++;
            }

            // for (int i = 0; i < numNodesInSubgraphs.length; i++){
            //     System.out.println("[" + i +"] Val " + numNodesInSubgraphs[i]);
            // }

            float mean = (Arrays.stream(numNodesInSubgraphs).sum()) / processedChildNodes.size();
            System.out.println("Mean: " + mean);
            float sumOfSquares = 0;
            for (int i = 0; i < processedChildNodes.size(); i++){
                sumOfSquares += Math.pow((numNodesInSubgraphs[i] - mean), 2);
            }
            float loadBalanceFactor = 1.0f / (processedChildNodes.size()) * sumOfSquares;
            B = loadBalanceFactor;
        }
 
        // for(V node : unsharedDag.vertexSet()){
        //     // Select "leaf" Processed nodes (Processed nodes with only Raw Material nodes as ancestors)
        //     if(node.getItemGroup() == AbstractProduct.ItemGroup.PROCESSED && unsharedDag.getAncestors(node).stream().allMatch(ancestor -> ancestor.getItemGroup() == AbstractProduct.ItemGroup.RAW_MATERIAL)){
        //         System.out.println("Node: " + node.getName() + " - Item: " + node.getItemGroup());
        //         unsharedDag.getDescendants(node).forEach(n -> System.out.println("          " + n.getName()));
        //     }
        // }

        System.out.println("A value: " + A);
        System.out.println("B value: " + B);
        return -1;
    }

    @Override
    public ListenableDAG<V, CustomEdge> getDag() {
        return dag;
    }

    @Override
    public void setDag(ListenableDAG<V, CustomEdge> dagToSet) {
        dag = dagToSet;
        setGraphListener();
        updateAllSubgraphs();
    }

    @Override
    public Class<V> getVertexClass() {
        return this.vertexClass;
    }

    public V getRootNode() {
        for (V node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                return node;
            }
        }
        System.err.println("ERROR: there isn't a root node");
        return null;
    }

    public String toString() {
        String dagInfo = "";
        Iterator<V> iter = new DepthFirstIterator<V, CustomEdge>(dag);
        while (iter.hasNext()) {
            V vertex = iter.next();
            dagInfo += vertex.toString() + " is connected to: \n";
            for (CustomEdge connectedEdge : dag.outgoingEdgesOf(vertex)) {
                dagInfo += "\t" + connectedEdge.toString() + "\n";
            }
        }
        return dagInfo;
    }

    public boolean isDagConnected() {
        ConnectivityInspector<V, CustomEdge> connInspector = new ConnectivityInspector<V, CustomEdge>(
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

        AbstractWorkflow<V> workflowToCompare = uncheckedCast(obj);

        // Convert HashSets to ArrayLists because hashset.equals() is based on hashCode() and we have only overloaded equals() (In all our classes) not hashCode()
        
        List<V> vertexListToCompare = new ArrayList<>(workflowToCompare.getDag().vertexSet());
        List<V> vertexList = new ArrayList<>(dag.vertexSet());

        // Check if all element of a list are contained in the other and vice versa 
        // (very inneficent, need to implement custum hashcode if it will be developed further)
        for (V vertex : vertexList) {
            Boolean isContained = false;
            for (V vertexToCompare : vertexListToCompare) {
                if(vertex.equals(vertexToCompare)){
                    isContained = true;
                    break;
                } 
            }
            if(!isContained){
                return false;
            }
        }

        for (V vertexToCompare : vertexListToCompare) {
            Boolean isContained = false;
            for (V vertex : vertexList) {
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

    public <T extends AbstractProduct> boolean equalsNodesAttributes(AbstractWorkflow<T> workflowToCompare){
        ArrayList<V> workflowNodes = new ArrayList<>();
        ArrayList<T> workflowToCompareNodes = new ArrayList<>();

        Iterator<V> iterWorkflow = new DepthFirstIterator<V, CustomEdge>(dag);
        while (iterWorkflow.hasNext()) {
            workflowNodes.add(iterWorkflow.next());
        }

        Iterator<T> iterWorkflowToCompare = new DepthFirstIterator<T, CustomEdge>(workflowToCompare.getDag());
        while (iterWorkflowToCompare.hasNext()) {
            workflowToCompareNodes.add(iterWorkflowToCompare.next());
        }

        Integer totalNodes = dag.vertexSet().size();
        for (int nodeNumber = 0; nodeNumber < totalNodes; nodeNumber++) {
            V node = workflowNodes.get(nodeNumber);
            T nodeToCompare = workflowToCompareNodes.get(nodeNumber);
            if(!node.equalsAttributes(nodeToCompare)){
                return false;
            }
        }

        return true;
    }

    public void toUnshared() {
        DAGSharedToUnsharedConverter<V> dagConverter = new DAGSharedToUnsharedConverter<V>(dag, getRootNode(), vertexClass);
        dag = dagConverter.makeConversion();
        setGraphListener();
        updateAllSubgraphs();
    } 

    public Optional<V> findProduct(String productName){
        for(V product : dag.vertexSet()){
            if(product.getName().equals(productName)){
                return Optional.of(product);
            }
        }
        return Optional.empty();
    } 

    // Jgrapht does the same
    @SuppressWarnings("unchecked")
    private AbstractWorkflow<V> uncheckedCast(Object o)
    {
        return (AbstractWorkflow<V>) o;
    }

    protected void setGraphListener(){
        if(graphListenerAdded || !isRootGraph){
            return;
        }
        GraphListener<V, CustomEdge> graphListener = new GraphListener<V,CustomEdge>() {

            @Override
            public void vertexAdded(GraphVertexChangeEvent<V> e) {
                updateChangedSubgraphs(e.getVertex(), null, null);
            }

            @Override
            public void vertexRemoved(GraphVertexChangeEvent<V> e) {
                updateChangedSubgraphs(e.getVertex(), null, null);
            }

            @Override
            public void edgeAdded(GraphEdgeChangeEvent<V, CustomEdge> e) {
                updateChangedSubgraphs(null, e.getEdgeSource(), e.getEdgeTarget());
            }

            @Override
            public void edgeRemoved(GraphEdgeChangeEvent<V, CustomEdge> e) {
                updateChangedSubgraphs(null, e.getEdgeSource(), e.getEdgeTarget());
            }
            
        };

        dag.addGraphListener(graphListener);
        graphListenerAdded = true;
    }

    protected void updateAllSubgraphs(){
        buildChangedSubGraphs(dag.vertexSet());
    }

    private void updateChangedSubgraphs(V vertexChanged, V edgeSource, V edgeTarget){
        if(vertexChanged != null){
            buildChangedSubGraphs(dag.getDescendants(vertexChanged));
        }
        else if(edgeSource != null && edgeTarget != null){
            Set<V> vertexSet = dag.getDescendants(edgeSource);
            vertexSet.addAll(dag.getDescendants(edgeTarget));
            buildChangedSubGraphs(vertexSet);
        }
    }

    protected abstract void buildChangedSubGraphs(Set<V> vertexSet);

    protected  ListenableDAG<V, CustomEdge> createSubgraph(ListenableDAG<V, CustomEdge> originalDAG, V root) {
        ListenableDAG<V, CustomEdge> subgraphDAG = new ListenableDAG<>(CustomEdge.class);

        Set<V> subgraphVertices = originalDAG.getAncestors(root);
        subgraphVertices.add(root);

        // Add vertices and edges to the subgraph
        for (V vertex : subgraphVertices) {
            subgraphDAG.addVertex(vertex);
        }

        for (V vertex : subgraphVertices) {
            Set<CustomEdge> edges = originalDAG.outgoingEdgesOf(vertex);
            for (CustomEdge edge : edges) {
                V target = originalDAG.getEdgeTarget(edge);
                if (subgraphVertices.contains(target)) {
                    CustomEdge subgraphEdge = new CustomEdge();
                    subgraphEdge.setQuantityRequired(edge.getQuantityRequired());
                    subgraphDAG.addEdge(vertex, target, subgraphEdge);
                }
            }
        }

        return subgraphDAG;
    } 
}
