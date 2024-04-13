package main;

import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;


public class Node {
    private ProductType node;
    private HashMap<Node, Integer> childrens;


    public Node(ProductType node) {
        this.node = node;
        this.childrens = new HashMap<Node, Integer>();
    }

    public void generateGraph(){
        if(node.getChildrens().isEmpty()){
            return;
        }

        // System.out.println(rootNode.getNameType());

        // Get direct childrens of current node
        for (Map.Entry<ProductType, Integer> children : node.getChildrens().entrySet()) {
            childrens.put(new Node(children.getKey()), children.getValue());
        }

        // Explore each children node
        for (Map.Entry<Node, Integer> node : childrens.entrySet()) {
            // System.out.println(subGraphTuple.getKey().rootNode.getNameType() + subGraphTuple.getValue());
            node.getKey().generateGraph();
        }
    }

    private void addIndentation(StringBuilder jsonContent, int indentCount){
        for(int indentIdx = 0; indentIdx < indentCount; indentIdx++)
            {
                jsonContent.append("\t");
            }
    }

    private void addContentToJson(StringBuilder jsonContent, int indentCount){

        int subGraphSize = childrens.size();
        int subGraphCounter = 0;

        for (Map.Entry<Node, Integer> subGraphTuple : childrens.entrySet()) {

            addIndentation(jsonContent, indentCount + 1);
            jsonContent.append("{\n");

            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"nameType\": \"" + subGraphTuple.getKey().node.getNameType() + "\",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityInStock\": " + subGraphTuple.getKey().node.getQuantityInStock() + ",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityNeeded\": " + subGraphTuple.getValue() + ",\n");
            addIndentation(jsonContent, indentCount + 2);
            jsonContent.append("\"quantityProduced\": " + subGraphTuple.getKey().node.getQuantityProduced() + ",\n");
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
            jsonContent.append("\"nameType\": \"" + this.node.getNameType() + "\",\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"quantityInStock\": " + this.node.getQuantityInStock() + ",\n");
            addIndentation(jsonContent, 1);
            jsonContent.append("\"quantityProduced\": " + this.node.getQuantityProduced() + ",\n");
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
