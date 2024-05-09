package com.qesm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

public class ProductTypeCustomEdgeProvider implements ExporterProvider<ProductType, CustomEdge> {
    final private Function<ProductType, String> vertexIdProvider;
    final private Function<ProductType, Map<String, Attribute>> vertexAttributeProvider;
    final private Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider;
    final private Supplier<Map<String, Attribute>> graphAttributeProvider;

    ProductTypeCustomEdgeProvider() {
        this.vertexIdProvider = v -> v.getNameType();

        this.vertexAttributeProvider = v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("circle", AttributeType.STRING));
            if (v.getClass().equals(RawMaterialType.class)) {
                map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
                map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nRAW_TYPE", AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("RawMaterialType", AttributeType.STRING));
            } else {
                map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
                map.put("label",
                        new DefaultAttribute<String>(
                                v.getNameType() + "\nPROCESSED_TYPE" + "\nquantityProduced: " + v.getQuantityProduced(),
                                AttributeType.STRING));
                map.put("vertex_type", new DefaultAttribute<String>("ProcessedType", AttributeType.STRING));
                map.put("quantity_produced", new DefaultAttribute<Integer>(v.getQuantityProduced(), AttributeType.INT));
            }
            return map;
        };

        this.edgeAttributeProvider = e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();

            map.put("label",
                    new DefaultAttribute<String>("quantityNeeded: " + e.getQuantityRequired(), AttributeType.STRING));
            map.put("quantity_required", new DefaultAttribute<Integer>(e.getQuantityRequired(), AttributeType.INT));

            return map;
        };

        this.graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };

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
    public Function<ProductType, Map<String, Attribute>> getVertexAttributeProvider() {
        return vertexAttributeProvider;
    }

    @Override
    public Function<ProductType, String> getVertexIdProvider() {
        return vertexIdProvider;
    }
}
