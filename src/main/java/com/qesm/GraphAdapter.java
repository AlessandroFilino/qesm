package com.qesm;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphAdapter implements ProductGraph{
    

    public class RandomWorkflow {

        private int maxDepth;
        private int branchingFactor;
        private int maxWidth;
        private Random random;
        private HashMap<Integer, Integer> levelWidthCount;
        private Integer processedTypeCount = 0;
        private Integer rawMaterialTypeCount = 0;

        public RandomWorkflow(int maxDepth, int branchingFactor, int maxWidth) {
            this.maxDepth = maxDepth;
            this.branchingFactor = branchingFactor;
            this.maxWidth = maxWidth;
            this.random = new Random();
            this.levelWidthCount = new HashMap<>();
            this.processedTypeCount = 0;
            this.rawMaterialTypeCount = 0;  
        }
    
        public ProductType generateRandomWorkflow(int depth) {

            ProductType root;
    
            if(depth == maxDepth - 1){
                root = new RawMaterialType("r" + rawMaterialTypeCount);
                rawMaterialTypeCount++;
                return root;
            }
            
            if(! levelWidthCount.containsKey(depth + 1)){
                levelWidthCount.put(depth + 1, 0);
            }
            
            int currentBelowWidth = levelWidthCount.get(depth + 1);
            int belowWidthLeft = maxWidth - currentBelowWidth;
            int numChildren = 0;
    
            if(belowWidthLeft - branchingFactor >= 0){
                numChildren = random.nextInt(branchingFactor + 1);
            }
            else{
                numChildren = (belowWidthLeft > 0) ? random.nextInt(belowWidthLeft + 1) : 0;
            }
            
            levelWidthCount.put(depth + 1, currentBelowWidth + numChildren);
    
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
                    ProductType child = generateRandomWorkflow(depth + 1);
                    
                    RequirementEntryType req = new RequirementEntryType(child, 2);
                    root.addRequirementEntry(req);
                }
            } 
    
            return root;
        }
    }

    public GraphAdapter() {

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

    private void PrintTab(int numTab){
        for (int index = 0; index < numTab; index++) {
            System.out.print("  ");
        }
    }

    @Override
    public ProductType generateRandomWorkflow(int maxDepth, int branchingFactor, int maxWidth) {
        RandomWorkflow randomGraph = new RandomWorkflow(maxDepth, branchingFactor, maxWidth);

        return randomGraph.generateRandomWorkflow(0);
    }

    @FunctionalInterface
    private interface Function<A,B> {
        A apply(B args); 
    }

    @Override
    public String PrintGraph(ProductType rootNode, int currentDepth) {
        Function <Void, Void> print = (Void) -> {
            System.out.println("x"); 
            return Void;
        };

        //ExploreWorkflow(rootNode, currentDepth ); 
        return "a";

    }

    private String ExploreWorkflow <A, B>(ProductType rootNode, int currentDepth, Function<A, B> function){

        ArrayList<RequirementEntryType> children = rootNode.getRequirements();
        
        if(children.isEmpty()){
            PrintTab(currentDepth);
            System.out.println(rootNode.getNameType());
            //TODO
            function.
            return rootNode.getNameType();
        }
        else{
            PrintTab(currentDepth);
            System.out.println(rootNode.getNameType());
            for (RequirementEntryType child : children) {
                ExploreWorkflow(child.getEntryType(), currentDepth + 1) ;
            }
            return rootNode.getNameType();
        }
    }
}
