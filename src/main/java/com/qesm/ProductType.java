package com.qesm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class ProductType implements Serializable, DotFileConvertible{
    
    private String nameType;
    private UUID uuid;
    private int quantityProduced;
    private transient StochasticTime pdf;
    
    enum ItemType{
        RAW_MATERIAL,
        PROCESSED
    }

    private ItemType itemType;

    public ProductType(String nameType, ItemType itemType){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = itemType;
    }

    public ProductType(String nameType){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = ItemType.RAW_MATERIAL;
    }

    public ProductType(String nameType, int quantityProduced, StochasticTime pdf){
        this.nameType = nameType;
        this.uuid = UUID.randomUUID();
        this.itemType = ItemType.PROCESSED;
        this.pdf = pdf;
    }

    public ItemType getItemType() {
        return itemType;
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

    @Override
    public String toString() {
        String productTypeString = nameType + " " + itemType;

        if(this.isProcessedType()){
            productTypeString += " quantityProduced: " + quantityProduced + " pdf: " + pdf; 
        }
        return productTypeString;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null){
            return false;
        }
        if (getClass() != obj.getClass()){
            return false;
        }
        
        ProductType productTypeToCompare = (ProductType) obj;

        return equalsAttributes(productTypeToCompare);
        
    }

    public <T extends ProductType> boolean equalsAttributes(T productToCompare){
        if(! productToCompare.getNameType().equals(nameType) ||
           ! productToCompare.getItemType().equals(itemType)){
            return false;
        }
        
        if(this.isProcessedType()){

            if(! productToCompare.getQuantityProduced().equals(quantityProduced)){
                return false;
            }
            else{
                // Custum equals for pdf (StochasticTime doesn't implement it)
                StochasticTime pdfToCompare = productToCompare.getPdf();
                if(! pdfToCompare.getClass().isInstance(pdf)){
                    return false;
                }
                else{

                    if(pdfToCompare.getClass() == UniformTime.class){
                        if(! pdfToCompare.getEFT().equals(pdf.getEFT()) ||
                           ! pdfToCompare.getLFT().equals(pdf.getLFT())){
                            return false;
                        } 
                    }
                    else if(pdfToCompare.getClass() == ErlangTime.class){
                        ErlangTime erlangPdfToCompare = (ErlangTime) pdfToCompare;
                        ErlangTime erlangPdf = (ErlangTime) pdf;
                        
                        if(erlangPdfToCompare.getK() != (erlangPdf.getK()) ||
                           erlangPdfToCompare.getRate() != (erlangPdf.getRate())){
                            return false;
                        } 
                    }
                    else if(pdfToCompare.getClass() == ExponentialTime.class){
                        ExponentialTime exponentialPdfToCompare = (ExponentialTime) pdfToCompare;
                        ExponentialTime exponentialPdf = (ExponentialTime) pdf;
                        
                        if( ! exponentialPdfToCompare.getRate().equals(exponentialPdf.getRate())){
                            return false;
                        } 
                    }
                    else if(pdfToCompare.getClass() == DeterministicTime.class){
                        if(! pdfToCompare.getEFT().equals(pdf.getEFT()) ||
                           ! pdfToCompare.getLFT().equals(pdf.getLFT())){
                            return false;
                        } 
                    }
                    else{
                        return false;
                    }
                }
            }
        }
        
        return true;
    } 

    @Override
    public Map<String, Attribute> getExporterAttributes() {
        Map<String, Attribute> map = new LinkedHashMap<String, Attribute>();
        map.put("shape", new DefaultAttribute<String>("circle", AttributeType.STRING));
        if (this.isProcessedType()) {
            map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
            map.put("label",
                    new DefaultAttribute<String>(nameType + "\nPROCESSED_TYPE" + "\nquantityProduced: " + quantityProduced,
                            AttributeType.STRING));
        } else {
            map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
            map.put("label", new DefaultAttribute<String>(nameType + "\nRAW_TYPE", AttributeType.STRING));
        }
        return map;
    }

    @Override
    public String getExporterId() {
        // Id based on name
        return nameType;
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
        
        Class<?> pdfClass = (Class<?>) ois.readObject();

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
