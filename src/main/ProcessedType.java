package main;

import java.util.HashMap;

// Composite Class

public class ProcessedType implements ProductType{
    private String nameType;
    private int quantityInStock;
    private TransformationType transformation;

    protected ProcessedType(String nameType, int quantity, TransformationType transformation){
        this.nameType = nameType;
        this.quantityInStock = quantity;
        this.transformation = transformation;
    }

    public TransformationType getTransformation() {
        return transformation;
    }

    @Override
    public String getNameType() {
        return nameType;
    }

    @Override
    public int getQuantityInStock() {
        return quantityInStock;  
    }

    @Override
    public HashMap<ProductType, Integer> getChildrens(){
        return this.transformation.getProductTypes();
    }

    @Override
    public int getQuantityProduced(){
        return transformation.getQuantityProduced();
    }
}
