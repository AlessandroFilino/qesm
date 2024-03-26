package main;

// import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
// import java.util.List;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;


public class Graph {
    private ProductType rootNode;
    private HashMap<Graph, Integer> subGraphs;


    public Graph(ProductType rootNode) {
        this.rootNode = rootNode;
        this.subGraphs = new HashMap<Graph, Integer>();
    }

    public void generateGraph(){
        if(rootNode.getSubGraph().isEmpty()){
            return;
        }

        // System.out.println(rootNode.getNameType());

        for (Map.Entry<ProductType, Integer> nodeTuple :rootNode.getSubGraph().entrySet()) {
            subGraphs.put(new Graph(nodeTuple.getKey()), nodeTuple.getValue());
        }

        for (Map.Entry<Graph, Integer> subGraphTuple :subGraphs.entrySet()) {
            // System.out.println(subGraphTuple.getKey().rootNode.getNameType() + subGraphTuple.getValue());
            subGraphTuple.getKey().generateGraph();
        }
    }

    private void addIndentation(StringBuilder jsonContent, int indentCount){
        for(int indentIdx = 0; indentIdx < indentCount; indentIdx++)
            {
                jsonContent.append("\t");
            }
    }

    private void addContentToJson(StringBuilder jsonContent, int indentCount){

        int subGraphSize = subGraphs.size();
        int subGraphCounter = 0;

        for (Map.Entry<Graph, Integer> subGraphTuple :subGraphs.entrySet()) {

            addIndentation(jsonContent, indentCount + 1);
            jsonContent.append("{\n");

            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"nameType\": \"" + subGraphTuple.getKey().rootNode.getNameType() + "\",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityInStock\": " + subGraphTuple.getKey().rootNode.getQuantityInStock() + ",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityNeeded\": " + subGraphTuple.getValue() + ",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityProduced\": " + subGraphTuple.getKey().rootNode.getQuantityProduced() + ",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"subGraph\":\n");
            addIndentation(jsonContent, indentCount + 3);
            jsonContent.append("[\n");
            
            // Append subgraph info recursively
            subGraphTuple.getKey().addContentToJson(jsonContent, indentCount + 3);

            addIndentation(jsonContent, indentCount + 3);
            jsonContent.append("]\n");
            addIndentation(jsonContent, indentCount + 1);

            if(subGraphCounter == subGraphSize - 1){
                jsonContent.append("}\n");
            }
            else{
                jsonContent.append("},\n");
            }
            subGraphCounter++;
        }
    } 

    public void serializeGraphToJson(String  jsonPath){
        try {
            FileWriter fileWriter = new FileWriter(jsonPath);
            
            StringBuilder jsonContent = new StringBuilder();
            jsonContent.append("{\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"nameType\": \"" + this.rootNode.getNameType() + "\",\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"quantityInStock\": " + this.rootNode.getQuantityInStock() + ",\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"quantityProduced\": " + this.rootNode.getQuantityProduced() + ",\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"subGraph\":\n");
            addIndentation(jsonContent, 2);
            jsonContent.append("[\n");
            addContentToJson(jsonContent, 2);
            addIndentation(jsonContent, 2);
            jsonContent.append("]\n}");

            fileWriter.write(jsonContent.toString());

            fileWriter.close();

            System.out.println("JSON succesfully created.");
        } catch (IOException e) {
            System.out.println("Error during the creation of JSON file.");
            e.printStackTrace();
        }
    }
}
