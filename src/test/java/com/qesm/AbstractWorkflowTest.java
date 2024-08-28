package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class AbstractWorkflowTest {

    private WorkflowType wf1;
    private WorkflowType wf2;
    private WorkflowType wf2_copy;
    private WorkflowType wf3;
    private WorkflowType wf1Reference;
    private WorkflowType wfNull;
    private WorkflowIstance wi1;

    // Nodes of wf2 and wf3
    private ProductType v0;
    private ProductType v1;
    private ProductType v2;
    private ProductType v3;

    // Nodes of wf2 copy
    private ProductType v0_copy;
    private ProductType v1_copy;
    private ProductType v2_copy;

    @BeforeEach
    public void setup() {
        wf1 = new WorkflowType();
        wf1.generateRandomDAG(3, 3, 2, 2, 60, PdfType.UNIFORM);
        wf1Reference = wf1;

        wi1 = wf1.makeIstance();

        DirectedAcyclicGraph<ProductType, CustomEdge> dag2 = new DirectedAcyclicGraph<>(CustomEdge.class);
        v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        v1 = new ProductType("v1", 2, new UniformTime(2, 4));
        v2 = new ProductType("v2", 3, new UniformTime(4, 6));
        dag2.addVertex(v0);
        dag2.addVertex(v1);
        dag2.addVertex(v2);
        dag2.addEdge(v2, v1);
        dag2.addEdge(v1, v0);
        wf2 = new WorkflowType(dag2);

        DirectedAcyclicGraph<ProductType, CustomEdge> dag2_copy = new DirectedAcyclicGraph<>(CustomEdge.class);
        v0_copy = new ProductType("v0", 1, new UniformTime(0, 2));
        v1_copy = new ProductType("v1", 2, new UniformTime(2, 4));
        v2_copy = new ProductType("v2", 3, new UniformTime(4, 6));
        dag2_copy.addVertex(v0_copy);
        dag2_copy.addVertex(v1_copy);
        dag2_copy.addVertex(v2_copy);
        dag2_copy.addEdge(v2_copy, v1_copy);
        dag2_copy.addEdge(v1_copy, v0_copy);
        wf2_copy = new WorkflowType(dag2_copy);

        DirectedAcyclicGraph<ProductType, CustomEdge> dag3 = new DirectedAcyclicGraph<>(CustomEdge.class);
        v3 = new ProductType("v3", 4, new UniformTime(6, 8));
        dag3.addVertex(v0);
        dag3.addVertex(v1);
        dag3.addVertex(v2);
        dag3.addVertex(v3);
        dag3.addEdge(v3, v2);
        dag3.addEdge(v2, v1);
        dag3.addEdge(v1, v0);
        wf3 = new WorkflowType(dag3);

    }

    @Test
    void testCustomEquals() {
        assertTrue(wf1.equalsNodesAttributes(wf1Reference));
        assertFalse(wf1.equalsNodesAttributes(wfNull));
        assertTrue(wf1.equalsNodesAttributes(wi1));
        assertTrue(wf2.equalsNodesAttributes(wf2_copy));
        assertFalse(wf2.equalsNodesAttributes(wf3));
    }

    @Test
    void testGetRootNode() {
        assertNotEquals(wf1.computeRootNode(), null);
        assertEquals(wf2.computeRootNode(), v0);
        assertEquals(wf2_copy.computeRootNode(), v0_copy);
        assertEquals(wf3.computeRootNode(), v0);
    }

    @Test
    void testFindProduct() {
        assertEquals(v0, wf2.findProduct("v0").get());
        assertEquals(v1, wf2.findProduct("v1").get());
        assertEquals(v2, wf2.findProduct("v2").get());
        assertEquals(Optional.empty(), wf2.findProduct("v3"));
    }

    @Test
    void testComputeParallelismValue() {
        // TODO TEST: Implement test
        wf1.computeParallelismValue();
    }

}
