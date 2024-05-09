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

public interface ExporterProvider<V, E> {

    Function<V, String> getVertexIdProvider();
    Function<V, Map<String, Attribute>> getVertexAttributeProvider();
    Function<E, Map<String, Attribute>> getEdgeAttributeProvider();
    Supplier<Map<String, Attribute>> getGraphAttributeProvider();

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

    static public void renderDotFile(String dotFilePath, String outputFilePath, double scale) {
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
