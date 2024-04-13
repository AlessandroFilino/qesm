package com.qesm;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphAdapter{

    private int maxDepth;
    private int branchingFactor;
    private int maxWidth;
    private Random random;
    private HashMap<Integer, Integer> levelWidthCount = new HashMap<>();
    private Integer processedTypeCount = 0;
    private Integer rawMaterialTypeCount = 0;


    public GraphAdapter(int maxDepth, int branchingFactor, int maxWidth) {
        this.maxDepth = maxDepth;
        this.branchingFactor = branchingFactor;
        this.maxWidth = maxWidth;
        this.random = new Random();
    }

    public void testGraph(ProductType rootNode){

        RawMaterialType r1 = new RawMaterialType("r1");
        RawMaterialType r2 = new RawMaterialType("r2");

        RequirementEntryType req1 = new RequirementEntryType(r1, 1);
        RequirementEntryType req2 = new RequirementEntryType(r2, 2);

        ProcessedType p1 = new ProcessedType("p1", new ArrayList<>(List.of(req1, req2)), 5);

        Graph<ProductType, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        // Aggiunta di vertici al grafo
        graph.addVertex(r1);
        graph.addVertex(r2);
        graph.addVertex(p1);

        // Aggiunta di archi al grafo
        graph.addEdge(p1, r1);
        graph.addEdge(p1, r2);

        // // Stampa dei vertici del grafo
        // System.out.println("Vertici del grafo: " );
        // for (ProductType  product : graph.vertexSet()) {
        //     System.out.println(product.getNameType());
        // }
        // // Stampa degli archi del grafo
        // System.out.println("Archi del grafo: ");
        // for (DefaultEdge  edge : graph.edgeSet()) {
        //     System.out.println(edge.toString());
        // }
    }

    public ProductType generateRandomGraph(int depth) {

        ProductType root;

        if(depth == maxDepth - 1){
            root = new RawMaterialType("r" + rawMaterialTypeCount);
            rawMaterialTypeCount++;
            return root;
        }
        
        if(! levelWidthCount.containsKey(depth)){
            levelWidthCount.put(depth, 0);
        }
        
        int currentWidth = levelWidthCount.get(depth); 
        int currentWidthLeft = maxWidth - currentWidth;
        int numChildren = 0;

        if(currentWidthLeft - branchingFactor >= 0){
            numChildren = random.nextInt(branchingFactor + 1);
        }
        else{
            numChildren = (currentWidthLeft > 0) ? random.nextInt(currentWidthLeft) : 0;
        }
        
        levelWidthCount.put(depth, currentWidth + numChildren);

        // System.out.println("Depth : " + depth);
        // System.out.println(rawMaterialTypeCount);
        // System.out.println(processedTypeCount);

        if(numChildren == 0){
            root = new RawMaterialType("r" + rawMaterialTypeCount);
            rawMaterialTypeCount++;
        }
        else{
            root = new ProcessedType("p" + processedTypeCount, new ArrayList<>(), 1);
            processedTypeCount++;

            for (int i = 0; i < numChildren; i++) {
                ProductType child = generateRandomGraph(depth + 1);
                
                RequirementEntryType req = new RequirementEntryType(child, 2);
                root.addRequirementEntry(req);
            }
        } 

        return root;
    }

    private void PrintTab(int numTab){
        for (int index = 0; index < numTab; index++) {
            System.out.print("  ");
        }
    }

    public String PrintGraph(ProductType rootNode, int currentDepth){

        ArrayList<RequirementEntryType> children = rootNode.getRequirements();
        
        if(children.isEmpty()){
            PrintTab(currentDepth);
            System.out.println(rootNode.getNameType());
            return rootNode.getNameType();
        }
        else{
            PrintTab(currentDepth);
            System.out.println(rootNode.getNameType());
            for (RequirementEntryType child : children) {
                PrintGraph(child.getEntryType(), currentDepth + 1) ;
            }
            return rootNode.getNameType();
        }
    }
}
