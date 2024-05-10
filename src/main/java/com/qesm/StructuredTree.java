package com.qesm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DirectedAcyclicGraph;

import com.qesm.ProductGraph.DagType;

public class StructuredTree {

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

            if (node.getClass() == ProcessedType.class) {

                STPNBlock newBlock = new SimpleBlock((ProcessedType) node);
                ArrayList<RawMaterialType> enablingTokens = new ArrayList<>();

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);

                    if (sourceNode.getClass() == RawMaterialType.class) {
                        enablingTokens.add((RawMaterialType) sourceNode);
                    }
                }
                structuredWorkflow.addVertex(newBlock);
            }
        }

        // originalWorkflow.forEach(n -> System.out.println(n.getNameType()));

        // Mapping alla edges of originalWorkflow to structuredWorkflow
        for (ProductType node : originalWorkflow.vertexSet()) {
            if (node.getClass() == ProcessedType.class) {
                STPNBlock currBlock = findSimpleBlockFromProcessedType((ProcessedType) node);

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);
                    if (sourceNode.getClass() == ProcessedType.class) {
                        structuredWorkflow.addEdge(findSimpleBlockFromProcessedType((ProcessedType) sourceNode),
                                currBlock);
                    }
                }

                for (CustomEdge outEdge : originalWorkflow.outgoingEdgesOf(node)) {
                    ProductType targetNode = originalWorkflow.getEdgeTarget(outEdge);
                    if (targetNode.getClass() == ProcessedType.class) {
                        structuredWorkflow.addEdge(currBlock,
                                findSimpleBlockFromProcessedType((ProcessedType) targetNode));
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

    private STPNBlock findSimpleBlockFromProcessedType(ProcessedType elementToFind) {
        for (STPNBlock block : structuredWorkflow) {
            if (elementToFind == block.getSimpleElement()) {
                return block;
            }
        }
        return null;
    }

    // private STPNBlock initializeStructuredTree(ProductType currentNode){

    // STPNBlock newBlock;
    // newBlock = new SimpleBlock((ProcessedType)currentNode);
    // boolean newBlockAlreadyInserted = false;
    // for (STPNBlock block : structuredWorkflow.vertexSet()) {
    // if(block.getSimpleElement() == newBlock.getSimpleElement()){
    // newBlockAlreadyInserted = true;
    // break;
    // }
    // }
    // if(!newBlockAlreadyInserted){
    // structuredWorkflow.addVertex(newBlock);
    // }
    // for(CustomEdge inEdge : originalWorkflow.incomingEdgesOf(currentNode)){
    // ProductType childNode = originalWorkflow.getEdgeSource(inEdge);

    // if(childNode.getClass() == RawMaterialType.class){
    // newBlock.addEnablingToken((RawMaterialType)childNode);
    // }
    // else{
    // STPNBlock childBlock = initializeStructuredTree(childNode);
    // if(childBlock != null){
    // structuredWorkflow.addEdge(childBlock, newBlock);
    // }
    // }
    // }
    // if(!newBlockAlreadyInserted){
    // return newBlock;
    // }
    // else{
    // return null;
    // }

    // }

    public void testPrint() {
        STPNBlock stpnBlock1 = new SimpleBlock(new ProcessedType("v1", 0));
        STPNBlock stpnBlock2 = new SimpleBlock(new ProcessedType("v2", 0));
        STPNBlock stpnBlock3 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock1, stpnBlock2)));

        STPNBlock stpnBlock4 = new SimpleBlock(new ProcessedType("v4", 0));
        STPNBlock stpnBlock5 = new SimpleBlock(new ProcessedType("v5", 0));
        STPNBlock stpnBlock6 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock4, stpnBlock5)));


        STPNBlock stpnBlock7 = new SeqBlock(new ArrayList<STPNBlock>(List.of(stpnBlock3, stpnBlock6)));
        STPNBlock stpnBlock8 = new SimpleBlock(new ProcessedType("v8", 0));
        STPNBlock stpnBlock9 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock7, stpnBlock8)));

        // stpnBlock7.printBlockInfo(0);

        DirectedAcyclicGraph<STPNBlock, CustomEdge> testTree = new DirectedAcyclicGraph<>(CustomEdge.class);
        testTree.addVertex(stpnBlock9);
        STPNBlockCustumEdgeIO exporter = new STPNBlockCustumEdgeIO();
        exporter.writeDotFile("./output/test.html", testTree);
    }

    public void buildStructuredTree() {
        // Calculate SEQs
        ArrayList<ArrayList<STPNBlock>> seqList = new ArrayList<>();
        for (STPNBlock block : structuredWorkflow.vertexSet()) {

            if (block == structuredTreeRootNode) {
                ArrayList<STPNBlock> seq = new ArrayList<>();
                calculateSeq(block, seq);
                seqList.add(seq);
            } else if (structuredWorkflow.outDegreeOf(block) == 1 && structuredWorkflow.inDegreeOf(block) == 1) {

                STPNBlock parentBlock = structuredWorkflow.getEdgeTarget(structuredWorkflow.outgoingEdgesOf(block).iterator().next());
                if(structuredWorkflow.outDegreeOf(parentBlock) > 1 || structuredWorkflow.inDegreeOf(parentBlock) > 1){
                    ArrayList<STPNBlock> seq = new ArrayList<>();
                    calculateSeq(block, seq);
                    seqList.add(seq);
                }
            }
        }

        // Replace SEQs with SeqBlocks
        for (ArrayList<STPNBlock> seq : seqList) {
            if (seq.size() > 1) {

                SeqBlock seqBlock = new SeqBlock(seq);
                structuredWorkflow.addVertex(seqBlock);

                STPNBlock targetBlock =  structuredWorkflow.getEdgeTarget(structuredWorkflow.outgoingEdgesOf(seq.get(0)).iterator().next());
                STPNBlock sourceBlock =  structuredWorkflow.getEdgeSource(structuredWorkflow.outgoingEdgesOf(seq.get(seq.size() - 1)).iterator().next());
                structuredWorkflow.addEdge(seqBlock, targetBlock);
                structuredWorkflow.addEdge(sourceBlock, seqBlock);

                for (STPNBlock blockToRemove : seq) {
                    structuredWorkflow.removeVertex(blockToRemove);
                    // System.out.print(seqBlock.getSimpleElement().getNameType() + " ");
                }
                // System.out.println("");
            }
        }
    }

    private void calculateSeq(STPNBlock startingBlock, ArrayList<STPNBlock> seq) {

        STPNBlock currentBlock = startingBlock;
        seq.add(currentBlock);
        
        while(true){
            currentBlock = structuredWorkflow.getEdgeSource(structuredWorkflow.incomingEdgesOf(currentBlock).iterator().next());
            if(structuredWorkflow.outDegreeOf(currentBlock) == 1){
                if(structuredWorkflow.inDegreeOf(currentBlock) == 1){
                    seq.add(currentBlock);
                }
                else if(structuredWorkflow.inDegreeOf(currentBlock) == 0){
                    seq.add(currentBlock);
                    break;
                }
                else{
                    break;
                }
            }
            else{
                break;
            }
        }
    }

    public void exportDagToDotFile(String filePath) {

        ProductTypeCustomEdgeIO exporter = new ProductTypeCustomEdgeIO();
        exporter.writeDotFile(filePath, originalWorkflow);
    }

    // TODO check if it will be needed
    // public void importDagFromDotFile(String filePath) {

    // ProductTypeCustomEdgeIO importer = new ProductTypeCustomEdgeIO();
    // originalWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);
    // importer.readDotFile(filePath, originalWorkflow);

    // }

    public void exportStructuredTreeToDotFile(String filePath) {

        STPNBlockCustumEdgeIO exporter = new STPNBlockCustumEdgeIO();
        exporter.writeDotFile(filePath, structuredWorkflow);
    }

    public void importStructuredTreeFromDotFile(String filePath) {

        STPNBlockCustumEdgeIO importer = new STPNBlockCustumEdgeIO();
        importer.readDotFile(filePath, structuredWorkflow);

    }

    void renderDotFile(String dotFilePath, String outputFilePath, double scale) {
        BasicImportExport.renderDotFile(dotFilePath, outputFilePath, scale);
    }

}
