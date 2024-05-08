package com.qesm;

import java.util.UUID;

// Composite Class

public class ProcessedType implements ProductType{
    private String nameType;
    private UUID uuid;
    private int quantityProduced;

    protected ProcessedType(String nameType, int quantityProduced){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
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
    public String getNameType() {
        return nameType;
    }

}
