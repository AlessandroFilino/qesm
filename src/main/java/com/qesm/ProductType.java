package com.qesm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class ProductType implements Serializable{
    
    private String nameType;
    private UUID uuid;
    private int quantityProduced;
    private transient StochasticTime pdf;
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
        this.pdf = pdf;
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

    public boolean isProcessedType(){
        if(itemType == ItemType.PROCESSED){
            return true;
        }
        else{
            return false;
        }
    }

    protected <T> T returnWithItemTypeCheck(T valueToReturn){
        if(itemType == ItemType.PROCESSED){
            return valueToReturn;
        }
        else{
            return null;
        }
    }

    // Custom serialization logic for StochasticTime
    private void writeObject(ObjectOutputStream oos) throws IOException {

        oos.defaultWriteObject();

        if(!this.isProcessedType()){
            oos.writeObject(null);
        }
        else{
            Class<? extends StochasticTime> pdfClass = pdf.getClass();
            oos.writeObject(pdfClass);

            if(pdfClass == UniformTime.class){
                oos.writeObject(pdf.getEFT()); 
                oos.writeObject(pdf.getLFT()); 
            }
            else if(pdfClass == ErlangTime.class){
                ErlangTime erlangPdf = (ErlangTime) pdf;
                oos.writeObject(erlangPdf.getK()); 
                oos.writeObject(erlangPdf.getRate()); 
            }
            else if(pdfClass == ExponentialTime.class){
                ExponentialTime exponentialPdf = (ExponentialTime) pdf;
                oos.writeObject(exponentialPdf.getRate()); 
            }
            else if(pdfClass == DeterministicTime.class){
                oos.writeObject(pdf.getEFT());
            }
            else{
                throw new IOException("Serialization error: pdfClass " + pdfClass + " not supported");
            }
        } 
    }

    // Custom deserialization logic for StochasticTime
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

        ois.defaultReadObject();
        
        Class<? extends StochasticTime> pdfClass = (Class<? extends StochasticTime>) ois.readObject();

        if(pdfClass == null){
            // RawMaterial
            return;
        }
        else if(pdfClass == UniformTime.class){
            BigDecimal eft = (BigDecimal) ois.readObject();
            BigDecimal lft = (BigDecimal) ois.readObject();
            pdf = new UniformTime(eft, lft);
        }
        else if(pdfClass == ErlangTime.class){
            int k = (int) ois.readObject();
            Double rate = (Double) ois.readObject();
            pdf = new ErlangTime(k, rate);
        }
        else if(pdfClass == ExponentialTime.class){
            BigDecimal rate = (BigDecimal) ois.readObject();
            pdf = new ExponentialTime(rate);
        }
        else if(pdfClass == DeterministicTime.class){
            BigDecimal value = (BigDecimal) ois.readObject();
            pdf = new DeterministicTime(value);
        }

    }


}
