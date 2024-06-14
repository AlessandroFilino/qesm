package com.qesm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;

import com.qesm.ProductType.ItemType;

public class ProductTypeCustomEdgeIO <T extends ProductType> implements BasicImportExport<T, CustomEdge> {
    final private Function<T, String> vertexIdProvider;
    final private Function<T, Map<String, Attribute>> vertexAttributeProvider;
    final private Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider;
    final private Supplier<Map<String, Attribute>> graphAttributeProvider;

    final private BiFunction<String, Map<String, Attribute>, T> vertexFactoryFunction;
    final private Function<Map<String, Attribute>, CustomEdge> edgeWithAttributesFactory;

    ProductTypeCustomEdgeIO(Class<T> classType) {

        // Exporter's Providers
        this.vertexIdProvider = v -> v.getNameType();

        this.vertexAttributeProvider = v -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("shape", new DefaultAttribute<String>("circle", AttributeType.STRING));
            if (v.getItemType() == ItemType.RAW_MATERIAL) {
                map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
                map.put("label", new DefaultAttribute<String>(v.getNameType() + "\nRAW_TYPE", AttributeType.STRING));
            } else {
                map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
                map.put("label",
                        new DefaultAttribute<String>(
                                v.getNameType() + "\nPROCESSED_TYPE" + "\nquantityProduced: " + v.getQuantityProduced(),
                                AttributeType.STRING));
            }
            // Serialize vertex object
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
                    objectOutputStream.writeObject(v);
                    String encodedVertex = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                    map.put("vertex_data", new DefaultAttribute<String>(encodedVertex, AttributeType.STRING));
            }catch (Exception exception) {
                exception.printStackTrace();
            } 

            return map;
        };


        //TODO: add edge serialization (like vertex)
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

        // Importer's Factories
        this.vertexFactoryFunction = (vertexName, attributesMap) -> {

            // Deserialize vertex object
            try {
                String encodedVertex = attributesMap.get("vertex_data").toString();
                
                byte[] data = Base64.getDecoder().decode(encodedVertex);
                ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data));
                T vertex = castDeserializedObject(objectInputStream.readObject(), classType);
                return vertex;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };

        this.edgeWithAttributesFactory = (attributesMap) -> {
            CustomEdge edge = new CustomEdge();

            try {
                Integer quantityRequired = Integer.valueOf((attributesMap.get("quantity_required").getValue()));
                edge.setQuantityRequired(quantityRequired);

            } catch (NullPointerException e) {
                System.err.println("Import error: unable to find quantity_required field");
                return edge;
            }

            return edge;
        };
    }

    private T castDeserializedObject(Object object, Class<T> classType){
        if (classType.isInstance(object)) {
            return classType.cast(object);
        } else {
            throw new ClassCastException("Cannot cast object to " + classType.getName());
        }
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
    public Function<T, Map<String, Attribute>> getVertexAttributeProvider() {
        return vertexAttributeProvider;
    }

    @Override
    public Function<T, String> getVertexIdProvider() {
        return vertexIdProvider;
    }

    @Override
    public BiFunction<String, Map<String, Attribute>, T> getVertexFactoryFunction() {
        return vertexFactoryFunction;
    }

    @Override
    public Function<Map<String, Attribute>, CustomEdge> getEdgeWithAttributesFactory() {
        return edgeWithAttributesFactory;
    }

}
