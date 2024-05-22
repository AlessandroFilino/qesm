package com.qesm;

import java.util.Iterator;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

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

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, pdfType);
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
        ProductTypeCustomEdgeIO exporter = new ProductTypeCustomEdgeIO();
        exporter.writeDotFile(filePath, dag);
    }

    public void importDagFromDotFile(String filePath) {
        dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        ProductTypeCustomEdgeIO importer = new ProductTypeCustomEdgeIO();
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
        Workflow dag = new Workflow();


        return dag;

    }

}
