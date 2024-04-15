package com.qesm;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Random;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GraphAdapter implements ProductGraph{
    GraphStats graphStats = new GraphStats();
    ProductType rootNode;

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
    
        private ProductType generate(int depth) {

            ProductType root;
    
            if(depth == maxDepth){
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
    
            if(numChildren == 0){
                root = new RawMaterialType("r" + rawMaterialTypeCount);
                rawMaterialTypeCount++;
            }
            else{
                root = new ProcessedType("p" + processedTypeCount, new ArrayList<>(), 1);
                processedTypeCount++;
    
                for (int i = 0; i < numChildren; i++) {
                    ProductType child = generate(depth + 1);
                    
                    RequirementEntryType req = new RequirementEntryType(child, 2);
                    root.addRequirementEntry(req);
                }
            } 
    
            return root;
        }
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

    private void printTab(int numTab){
        for (int index = 0; index < numTab; index++) {
            System.out.print("  ");
        }
    }

    public int getTotNodes() {
        return graphStats.getTotNodes();
    }

    public int getGraphDepth() {
        return graphStats.getGraphDepth();
    }
    public HashMap<Integer, Integer> getLevelWidthCount() {
        return graphStats.getLevelWidthCount();
    }
    public HashMap<UUID, Integer> getNodeToNumChildren() {
        return graphStats.getNodeToNumChildren();
    }
    
    @Override
    public void generateRandomWorkflow(int maxDepth, int branchingFactor, int maxWidth) {
        RandomWorkflow workflow = new RandomWorkflow(maxDepth, branchingFactor, maxWidth);
        rootNode = workflow.generate(0);
        computeWorkflowStats();
    }

    @FunctionalInterface
    private interface Function {
        // Callback to be called in exploreWorkflow
        void apply(Integer cur_depth, ProductType node);
    }

    private void exploreWorkflow(ProductType currentNode, int currentDepth, Function callback){

        ArrayList<RequirementEntryType> children = currentNode.getRequirements();
        
        if(children.isEmpty()){
            callback.apply(currentDepth, currentNode);
            return;
        }
        else{
            callback.apply(currentDepth, currentNode);
            for (RequirementEntryType child : children) {
                exploreWorkflow(child.getEntryType(), currentDepth + 1, callback) ;
            }
            return;
        }
    }

    @Override
    public void printWorkflow() {
        
        Function print = (Integer cur_depth, ProductType node) -> {
            printTab(cur_depth);
            System.out.println(node.getNameType());
            return;
        };
        
        exploreWorkflow(rootNode, 0, print); 

        return;

    }
    
    private void computeWorkflowStats() {

        final class Count implements Function{
            private GraphStats stats = new GraphStats();
  
            public GraphStats getStats() {
                return stats;
            }

            @Override
            public void apply(Integer cur_depth, ProductType node){
                // Update total number of nodes in graph
                stats.setTotNodes(stats.getTotNodes() + 1);

                // Update depth of graph
                if (stats.getGraphDepth() < cur_depth){
                    stats.setGraphDepth(cur_depth);
                }
                
                // Update width of each layer of graph (if key doesn't exist add it to the hashmap)
                if(! stats.getLevelWidthCount().containsKey(cur_depth)){
                    stats.getLevelWidthCount().put(cur_depth, 1);
                }
                else{
                    int currentWidth = stats.getLevelWidthCount().get(cur_depth);
                    stats.getLevelWidthCount().put(cur_depth, currentWidth + 1);
                }

                // Update number of children that a node has
                stats.getNodeToNumChildren().put(node.getUuid(), node.getRequirements().size());

            }
            
        };

        Count countCallback = new Count();
        
        exploreWorkflow(rootNode, 0, countCallback);

        graphStats = countCallback.getStats();

    }

}
