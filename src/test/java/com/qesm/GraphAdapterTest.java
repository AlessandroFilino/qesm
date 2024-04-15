package com.qesm;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphAdapterTest {
    
    @Test
    public void testGenerateRandomWorkflow(){
        int maxDepth = 4;
        int maxWidth = 4;
        int branchingFactor = 4;

        ProductType workflow;
        GraphAdapter randomGraph = new GraphAdapter();
        workflow = randomGraph.generateRandomWorkflow(maxDepth, maxWidth, branchingFactor);
        
        randomGraph.PrintGraph(workflow, 0);

    }
    
}
