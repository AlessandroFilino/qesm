package com.qesm.workflow;

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

import com.qesm.workflow.RandomDAGGenerator.PdfType;

public class AbstractWorkflowTest {

    private WorkflowTemplate wf1;
    DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag2;
    private WorkflowTemplate wf2;
    private WorkflowTemplate wf2_copy;
    private WorkflowTemplate wf3;
    private WorkflowTemplate wf1Reference;
    private WorkflowTemplate wfNull;
    private WorkflowInstance wi1;

    // Nodes of wf2 and wf3
    private ProductTemplate v0;
    private ProductTemplate v1;
    private ProductTemplate v2;
    private ProductTemplate v3;

    // Nodes of wf2 copy
    private ProductTemplate v0_copy;
    private ProductTemplate v1_copy;
    private ProductTemplate v2_copy;

    @BeforeEach
    public void setup() {
        wf1 = new WorkflowTemplate();
        wf1.generateRandomDAG(3, 3, 2, 2, 60, PdfType.UNIFORM);
        wf1Reference = wf1;

        wi1 = wf1.makeInstance();

        dag2 = new DirectedAcyclicGraph<>(CustomEdge.class);
        v0 = new ProductTemplate("v0", 1, new UniformTime(0, 2));
        v1 = new ProductTemplate("v1", 2, new UniformTime(2, 4));
        v2 = new ProductTemplate("v2", 3, new UniformTime(4, 6));
        dag2.addVertex(v0);
        dag2.addVertex(v1);
        dag2.addVertex(v2);
        dag2.addEdge(v2, v1);
        dag2.addEdge(v1, v0);
        wf2 = new WorkflowTemplate(dag2);

        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag2_copy = new DirectedAcyclicGraph<>(CustomEdge.class);
        v0_copy = new ProductTemplate("v0", 1, new UniformTime(0, 2));
        v1_copy = new ProductTemplate("v1", 2, new UniformTime(2, 4));
        v2_copy = new ProductTemplate("v2", 3, new UniformTime(4, 6));
        dag2_copy.addVertex(v0_copy);
        dag2_copy.addVertex(v1_copy);
        dag2_copy.addVertex(v2_copy);
        dag2_copy.addEdge(v2_copy, v1_copy);
        dag2_copy.addEdge(v1_copy, v0_copy);
        wf2_copy = new WorkflowTemplate(dag2_copy);

        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag3 = new DirectedAcyclicGraph<>(CustomEdge.class);
        v3 = new ProductTemplate("v3", 4, new UniformTime(6, 8));
        dag3.addVertex(v0);
        dag3.addVertex(v1);
        dag3.addVertex(v2);
        dag3.addVertex(v3);
        dag3.addEdge(v3, v2);
        dag3.addEdge(v2, v1);
        dag3.addEdge(v1, v0);
        wf3 = new WorkflowTemplate(dag3);

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
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag4 = new DirectedAcyclicGraph<>(CustomEdge.class);
        ProductTemplate v3 = new ProductTemplate("v3", 1, new UniformTime(0, 2));
        dag4.addVertex(v0);
        dag4.addVertex(v1);
        dag4.addVertex(v3);
        dag4.addEdge(v0, v1);
        dag4.addEdge(v1, v3);
        WorkflowTemplate wf4 = new WorkflowTemplate(dag4);
        // System.out.println(wf4.cloneDag().vertexSet());
        assertTrue(wf4.computeRootNode().equalsAttributes(v3));
    }

    @Test
    void testValidation() {
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dag5 = new DirectedAcyclicGraph<>(CustomEdge.class);
        dag5.addVertex(v0);
        dag5.addVertex(v1);
        dag5.addVertex(v2);
        dag5.addVertex(v3);
        dag5.addEdge(v1, v0);
        dag5.addEdge(v2, v1);
        dag5.addEdge(v1, v3);

        // Root node validation
        assertThrows(WorkflowValidationException.class, () -> new WorkflowTemplate(dag5));
        // Leaf node validation
        dag5.removeEdge(v1, v3);
        dag5.addEdge(v3, v1);
        ProductTemplate v4 = new ProductTemplate("v4");
        dag5.addVertex(v4);
        dag5.addEdge(v1, v4);
        dag5.addEdge(v4, v0);
        assertThrows(WorkflowValidationException.class, () -> new WorkflowTemplate(dag5));
    }

    @Test
    void testAddEdge() {
        // Adding a edge in a correct position
        assertNotNull(wf2.addEdge(v2, v0));
        // Adding an edge that already exists
        assertNull(wf2.addEdge(v2, v1));

        // Extra: Adding an edge that causes a cycle
        assertThrows(GraphCycleProhibitedException.class, () -> wf2.cloneDag().addEdge(v0, v2));
    }

    @Test
    void testConnectVertex() {
        ProductTemplate v4 = new ProductTemplate("v4", 1, new UniformTime(0, 2));
        // Adding a new vertex
        assertNotNull(wf2.connectVertex(v4, v0));
        // Trying to add a vertex that already exists
        assertNull(wf2.connectVertex(v2, v0));

    }

    @Test
    void testRemoveEdge() {
        WorkflowTemplate wf4 = new WorkflowTemplate(dag2);
        // Removing not existing edges
        assertFalse(wf4.removeEdge(v0, v1));
        assertFalse(wf4.removeEdge(new CustomEdge()));
        // Removing edge correctly
        assertTrue(wf4.removeEdge(v1, v0));
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dagCopy = wf4.cloneDag();
        assertEquals(wf4.toString(), dagCopy.toString());
        assertTrue(dagCopy.vertexSet().size() == 1);
        assertTrue(dagCopy.vertexSet().contains(v0));
        assertTrue(dagCopy.edgeSet().isEmpty());
    }

    @Test
    void testRemoveVertex() {
        WorkflowTemplate wf5 = new WorkflowTemplate(dag2);
        // Removing not existing vertex
        assertFalse(wf5.removeVertex(v3));
        assertFalse(wf5.removeEdge(null));
        // Removing vertex correctly
        assertTrue(wf5.removeVertex(v1));
        DirectedAcyclicGraph<ProductTemplate, CustomEdge> dagCopy = wf5.cloneDag();
        assertEquals(wf5.toString(), dagCopy.toString());
        assertTrue(dagCopy.vertexSet().size() == 1);
        assertTrue(dagCopy.vertexSet().contains(v0));
        assertTrue(dagCopy.edgeSet().isEmpty());
    }

}
