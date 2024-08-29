package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.graph.GraphCycleProhibitedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.RandomDAGGenerator.PdfType;

public class AbstractWorkflowTest {

    private WorkflowType wf1;
    DirectedAcyclicGraph<ProductType, CustomEdge> dag2;
    private WorkflowType wf2;
    private WorkflowType wf2_copy;
    private WorkflowType wf3;
    private WorkflowType wf1Reference;
    private WorkflowType wfNull;
    private WorkflowInstance wi1;

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

        wi1 = wf1.makeInstance();

        dag2 = new DirectedAcyclicGraph<>(CustomEdge.class);
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
    void testDefaultEquals() {
        assertEquals(wf1, wf1Reference);
        assertNotEquals(wf1, wfNull);
        assertNotEquals(wf1, wf2);
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

    @Test
    void computeRootNode() {
        DirectedAcyclicGraph<ProductType, CustomEdge> dag4 = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductType v3 = new ProductType("v3", 1, new UniformTime(0, 2));
        dag4.addVertex(v0);
        dag4.addVertex(v1);
        dag4.addVertex(v3);
        dag4.addEdge(v0, v1);
        dag4.addEdge(v1, v3);
        WorkflowType wf4 = new WorkflowType(dag4);
        System.out.println(wf4.dag.vertexSet());
        assertTrue(wf4.computeRootNode().equalsAttributes(v3));
    }

    @Test
    void testValidation() {
        DirectedAcyclicGraph<ProductType, CustomEdge> dag5 = new DirectedAcyclicGraph<>(CustomEdge.class);
        dag5.addVertex(v0);
        dag5.addVertex(v1);
        dag5.addVertex(v2);
        dag5.addVertex(v3);
        dag5.addEdge(v1, v0);
        dag5.addEdge(v2, v1);
        dag5.addEdge(v1, v3);

        // Root node validation
        assertThrows(WorkflowValidationException.class, () -> new WorkflowType(dag5));
        // Leaf node validation
        dag5.removeEdge(v1, v3);
        dag5.addEdge(v3, v1);
        ProductType v4 = new ProductType("v4");
        dag5.addVertex(v4);
        dag5.addEdge(v1, v4);
        dag5.addEdge(v4, v0);
        assertThrows(WorkflowValidationException.class, () -> new WorkflowType(dag5));
    }

    @Test
    void testAddEdge() {
        // Adding a edge in a correct position
        assertNotNull(wf2.addEdge(v2, v0));
        // Adding an edge that already exists
        assertNull(wf2.addEdge(v2, v1));

        // Extra: Adding an edge that causes a cycle
        assertThrows(GraphCycleProhibitedException.class, () -> wf2.dag.addEdge(v0, v2));
    }

    @Test
    void testConnectVertex() {
        ProductType v4 = new ProductType("v4", 1, new UniformTime(0, 2));
        // Adding a new vertex
        assertNotNull(wf2.connectVertex(v4, v0));
        // Trying to add a vertex that already exists
        assertNull(wf2.connectVertex(v2, v0));

    }

    @Test
    void testRemoveEdge() {
        WorkflowType wf4 = new WorkflowType(dag2);
        // Removing not existing edges
        assertFalse(wf4.removeEdge(v0, v1));
        assertFalse(wf4.removeEdge(new CustomEdge()));
        // Removing edge correctly
        assertTrue(wf4.removeEdge(v1, v0));
        DirectedAcyclicGraph<ProductType, CustomEdge> dagCopy = wf4.cloneDag();
        assertEquals(wf4.toString(), dagCopy.toString());
        assertTrue(dagCopy.vertexSet().size() == 1);
        assertTrue(dagCopy.vertexSet().contains(v0));
        assertTrue(dagCopy.edgeSet().isEmpty());
    }

    @Test
    void testRemoveVertex() {
        WorkflowType wf5 = new WorkflowType(dag2);
        // Removing not existing vertex
        assertFalse(wf5.removeVertex(v3));
        assertFalse(wf5.removeEdge(null));
        // Removing vertex correctly
        assertTrue(wf5.removeVertex(v1));
        DirectedAcyclicGraph<ProductType, CustomEdge> dagCopy = wf5.cloneDag();
        assertEquals(wf5.toString(), dagCopy.toString());
        assertTrue(dagCopy.vertexSet().size() == 1);
        assertTrue(dagCopy.vertexSet().contains(v0));
        assertTrue(dagCopy.edgeSet().isEmpty());
    }

}
