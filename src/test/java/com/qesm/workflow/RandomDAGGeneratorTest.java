package com.qesm.workflow;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.RepeatedTest;

import com.qesm.workflow.CustomEdge;
import com.qesm.workflow.ProductTemplate;
import com.qesm.workflow.RandomDAGGenerator;
import com.qesm.workflow.RandomDAGGenerator.PdfType;

public class RandomDAGGeneratorTest {

    private DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag;
    ProductTemplate rootNode;
    HashMap<Integer, ArrayList<ProductTemplate>> levelToVertices = new HashMap<Integer, ArrayList<ProductTemplate>>();

    @RepeatedTest(100)
    void testGenerateGraph() {

        Random random = new Random();
        int maxHeight = random.nextInt(20) + 1;
        int maxWidth = random.nextInt(20) + 1;
        int maxBranchingUpFactor = random.nextInt(5) + 1;
        int maxBranchingDownFactor = random.nextInt(5) + 1;
        int branchingUpProbability = random.nextInt(1, 101);

        RandomDAGGenerator randDAGGenerator = new RandomDAGGenerator(maxHeight, maxWidth, maxBranchingUpFactor,
                maxBranchingDownFactor, branchingUpProbability, PdfType.UNIFORM);
        dag = randDAGGenerator.generateGraph();

        // Test maxBranchingDownFactor and maxBranchingUpFactor
        for (ProductTemplate vertex : dag.vertexSet()) {
            assertTrue(dag.inDegreeOf(vertex) <= maxBranchingDownFactor
                    && dag.outDegreeOf(vertex) <= maxBranchingUpFactor);
        }

        rootNode = randDAGGenerator.getRootNode();
        // Calculate levels for each vertex exploring all passible paths from vertex to
        // rootNode
        for (ProductTemplate vertex : dag.vertexSet()) {
            if (vertex == rootNode) {
                continue;
            }
            recursiveSearch(0, vertex, vertex);
        }

        // Test maxWidth
        for (Integer level : levelToVertices.keySet()) {
            // System.out.println("Level: " + level);
            // for (ProductTemplate vertex: levelToVertices.get(level)) {
            // System.out.println(vertex.getName());
            // }
            assertTrue(levelToVertices.get(level).size() <= maxWidth);
        }

        // Test maxHeight
        assertTrue(Collections.max(levelToVertices.keySet()) <= maxHeight);

        // No need to test for branchingUpProbability

    }

    private void recursiveSearch(int currLevel, ProductTemplate currVertex, ProductTemplate sourceVertex) {
        if (currVertex == rootNode) {
            if (!levelToVertices.containsKey(currLevel)) {
                levelToVertices.put(currLevel, new ArrayList<ProductTemplate>(List.of(sourceVertex)));
            } else {
                levelToVertices.get(currLevel).add(sourceVertex);
            }
            return;
        } else {
            for (CustomEdge edge : dag.outgoingEdgesOf(currVertex)) {
                recursiveSearch(currLevel + 1, dag.getEdgeTarget(edge), sourceVertex);
            }
        }
    }

}
