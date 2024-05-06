package com.qesm;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class StructuredTree {

    private DirectedAcyclicGraph<ProductType, CustomEdge> originalWorkflow;
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;


    public StructuredTree(DirectedAcyclicGraph<ProductType, CustomEdge> dag) {
        this.originalWorkflow = dag;
        this.structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);
    }

    public void testPrint(){
        STPNBlock stpnBlock1 = new SimpleBlock(new ProcessedType("p1", null, 0));
        STPNBlock stpnBlock2 = new SimpleBlock(new ProcessedType("p2", null, 0));
        STPNBlock stpnBlock3 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock1, stpnBlock2)));

        STPNBlock stpnBlock4 = new SimpleBlock(new ProcessedType("p4", null, 0));
        STPNBlock stpnBlock5 = new SimpleBlock(new ProcessedType("p5", null, 0));
        STPNBlock stpnBlock6 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock4, stpnBlock5)));
        
        STPNBlock stpnBlock7 = new SeqBlock(new ArrayList<STPNBlock>(List.of(stpnBlock3, stpnBlock6)));

        
        stpnBlock7.printBlockInfo(0);
    }

    public void buildStructuredTree(){
        
    }

}
