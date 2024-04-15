package com.qesm;

import java.util.ArrayList;
import java.util.UUID;

public interface ProductType {

    public ArrayList<RequirementEntryType> getRequirements();
    public boolean addRequirementEntry(RequirementEntryType req);
    public String getNameType();
    public UUID getUuid();
}
