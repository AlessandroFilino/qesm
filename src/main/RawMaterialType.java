package main;

import java.util.List;

public class RawMaterialType implements ProductType {
    private String rawMaterialName;
    private int quantity;

    protected RawMaterialType(String rawMaterialName, int quantity){
        this.rawMaterialName = rawMaterialName;
        this.quantity = quantity;
    }

    @Override
    public List<RequirementType> getRequirements() {
        return List.of();
    }

    @Override
    public String getNameType() {
        return rawMaterialName;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }


}
