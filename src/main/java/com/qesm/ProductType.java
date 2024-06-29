package com.qesm;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

public class ProductType extends AbstractProduct {

    private WorkflowType productWorkflow;

    public ProductType(String nameType) {
        super(nameType);
    }
    
    public ProductType(String nameType, ItemGroup itemGroup){
        super(nameType, itemGroup);
    }
    
    public ProductType(String nameType, Integer quantityProduced, StochasticTime pdf){
        super(nameType, quantityProduced, pdf);
    }

    public WorkflowType getProductWorkflow() {
        return returnWithItemGroupCheck(productWorkflow);
    }

    public Integer setProductWorkflow(WorkflowType productWorkflow) {
        Integer returnValue = returnWithItemGroupCheck(0);
        if(returnValue != null){
            this.productWorkflow = productWorkflow;
        }
        return returnValue;
    }
}
