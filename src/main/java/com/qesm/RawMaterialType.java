package com.qesm;

import java.util.ArrayList;
import java.util.UUID;

public class RawMaterialType implements ProductType {
    private String nameType;
    private UUID uuid;

    protected RawMaterialType(String nameType){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public String getNameType() {
        return nameType;
    }


    @Override
    public boolean addRequirementEntry(RequirementEntryType req) {
        return false;
    }

    @Override
    public ArrayList<RequirementEntryType> getRequirements(){
        return new ArrayList<>();
    }

    @Override
    public int getQuantityProduced() {
        return 0;
    }
}
