package com.qesm;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.Optional;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultDirectedGraph ;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ProductGraph{
    GraphStats graphStats = new GraphStats();
    ProductType rootNode;
    Graph<ProductType, DefaultEdge> jGraphT = new DefaultDirectedGraph<>(DefaultEdge.class);

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
    
    public void generateRandomWorkflow(int maxDepth, int branchingFactor, int maxWidth) {
        RandomWorkflow workflow = new RandomWorkflow(maxDepth, branchingFactor, maxWidth);
        rootNode = workflow.generate(0);
        computeWorkflowStats();
        workflowToJGraphT();
    }

    public void drawGraph() {

        // Esportazione del grafo in formato DOT
        DOTExporter<ProductType, DefaultEdge> exporter = new DOTExporter<>(v -> v.getNameType());

        exporter.setVertexIdProvider(v -> v.getNameType());
        exporter.setEdgeIdProvider(e -> jGraphT.getEdgeSource(e).getNameType() + "-" + jGraphT.getEdgeTarget(e).getNameType());


        try {
            FileWriter writer = new FileWriter("./output/jGraphT.dot");
            exporter.exportGraph(jGraphT, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FunctionalInterface
    private interface CallBack {
        // Callback to be called in exploreWorkflow
        void apply(Integer cur_depth, ProductType node);
    }

    private void exploreWorkflow(ProductType currentNode, int currentDepth, CallBack callback){

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

    public void printWorkflow() {
        
        CallBack print = (Integer cur_depth, ProductType node) -> {
            printTab(cur_depth);
            System.out.println(node.getNameType());
            return;
        };
        
        exploreWorkflow(rootNode, 0, print); 

        return;

    }
    
    private void computeWorkflowStats() {

        final class Count implements CallBack{
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

    private void workflowToJGraphT(){

        CallBack populateGraph = (Integer cur_depth, ProductType node) -> {
            ArrayList<RequirementEntryType> children = node.getRequirements();
            
            jGraphT.addVertex(node);

            if(!children.isEmpty()){
                for (RequirementEntryType child : children) {
                    jGraphT.addVertex(child.getEntryType());
                    jGraphT.addEdge(child.getEntryType(), node);
                }
            }

            return;
        };
        
        exploreWorkflow(rootNode, 0, populateGraph); 

        return;
    }

}
