package main;

// import java.util.List;
// import java.util.ArrayList;
import java.util.HashMap;

public class TransformationType {
    private String nameType;
    private int quantityProduced;
    private HashMap<ProductType, Integer> requirements;     // map of requirements and related quantities needed for the transformation

    public TransformationType(String nameType, int quantityProduced, HashMap<ProductType, Integer> requirements) {
        this.nameType = nameType;
        this.quantityProduced = quantityProduced;
        this.requirements = requirements;
    }

    public String getNameType() {
        return nameType;
    }

    public int getQuantityProduced() {
        return quantityProduced;
    }

    public HashMap<ProductType, Integer> getRequirements() {
        return requirements;
    }

    public HashMap<ProductType, Integer> getProductTypes(){
        return requirements;
    }

    public void addRequirement(ProductType productType, int quantityNeeded){
        requirements.put(productType, quantityNeeded);
    }

    public void removeRequirement(ProductType productType){
        requirements.remove(productType);
    }

}
