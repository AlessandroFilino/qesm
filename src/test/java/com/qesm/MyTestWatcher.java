package com.qesm;

import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class MyTestWatcher implements TestWatcher {
    private boolean testFailed = false;

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        if(!testFailed){
            Optional <Object> testIstance = context.getTestInstance();
            ProductGraphTest curTestIstance = (ProductGraphTest) testIstance.get();

            ProductGraph my_graph = curTestIstance.getProductGraph();
            my_graph.printWorkflow();
            System.out.println("Tot nodes: " + my_graph.getTotNodes());
            System.out.println("Depth: " + my_graph.getGraphDepth()); 
            for(Map.Entry<Integer, Integer> entry : my_graph.getLevelWidthCount().entrySet()){
                System.out.println("Level " + entry.getKey() + "    width: " + entry.getValue());
            }
            // for(Map.Entry<UUID, Integer> entry : graphAdapter.getNodeToNumChildren().entrySet()){
            //     assertTrue(entry.getValue() <= branchingFactor);
            // }

            
            System.out.println("maxWidth: " + curTestIstance.getMaxWidth());
            System.out.println("maxDepth: " + curTestIstance.getMaxDepth());
            System.out.println("branchingFactor: " + curTestIstance.getBranchingFactor());

            testFailed = true;
        }
    }
}