package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.traverse.DepthFirstIterator;

public class StructuredTree<V extends AbstractProduct> implements DotFileConverter<STPNBlock> {

    private final DirectedAcyclicGraph<V, CustomEdge> originalWorkflow;
    private DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow;
    private STPNBlock structuredTreeRootBlock;
    private Class<V> dagVertexClass;

    public StructuredTree(Class<V> dagVertexClass) {
        originalWorkflow = null;
        this.dagVertexClass = dagVertexClass;
    }

    public StructuredTree(DirectedAcyclicGraph<V, CustomEdge> dag, Class<V> dagVertexClass) {
        this.originalWorkflow = dag;
        this.dagVertexClass = dagVertexClass;
        initializeStructuredWorkflow();
    }

    private void initializeStructuredWorkflow() {
        this.structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);

        // Add all processedType as simpleBlock to structuredWorkflow
        for (V node : originalWorkflow.vertexSet()) {

            if (node.isProcessed()) {

                STPNBlock newBlock = new SimpleBlock(node);

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    V sourceNode = originalWorkflow.getEdgeSource(inEdge);

                    if (!sourceNode.isProcessed()) {
                        newBlock.addEnablingToken(sourceNode);
                    }
                }
                structuredWorkflow.addVertex(newBlock);
            }
        }

        // Mapping alla edges of originalWorkflow to structuredWorkflow
        for (V node : originalWorkflow.vertexSet()) {
            if (node.isProcessed()) {
                STPNBlock currBlock = findSimpleBlockFromProcessedType(node);

                for (CustomEdge inEdge : originalWorkflow.incomingEdgesOf(node)) {
                    V sourceNode = originalWorkflow.getEdgeSource(inEdge);
                    if (sourceNode.isProcessed()) {
                        structuredWorkflow.addEdge(findSimpleBlockFromProcessedType(sourceNode),
                                currBlock);
                    }
                }

                for (CustomEdge outEdge : originalWorkflow.outgoingEdgesOf(node)) {
                    V targetNode = originalWorkflow.getEdgeTarget(outEdge);
                    if (targetNode.isProcessed()) {
                        structuredWorkflow.addEdge(currBlock,
                                findSimpleBlockFromProcessedType(targetNode));
                    }
                }

            }
        }

        // Find rootBlock
        for (STPNBlock block : structuredWorkflow) {
            if (structuredWorkflow.outDegreeOf(block) == 0) {
                structuredTreeRootBlock = block;
                break;
            }
        }
    }

    public StructuredTree(DirectedAcyclicGraph<V, CustomEdge> dag,
            DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow,
            Class<V> dagVertexClass) {
        this.originalWorkflow = dag;
        this.structuredWorkflow = structuredWorkflow;
        this.dagVertexClass = dagVertexClass;
    }

    public DirectedAcyclicGraph<STPNBlock, CustomEdge> getStructuredWorkflow() {
        return structuredWorkflow;
    }

    public STPNBlock getStructuredTreeRootBlock() {
        return structuredTreeRootBlock;
    }

    private STPNBlock findSimpleBlockFromProcessedType(V elementToFind) {
        for (STPNBlock block : structuredWorkflow) {
            if (elementToFind == block.getSimpleElement()) {
                return block;
            }
        }
        return null;
    }

    public void buildStructuredTree() {
        buildStructuredTree(false, false, null);
    }

    public void buildStructuredTreeAndExportSteps(String folderPath, boolean serialization) {
        buildStructuredTree(true, serialization, folderPath);
    }

    private void buildStructuredTree(boolean exportAllIteration, boolean serialization, String folderPath) {
        int seqReplacedCount = 0;
        int andReplacedCount = 0;
        int stepCount = 0;

        if (exportAllIteration) {
            if (serialization) {
                this.exportDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
            } else {
                this.exportDotFileNoSerialization(folderPath + "/structuredTree_" + stepCount + ".dot");
            }
            stepCount++;
        }

        do {

            seqReplacedCount = findAndReplaceSeqs();
            if (exportAllIteration && seqReplacedCount > 0) {
                // System.out.println("Seq replaced: " + seqReplacedCount);
                if (serialization) {
                    this.exportDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
                } else {
                    this.exportDotFileNoSerialization(folderPath + "/structuredTree_" + stepCount + ".dot");
                }
                stepCount++;
            }

            andReplacedCount = findAndReplaceAnds();
            if (exportAllIteration && andReplacedCount > 0) {
                // System.out.println("And replaced: " + andReplacedCount);
                if (serialization) {
                    this.exportDotFile(folderPath + "/structuredTree_" + stepCount + ".dot");
                } else {
                    this.exportDotFileNoSerialization(folderPath + "/structuredTree_" + stepCount + ".dot");
                }
                stepCount++;
            }

        } while (seqReplacedCount > 0 || andReplacedCount > 0);
    }

    private int findAndReplaceSeqs() {

        int seqReplacedCount = 0;
        // Calculate SEQs
        ArrayList<ArrayList<STPNBlock>> seqList = new ArrayList<>();
        for (STPNBlock block : structuredWorkflow.vertexSet()) {

            if (block == structuredTreeRootBlock && structuredWorkflow.inDegreeOf(block) == 1) {
                ArrayList<STPNBlock> seq = new ArrayList<>();
                calculateSeq(block, seq);
                seqList.add(seq);
            } else if (structuredWorkflow.outDegreeOf(block) == 1 && structuredWorkflow.inDegreeOf(block) == 1) {

                STPNBlock parentBlock = structuredWorkflow
                        .getEdgeTarget(structuredWorkflow.outgoingEdgesOf(block).iterator().next());
                if (structuredWorkflow.outDegreeOf(parentBlock) > 1 || structuredWorkflow.inDegreeOf(parentBlock) > 1) {
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

                if (seq.get(0) == structuredTreeRootBlock) {
                    structuredTreeRootBlock = seqBlock;
                } else {
                    STPNBlock targetBlock = structuredWorkflow
                            .getEdgeTarget(structuredWorkflow.outgoingEdgesOf(seq.get(0)).iterator().next());
                    structuredWorkflow.addEdge(seqBlock, targetBlock);
                }

                if (structuredWorkflow.inDegreeOf(seq.get(seq.size() - 1)) == 1) {
                    STPNBlock sourceBlock = structuredWorkflow.getEdgeSource(
                            structuredWorkflow.incomingEdgesOf(seq.get(seq.size() - 1)).iterator().next());
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

        while (true) {
            currentBlock = structuredWorkflow
                    .getEdgeSource(structuredWorkflow.incomingEdgesOf(currentBlock).iterator().next());
            if (structuredWorkflow.outDegreeOf(currentBlock) == 1) {
                if (structuredWorkflow.inDegreeOf(currentBlock) == 1) {
                    seq.add(currentBlock);
                } else if (structuredWorkflow.inDegreeOf(currentBlock) == 0) {
                    seq.add(currentBlock);
                    break;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private int findAndReplaceAnds() {

        int andReplacedCount = 0;
        // Calculate ANDs
        ArrayList<HashMap<STPNBlock, ArrayList<STPNBlock>>> andList = new ArrayList<>();

        for (STPNBlock block : structuredWorkflow.vertexSet()) {

            if (structuredWorkflow.inDegreeOf(block) > 1) {

                HashMap<STPNBlock, ArrayList<STPNBlock>> forkMap = new HashMap<>();

                for (CustomEdge customEdge : structuredWorkflow.incomingEdgesOf(block)) {
                    STPNBlock childBlock = structuredWorkflow.getEdgeSource(customEdge);

                    if (structuredWorkflow.inDegreeOf(childBlock) <= 1
                            && structuredWorkflow.outDegreeOf(childBlock) == 1) {
                        STPNBlock forkBlock;
                        if (structuredWorkflow.inDegreeOf(childBlock) == 0) {
                            forkBlock = null;
                        } else {
                            forkBlock = structuredWorkflow
                                    .getEdgeSource(structuredWorkflow.incomingEdgesOf(childBlock).iterator().next());
                        }

                        if (!forkMap.containsKey(forkBlock)) {
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
                if (andBlocks.size() > 1) {
                    // System.out.println(andBlocks);
                    AndBlock andBlock = new AndBlock(andBlocks);
                    structuredWorkflow.addVertex(andBlock);

                    STPNBlock targetBlock = structuredWorkflow
                            .getEdgeTarget(structuredWorkflow.outgoingEdgesOf(andBlocks.get(0)).iterator().next());
                    structuredWorkflow.addEdge(andBlock, targetBlock);

                    if (structuredWorkflow.inDegreeOf(andBlocks.get(0)) == 1) {
                        STPNBlock sourceBlock = structuredWorkflow
                                .getEdgeSource(structuredWorkflow.incomingEdgesOf(andBlocks.get(0)).iterator().next());
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        // Check if generic types are different
        StructuredTree<?> genericStructuredTreeToCompare = (StructuredTree<?>) obj;
        Class<?> dagVertexClassToCompare = genericStructuredTreeToCompare.getDAGVertexClass();

        if (!dagVertexClass.equals(dagVertexClassToCompare)) {
            return false;
        }

        StructuredTree<V> structuredTreeToCompare = uncheckedCast(obj);

        if (structuredWorkflow.vertexSet().size() != structuredTreeToCompare.structuredWorkflow.vertexSet().size()) {
            return false;
        }

        ArrayList<STPNBlock> structuredWorkflowNodes = new ArrayList<>();
        ArrayList<STPNBlock> structuredTreeToCompareNodes = new ArrayList<>();

        Iterator<STPNBlock> iterStructuredWorkflow = new DepthFirstIterator<STPNBlock, CustomEdge>(structuredWorkflow);
        while (iterStructuredWorkflow.hasNext()) {
            structuredWorkflowNodes.add(iterStructuredWorkflow.next());
        }

        Iterator<STPNBlock> iterWorkflowToCompare = new DepthFirstIterator<STPNBlock, CustomEdge>(
                structuredTreeToCompare.structuredWorkflow);
        while (iterWorkflowToCompare.hasNext()) {
            structuredTreeToCompareNodes.add(iterWorkflowToCompare.next());
        }

        Integer totalNodes = structuredWorkflow.vertexSet().size();
        for (int nodeNumber = 0; nodeNumber < totalNodes; nodeNumber++) {
            STPNBlock node = structuredWorkflowNodes.get(nodeNumber);
            STPNBlock nodeToCompare = structuredTreeToCompareNodes.get(nodeNumber);
            if (!node.equals(nodeToCompare)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public String toString() {
        return "StructuredWorkflow:\n" + structuredWorkflow.toString();
    }

    protected Class<V> getDAGVertexClass() {
        return dagVertexClass;
    }

    @SuppressWarnings("unchecked")
    private StructuredTree<V> uncheckedCast(Object o) {
        return (StructuredTree<V>) o;
    }

    @Override
    public DirectedAcyclicGraph<STPNBlock, CustomEdge> CloneDag() {
        return structuredWorkflow;
    }

    @Override
    public Class<STPNBlock> getVertexClass() {
        return STPNBlock.class;
    }

    @Override
    public void setDag(DirectedAcyclicGraph<STPNBlock, CustomEdge> dagToSet) {
        structuredWorkflow = dagToSet;
    }

}
