package main;

public class RequirementType {
    private String nameType;
    private int quantity;
    private ProductType productType;

    public RequirementType(String nameType, int quantity, ProductType productType) {
        this.nameType = nameType;
        this.quantity = quantity;
        this.productType = productType;
    }

    public String getNameType() {
        return nameType;
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductType getProductType() {
        return productType;
    }
}
