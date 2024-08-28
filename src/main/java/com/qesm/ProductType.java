package com.qesm;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductType extends AbstractProduct {

    public ProductType(String nameType) {
        super(nameType);
    }

    public ProductType(String nameType, ItemGroup itemGroup) {
        super(nameType, itemGroup);
    }

    public ProductType(String nameType, Integer quantityProduced, StochasticTime pdf) {
        super(nameType, quantityProduced, pdf);
    }
}
