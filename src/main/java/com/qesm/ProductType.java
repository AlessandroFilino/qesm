package com.qesm;

import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

public class ProductType extends AbstractProduct {

    public ProductType(String nameType) {
        super(nameType);
    }

    public ProductType(String nameType, UUID uuid) {
        super(nameType, uuid);
    }

    public ProductType(String nameType, ItemGroup itemGroup) {
        super(nameType, itemGroup);
    }

    public ProductType(String nameType, Integer quantityProduced, StochasticTime pdf) {
        super(nameType, quantityProduced, pdf);
    }

    public ProductType(String nameType, UUID uuid, Integer quantityProduced, StochasticTime pdf) {
        super(nameType, uuid, quantityProduced, pdf);
    }
}
