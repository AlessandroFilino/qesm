package com.qesm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class StructuredTree {

    private final DirectedAcyclicGraph<ProductType, CustomEdge> originalWorkflow;
    private final ProductType originalRootNode;
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    private STPNBlock structuredTreeRootNode;


    public StructuredTree(DirectedAcyclicGraph<ProductType, CustomEdge> dag, ProductType rootNode) {
        this.originalWorkflow = dag;
        this.structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);

        this.originalRootNode = rootNode;

        this.structuredTreeRootNode = initializeStructuredTree(rootNode);
        
        // this.structuredTreeRootNode.printBlockInfo(0);
        System.out.println();
        this.structuredWorkflow.vertexSet().forEach(block -> System.out.println(block.getSimpleElement().getNameType()));
    }

    private STPNBlock initializeStructuredTree(ProductType currentNode){
        
        STPNBlock newBlock;
        newBlock = new SimpleBlock((ProcessedType)currentNode);
        boolean newBlockAlreadyInserted = false;
        for (STPNBlock block : structuredWorkflow.vertexSet()) {
            if(block.getSimpleElement() == newBlock.getSimpleElement()){
                newBlockAlreadyInserted = true;
                break;
            }
        }
        if(!newBlockAlreadyInserted){
            structuredWorkflow.addVertex(newBlock);
        }
        for (ProductType childNode : originalWorkflow.getAncestors(currentNode)){
            
            if(childNode.getClass() == RawMaterialType.class){
                newBlock.addEnablingToken((RawMaterialType)childNode);
            }
            else{
                STPNBlock childBlock = initializeStructuredTree(childNode);
                if(childBlock != null){
                    structuredWorkflow.addEdge(childBlock, newBlock);
                }
            }
        }
        if(!newBlockAlreadyInserted){
            return newBlock;
        }
        else{
            return null;
        }
        
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
