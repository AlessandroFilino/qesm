package main;

import java.util.HashMap;

public interface ProductType {

    public HashMap<ProductType, Integer> getChildrens();
    public String getNameType();
    public int getQuantityInStock();
    public int getQuantityProduced();

}
