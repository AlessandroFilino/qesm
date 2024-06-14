package com.qesm;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.RepeatedTest;

import com.qesm.RandomDAGGenerator.PdfType;

public class RandomDAGGeneratorTest {

    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;
    ProductType rootNode;
    HashMap<Integer, ArrayList<ProductType>> levelToVertices = new HashMap<Integer, ArrayList<ProductType>>();

    @RepeatedTest(100)
    void testGenerateGraph() {

        // int maxHeight = 5;
        // int maxWidth = 3;
        // int maxBranchingUpFactor = 3;
        // int maxBranchingDownFactor = 3; 

        Random random = new Random();
        int maxHeight = random.nextInt(20) + 1;
        int maxWidth = random.nextInt(20) + 1;
        int maxBranchingUpFactor = random.nextInt(5) + 1;
        int maxBranchingDownFactor = random.nextInt(5) + 1;
        int branchingUpProbability = random.nextInt(1, 101);

        dag = new DirectedAcyclicGraph<ProductType, CustomEdge>(CustomEdge.class);

        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor, maxBranchingDownFactor, branchingUpProbability, PdfType.UNIFORM);
        randDAGGenerator.generateGraph(dag);

        for (ProductType vertex : dag.vertexSet()) {
            assertTrue(dag.inDegreeOf(vertex) <= maxBranchingDownFactor && dag.outDegreeOf(vertex) <= maxBranchingUpFactor);
        }

        
        rootNode = randDAGGenerator.getRootNode();
        // Calculate levels for each vertex exploring all passible paths from vertex to rootNode
        for (ProductType vertex : dag.vertexSet()) {
            if(vertex == rootNode){
                continue;
            }
            recursiveSearch(0, vertex, vertex);
        }

        

        // ProductGraph testGraph = new ProductGraph();
        // testGraph.importDag(dag);
        // testGraph.exportDAGDotLanguage("./output/test.dot");
        // testGraph.renderDotFile("./output/test.dot", "./media/testGenerateGraph.png", 3);
        
        for (Integer level : levelToVertices.keySet()) {
            // System.out.println("Level: " + level);
            // for (ProductType vertex: levelToVertices.get(level)) {
            //     System.out.println(vertex.getNameType());
            // }
            assertTrue(levelToVertices.get(level).size() <= maxWidth);
        }

        assertTrue(Collections.max(levelToVertices.keySet()) <= maxHeight);

    }

    private void recursiveSearch(int currLevel, ProductType currVertex, ProductType sourceVertex){
        if(currVertex == rootNode){
            if(!levelToVertices.containsKey(currLevel)){
                levelToVertices.put(currLevel, new ArrayList<ProductType>(List.of(sourceVertex)));
            }
            else{
                levelToVertices.get(currLevel).add(sourceVertex);
            }
            return;
        }
        else{
            for (CustomEdge edge : dag.outgoingEdgesOf(currVertex)) {
                recursiveSearch(currLevel + 1, dag.getEdgeTarget(edge), sourceVertex);
            }
        } 
    }

}
