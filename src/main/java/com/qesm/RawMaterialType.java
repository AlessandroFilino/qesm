package com.qesm;

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
    
}
