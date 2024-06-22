package com.qesm;

import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

public class ProductIstance extends ProductType{

    private WorkflowIstance productWorkflow;
   
    protected ProductIstance(ProductType productType){
        super(new String(productType.getNameType()), productType.getItemType());

        if(productType.getItemType() == ItemType.PROCESSED){
            setQuantityProduced(productType.getQuantityProduced());

            StochasticTime pdfToCopy = productType.getPdf();
            Class<? extends StochasticTime> pdfClass = pdfToCopy.getClass();

            if(pdfClass == UniformTime.class){
                setPdf(new UniformTime(pdfToCopy.getEFT(), pdfToCopy.getLFT())); 
            }
            else if(pdfClass == ErlangTime.class){
                setPdf(new ErlangTime(((ErlangTime)pdfToCopy).getK(), ((ErlangTime)pdfToCopy).getRate()));
            }
            else if(pdfClass == ExponentialTime.class){
                setPdf(new ExponentialTime(((ExponentialTime)pdfToCopy).getRate()));
            }
            else if(pdfClass == DeterministicTime.class){
                setPdf(new DeterministicTime(((DeterministicTime)pdfToCopy).getEFT()));
            }
            else{
                System.err.println("Error copyCostructor: pdfClass " + pdfClass + " not supported");
                setPdf(null);   
            }
        }
        
    }

    public ProductIstance(String nameType, ItemType itemType){
        super(nameType, itemType);
    }
   
    public WorkflowIstance getProductWorkflow() {
        return returnWithItemTypeCheck(productWorkflow);
    }

    public Integer setProductWorkflow(WorkflowIstance productWorkflow) {
        Integer returnValue = returnWithItemTypeCheck(0);
        if(returnValue != null){
            this.productWorkflow = productWorkflow;
        }
        return returnValue;
    }
    
}
