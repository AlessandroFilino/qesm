package com.qesm;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;

public interface BasicImportExport<V, E> {

    public Function<V, String> getVertexIdProvider();
    public Function<V, Map<String, Attribute>> getVertexAttributeProvider();
    public Function<E, Map<String, Attribute>> getEdgeAttributeProvider();
    public Supplier<Map<String, Attribute>> getGraphAttributeProvider();

    public BiFunction<String, Map<String, Attribute>, V> getVertexFactoryFunction();
    public Function<Map<String, Attribute>, E> getEdgeWithAttributesFactory();

    default public void writeDotFile(String filePath, DirectedAcyclicGraph<V, E> dagToExport) {
        DOTExporter<V, E> exporter = new DOTExporter<>(getVertexIdProvider());
        exporter.setVertexAttributeProvider(getVertexAttributeProvider());
        // exporter.setEdgeIdProvider(edgeIdProvider); Function<E, String>
        // edgeIdProvider
        exporter.setEdgeAttributeProvider(getEdgeAttributeProvider());
        exporter.setGraphAttributeProvider(getGraphAttributeProvider());
        try {
            FileWriter writer = new FileWriter(filePath);
            exporter.exportGraph(dagToExport, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    default public void readDotFile(String filePath, DirectedAcyclicGraph<V, E> resultDag){
        DOTImporter<V, E> importer = new DOTImporter<V, E>();
        importer.setVertexWithAttributesFactory(getVertexFactoryFunction());
        importer.setEdgeWithAttributesFactory(getEdgeWithAttributesFactory());

        try {
            FileReader reader = new FileReader(filePath);
            importer.importGraph(resultDag, reader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
