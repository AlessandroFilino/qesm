package com.qesm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

public class StructuredTree implements Exporter<STPNBlock, CustomEdge>{

    private final DirectedAcyclicGraph<ProductType, CustomEdge> originalWorkflow;
    private final ProductType originalRootNode;
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    private STPNBlock structuredTreeRootNode;


    public StructuredTree(DirectedAcyclicGraph<ProductType, CustomEdge> dag, ProductType rootNode) {
        this.originalWorkflow = dag;
        this.structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);

        this.originalRootNode = rootNode;

        // this.structuredTreeRootNode = initializeStructuredTree(rootNode);


        // Add all processedType as simpleBlock to structuredWorkflow
        for (ProductType node : originalWorkflow.vertexSet()) {

            if(node.getClass() == ProcessedType.class){

                STPNBlock newBlock = new SimpleBlock((ProcessedType)node);
                ArrayList<RawMaterialType> enablingTokens = new ArrayList<>();

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);

                    if(sourceNode.getClass() == RawMaterialType.class){
                        enablingTokens.add((RawMaterialType)sourceNode);
                    }
                }
                structuredWorkflow.addVertex(newBlock);
            }
        }

        // originalWorkflow.forEach(n -> System.out.println(n.getNameType()));

        // Mapping alla edges of originalWorkflow to structuredWorkflow
        for (ProductType node : originalWorkflow.vertexSet()) {
            if(node.getClass() == ProcessedType.class){
                STPNBlock currBlock = findSimpleBlockFromProcessedType((ProcessedType)node);

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);
                    if(sourceNode.getClass() == ProcessedType.class){
                        structuredWorkflow.addEdge(findSimpleBlockFromProcessedType((ProcessedType)sourceNode), currBlock);
                    }
                }

                for (CustomEdge outEdge : originalWorkflow.outgoingEdgesOf(node)) {
                    ProductType targetNode = originalWorkflow.getEdgeTarget(outEdge);
                    if(targetNode.getClass() == ProcessedType.class){
                        structuredWorkflow.addEdge(currBlock, findSimpleBlockFromProcessedType((ProcessedType)targetNode));
                    }
                }
                
            }
        }
    }


    
    public DirectedAcyclicGraph<STPNBlock, CustomEdge> getStructuredWorkflow() {
        return structuredWorkflow;
    }



    public STPNBlock getStructuredTreeRootNode() {
        return structuredTreeRootNode;
    }



    private STPNBlock findSimpleBlockFromProcessedType(ProcessedType elementToFind){
        for (STPNBlock block : structuredWorkflow) {
            if(elementToFind == block.getSimpleElement()){
                return block;
            }
        }
        return null;
    }

    // private STPNBlock initializeStructuredTree(ProductType currentNode){
        
    //     STPNBlock newBlock;
    //     newBlock = new SimpleBlock((ProcessedType)currentNode);
    //     boolean newBlockAlreadyInserted = false;
    //     for (STPNBlock block : structuredWorkflow.vertexSet()) {
    //         if(block.getSimpleElement() == newBlock.getSimpleElement()){
    //             newBlockAlreadyInserted = true;
    //             break;
    //         }
    //     }
    //     if(!newBlockAlreadyInserted){
    //         structuredWorkflow.addVertex(newBlock);
    //     }
    //     for(CustomEdge inEdge : originalWorkflow.incomingEdgesOf(currentNode)){
    //         ProductType childNode = originalWorkflow.getEdgeSource(inEdge);
            
    //         if(childNode.getClass() == RawMaterialType.class){
    //             newBlock.addEnablingToken((RawMaterialType)childNode);
    //         }
    //         else{
    //             STPNBlock childBlock = initializeStructuredTree(childNode);
    //             if(childBlock != null){
    //                 structuredWorkflow.addEdge(childBlock, newBlock);
    //             }
    //         }
    //     }
    //     if(!newBlockAlreadyInserted){
    //         return newBlock;
    //     }
    //     else{
    //         return null;
    //     }
        
    // }

    @Override
    public Function<CustomEdge, Map<String, Attribute>> defineExporterEdgeAttributeProvider() {
        return e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            
            // map.put("label", new DefaultAttribute<String>("quantityNeeded: " + e.getQuantityRequired() , AttributeType.STRING));
            // map.put("quantity_required", new DefaultAttribute<Integer>(e.getQuantityRequired() , AttributeType.INT));

            return map;
        };
    }

    @Override
    public Function<CustomEdge, String> defineExporterEdgeIdProvider(DirectedAcyclicGraph<STPNBlock, CustomEdge> dag) {
        return e -> dag.getEdgeSource(e).getSimpleElement().getNameType() + "-" + dag.getEdgeTarget(e).getSimpleElement().getNameType();
    }

    @Override
    public Supplier<Map<String, Attribute>> defineExporterGraphAttributeProvider() {
        return () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };
    }

    @Override
    public Function<STPNBlock, Map<String, Attribute>> defineExporterVertexAttributeProvider() {
        return v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("box", AttributeType.STRING));
            // if(v.getClass().equals(RawMaterialType.class)){
            //     map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
            //     map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nRAW_TYPE", AttributeType.STRING));
            //     map.put("vertex_type", new DefaultAttribute<String>("RawMaterialType", AttributeType.STRING));
            // }
            // else{
            //     map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
            //     map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nPROCESSED_TYPE" + "\nquantityProduced: " + v.getQuantityProduced(), AttributeType.STRING));
            //     map.put("vertex_type", new DefaultAttribute<String>("ProcessedType", AttributeType.STRING));
            //     map.put("quantity_produced", new DefaultAttribute<Integer>(v.getQuantityProduced(), AttributeType.INT));
            // }
            return map;
        };
    }

    @Override
    public Function<STPNBlock, String> defineExporterVertexIdProvider() {
        return v -> v.getSimpleElement().getNameType();
    }

    public void testPrint(){
        STPNBlock stpnBlock1 = new SimpleBlock(new ProcessedType("p1", 0));
        STPNBlock stpnBlock2 = new SimpleBlock(new ProcessedType("p2", 0));
        STPNBlock stpnBlock3 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock1, stpnBlock2)));

        STPNBlock stpnBlock4 = new SimpleBlock(new ProcessedType("p4", 0));
        STPNBlock stpnBlock5 = new SimpleBlock(new ProcessedType("p5", 0));
        STPNBlock stpnBlock6 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock4, stpnBlock5)));
        
        STPNBlock stpnBlock7 = new SeqBlock(new ArrayList<STPNBlock>(List.of(stpnBlock3, stpnBlock6)));

        
        stpnBlock7.printBlockInfo(0);
    }

    public void buildStructuredTree(){
        // Check SEQ

        ArrayList<HashSet<STPNBlock>> optimalSeqList = new ArrayList<>(); 
        for (STPNBlock block : structuredWorkflow.vertexSet()) {
            HashSet<STPNBlock> tempSeq = new HashSet<>();
            findSeq(block, tempSeq);

            if(optimalSeqList.size() == 0){
                optimalSeqList.add(tempSeq);
            }
            else{
                boolean sameSeq = false;
                for (int seqIndex = 0; seqIndex < optimalSeqList.size(); seqIndex++) {
                    HashSet<STPNBlock> optimalSeq = optimalSeqList.get(seqIndex);
                    if (tempSeq.containsAll(optimalSeq)) {
                        optimalSeqList.set(seqIndex, tempSeq);
                        sameSeq = true;
                    } else if (optimalSeq.containsAll(tempSeq)) {
                        sameSeq = true;
                    }
                }
                if(!sameSeq){
                    optimalSeqList.add(tempSeq);
                }
            }
        }

        for (HashSet<STPNBlock> seqSet : optimalSeqList) {
            if(seqSet.size() > 1){

                for (STPNBlock seqBlock : seqSet) {
                        System.out.print(seqBlock.getSimpleElement().getNameType() + " ");
                }
                System.out.println("");
            }
        }
    }

    private void findSeq(STPNBlock currentBlock, HashSet<STPNBlock> tempSeq){
        
        switch (structuredWorkflow.inDegreeOf(currentBlock)) {
            case 0:
                tempSeq.add(currentBlock);
                break;
            
            case 1:
                if(structuredWorkflow.outDegreeOf(currentBlock) == 1){
                    tempSeq.add(currentBlock);
                    findSeq(structuredWorkflow.getEdgeSource(structuredWorkflow.incomingEdgesOf(currentBlock).iterator().next()), tempSeq);
                }
                break;
        
            default:
                break;
        }
    }

}
