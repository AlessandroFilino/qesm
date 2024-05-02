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
        if(requirements == null){
            this.requirements = new ArrayList<RequirementEntryType>();
        }
        else{
            this.requirements = requirements;
        }
        this.quantityProduced = quantityProduced;
    }

    @Override
    public int getQuantityProduced() {
        return quantityProduced;
    }

    @Override
    public int setQuantityProduced(int quantityProduced) {
        this.quantityProduced = quantityProduced;
        return 0;
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
