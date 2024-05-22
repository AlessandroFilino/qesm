package com.qesm;

import java.util.Iterator;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.qesm.RandomDAGGenerator.PdfType;


public class ProductGraph {
    private DirectedAcyclicGraph<ProductType, CustomEdge> sharedDag;
    private DirectedAcyclicGraph<ProductType, CustomEdge> unsharedDag;

    private boolean sharedDagExists = false;
    private boolean unsharedDagExists = false;

    public enum DagType {
        SHARED,
        UNSHARED
    }

    public ProductGraph() {
        this.sharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
        this.unsharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> getSharedDag() throws ExceptionQesm {
        try {
            checkDAGs(DagType.SHARED);
            return sharedDag;
        } catch (Exception e) {
            throw e;
        }
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> getUnsharedDag() throws ExceptionQesm {
        try {
            checkDAGs(DagType.UNSHARED);
            return unsharedDag;
        } catch (Exception e) {
            throw e;
        }
    }

    public ProductType getRootNode(DagType dagType) throws ExceptionQesm {
        DirectedAcyclicGraph<ProductType, CustomEdge> dag;
        if (dagType == DagType.SHARED) {
            dag = sharedDag;
        } else if (dagType == DagType.UNSHARED) {
            dag = unsharedDag;
        } else {
            throw new ExceptionQesm("ERROR: DAG Type not expected");
        }

        for (ProductType node : dag.vertexSet()) {
            if (dag.outDegreeOf(node) == 0) {
                return node;
            }
        }
        throw new ExceptionQesm("ERROR: there isn't a root node");
    }

    // TODO: We have to manage two different type of graphs --> Does it make sense
    // to verify which type of graph we import?
    public void importDag(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport) {
        this.sharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        // import all verteces
        for (ProductType vertex : dagToImport.vertexSet()) {
            sharedDag.addVertex(vertex);
        }

        // Aggiungi tutti gli archi dal DAG originale alla copia
        for (CustomEdge edge : dagToImport.edgeSet()) {
            ProductType source = dagToImport.getEdgeSource(edge);
            ProductType target = dagToImport.getEdgeTarget(edge);
            sharedDag.addEdge(source, target, edge);
        }

        checkSharedDagExist();
    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor, PdfType pdfType) {
        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, pdfType);
        randDAGGenerator.generateGraph(sharedDag);
        checkSharedDagExist();
    }

    public void printDAG(DagType dagType) throws ExceptionQesm {
        DirectedAcyclicGraph<ProductType, CustomEdge> dag;
        try {
            dag = selectDAG(dagType);
        } catch (Exception e) {
            throw e;
        }

        Iterator<ProductType> iter = new DepthFirstIterator<ProductType, CustomEdge>(dag);
        while (iter.hasNext()) {
            ProductType vertex = iter.next();
            System.out.println("Vertex " + vertex.getNameType() + " type: " + vertex.getClass() + " is connected to: ");
            for (CustomEdge connectedEdge : dag.edgesOf(vertex)) {
                System.out.println("\t[" + dag.getEdgeSource(connectedEdge).getNameType() + " -> "
                        + dag.getEdgeTarget(connectedEdge).getNameType() + "]");
            }
        }
    }

    public void exportDagToDotFile(String filePath, DagType dagType) throws ExceptionQesm{
        DirectedAcyclicGraph<ProductType, CustomEdge> dag;
        try {
            dag = selectDAG(dagType);
        } catch (Exception e) {
            throw e;
        }

        ProductTypeCustomEdgeIO exporter = new ProductTypeCustomEdgeIO();
        exporter.writeDotFile(filePath, dag);
    }

    public void importDagFromDotFile(String filePath) {
        sharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        ProductTypeCustomEdgeIO importer = new ProductTypeCustomEdgeIO();
        importer.readDotFile(filePath, sharedDag);

        
        // TODO: not deleting this part at the moment just because we could need it
        // while removing the RequirementEntryType
        // // Generate ProcessedType requirements based on edge connections
        // Iterator<ProductType> iter = new DepthFirstIterator<ProductType,
        // CustomEdge>(sharedDag);
        // while (iter.hasNext()) {
        // ProductType vertex = iter.next();
        // for (CustomEdge connectedEdge : sharedDag.edgesOf(vertex)) {
        // ProductType targetVertex = sharedDag.getEdgeTarget(connectedEdge);
        // ProductType sourceVertex = sharedDag.getEdgeSource(connectedEdge);
        // if(targetVertex.getUuid() == vertex.getUuid()){
        // vertex.addRequirementEntry(new RequirementEntryType(sourceVertex,
        // connectedEdge.getQuantityRequired()));
        // }
        // }
        // }

        checkSharedDagExist();
    }

    public boolean isDagConnected(DagType dagType) throws ExceptionQesm {
        DirectedAcyclicGraph<ProductType, CustomEdge> dag;
        try {
            dag = selectDAG(dagType);
        } catch (Exception e) {
            throw e;
        }

        ConnectivityInspector<ProductType, CustomEdge> connInspector = new ConnectivityInspector<ProductType, CustomEdge>(
                dag);
        return connInspector.isConnected();

    }

    private void checkSharedDagExist() {
        // Check if sharedDag exist and it's not empty
        if (sharedDag == null || sharedDag.vertexSet().isEmpty()) {
            sharedDagExists = false;
        } else {
            sharedDagExists = true;
        }
    }

    private void checkDAGs(DagType dagType) throws ExceptionQesm {
        if (!sharedDagExists) {
            throw new ExceptionQesm("ERROR: shared DAG need to be generated first");
        } else if (!unsharedDagExists && dagType == DagType.UNSHARED) {
            DAGSharedToUnsharedConverter dagConverter = new DAGSharedToUnsharedConverter(sharedDag, unsharedDag,
                    getRootNode(DagType.SHARED));
            dagConverter.makeConversion();
            unsharedDagExists = true;
        }
    }

    private DirectedAcyclicGraph<ProductType, CustomEdge> selectDAG(DagType dagType) throws ExceptionQesm {
        try {
            checkDAGs(dagType);
        } catch (Exception e) {
            throw e;
        }

        DirectedAcyclicGraph<ProductType, CustomEdge> dag;

        if (dagType == DagType.SHARED) {
            dag = sharedDag;
        } else if (dagType == DagType.UNSHARED) {
            dag = unsharedDag;
        } else {
            throw new ExceptionQesm("ERROR: DAG Type not expected");
        }

        return dag;
    }

}
