package com.qesm;

import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

// Composite Class

public class ProcessedType implements ProductType{
    private String nameType;
    private UUID uuid;
    private int quantityProduced;
    private StochasticTime pdf;

    protected ProcessedType(String nameType, int quantityProduced, StochasticTime pdf){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.quantityProduced = quantityProduced;
        this.pdf = pdf;
    }

    @Override
    public Integer getQuantityProduced() {
        return quantityProduced;
    }

    @Override
    public Integer setQuantityProduced(int quantityProduced) {
        this.quantityProduced = quantityProduced;
        return 0;
    }

    @Override
    public StochasticTime getPdf() {
        return pdf;
    }
    
    @Override
    public Integer setPdf(StochasticTime pdf) {
        this.pdf = pdf;
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
