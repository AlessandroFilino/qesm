package main;

import java.util.HashMap;
// import java.util.List;

public interface ProductType {

    public HashMap<ProductType, Integer> getSubGraph();
    public String getNameType();
    public int getQuantityInStock();
    public int getQuantityProduced();

}
