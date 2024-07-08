package com.qesm;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.AttributeType;
import org.jgrapht.nio.DefaultAttribute;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public abstract class AbstractProduct implements Serializable, DotFileConvertible{
    private String name;
    private UUID uuid;
    private int quantityProduced;
    private transient StochasticTime pdf;
    
    enum ItemGroup{
        RAW_MATERIAL,
        PROCESSED
    }

    private ItemGroup itemGroup;

    public AbstractProduct(String name, ItemGroup itemGroup){
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.itemGroup = itemGroup;
    }

    public AbstractProduct(String name){
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.itemGroup = ItemGroup.RAW_MATERIAL;
    }

    public AbstractProduct(String name, int quantityProduced, StochasticTime pdf){
        this.name = name;
        this.uuid = UUID.randomUUID();
        this.itemGroup = ItemGroup.PROCESSED;
        this.pdf = pdf;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public Optional<Integer> getQuantityProduced() {
        return getWithItemGroupCheck(quantityProduced);
    }

    public Boolean setQuantityProduced(int quantityProduced) {
        if(itemGroup == ItemGroup.PROCESSED){
            this.quantityProduced = quantityProduced;
            return true;
        }
        else{
            return false;
        }
    }

    public Optional<StochasticTime> getPdf() {
        return getWithItemGroupCheck(pdf);
    }
    
    public Boolean setPdf(StochasticTime pdf) {
        if(itemGroup == ItemGroup.PROCESSED){
            this.pdf = pdf;
            return true;
        }
        else{
            return false;
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isProcessed(){
        if(itemGroup == ItemGroup.PROCESSED){
            return true;
        }
        else{
            return false;
        }
    }

    protected <T> Optional<T> getWithItemGroupCheck(T valueToReturn){
        if(itemGroup == ItemGroup.PROCESSED){
            return Optional.of(valueToReturn);
        }
        else{
            return Optional.empty();
        }
    }

    @Override
    public String toString() {
        String productString = name + " " + itemGroup;

        if(this.isProcessed()){
            productString += " quantityProduced: " + quantityProduced + " pdf: " + pdf; 
        }
        return productString;
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
        
        AbstractProduct productToCompare = (AbstractProduct) obj;

        return equalsAttributes(productToCompare);
        
    }

    public <T extends AbstractProduct> boolean equalsAttributes(T productToCompare){
        if(! productToCompare.getName().equals(name) ||
           ! productToCompare.getItemGroup().equals(itemGroup)){
            return false;
        }
        
        if(this.isProcessed()){

            if(! productToCompare.getQuantityProduced().get().equals(quantityProduced)){
                return false;
            }
            else{
                // Custum equals for pdf (StochasticTime doesn't implement it)
                StochasticTime pdfToCompare = productToCompare.getPdf().get();
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
        if (this.isProcessed()) {
            map.put("color", new DefaultAttribute<String>("orange", AttributeType.STRING));
            map.put("label",
                    new DefaultAttribute<String>(name + "\nPROCESSED_TYPE" + "\nquantityProduced: " + quantityProduced,
                            AttributeType.STRING));
        } else {
            map.put("color", new DefaultAttribute<String>("blue", AttributeType.STRING));
            map.put("label", new DefaultAttribute<String>(name + "\nRAW_TYPE", AttributeType.STRING));
        }
        return map;
    }

    @Override
    public String getExporterId() {
        // Id based on name
        return name;
    }

    // Custom serialization logic for StochasticTime
    private void writeObject(ObjectOutputStream oos) throws IOException {

        oos.defaultWriteObject();

        if(!this.isProcessed()){
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
