package main;

import java.util.List;

// Composite Class

public class TrasformationType implements ProductType{
    private String trasformationName;
    private int quantity;
    private List<RequirementType> requirements;

    protected TrasformationType(String trasformationName, int quantity, List<RequirementType> requirements){
        this.trasformationName = trasformationName;
        this.quantity = quantity;
        this.requirements = requirements;
    }

    public void addRequirement(RequirementType productType){
        requirements.add(productType);
    }

    public void removeRequirement(RequirementType productType){
        requirements.remove(productType);
    }

    public List<RequirementType> getRequirements() {
        return requirements;
    }

    @Override
    public String getNameType() {
        return trasformationName;
    }

    @Override
    public int getQuantity() {
        return quantity;  
    }


}
