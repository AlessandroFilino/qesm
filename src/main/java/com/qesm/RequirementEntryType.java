package com.qesm;

public class RequirementEntryType {

    private ProductType entryType;
    private int quantityRequired;

    public RequirementEntryType(ProductType entryType, int quantityRequired) {
        this.entryType = entryType;
        this.quantityRequired = quantityRequired;
    }

    public ProductType getEntryType() {
        return entryType;
    }

    public int getQuantityRequired() {
        return quantityRequired;
    }

}
