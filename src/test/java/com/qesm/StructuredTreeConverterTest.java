package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.Activity;
import org.oristool.eulero.modeling.ModelFactory;
import org.oristool.eulero.modeling.Simple;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class StructuredTreeConverterTest {
    @Test
    void testConvertToActivity() {
        // Structured Tree structure:
        // v0
        // |
        // v1
        // / \
        // v2 v3

        ProductType v0 = new ProductType("v0", 1, new UniformTime(0, 2));
        ProductType v1 = new ProductType("v1", 2, new UniformTime(2, 4));
        ProductType v2 = new ProductType("v2", 3, new UniformTime(4, 6));
        ProductType v3 = new ProductType("v3", 4, new UniformTime(6, 8));
        ProductType v4 = new ProductType("v4");
        ProductType v5 = new ProductType("v5");
        ProductType v6 = new ProductType("v6");

        DirectedAcyclicGraph<STPNBlock, CustomEdge> structuredWorkflow = new DirectedAcyclicGraph<>(CustomEdge.class);

        STPNBlock simpleBlock0 = new SimpleBlock(v0);
        STPNBlock simpleBlock1 = new SimpleBlock(v1);
        simpleBlock1.addEnablingToken(v4);
        STPNBlock simpleBlock2 = new SimpleBlock(v2);
        simpleBlock2.addEnablingToken(v5);
        STPNBlock simpleBlock3 = new SimpleBlock(v3);
        simpleBlock3.addEnablingToken(v6);

        STPNBlock andBlock1 = new AndBlock(new ArrayList<>(List.of(simpleBlock2, simpleBlock3)));
        STPNBlock seqBlock1 = new SeqBlock(new ArrayList<>(List.of(simpleBlock0, simpleBlock1, andBlock1)));

        structuredWorkflow.addVertex(seqBlock1);

        StructuredTree<ProductType> structuredTree = new StructuredTree<>(null, structuredWorkflow, ProductType.class);

        structuredTree.buildStructuredTree();

        StructuredTreeConverter structuredTreeConverter = new StructuredTreeConverter(
                structuredTree.getStructuredWorkflow());
        Activity resultActivity = structuredTreeConverter.convertToActivity();

        Activity t0 = new Simple("v0", new UniformTime(0, 2));
        Activity t1 = new Simple("v1", new UniformTime(2, 4));

        Activity t2 = new Simple("v2", new UniformTime(4, 6));
        Activity t3 = new Simple("v3", new UniformTime(6, 8));

        Activity joinActivity = ModelFactory.forkJoin(t2, t3);
        Activity seqActivity = ModelFactory.sequence(t0, t1, joinActivity);
        Activity dagActivity = ModelFactory.DAG(seqActivity);

        // Activity doesn't implement equals()
        assertEquals(resultActivity.yaml(), dagActivity.yaml());

    }

}
