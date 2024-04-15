package com.qesm;

import java.util.ArrayList;
import java.util.UUID;

// Composite Class

public class ProcessedType implements ProductType{
    private String nameType;
    private UUID uuid;
    private ArrayList<RequirementEntryType> requirements;
    private int quantityProduced;

    protected ProcessedType(String nameType, ArrayList<RequirementEntryType> requirements, int quantityProduced){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.requirements = requirements;
        this.quantityProduced = quantityProduced;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }
    
    @Override
    public boolean addRequirementEntry(RequirementEntryType req) {
        return requirements.add(req);
    }

    @Override
    public String getNameType() {
        return nameType;
    }

    @Override
    public ArrayList<RequirementEntryType> getRequirements(){
        return requirements;
    }
}
