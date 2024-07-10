package com.qesm;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ListenableDAGTest {


    @Test
    void testDuplicateProductName(){
        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);

        ProductType v0 = new ProductType("v0");
        ProductType v1 = new ProductType("v0");

        assertTrue(dag.addVertex(v0));
        assertFalse(dag.addVertex(v1));

    }
}
