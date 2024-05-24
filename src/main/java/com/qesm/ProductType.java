package com.qesm;

import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.StochasticTime;

public class ProductType {

    private String nameType;
    private UUID uuid;
    private int quantityProduced;
    private StochasticTime pdf;
    private Workflow productWorkflow;
    
    enum ItemType{
        RAW_MATERIAL,
        PROCESSED
    }

    private ItemType itemType;

    protected ProductType(String nameType, ItemType itemType){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = itemType;
    }

    protected ProductType(String nameType){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = ItemType.RAW_MATERIAL;
    }

    protected ProductType(String nameType, int quantityProduced, StochasticTime pdf){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = ItemType.PROCESSED;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public Workflow getProductWorkflow() {
        return returnWithItemTypeCheck(productWorkflow);
    }

    public Integer setProductWorkflow(Workflow productWorkflow) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.productWorkflow = productWorkflow;
        }
        return returnValue;
    }

    public Integer getQuantityProduced() {
        return returnWithItemTypeCheck(quantityProduced);
    }

    public Integer setQuantityProduced(int quantityProduced) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.quantityProduced = quantityProduced;
        }
        return returnValue;
    }

    public StochasticTime getPdf() {
        return returnWithItemTypeCheck(pdf);
    }
    
    public Integer setPdf(StochasticTime pdf) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.pdf = pdf;
        }
        return returnValue;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getNameType() {
        return nameType;
    }

    protected <T> T returnWithItemTypeCheck(T valueToReturn){
        if(itemType == ItemType.PROCESSED){
            return valueToReturn;
        }
        else{
            return null;
        }
    }
}
