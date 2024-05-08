package com.qesm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jgrapht.graph.DirectedAcyclicGraph;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.dot.DOTExporter;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public interface Exporter<V, E>{

    // Graph export in DOT language
    default public void exportDotFile(String filePath, DirectedAcyclicGraph<V, E> dagToExport){
        DOTExporter<V, E> exporter = new DOTExporter<>(defineExporterVertexIdProvider());
        exporter.setEdgeIdProvider(defineExporterEdgeIdProvider(dagToExport));
        exporter.setVertexAttributeProvider(defineExporterVertexAttributeProvider());
        exporter.setEdgeAttributeProvider(defineExporterEdgeAttributeProvider());
        exporter.setGraphAttributeProvider(defineExporterGraphAttributeProvider());

        try {
            FileWriter writer = new FileWriter(filePath);
            exporter.exportGraph(dagToExport, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Function<V, String> defineExporterVertexIdProvider();
    public Function<E, String> defineExporterEdgeIdProvider(DirectedAcyclicGraph<V, E> dag);
    public Function<V, Map<String, Attribute>> defineExporterVertexAttributeProvider();
    public Function<E, Map<String, Attribute>> defineExporterEdgeAttributeProvider();
    public Supplier<Map<String, Attribute>> defineExporterGraphAttributeProvider();

    default public void renderDotFile(String dotFilePath, String outputFilePath, double scale){
        try {
            // Render DOT file to PNG
            Graphviz.fromFile(new File(dotFilePath))
                    .scale(scale)
                    .render(Format.PNG) // Render to PNG format
                    .toFile(new File(outputFilePath)); // Save the rendered graph to a file
            System.out.println("Graph rendered successfully.");
        } catch (IOException e) {
            System.err.println("Error rendering graph: " + e.getMessage());
        }
    }

}
