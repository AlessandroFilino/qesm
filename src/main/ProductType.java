package main;

import java.util.List;

public interface ProductType {

    public List<RequirementType> getRequirements();
    public String getNameType();
    public int getQuantity();

}
