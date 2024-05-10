package com.qesm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

public class STPNBlockCustumEdgeIO implements BasicImportExport<STPNBlock, CustomEdge>{
    final private Function<STPNBlock, String> vertexIdProvider;
    final private Function<STPNBlock, Map<String, Attribute>> vertexAttributeProvider;
    final private Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider;
    final private Supplier<Map<String, Attribute>> graphAttributeProvider;

    final private BiFunction<String, Map<String, Attribute>, STPNBlock> vertexFactoryFunction;
    final private Function<Map<String, Attribute>, CustomEdge> edgeWithAttributesFactory;

    private Integer blockIdCounter = 0;

    public STPNBlockCustumEdgeIO() {

        // Exporter's Providers
        this.vertexIdProvider = v -> { 
            blockIdCounter++;
            return blockIdCounter.toString();
        };

        this.vertexAttributeProvider =  v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("box", AttributeType.STRING));
            map.put("label", new DefaultAttribute<String>(v.getHTMLLabel(null), AttributeType.HTML));

            return map;
        };

        this.edgeAttributeProvider = e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();

            return map;
        };

        this.graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };

        // Importer's Factories
        this.vertexFactoryFunction = (vertexName, attributesMap) -> {

            // TODO define vertexFactoryRules

            return null;
        };

        
        this.edgeWithAttributesFactory = (attributesMap) -> {
            CustomEdge edge = new CustomEdge();

            // TODO define edgeFactoryRules

            return edge;
        };

    }

    @Override
    public Function<STPNBlock, String> getVertexIdProvider() {
        return vertexIdProvider;
    }

    @Override
    public Function<STPNBlock, Map<String, Attribute>> getVertexAttributeProvider() {
        return vertexAttributeProvider;
    }

    @Override
    public Function<CustomEdge, Map<String, Attribute>> getEdgeAttributeProvider() {
        return edgeAttributeProvider;
    }

    @Override
    public Supplier<Map<String, Attribute>> getGraphAttributeProvider() {
        return graphAttributeProvider;
    }

    @Override
    public BiFunction<String, Map<String, Attribute>, STPNBlock> getVertexFactoryFunction() {
        return vertexFactoryFunction;
    }

    @Override
    public Function<Map<String, Attribute>, CustomEdge> getEdgeWithAttributesFactory() {
        return edgeWithAttributesFactory;
    }

    
    
}
