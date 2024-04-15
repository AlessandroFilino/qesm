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
public class GraphAdapterTest {
    private GraphAdapter graphAdapter = new GraphAdapter();

    public GraphAdapter getGraphAdapter() {
        return graphAdapter;
    }

    @RepeatedTest(50)
    public void testGenerateRandomWorkflow(){
        Random random = new Random();
        int maxDepth = random.nextInt(10) + 1;
        int maxWidth = random.nextInt(30) + 1;
        int branchingFactor = random.nextInt(10) + 1;

        graphAdapter.generateRandomWorkflow(maxDepth, maxWidth, branchingFactor);
        assertTrue(graphAdapter.getGraphDepth() <= maxDepth);

        for(Map.Entry<Integer, Integer> entry : graphAdapter.getLevelWidthCount().entrySet()){
            assertTrue(entry.getValue() <= maxWidth);
        }

        for(Map.Entry<UUID, Integer> entry : graphAdapter.getNodeToNumChildren().entrySet()){
            assertTrue(entry.getValue() <= branchingFactor);
        }

        // graphAdapter.printWorkflow();

    }
    
}
