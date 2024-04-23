package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import org.jgrapht.graph.DirectedAcyclicGraph;

public class RandomDAGGenerator{

    // Level definition: hop distance from root. A node can belong to more than one level
    private int maxHeight;                  //Max level number
    private int maxWidth;                   //Max number of vertex in each level
    private int maxBranchingUpFactor;       //Max number of incoming edge for each node
    private int maxBranchingDownFactor;     //Max number of outcoming edge for each node
    
    private Random random;
    private int vId;
    private int maxRandomQuantity;
    private ProductType rootNode;
    private Supplier<ProductType> vSupplierRandom;
    private Supplier<ProductType> vSupplierProcessedType;


    public RandomDAGGenerator(int maxHeight, int maxWidth, int maxBranchingUpFactor, int maxBranchingDownFactor){
        
        this.maxHeight = maxHeight;
        this.maxWidth = maxWidth;
        this.maxBranchingUpFactor = maxBranchingUpFactor;
        this.maxBranchingDownFactor = maxBranchingDownFactor;

        this.random = new Random();
        this.vId = 0;
        this.maxRandomQuantity = 100;

        this.vSupplierRandom = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex;

                if(random.nextBoolean()){
                    vertex = new RawMaterialType("v" + vId);
                    vId++;
                }
                else{
                    vertex = new ProcessedType("v" + vId, null, random.nextInt(maxRandomQuantity) + 1);
                    vId++;
                }
                
                return vertex;
            }
        };

        this.vSupplierProcessedType = new Supplier<ProductType>()
        {
            @Override
            public ProductType get()
            {
                ProductType vertex = new ProcessedType("v" + vId, null, random.nextInt(maxRandomQuantity) + 1);
                vId++;
                return vertex;
            }
        };
    }

    public void generateGraph(DirectedAcyclicGraph<ProductType, CustomEdge> dag){

        dag.setVertexSupplier(vSupplierProcessedType);

        HashMap<ProductType, ArrayList<Integer>> vertexToLevels = new HashMap<ProductType, ArrayList<Integer>>();
        HashMap<Integer, ArrayList<ProductType>> levelToVertices = new HashMap<Integer, ArrayList<ProductType>>();
        ArrayList<ProductType> vTargetList = new ArrayList<ProductType>();
        
        rootNode = dag.addVertex();
        vertexToLevels.put(rootNode, new ArrayList<Integer>(List.of(0)));
        levelToVertices.put(0, new ArrayList<ProductType>(List.of(rootNode)));
        vTargetList.add(rootNode);

        dag.setVertexSupplier(vSupplierRandom);

        
        while(true) {
            
            ProductType sourceVertex = dag.addVertex();
            ArrayList<ProductType> vTargetListCopy = new ArrayList<ProductType>(vTargetList);

            Integer branchingUpFactor = random.nextInt(maxBranchingUpFactor) + 1;

            while (!vTargetListCopy.isEmpty()) {
                ProductType targetVertex = vTargetListCopy.remove(random.nextInt(vTargetListCopy.size()));

                boolean nonValidTarget = false;
                ArrayList<HashMap<Integer, ProductType>> vertecesToRemoveFromLevelToVertices = new ArrayList<HashMap<Integer, ProductType>>();

                // Calculate possible source levels if it will be connected to target
                ArrayList<Integer> sourceLevels = new ArrayList<Integer>();
                for (Integer targeLevel : vertexToLevels.get(targetVertex)) {
                    sourceLevels.add(targeLevel + 1);   
                }

                // check maxHeight
                for (Integer sourceLevel : sourceLevels) {
                    if(sourceLevel > maxHeight){
                        nonValidTarget = true;
                        // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: maxHeight");
                        break;
                    }  
                }
                if(nonValidTarget){
                    continue;
                }

                // check maxWidth
                for (Integer sourceLevel : sourceLevels) {
                    if(!levelToVertices.containsKey(sourceLevel)){
                        levelToVertices.put(sourceLevel, new ArrayList<ProductType>());
                    }
                    
                    if(levelToVertices.get(sourceLevel).size() + 1 > maxWidth){
                        nonValidTarget = true;
                        // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: maxWidth");
                        break;
                    }
                    
                    // System.out.println("sourceLevel: " + sourceLevel + " width: " + levelToVertices.get(sourceLevel).size());
                    // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType());

                    levelToVertices.get(sourceLevel).add(sourceVertex);
                    vertecesToRemoveFromLevelToVertices.add(new HashMap<>(){{put(sourceLevel, sourceVertex);}});
                }
                if(nonValidTarget){

                    for (HashMap<Integer, ProductType> entryToRemove : vertecesToRemoveFromLevelToVertices) {
                        for (Integer levelFromWhichRemove : entryToRemove.keySet()) {
                            if(levelToVertices.containsKey(levelFromWhichRemove)){
                                levelToVertices.get(levelFromWhichRemove).remove(entryToRemove.get(levelFromWhichRemove));
                                // System.out.println("Removed from level: " + levelFromWhichRemove + " " + entryToRemove.get(levelFromWhichRemove).getNameType() + " -> " + targetVertex.getNameType());
                            }
                        }
                    }
                    
                    continue;
                }

                // check maxBranchingDownFactor
                if(dag.inDegreeOf(targetVertex) + 1 > maxBranchingDownFactor){
                    // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: BFDown");

                    for (HashMap<Integer, ProductType> entryToRemove : vertecesToRemoveFromLevelToVertices) {
                        for (Integer levelFromWhichRemove : entryToRemove.keySet()) {
                            if(levelToVertices.containsKey(levelFromWhichRemove)){
                                levelToVertices.get(levelFromWhichRemove).remove(entryToRemove.get(levelFromWhichRemove));
                                // System.out.println("Removed from level: " + levelFromWhichRemove + " " + entryToRemove.get(levelFromWhichRemove).getNameType() + " -> " + targetVertex.getNameType());
                            }
                        }
                    }
                    continue;
                }

                // check maxBranchinUpFactor
                if(dag.outDegreeOf(sourceVertex) + 1 > branchingUpFactor){  
                    // System.out.println(sourceVertex.getNameType() + " -> " + targetVertex.getNameType() + " not valid for: BFUp");

                    for (HashMap<Integer, ProductType> entryToRemove : vertecesToRemoveFromLevelToVertices) {
                        for (Integer levelFromWhichRemove : entryToRemove.keySet()) {
                            if(levelToVertices.containsKey(levelFromWhichRemove)){
                                levelToVertices.get(levelFromWhichRemove).remove(entryToRemove.get(levelFromWhichRemove));
                                // System.out.println("Removed from level: " + levelFromWhichRemove + " " + entryToRemove.get(levelFromWhichRemove).getNameType() + " -> " + targetVertex.getNameType());
                            }
                        }
                    }
                    break;
                }

                dag.addEdge(sourceVertex, targetVertex);
                targetVertex.addRequirementEntry(new RequirementEntryType(sourceVertex, random.nextInt(maxRandomQuantity) + 1));

                // Update vertexToLevels
                if(!vertexToLevels.containsKey(sourceVertex)){
                    vertexToLevels.put(sourceVertex, new ArrayList<Integer>());
                }
                for (Integer sourceLevel : sourceLevels) {
                    vertexToLevels.get(sourceVertex).add(sourceLevel);
                }

            }

            // Update TargetList if vertex is connected to graph
            if(dag.outDegreeOf(sourceVertex) > 0){
                if(sourceVertex.getClass() == ProcessedType.class){
                    vTargetList.add(sourceVertex);
                }
            }
            else{
                // if vertex is not connected, it means that we can't append any more vertex to the DAG   
                dag.removeVertex(sourceVertex);
                break;
            }
        }
    }

    public ProductType getRootNode() {
        return rootNode;
    }

}