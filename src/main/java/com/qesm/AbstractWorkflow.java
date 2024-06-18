package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.qesm.ProductType.ItemType;
import com.qesm.RandomDAGGenerator.PdfType;

public abstract class AbstractWorkflow <T extends ProductType> implements DotFileConverter<T>{

    private DirectedAcyclicGraph<T, CustomEdge> dag;
    private final Class<T> vertexClass;

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
        // TODO: check if toString does the job
        return dag.toString();
    }

    public boolean isDagConnected() {
        ConnectivityInspector<T, CustomEdge> connInspector = new ConnectivityInspector<T, CustomEdge>(
                dag);
        return connInspector.isConnected();
    }

    public void toUnshared() {
        DAGSharedToUnsharedConverter dagConverter = new DAGSharedToUnsharedConverter(dag, getRootNode());
        dag = dagConverter.makeConversion();
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

        WorkflowType workflowTypeToCompare = (WorkflowType) obj;

        // Convert HashSets to ArrayLists because hashset.equals() is based on hashCode() and we have only defined equals() for T
        List<T> vertexListToCompare = new ArrayList<>(workflowTypeToCompare.getDag().vertexSet());
        List<T> vertexList = new ArrayList<>(dag.vertexSet());

        if(! vertexList.equals(vertexListToCompare)){
            return false;
        }

        List<CustomEdge> edgeListToCompare = new ArrayList<>(workflowTypeToCompare.getDag().edgeSet());
        List<CustomEdge> edgeList = new ArrayList<>(dag.edgeSet());

        if(! edgeList.equals(edgeListToCompare)){
            return false;
        }

        return true;
    }

    public WorkflowIstance makeIstance() {
        DirectedAcyclicGraph<ProductIstance, CustomEdge> dagIstance = new DirectedAcyclicGraph<>(CustomEdge.class);

        HashMap<T, ProductIstance> TToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (T vertex : dag.vertexSet()) {
            ProductIstance product = new ProductIstance(vertex);
            dagIstance.addVertex(product);
            TToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dag.edgeSet()) {
            T sourceType = dag.getEdgeSource(edge);
            T targetType = dag.getEdgeTarget(edge);
            dagIstance.addEdge(TToProductMap.get(sourceType), TToProductMap.get(targetType), new CustomEdge(edge));
        }

        WorkflowIstance workflow = new WorkflowIstance(dagIstance);


        for (ProductIstance product : dagIstance.vertexSet()) {
            if(product.getItemType() == ItemType.PROCESSED){
                product.setProductWorkflow(buildSubgraphWorkflow(dagIstance, product));
            }
        }

        return workflow;

    }

    private WorkflowIstance buildSubgraphWorkflow(DirectedAcyclicGraph<ProductIstance, CustomEdge> fullDag, ProductIstance currentVertex){
        DirectedAcyclicGraph<ProductIstance, CustomEdge> subGraph = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<ProductIstance>  subGraphVertexSet = fullDag.getAncestors(currentVertex); 
        subGraphVertexSet.add(currentVertex);

        // Add all subgraph vertexes
        for (ProductIstance product : subGraphVertexSet) {
            subGraph.addVertex(product);
        }

        // Add all subgraph edges
        for (ProductIstance product : subGraphVertexSet) {
            for (CustomEdge edge : fullDag.edgesOf(product)) {

                ProductIstance sourceProduct = fullDag.getEdgeSource(edge);
                if(!subGraphVertexSet.contains(sourceProduct)){
                    continue;
                }

                ProductIstance targetProduct = fullDag.getEdgeTarget(edge);
                if(!subGraphVertexSet.contains(targetProduct)){
                    continue;
                }
                
                subGraph.addEdge(sourceProduct, targetProduct, edge);
            }
            
        }


        WorkflowIstance subgraphWorkflow = new WorkflowIstance(subGraph);
        return subgraphWorkflow;

    }
}
