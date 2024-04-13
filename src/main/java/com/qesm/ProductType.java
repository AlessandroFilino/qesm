package com.qesm;

import java.util.ArrayList;

public interface ProductType {

    public ArrayList<RequirementEntryType> getRequirements();
    public boolean addRequirementEntry(RequirementEntryType req);
    public String getNameType();
}
