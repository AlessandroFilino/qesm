package com.qesm;
import java.util.Map;
import java.util.Random;
import java.util.LinkedHashMap;
import java.util.function.Supplier;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.Iterator;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.traverse.DepthFirstIterator;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;


public class ProductGraph{
    private DirectedAcyclicGraph<ProductType, CustomEdge> sharedDag;
    private DirectedAcyclicGraph<ProductType, CustomEdge> unsharedDag;
    private ProductType rootNode;

    private boolean unsharedDagExists = false;

    public enum DagType {
        SHARED,
        UNSHARED
    }
    
    public ProductGraph() {
        this.sharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
        this.unsharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> getSharedDag() {
        return sharedDag;
    }

    public DirectedAcyclicGraph<ProductType, CustomEdge> getUnsharedDag() {
        if(checkDAGs()){
            return unsharedDag;
        }
        return null;
        
    }

    public ProductType getRootNode() {
        return rootNode;
    }

    //TODO: Abbiamo due grafi da gestire --> Ha senso verificare il tipo importato ?
    public void importDag(DirectedAcyclicGraph<ProductType, CustomEdge> dagToImport){
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

    }

    public void generateRandomDAG(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor){

        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor, maxBranchingDownFactor);
        randDAGGenerator.generateGraph(sharedDag);
        rootNode = randDAGGenerator.getRootNode();
    }

    public void printDAG(DagType dagType){

        if(!checkDAGs()){
            return;
        }

        DirectedAcyclicGraph<ProductType, CustomEdge> dag;

        if(dagType == DagType.SHARED) {
            dag = sharedDag;
        }
        else if (dagType == DagType.UNSHARED) {
            dag = unsharedDag;
        }
        else {
            System.out.println("ERRORE: DAG Type non previsto");
            return;
        }

        Iterator<ProductType> iter = new DepthFirstIterator<ProductType, CustomEdge>(dag);
        while (iter.hasNext()) {
            ProductType vertex = iter.next();
            System.out.println("Vertex " + vertex.getNameType() + " type: " + vertex.getClass() + " is connected to: ");
            for (CustomEdge connectedEdge : dag.edgesOf(vertex)) {
                System.out.println("\t[" + dag.getEdgeSource(connectedEdge).getNameType() + " -> " + dag.getEdgeTarget(connectedEdge).getNameType() + "]");
            }
        }
         
    }
    
    public void exportDAGDotLanguage(String filePath, DagType dagType){
        if(!checkDAGs()){
            return;
        }

        DirectedAcyclicGraph<ProductType, CustomEdge> dag;

        if(dagType == DagType.SHARED) {
            dag = sharedDag;
        }
        else if (dagType == DagType.UNSHARED) {
            dag = unsharedDag;
        }
        else {
            System.out.println("ERRORE: DAG Type non previsto");
            return;
        }

        // Esportazione del grafo in formato DOT
        DOTExporter<ProductType, CustomEdge> exporter = new DOTExporter<>(v -> v.getNameType());

        exporter.setVertexIdProvider(v -> v.getNameType());
        exporter.setEdgeIdProvider(e -> dag.getEdgeSource(e).getNameType() + "-" + dag.getEdgeTarget(e).getNameType());
        
        Function<ProductType, Map<String, Attribute>> vertexAttributeProvider = v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("circle", AttributeType.STRING));
            if(v.getClass().equals(RawMaterialType.class)){
                map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
                map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nRAW_TYPE", AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("RawMaterialType", AttributeType.STRING));
            }
            else{
                map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
                map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nPROCESSED_TYPE" + "\nquantityProduced: " + v.getQuantityProduced(), AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("ProcessedType", AttributeType.STRING));
                map.put("quantity_produced", new DefaultAttribute<Integer>(v.getQuantityProduced(), AttributeType.INT));
            }
            return map;
        };

        exporter.setVertexAttributeProvider(vertexAttributeProvider);

        Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider = e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            ProductType sourceVertex = dag.getEdgeSource(e);
            ProductType targetVertex = dag.getEdgeTarget(e); 
            
            for (RequirementEntryType requirementEntry : targetVertex.getRequirements()) {
                if(requirementEntry.getEntryType().getUuid() == sourceVertex.getUuid()){
                    map.put("label", new DefaultAttribute<String>("quantityNeeded: " + requirementEntry.getQuantityRequired(), AttributeType.STRING));
                    map.put("quantity_required", new DefaultAttribute<Integer>(requirementEntry.getQuantityRequired(), AttributeType.INT));
                    break;
                }
            } 
            return map;
        };

        exporter.setEdgeAttributeProvider(edgeAttributeProvider);

        Supplier<Map<String, Attribute>> graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };

        exporter.setGraphAttributeProvider(graphAttributeProvider);


        try {
            FileWriter writer = new FileWriter(filePath);
            exporter.exportGraph(dag, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDagDotLanguage(String filePath){

        sharedDag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        DOTImporter<ProductType, CustomEdge> importer = new DOTImporter<ProductType, CustomEdge>();

        BiFunction<String, Map<String, Attribute>, ProductType> vertexFactoryFunction = (vertexName, attributesMap) -> {
            
            ProductType vertex;
            String vertexType;

            try {
                vertexType = attributesMap.get("vertex_type").toString();
            } catch (NullPointerException e) {
                System.err.println("Errore nell'importazione, impossibile trovare il campo vertex_type per il nodo: " + vertexName);
                return new RawMaterialType(""); 
            }

            if(vertexType.equals("RawMaterialType")){
                vertex = new RawMaterialType(vertexName);
            }
            else if(vertexType.equals("ProcessedType")){
                try {
                    Integer quantityProduced = Integer.valueOf(attributesMap.get("quantity_produced").getValue());
                    vertex = new ProcessedType(vertexName, null, quantityProduced);
                    
                } catch (NullPointerException e) {
                    System.err.println("Errore nell'importazione, impossibile trovare il campo quantity_produced per il nodo: " + vertexName);
                    return new RawMaterialType(""); 
                }
            }
            else{
                System.err.println("Errore nell'importazione, tipo non riconosciuto del campo vertex_type per il nodo: " + vertexName);
                return new RawMaterialType("");
            }
            
            return vertex;
        };

        importer.setVertexWithAttributesFactory(vertexFactoryFunction);

        Function<Map<String,Attribute>, CustomEdge> edgeWithAttributesFactory = (attributesMap) -> {
            CustomEdge edge = new CustomEdge();

            try {
                Integer quantityRequired = Integer.valueOf((attributesMap.get("quantity_required").getValue()));
                edge.setQuantityRequired(quantityRequired);
                
            } catch (NullPointerException e) {
                System.err.println("Errore nell'importazione, impossibile trovare il campo quantity_required");
                return edge; 
            }

            return edge;
        };

        importer.setEdgeWithAttributesFactory(edgeWithAttributesFactory);

        try {
            FileReader reader = new FileReader(filePath);
            sharedDag = new DirectedAcyclicGraph<ProductType,CustomEdge>(CustomEdge.class);
            importer.importGraph(sharedDag, reader);
        } catch (IOException e) {
            e.printStackTrace();
        }


        // Generate ProcessedType requirements based on edge connections
        Iterator<ProductType> iter = new DepthFirstIterator<ProductType, CustomEdge>(sharedDag);
        while (iter.hasNext()) {
            ProductType vertex = iter.next();
            for (CustomEdge connectedEdge : sharedDag.edgesOf(vertex)) {
                ProductType targetVertex = sharedDag.getEdgeTarget(connectedEdge);
                ProductType sourceVertex = sharedDag.getEdgeSource(connectedEdge);
                if(targetVertex.getUuid() == vertex.getUuid()){
                    vertex.addRequirementEntry(new RequirementEntryType(sourceVertex, connectedEdge.getQuantityRequired()));
                }
            }
        }
    }

    public void renderDotFile(String dotFilePath, String outputFilePath, double scale){

        try {
            // Render DOT file to PNG
            Graphviz.fromFile(new File(dotFilePath))
                    .scale(scale)
                    .render(Format.PNG) // Render to PNG format
                    .toFile(new File(outputFilePath)); // Save the rendered graph to a file
            System.out.println("Graph rendered successfully.");
        } catch (IOException e) {
            System.err.println("Error rendering graph: " + e.getMessage());
        }
    }

    public boolean isDagConnected(DagType dagType){
        if(!checkDAGs()){
            return false;
        }

        DirectedAcyclicGraph<ProductType, CustomEdge> dag;

        if(dagType == DagType.SHARED) {
            dag = sharedDag;
        }
        else if (dagType == DagType.UNSHARED) {
            dag = unsharedDag;
        }
        else {
            System.out.println("ERRORE: DAG Type non previsto");
            return false;
        }

        ConnectivityInspector<ProductType, CustomEdge> connInspector = new ConnectivityInspector<ProductType, CustomEdge>(dag);
        return connInspector.isConnected();

    }

    private boolean checkDAGs() {
        if(sharedDag == null){
            System.out.println("ERRORE: Il DAG shared deve ancora essere generato");
            return false;
        }
        else if(!unsharedDagExists){
            sharedToUnsharedGraph(rootNode);
            unsharedDagExists = true;
            return true;
        } 
        else {
            return true;
        }

    }

    int id = 0;
    public ProductType sharedToUnsharedGraph(ProductType node) {
        ProductType newNode;
        if (node.getClass() == ProcessedType.class) {
            newNode = new ProcessedType(node.getNameType()+"_"+id, null, node.getQuantityProduced());
            id++;
        }
        else {
            newNode = new RawMaterialType(node.getNameType()+"_"+id);
            id ++;
        }
        unsharedDag.addVertex(newNode);

        if(sharedDag.inDegreeOf(node) == 0){
            return newNode;
        } else { 
            id = 0;
            for(CustomEdge edge: sharedDag.incomingEdgesOf(node)) {
                ProductType child = sharedDag.getEdgeSource(edge);
                ProductType newChild = sharedToUnsharedGraph(child);
                unsharedDag.addEdge(newChild, newNode);
            }
        }
        return newNode;
    }


    public ProductType sharedToUnsharedGraph_V2(ProductType node) {
        
    }



}

