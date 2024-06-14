package com.qesm;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.qesm.ProductType.ItemType;
import com.qesm.RandomDAGGenerator.PdfType;


public class WorkflowType {

    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;

    public WorkflowType() {
        this.dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
    }

    public WorkflowType(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport) {
        this.dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        // import all verteces
        for (ProductType vertex : dagToImport.vertexSet()) {
            dag.addVertex(vertex);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dagToImport.edgeSet()) {
            ProductType source = dagToImport.getEdgeSource(edge);
            ProductType target = dagToImport.getEdgeTarget(edge);
            dag.addEdge(source, target, edge);
        }
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> getDag() {
        return dag;
    }

    public ProductType getRootNode() {
        for (ProductType node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                return node;
            }
        }
        System.err.println("ERROR: there isn't a root node");
        return null;
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, int branchingUpProbability, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, pdfType);
        randDAGGenerator.generateGraph(dag);
    }

    public String toString() {
        String result = "";
        Iterator<ProductType> iter = new DepthFirstIterator<ProductType, CustomEdge>(dag);
        while (iter.hasNext()) {
            ProductType vertex = iter.next();
            result += "Vertex " + vertex.getNameType() + " type: " + vertex.getClass() + " is connected to: \n";
            for (CustomEdge connectedEdge : dag.edgesOf(vertex)) {
                result += "\t[" + dag.getEdgeSource(connectedEdge).getNameType() + " -> "
                        + dag.getEdgeTarget(connectedEdge).getNameType() + "]\n";
            }
        }
        return result;
    }

    public void exportDagToDotFile(String filePath) {
        ProductTypeCustomEdgeIO<ProductType> exporter = new ProductTypeCustomEdgeIO<>(ProductType.class);
        exporter.writeDotFile(filePath, dag);
    }

    public void importDagFromDotFile(String filePath) {
        dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        ProductTypeCustomEdgeIO<ProductType> importer = new ProductTypeCustomEdgeIO<>(ProductType.class);
        importer.readDotFile(filePath, dag);
    }

    public boolean isDagConnected() {
        ConnectivityInspector<ProductType, CustomEdge> connInspector = new ConnectivityInspector<ProductType, CustomEdge>(
                dag);
        return connInspector.isConnected();
    }

    public void toUnshared() {
        DAGSharedToUnsharedConverter dagConverter = new DAGSharedToUnsharedConverter(dag, getRootNode());
        dag = dagConverter.makeConversion();
    }

    //TODO Completare reflection
    public Workflow makeIstance() {
        DirectedAcyclicGraph<Product, CustomEdge> dagIstance = new DirectedAcyclicGraph<>(CustomEdge.class);

        HashMap<ProductType, Product> productTypeToProductMap = new HashMap<>();

        // Deepcopy of all vertexes
        for (ProductType vertex : dag.vertexSet()) {
            Product product = new Product(vertex);
            dagIstance.addVertex(product);
            productTypeToProductMap.put(vertex, product);
        }

        // Add all the edges from the original DAG to the copy 
        for (CustomEdge edge : dag.edgeSet()) {
            ProductType sourceType = dag.getEdgeSource(edge);
            ProductType targetType = dag.getEdgeTarget(edge);
            dagIstance.addEdge(productTypeToProductMap.get(sourceType), productTypeToProductMap.get(targetType), new CustomEdge(edge));
        }

        Workflow workflow = new Workflow(dagIstance);


        for (Product product : dagIstance.vertexSet()) {
            if(product.getItemType() == ItemType.PROCESSED){
                product.setProductWorkflow(buildSubgraphWorkflow(dagIstance, product));
            }
        }

        return workflow;

    }

    private Workflow buildSubgraphWorkflow(DirectedAcyclicGraph<Product, CustomEdge> fullDag, Product currentVertex){
        DirectedAcyclicGraph<Product, CustomEdge> subGraph = new DirectedAcyclicGraph<>(CustomEdge.class);

        Set<Product>  subGraphVertexSet = fullDag.getAncestors(currentVertex); 
        subGraphVertexSet.add(currentVertex);

        // Add all subgraph vertexes
        for (Product product : subGraphVertexSet) {
            subGraph.addVertex(product);
        }

        // Add all subgraph edges
        for (Product product : subGraphVertexSet) {
            for (CustomEdge edge : fullDag.edgesOf(product)) {

                Product sourceProduct = fullDag.getEdgeSource(edge);
                if(!subGraphVertexSet.contains(sourceProduct)){
                    continue;
                }

                Product targetProduct = fullDag.getEdgeTarget(edge);
                if(!subGraphVertexSet.contains(targetProduct)){
                    continue;
                }
                
                subGraph.addEdge(sourceProduct, targetProduct, edge);
            }
            
        }


        Workflow subgraphWorkflow = new Workflow(subGraph);
        return subgraphWorkflow;

    }

}
