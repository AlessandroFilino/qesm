package com.qesm;

import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class Product implements ProductType{

    private String nameType;
    private UUID uuid;
    private int quantityProduced;
    private StochasticTime pdf;
    private WorkflowType productWorkflow;
    
    enum ItemType{
        RAW_MATERIAL,
        PROCESSED
    }

    ItemType itemType;
    
    protected Product(ProductType productType){
        this.nameType = new String(productType.getNameType());
        this.uuid = UUID.randomUUID();
        this.quantityProduced = productType.getQuantityProduced();
        StochasticTime pdfToCopy = productType.getPdf();
        Class<? extends StochasticTime> pdfClass = pdfToCopy.getClass();
        
        if(pdfClass == UniformTime.class){
            this.pdf = new UniformTime(pdfToCopy.getEFT(), pdfToCopy.getLFT()); 
        }
        else if(pdfClass == ErlangTime.class){
            this.pdf = new ErlangTime(((ErlangTime)pdfToCopy).getK(), ((ErlangTime)pdfToCopy).getRate());
        }
        else if(pdfClass == ExponentialTime.class){
            this.pdf = new ExponentialTime(((ExponentialTime)pdfToCopy).getRate());
        }
        else if(pdfClass == DeterministicTime.class){
            this.pdf = new DeterministicTime(((DeterministicTime)pdfToCopy).getEFT());
        }
        else{
            System.err.println("Error copyCostructor: pdfClass " + pdfClass + " not supported");
            this.pdf = null;   
        }

        if(productType.getClass() == ProcessedType.class){
            this.itemType = ItemType.PROCESSED;
        }
        else if(productType.getClass() == RawMaterialType.class){
            this.itemType = ItemType.RAW_MATERIAL;
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public WorkflowType getProductWorkflow() {
        return returnWithItemTypeCheck(productWorkflow);
    }

    public Integer setProductWorkflow(WorkflowType productWorkflow) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.productWorkflow = productWorkflow;
        }
        return returnValue;
    }

    @Override
    public Integer getQuantityProduced() {
        return returnWithItemTypeCheck(quantityProduced);
    }

    @Override
    public Integer setQuantityProduced(int quantityProduced) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.quantityProduced = quantityProduced;
        }
        return returnValue;
    }

    @Override
    public StochasticTime getPdf() {
        return returnWithItemTypeCheck(pdf);
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

    private <T> T returnWithItemTypeCheck(T valueToReturn){
        if(itemType == ItemType.PROCESSED){
            
            return valueToReturn;
        }
        else{
            return null;
        }
    }
    
}
