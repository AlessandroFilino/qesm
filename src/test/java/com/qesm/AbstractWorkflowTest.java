package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class AbstractWorkflowTest {
    
    private static WorkflowType wf1;
    private static WorkflowType wf2;
    private static WorkflowType wf2_copy;
    private static WorkflowType wf3;
    private static WorkflowType wf1Reference;
    private static WorkflowType wfNull;
    private static WorkflowIstance wi1;

    // Nodes of wf2 and wf3
    private static ProductType v0;
    private static ProductType v1;
    private static ProductType v2;
    private static ProductType v3;

    // Nodes of wf2 copy
    private static ProductType v0_copy;
    private static ProductType v1_copy;
    private static ProductType v2_copy;


    @BeforeAll
    public static void setup(){
        wf1 = new WorkflowType();
        wf1.generateRandomDAG(5, 5, 2, 2, 60, PdfType.UNIFORM);
        wf1Reference = wf1;

        wi1 = wf1.makeIstance();

        ListenableDAG<ProductType, CustomEdge> dag2 = new ListenableDAG<>(CustomEdge.class);
        v0 = new ProductType("v0", 1, new UniformTime(0,2));
        v1 = new ProductType("v1", 2, new UniformTime(2,4));
        v2 = new ProductType("v2", 3, new UniformTime(4,6));
        dag2.addVertex(v0);
        dag2.addVertex(v1);
        dag2.addVertex(v2);
        dag2.addEdge(v2, v1);
        dag2.addEdge(v1, v0);
        wf2 = new WorkflowType(dag2);

        ListenableDAG<ProductType, CustomEdge> dag2_copy = new ListenableDAG<>(CustomEdge.class);
        v0_copy = new ProductType("v0", 1, new UniformTime(0,2));
        v1_copy = new ProductType("v1", 2, new UniformTime(2,4));
        v2_copy = new ProductType("v2", 3, new UniformTime(4,6));
        dag2_copy.addVertex(v0_copy);
        dag2_copy.addVertex(v1_copy);
        dag2_copy.addVertex(v2_copy);
        dag2_copy.addEdge(v2_copy, v1_copy);
        dag2_copy.addEdge(v1_copy, v0_copy);
        wf2_copy = new WorkflowType(dag2_copy);

        ListenableDAG<ProductType, CustomEdge> dag3 = new ListenableDAG<>(CustomEdge.class);
        v3 = new ProductType("v3", 4, new UniformTime(6,8));
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
    void testEquals() {
        assertEquals(wf1, wf1Reference);
        assertNotEquals(wf1, wfNull);
        assertNotEquals(wf1, wi1);
        assertEquals(wf2, wf2_copy);
        assertNotEquals(wf2, wf3);
    }

    @Test
    void testGetRootNode(){
        assertNotEquals(wf1.getRootNode(), null);
        assertEquals(wf2.getRootNode(), v0);
        assertEquals(wf2_copy.getRootNode(), v0_copy);
        assertEquals(wf3.getRootNode(), v0);
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
        
    }
    
}
