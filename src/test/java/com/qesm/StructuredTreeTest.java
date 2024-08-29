package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class StructuredTreeTest {

    private WorkflowType wf1;
    private StructuredTree<ProductType> structuredTree1;
    private StructuredTree<ProductType> structuredTree2;
    private DirectedAcyclicGraph<ProductType, CustomEdge> dag;

    // Nodes of wf1
    private ProductType v0;
    private ProductType v1;
    private ProductType v2;
    private ProductType v3;
    private ProductType v4;
    private ProductType v5;
    private ProductType v6;

    @BeforeEach
    public void setup() {

        // DAG structure:
        // v0
        // |
        // v1
        // | \ \
        // v2 v3 v4
        // | |
        // v5 v6

        dag = new DirectedAcyclicGraph<>(CustomEdge.class);
        v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        v1 = new ProductType("v1", 2, new UniformTime(2, 4));
        v2 = new ProductType("v2", 3, new UniformTime(4, 6));
        v3 = new ProductType("v3", 4, new UniformTime(6, 8));
        v4 = new ProductType("v4");
        v5 = new ProductType("v5");
        v6 = new ProductType("v6");
        dag.addVertex(v0);
        dag.addVertex(v1);
        dag.addVertex(v2);
        dag.addVertex(v3);
        dag.addVertex(v4);
        dag.addVertex(v5);
        dag.addVertex(v6);
        dag.addEdge(v3, v1);
        dag.addEdge(v2, v1);
        dag.addEdge(v1, v0);
        dag.addEdge(v4, v1);
        dag.addEdge(v5, v2);
        dag.addEdge(v6, v3);
        wf1 = new WorkflowType(dag);

        structuredTree1 = new StructuredTree<>(wf1.cloneDag(), ProductType.class);
    }

    @Test
    void testInitializeStructuredTree() {
        DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow2 = new DirectedAcyclicGraph<>(CustomEdge.class);

        SimpleBlock simpleBlock0 = new SimpleBlock(v0);
        SimpleBlock simpleBlock1 = new SimpleBlock(v1);
        simpleBlock1.addEnablingToken(v4);
        SimpleBlock simpleBlock2 = new SimpleBlock(v2);
        simpleBlock2.addEnablingToken(v5);
        SimpleBlock simpleBlock3 = new SimpleBlock(v3);
        simpleBlock3.addEnablingToken(v6);

        structuredWorkflow2.addVertex(simpleBlock0);
        structuredWorkflow2.addVertex(simpleBlock1);
        structuredWorkflow2.addVertex(simpleBlock2);
        structuredWorkflow2.addVertex(simpleBlock3);
        structuredWorkflow2.addEdge(simpleBlock3, simpleBlock1);
        structuredWorkflow2.addEdge(simpleBlock2, simpleBlock1);
        structuredWorkflow2.addEdge(simpleBlock1, simpleBlock0);

        structuredTree2 = new StructuredTree<>(null, structuredWorkflow2, ProductType.class);

        assertEquals(structuredTree1, structuredTree2);
    }

    @Test
    void testBuildStructuredTree() {
        DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow2 = new DirectedAcyclicGraph<>(CustomEdge.class);

        STPNBlock simpleBlock0 = new SimpleBlock(v0);
        STPNBlock simpleBlock1 = new SimpleBlock(v1);
        simpleBlock1.addEnablingToken(v4);
        STPNBlock simpleBlock2 = new SimpleBlock(v2);
        simpleBlock2.addEnablingToken(v5);
        STPNBlock simpleBlock3 = new SimpleBlock(v3);
        simpleBlock3.addEnablingToken(v6);

        STPNBlock andBlock1 = new AndBlock(new ArrayList<>(List.of(simpleBlock2, simpleBlock3)));
        STPNBlock seqBlock1 = new SeqBlock(new ArrayList<>(List.of(simpleBlock0, simpleBlock1, andBlock1)));

        structuredWorkflow2.addVertex(seqBlock1);

        structuredTree2 = new StructuredTree<>(null, structuredWorkflow2, ProductType.class);

        structuredTree1.buildStructuredTree();

        assertEquals(structuredTree1, structuredTree2);

    }

    @Test
    void testEquals() {
        StructuredTree<ProductType> structuredTree1Reference = structuredTree1;
        assertEquals(structuredTree1Reference, structuredTree1);

        StructuredTree<ProductType> structuredTree2 = new StructuredTree<>(dag, ProductType.class);
        assertEquals(structuredTree1, structuredTree2);

        structuredTree1.buildStructuredTree();
        assertNotEquals(structuredTree1, structuredTree2);

        structuredTree2.buildStructuredTree();
        assertEquals(structuredTree1, structuredTree2);

        ProductType v7 = new ProductType("v7");
        dag.addVertex(v7);
        dag.addEdge(v7, v3);

        StructuredTree<ProductType> structuredTree3 = new StructuredTree<>(dag, ProductType.class);

        structuredTree3.buildStructuredTree();

        assertNotEquals(structuredTree1, structuredTree3);
    }
}
