package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class ListenableDAGTest {

    @Test
    void testDuplicateProductName() {
        ListenableDAG<ProductType, CustomEdge> dag = new ListenableDAG<>(CustomEdge.class);

        ProductType v0 = new ProductType("v0");
        ProductType v1 = new ProductType("v0");

        assertTrue(dag.addVertex(v0));
        assertFalse(dag.addVertex(v1));

    }

    @Test
    void testEquals() {
        ListenableDAG<ProductType, CustomEdge> dagProductTypeCustomEdge = new ListenableDAG<>(CustomEdge.class);
        ListenableDAG<ProductType, CustomEdge> dagProductTypeCustomEdgeReference = dagProductTypeCustomEdge;
        ListenableDAG<ProductIstance, CustomEdge> dagNull = null;
        Integer intVar = 5;

        assertNotEquals(dagProductTypeCustomEdge, dagNull);
        assertEquals(dagProductTypeCustomEdge, dagProductTypeCustomEdgeReference);
        assertNotEquals(dagProductTypeCustomEdge, intVar);
    }

    @Test
    void testToString() {
        ListenableDAG<ProductType, CustomEdge> dagProductTypeCustomEdge = new ListenableDAG<>(CustomEdge.class);
        ProductType rm1 = new ProductType("rm1");
        ProductType rm2 = new ProductType("rm2");
        ProductType rm3 = new ProductType("rm3");
        ProductType p1 = new ProductType("p1", 1, new UniformTime(1, 2));
        ProductType p2 = new ProductType("p2", 2, new UniformTime(2, 3));

        dagProductTypeCustomEdge.addVertex(rm1);
        dagProductTypeCustomEdge.addVertex(rm2);
        dagProductTypeCustomEdge.addVertex(rm3);
        dagProductTypeCustomEdge.addVertex(p1);
        dagProductTypeCustomEdge.addVertex(p2);
        dagProductTypeCustomEdge.addEdge(rm1, p1);
        dagProductTypeCustomEdge.addEdge(rm2, p1);
        dagProductTypeCustomEdge.addEdge(p1, p2);
        dagProductTypeCustomEdge.addEdge(rm3, p2);

        dagProductTypeCustomEdge.toString();

    }
}
