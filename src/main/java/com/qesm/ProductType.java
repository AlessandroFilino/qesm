package com.qesm;

import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

public interface ProductType {
    public String getNameType();
    public UUID getUuid();
    public default Integer getQuantityProduced(){
        return null;
    }

    public default Integer setQuantityProduced(int quantityProduced){
        return null;
    };

    public default StochasticTime getPdf(){
        return null;
    }

    public default Integer setPdf(StochasticTime pdf){
        return null;
    }
}
