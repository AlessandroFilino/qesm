package com.qesm;

import java.util.ArrayList;
import java.util.List;

public class ProductGraph extends GraphAdapter {
    
    public ProductGraph() {
        super(4, 4, 4);
    }


    public static void main(String[] args) {
        
        for (int idx = 0; idx < 10; idx++) {
            ProductGraph testGraph = new ProductGraph();
            ProductType rootNode = testGraph.generateRandomGraph(0);
            testGraph.PrintGraph(rootNode, 0);
            System.out.println("");
        }
    }
}
