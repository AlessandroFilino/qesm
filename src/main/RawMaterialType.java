package main;

import java.util.HashMap;
// import java.util.List;

public class RawMaterialType implements ProductType {
    private String rawMaterialName;
    private int quantityInStock;

    protected RawMaterialType(String rawMaterialName, int quantity){
        this.rawMaterialName = rawMaterialName;
        this.quantityInStock = quantity;
    }

    @Override
    public String getNameType() {
        return rawMaterialName;
    }

    @Override
    public int getQuantityInStock() {
        return quantityInStock;
    }

    @Override
    public HashMap<ProductType, Integer> getSubGraph(){
        return new HashMap<>();
    }

    @Override
    public int getQuantityProduced(){
        return -1;
    }
}
