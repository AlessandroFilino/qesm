package com.qesm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;

public interface DotFileConverter<T extends DotFileConvertible> {

    private DOTExporter<T, CustomEdge> setExporterProviders(boolean serialization) {
        DOTExporter<T, CustomEdge> exporter = new DOTExporter<>(this.getVertexIdProvider());

        if (serialization) {
            exporter.setVertexAttributeProvider(this.getVertexAttributeProvider());
        } else {
            exporter.setVertexAttributeProvider(this.getVertexAttributeProviderNoSerialization());
        }

        exporter.setEdgeAttributeProvider(this.getEdgeAttributeProvider());
        exporter.setGraphAttributeProvider(this.getGraphAttributeProvider());

        return exporter;
    }

    default public void exportDotFile(String filePath) {

        DOTExporter<T, CustomEdge> exporter = setExporterProviders(true);

        // export graph to dotFile
        try {
            FileWriter writer = new FileWriter(filePath);
            exporter.exportGraph(this.getDagCopy(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default public void exportDotFileNoSerialization(String filePath) {
        // NOTE: (serializing big object (ex workflow) can leads to massive attribute
        // string, dot renderer has a limit of (2^14 chars))
        DOTExporter<T, CustomEdge> exporter = setExporterProviders(false);

        // export graph to dotFile
        try {
            FileWriter writer = new FileWriter(filePath);
            exporter.exportGraph(this.getDagCopy(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default public void importDotFile(String filePath) {
        DOTImporter<T, CustomEdge> importer = new DOTImporter<T, CustomEdge>();
        importer.setVertexWithAttributesFactory(getVertexFactoryFunction());
        importer.setEdgeWithAttributesFactory(getEdgeWithAttributesFactory());

        try {
            FileReader reader = new FileReader(filePath);
            DirectedAcyclicGraph<T, CustomEdge> resultDag = new DirectedAcyclicGraph<T, CustomEdge>(CustomEdge.class);
            importer.importGraph(resultDag, reader);
            this.setDag(resultDag);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DirectedAcyclicGraph<T, CustomEdge> getDagCopy();

    public void setDag(DirectedAcyclicGraph<T, CustomEdge> dagToSet);

    public Class<T> getVertexClass();

    default public Function<CustomEdge, Map<String, Attribute>> getEdgeAttributeProvider() {
        Function<CustomEdge, Map<String, Attribute>> edgeAttributeProvider = e -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();

            map.put("label",
                    new DefaultAttribute<String>("quantityNeeded: " + e.getQuantityRequired(), AttributeType.STRING));

            map.put("quantity_required", new DefaultAttribute<Integer>(e.getQuantityRequired(), AttributeType.INT));

            return map;
        };

        return edgeAttributeProvider;
    }

    default public Supplier<Map<String, Attribute>> getGraphAttributeProvider() {
        Supplier<Map<String, Attribute>> graphAttributeProvider = () -> {
            Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
            map.put("rankdir", new DefaultAttribute<String>("BT", AttributeType.STRING));
            return map;
        };

        return graphAttributeProvider;
    }

    default public Function<T, Map<String, Attribute>> getVertexAttributeProvider() {
        Function<T, Map<String, Attribute>> vertexAttributeProvider = v -> {
            Map<String, Attribute> map = v.getExporterAttributes();
            // Serialize vertex object with compression
            try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(gzipOutputStream)) {
                objectOutputStream.writeObject(v);
                gzipOutputStream.finish();
                String encodedVertex = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
                // split string in chunk to avoid Graphviz limit on quoted string scanning
                // (16384 char)
                Integer chunkSize = 16000;
                Integer chunkNum = 0;
                for (int chunkIdx = 0; chunkIdx < encodedVertex.length(); chunkIdx += chunkSize) {
                    String chunk = encodedVertex.substring(chunkIdx,
                            Math.min(encodedVertex.length(), chunkIdx + chunkSize));
                    map.put("serialization_data_" + chunkNum,
                            new DefaultAttribute<String>(chunk, AttributeType.STRING));
                    chunkNum++;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }

            return map;
        };

        return vertexAttributeProvider;
    }

    default public Function<T, Map<String, Attribute>> getVertexAttributeProviderNoSerialization() {
        Function<T, Map<String, Attribute>> vertexAttributeProvider = v -> {
            return v.getExporterAttributes();
        };

        return vertexAttributeProvider;
    }

    default public Function<T, String> getVertexIdProvider() {
        Function<T, String> vertexIdProvider = v -> v.getExporterId();

        return vertexIdProvider;
    }

    default public BiFunction<String, Map<String, Attribute>, T> getVertexFactoryFunction() {
        BiFunction<String, Map<String, Attribute>, T> vertexFactoryFunction = (vertexName, attributesMap) -> {

            // Deserialize vertex object with decompression
            try {
                // Reassemble string from chunks
                Integer chunkNum = 0;
                String encodedVertex = "";
                while (attributesMap.containsKey("serialization_data_" + chunkNum)) {
                    encodedVertex += attributesMap.get("serialization_data_" + chunkNum).toString();
                    chunkNum++;
                }
                byte[] compressedData = Base64.getDecoder().decode(encodedVertex);

                try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
                        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
                        ObjectInputStream objectInputStream = new ObjectInputStream(gzipInputStream)) {
                    T vertex = castDeserializedObject(objectInputStream.readObject(), this.getVertexClass());
                    return vertex;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
        return vertexFactoryFunction;
    }

    private T castDeserializedObject(Object object, Class<T> classType) {
        if (classType.isInstance(object)) {
            return classType.cast(object);
        } else {
            throw new ClassCastException("Cannot cast object to " + classType.getName());
        }
    }

    default public Function<Map<String, Attribute>, CustomEdge> getEdgeWithAttributesFactory() {
        Function<Map<String, Attribute>, CustomEdge> edgeWithAttributesFactory = (attributesMap) -> {
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
        return edgeWithAttributesFactory;
    }
}
