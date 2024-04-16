package com.qesm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RandomWorkflow {

    private int maxDepth;
    private int branchingFactor;
    private int maxWidth;
    private Random random;
    private HashMap<Integer, Integer> levelWidthCount;
    private Integer processedTypeCount;
    private Integer rawMaterialTypeCount;
    private Integer nodeTypeCount;

    public RandomWorkflow(int maxDepth, int branchingFactor, int maxWidth) {
        this.maxDepth = maxDepth;
        this.branchingFactor = branchingFactor;
        this.maxWidth = maxWidth;
        this.random = new Random();
        this.levelWidthCount = new HashMap<>();
        this.processedTypeCount = 0;
        this.rawMaterialTypeCount = 0;  
        this.nodeTypeCount = 0;
    }

    ProductType generate(int depth) {

        ProductType root;

        if(depth == maxDepth){
            root = new RawMaterialType("r" + rawMaterialTypeCount + "_n" + nodeTypeCount);
            rawMaterialTypeCount++;
            nodeTypeCount++;
            return root;
        }
        
        if(! levelWidthCount.containsKey(depth + 1)){
            levelWidthCount.put(depth + 1, 0);
        }
        
        int currentBelowWidth = levelWidthCount.get(depth + 1);
        int belowWidthLeft = maxWidth - currentBelowWidth;
        int numChildren = 0;

        if(belowWidthLeft - branchingFactor >= 0){
            numChildren = random.nextInt(branchingFactor + 1);
        }
        else{
            numChildren = (belowWidthLeft > 0) ? random.nextInt(belowWidthLeft + 1) : 0;
        }
        
        levelWidthCount.put(depth + 1, currentBelowWidth + numChildren);

        if(numChildren == 0){
            root = new RawMaterialType("r" + rawMaterialTypeCount + "_n" + nodeTypeCount);
            rawMaterialTypeCount++;
            nodeTypeCount++;
        }
        else{
            root = new ProcessedType("p" + processedTypeCount + "_n" + nodeTypeCount, new ArrayList<>(), 1);
            processedTypeCount++;
            nodeTypeCount++;

            for (int i = 0; i < numChildren; i++) {
                ProductType child = generate(depth + 1);
                
                RequirementEntryType req = new RequirementEntryType(child, 2);
                root.addRequirementEntry(req);
            }
        } 

        return root;
    }
}