package com.qesm;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.extension.ExtendWith;

// @ExtendWith(MyTestWatcher.class)
public class ProductGraphTest {
    private ProductGraph productGraph;
    private static Path tempDir;

    
    public ProductGraph getProductGraph() {
        return productGraph;
    }

    @BeforeAll
    static void setUp() throws IOException {
        String relativePath = "src/test/java/com/qesm/test_files";
        tempDir = Paths.get(System.getProperty("user.dir"), relativePath);
        if(!Files.exists(tempDir)){
            Files.createDirectories(tempDir);
        }
    }

    @AfterAll
    static void tearDown() throws IOException {
        // Clean up the temporary directory after each test
        Files.walk(tempDir)
             .sorted(Comparator.reverseOrder())
             .map(Path::toFile)
             .forEach(File::delete);
    }

    @RepeatedTest(100)
    void testImportExportDagDotLanguage() throws IOException{
        
        productGraph = new ProductGraph();
        Random random = new Random();

        productGraph.generateRandomDAG(random.nextInt(20) + 2, random.nextInt(20) + 2);

        String dagPath = tempDir.toString() + "/test_dag.dot";
        String dagCopyPath = tempDir.toString() + "/test_dag_copy.dot";

        productGraph.exportDAGDotLanguage(dagPath);
        productGraph.importDagDotLanguage(dagPath);
        productGraph.exportDAGDotLanguage(dagCopyPath);

        File dagFile = new File(dagPath);
        File dagCopyFile = new File(dagCopyPath);

        try {
            assertTrue(filesAreEqual(dagFile, dagCopyFile), "Files are not equal");
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }

    }

    private boolean filesAreEqual(File file1, File file2) throws IOException {
        if (file1.length() != file2.length()) {
            return false;
        }

        try (FileInputStream fis1 = new FileInputStream(file1);
             FileInputStream fis2 = new FileInputStream(file2)) {

            byte[] buffer1 = new byte[4096];
            byte[] buffer2 = new byte[4096];

            int bytesRead1;
            int bytesRead2;

            while ((bytesRead1 = fis1.read(buffer1)) != -1) {
                bytesRead2 = fis2.read(buffer2);
                if (bytesRead1 != bytesRead2 || !Arrays.equals(buffer1, buffer2)) {
                    return false;
                }
            }

            return true;
        }
    }

}
