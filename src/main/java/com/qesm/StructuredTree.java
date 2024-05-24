package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.ProductType.ItemType;


public class StructuredTree {

    private final DirectedAcyclicGraph<ProductType, CustomEdge> originalWorkflow;
    private final ProductType originalRootNode;
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    private STPNBlock structuredTreeRootBlock;

    public StructuredTree(DirectedAcyclicGraph<ProductType, CustomEdge> dag, ProductType rootNode) {
        this.originalWorkflow = dag;
        this.structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);

        this.originalRootNode = rootNode;

        // this.structuredTreeRootNode = initializeStructuredTree(rootNode);

        // Add all processedType as simpleBlock to structuredWorkflow
        for (ProductType node : originalWorkflow.vertexSet()) {

            if (node.getItemType() == ItemType.PROCESSED) {

                STPNBlock newBlock = new SimpleBlock(node);
                ArrayList<ProductType> enablingTokens = new ArrayList<>();

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);

                    if (sourceNode.getItemType() == ItemType.RAW_MATERIAL) {
                        enablingTokens.add(sourceNode);
                    }
                }
                structuredWorkflow.addVertex(newBlock);
            }
        }

        // originalWorkflow.forEach(n -> System.out.println(n.getNameType()));

        // Mapping alla edges of originalWorkflow to structuredWorkflow
        for (ProductType node : originalWorkflow.vertexSet()) {
            if (node.getItemType() == ItemType.PROCESSED) {
                STPNBlock currBlock = findSimpleBlockFromProcessedType(node);

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    ProductType sourceNode = originalWorkflow.getEdgeSource(inEdge);
                    if (sourceNode.getItemType() == ItemType.PROCESSED) {
                        structuredWorkflow.addEdge(findSimpleBlockFromProcessedType(sourceNode),
                                currBlock);
                    }
                }

                for (CustomEdge outEdge : originalWorkflow.outgoingEdgesOf(node)) {
                    ProductType targetNode = originalWorkflow.getEdgeTarget(outEdge);
                    if (targetNode.getItemType() == ItemType.PROCESSED) {
                        structuredWorkflow.addEdge(currBlock,
                                findSimpleBlockFromProcessedType(targetNode));
                    }
                }

            }
        }

        // Find rootBlock
        for (STPNBlock block : structuredWorkflow) {
            if(structuredWorkflow.outDegreeOf(block) == 0){
                structuredTreeRootBlock = block;
                break;
            }
        }

    }

    public DirectedAcyclicGraph<STPNBlock, CustomEdge> getStructuredWorkflow() {
        return structuredWorkflow;
    }

    public STPNBlock getStructuredTreeRootBlock() {
        return structuredTreeRootBlock;
    }

    private STPNBlock findSimpleBlockFromProcessedType(ProductType elementToFind) {
        for (STPNBlock block : structuredWorkflow) {
            if (elementToFind == block.getSimpleElement()) {
                return block;
            }
        }
        return null;
    }

    public void testPrint() {
        StochasticTime pdf = new UniformTime(0,1);
        STPNBlock stpnBlock1 = new SimpleBlock(new ProductType("v1", 0, pdf));
        STPNBlock stpnBlock2 = new SimpleBlock(new ProductType("v2", 0, pdf));
        STPNBlock stpnBlock3 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock1, stpnBlock2)));

        STPNBlock stpnBlock4 = new SimpleBlock(new ProductType("v3", 0, pdf));
        STPNBlock stpnBlock5 = new SimpleBlock(new ProductType("v4", 0, pdf));
        STPNBlock stpnBlock6 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock4, stpnBlock5)));


        STPNBlock stpnBlock7 = new SeqBlock(new ArrayList<STPNBlock>(List.of(stpnBlock3, stpnBlock6)));
        STPNBlock stpnBlock8 = new SimpleBlock(new ProductType("v5", 0, pdf));
        STPNBlock stpnBlock9 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock7, stpnBlock8)));

        STPNBlock stpnBlock10 = new SimpleBlock(new ProductType("v6", 0, pdf));
        STPNBlock stpnBlock11 = new SeqBlock(new ArrayList<STPNBlock>(List.of(stpnBlock10, stpnBlock9)));
        
        STPNBlock stpnBlock12 = new SimpleBlock(new ProductType("v7", 0, pdf));
        STPNBlock stpnBlock13 = new SimpleBlock(new ProductType("v8", 0, pdf));
        STPNBlock stpnBlock14 = new SimpleBlock(new ProductType("v9", 0, pdf));

        STPNBlock stpnBlock15 = new AndBlock(new ArrayList<STPNBlock>(List.of(stpnBlock11, stpnBlock12, stpnBlock13, stpnBlock14)));

        DirectedAcyclicGraph<STPNBlock, CustomEdge> testTree = new DirectedAcyclicGraph<>(CustomEdge.class);
        testTree.addVertex(stpnBlock15);
        STPNBlockCustumEdgeIO exporter = new STPNBlockCustumEdgeIO();
        exporter.writeDotFile("./output/test.html", testTree);
    }

    public void buildStructuredTree(){
        buildStructuredTree(false, null);
    }

    public void buildStructuredTreeAndExportSteps(String folderPath){
        buildStructuredTree(true, folderPath);
    }

    private void buildStructuredTree(boolean exportAllIteration, String folderPath) {
        int seqReplacedCount = 0;
        int andReplacedCount = 0;
        int stepCount = 0;

        if(exportAllIteration){
            this.exportStructuredTreeToDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
            stepCount++;
        }

        do {
            seqReplacedCount = findAndReplaceSeqs();
            if(exportAllIteration && seqReplacedCount > 0){
                // System.out.println("Seq replaced: " + seqReplacedCount);
                this.exportStructuredTreeToDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
                stepCount++;
            }

            andReplacedCount = findAndReplaceAnds();
            if(exportAllIteration && andReplacedCount > 0){
                // System.out.println("And replaced: " + andReplacedCount);
                this.exportStructuredTreeToDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
                stepCount++;
            }

            
        } while (seqReplacedCount > 0 || andReplacedCount > 0);
    }

    private int findAndReplaceSeqs(){

        int seqReplacedCount = 0;
        // Calculate SEQs
        ArrayList<ArrayList<STPNBlock>> seqList = new ArrayList<>();
        for (STPNBlock block : structuredWorkflow.vertexSet()) {

            if (block == structuredTreeRootBlock && structuredWorkflow.inDegreeOf(block) == 1) {
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

                
                if(seq.get(0) == structuredTreeRootBlock){
                    structuredTreeRootBlock = seqBlock;
                }
                else{
                    STPNBlock targetBlock =  structuredWorkflow.getEdgeTarget(structuredWorkflow.outgoingEdgesOf(seq.get(0)).iterator().next());
                    structuredWorkflow.addEdge(seqBlock, targetBlock);
                }

                if(structuredWorkflow.inDegreeOf(seq.get(seq.size() - 1)) == 1){
                    STPNBlock sourceBlock =  structuredWorkflow.getEdgeSource(structuredWorkflow.incomingEdgesOf(seq.get(seq.size() - 1)).iterator().next());
                    structuredWorkflow.addEdge(sourceBlock, seqBlock);
                }
                

                for (STPNBlock blockToRemove : seq) {
                    structuredWorkflow.removeVertex(blockToRemove);
                    // System.out.print(seqBlock.getSimpleElement().getNameType() + " ");
                }
                // System.out.println("");
                seqReplacedCount++;
            }
        }

        return seqReplacedCount;
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

    private int findAndReplaceAnds(){

        int andReplacedCount = 0;
        // Calculate ANDs
        ArrayList<HashMap<STPNBlock, ArrayList<STPNBlock>>> andList = new ArrayList<>();
        
        for (STPNBlock block : structuredWorkflow.vertexSet()) {
            
            if(structuredWorkflow.inDegreeOf(block) > 1 ){
                
                HashMap<STPNBlock, ArrayList<STPNBlock>> forkMap = new HashMap<>();

                for (CustomEdge customEdge : structuredWorkflow.incomingEdgesOf(block)) {
                    STPNBlock childBlock = structuredWorkflow.getEdgeSource(customEdge);
                    
                    if(structuredWorkflow.inDegreeOf(childBlock) <= 1 && structuredWorkflow.outDegreeOf(childBlock) == 1){
                        STPNBlock forkBlock;
                        if(structuredWorkflow.inDegreeOf(childBlock) == 0){
                            forkBlock = null;
                        }
                        else{
                            forkBlock = structuredWorkflow.getEdgeSource(structuredWorkflow.incomingEdgesOf(childBlock).iterator().next());
                        }
                        
                        if(!forkMap.containsKey(forkBlock)){
                            forkMap.put(forkBlock, new ArrayList<>());
                        }
                        
                        forkMap.get(forkBlock).add(childBlock);
                        
                    }
                }
                andList.add(forkMap);
            }
        }

        // Replace Ands with andBlocks
        for (HashMap<STPNBlock, ArrayList<STPNBlock>> andMap : andList) {
            for (ArrayList<STPNBlock> andBlocks : andMap.values()) {
                if(andBlocks.size() > 1){
                    // System.out.println(andBlocks);
                    AndBlock andBlock = new AndBlock(andBlocks);
                    structuredWorkflow.addVertex(andBlock);

                    STPNBlock targetBlock =  structuredWorkflow.getEdgeTarget(structuredWorkflow.outgoingEdgesOf(andBlocks.get(0)).iterator().next());
                    structuredWorkflow.addEdge(andBlock, targetBlock);
                    
                    if(structuredWorkflow.inDegreeOf(andBlocks.get(0)) == 1){
                        STPNBlock sourceBlock =  structuredWorkflow.getEdgeSource(structuredWorkflow.incomingEdgesOf(andBlocks.get(0)).iterator().next());
                        structuredWorkflow.addEdge(sourceBlock, andBlock);
                    }
                    
                    for (STPNBlock andBlockElements : andBlocks) {
                        structuredWorkflow.removeVertex(andBlockElements);
                    }

                    andReplacedCount++;
                }
            }
        }


        return andReplacedCount;
    } 

    public void exportDagToDotFile(String filePath) {

        ProductTypeCustomEdgeIO<ProductType> exporter = new ProductTypeCustomEdgeIO<>(ProductType.class);
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

}
