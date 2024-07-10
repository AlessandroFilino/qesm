package com.qesm;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class Renderer {

    public static void renderDotFile(String dotFilePath, String outputFilePath) {

        try {
            // Render DOT file to PNG
            Graphviz.fromFile(new File(dotFilePath))
                    .render(Format.SVG) // Render to PNG format
                    .toFile(new File(outputFilePath)); // Save the rendered graph to a file
            System.out.println("Graph rendered successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void renderAllDotFile(String folderPath, String outputFolderPath) {

        try {
            Path inputFolder = Paths.get(folderPath);
            Path outputFolder = Paths.get(outputFolderPath);

            if(!Files.exists(outputFolder)){
                System.err.println("Output folder path does not exist: " + outputFolderPath);
                return;
            }

            if(!Files.isDirectory(outputFolder)){
                System.err.println("Output folder path is not a directory: " + outputFolderPath);
                return;
            }

            Files.walk(inputFolder, 1)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    
                    String fileNameWithoutExtension = path.getFileName().toString().split("\\.")[0];
                    renderDotFile(path.toString(), outputFolder.toAbsolutePath() + "/" + fileNameWithoutExtension + ".svg");
                    
                });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
