package com.qesm;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MyTestWatcher.class)
public class ProductGraphTest {
    private ProductGraph productGraph = new ProductGraph();
    private int maxDepth;
    private int maxWidth;
    private int branchingFactor;


    public int getMaxDepth() {
        return maxDepth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }
    public int getBranchingFactor() {
        return branchingFactor;
    }

    public ProductGraph getProductGraph() {
        return productGraph;
    }

    @RepeatedTest(1000)
    public void testGenerateRandomWorkflow(){
        Random random = new Random();
        maxDepth = random.nextInt(10) + 1;
        maxWidth = random.nextInt(30) + 1;
        branchingFactor = random.nextInt(10) + 1;


        productGraph.generateRandomWorkflow(maxDepth, branchingFactor, maxWidth);
        assertTrue(productGraph.getGraphDepth() <= maxDepth);

        for(Map.Entry<Integer, Integer> entry : productGraph.getLevelWidthCount().entrySet()){
            assertTrue(entry.getValue() <= maxWidth, "Max width: " + maxWidth);
        }

        for(Map.Entry<UUID, Integer> entry : productGraph.getNodeToNumChildren().entrySet()){
            assertTrue(entry.getValue() <= branchingFactor, "Branching factor: " + branchingFactor);
        }

    }

}
