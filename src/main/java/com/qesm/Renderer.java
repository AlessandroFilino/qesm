package com.qesm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.parse.Parser;

public class Renderer {

    public static void renderDotFile(String dotFilePath, String outputFilePath) {

        try {
            // Render DOT file to PNG
            Graphviz.fromFile(new File(dotFilePath))
                    .render(Format.SVG) // Render to PNG format
                    .toFile(new File(outputFilePath)); // Save the rendered graph to a file
            System.out.println("Graph rendered successfully.");
        } catch (IOException e) {
            throw new RuntimeException("Error while rendering DOT file");
        }

    }

    public static ByteArrayOutputStream renderDotFile(String dotSource) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Graphviz.fromGraph(new Parser().read(dotSource))
                    .render(Format.SVG)
                    .toOutputStream(outputStream);
            return outputStream;
        } catch (IOException e) {
            throw new RuntimeException("Error while rendering DOT file");
        }
    }

    public static void renderAllDotFile(String folderPath, String outputFolderPath) {

        try {
            Path inputFolder = Paths.get(folderPath);
            Path outputFolder = Paths.get(outputFolderPath);

            if (!Files.exists(outputFolder)) {
                throw new RuntimeException("Output folder path does not exist: " + outputFolderPath);
            }

            if (!Files.isDirectory(outputFolder)) {
                throw new RuntimeException("Output folder path is not a directory: " + outputFolderPath);
            }

            Files.walk(inputFolder, 1)
                    .filter(Files::isRegularFile)
                    .forEach(path -> {

                        String fileNameWithoutExtension = path.getFileName().toString().split("\\.")[0];
                        renderDotFile(path.toString(),
                                outputFolder.toAbsolutePath() + "/" + fileNameWithoutExtension + ".svg");

                    });
        } catch (IOException e) {
            throw new RuntimeException("Generic error while rendering DOT file");
        }

    }
}
