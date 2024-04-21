package com.qesm;

import java.util.ArrayList;
import java.util.Random;
import java.util.function.Supplier;

import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphCycleProhibitedException;

public class RandomDAGGenerator{

    private Random random;
    private int numVertices;
    private int numEdges;
    private boolean isConnected;
    private int vId;
    private int maxRandomQuantity;
    private Supplier<ProductType> vSupplierRandom;
    private Supplier<ProductType> vSupplierProcessedType;

    public RandomDAGGenerator(int numVertices, int minNumEdges){
        this.random = new Random();
        this.numVertices = numVertices;
        this.numEdges = minNumEdges;
        this.isConnected = false;
        this.vId = 0;
        this.maxRandomQuantity = 100;

        this.vSupplierRandom = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex;

                if(random.nextBoolean()){
                    vertex = new RawMaterialType("v" + vId);
                    vId++;
                }
                else{
                    vertex = new ProcessedType("v" + vId, null, random.nextInt(maxRandomQuantity));
                    vId++;
                }
                
                return vertex;
            }
        };

        this.vSupplierProcessedType = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex = new ProcessedType("v" + vId, null, random.nextInt(maxRandomQuantity));
                return vertex;
            }
        };
    }

    public void generateGraph(DirectedAcyclicGraph<ProductType, CustomEdge> dag) {
        
        dag.setVertexSupplier(vSupplierRandom);

        ArrayList<ProductType> vertexRawMaterialTypeArray = new ArrayList<ProductType>();
        ArrayList<ProductType> vertexProcessedTypeArray = new ArrayList<ProductType>();
        ArrayList<ProductType> vertexTotalArray = new ArrayList<ProductType>();
        ConnectivityInspector<ProductType, CustomEdge> connInspector;
        
        // Add vertices
        for (int i = 0; i < numVertices; i++) {
            
            // Make sure that there is at least one processedType
            if(i == numVertices - 1 && vertexProcessedTypeArray.isEmpty()){
                dag.setVertexSupplier(vSupplierProcessedType);
            }

            ProductType vertex = dag.addVertex();
            
            if(vertex.getClass() == RawMaterialType.class){
                vertexRawMaterialTypeArray.add(vertex);
            }
            else{
                vertexProcessedTypeArray.add(vertex);
            }
            vertexTotalArray.add(vertex);
        }


        int consecutiveFailCounter = 0;

        // try to add numEdges to the graph, if they're not enought, continue until it's connected
        while (dag.edgeSet().size() < numEdges || !isConnected) {

            // If too many consecutive fails are raised probably it's not possible to add any more edges to the graph
            // TODO test which value best fits
            if(consecutiveFailCounter > 1000){
                break;
            }

            ProductType sourceVertex = vertexTotalArray.get(random.nextInt(numVertices));
            ProductType targetVertex = vertexProcessedTypeArray.get(random.nextInt(vertexProcessedTypeArray.size()));

            // Ensure the edge doesn't introduce a loop between two vertex
            if (sourceVertex != targetVertex && !dag.containsEdge(sourceVertex, targetVertex) && !dag.containsEdge(targetVertex, sourceVertex)) {
                // Ensure the edge doesn't introduce a cycle in the DAG
                try {
                    dag.addEdge(sourceVertex, targetVertex);
                    targetVertex.addRequirementEntry(new RequirementEntryType(sourceVertex, random.nextInt(maxRandomQuantity)));
                    consecutiveFailCounter = 0;
                    connInspector = new ConnectivityInspector<ProductType, CustomEdge>(dag);
                    if(connInspector.isConnected()){
                        isConnected = true;
                    }
                    // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType());
                } catch (GraphCycleProhibitedException e) {
                    consecutiveFailCounter++;
                }
            }
            else{
                consecutiveFailCounter++;
            }
        }

    }
}