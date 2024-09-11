package com.qesm.workflow;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

public class ProductTemplate extends AbstractProduct {

    public ProductTemplate(String name) {
        super(name);
    }

    public ProductTemplate(String name, ItemGroup itemGroup) {
        super(name, itemGroup);
    }

    public ProductTemplate(String name, Integer quantityProduced, StochasticTime pdf) {
        super(name, quantityProduced, pdf);
    }
}
