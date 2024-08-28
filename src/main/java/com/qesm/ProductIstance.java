package com.qesm;

import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProductIstance extends AbstractProduct {

    public ProductIstance(ProductType productType) {
        super(new String(productType.getName()), productType.getItemGroup());

        if (productType.isProcessed()) {
            setQuantityProduced(productType.getQuantityProduced());

            StochasticTime pdfToCopy = productType.getPdf();
            Class<? extends StochasticTime> pdfClass = pdfToCopy.getClass();

            if (pdfClass == UniformTime.class) {
                setPdf(new UniformTime(pdfToCopy.getEFT(), pdfToCopy.getLFT()));
            } else if (pdfClass == ErlangTime.class) {
                setPdf(new ErlangTime(((ErlangTime) pdfToCopy).getK(), ((ErlangTime) pdfToCopy).getRate()));
            } else if (pdfClass == ExponentialTime.class) {
                setPdf(new ExponentialTime(((ExponentialTime) pdfToCopy).getRate()));
            } else if (pdfClass == DeterministicTime.class) {
                setPdf(new DeterministicTime(((DeterministicTime) pdfToCopy).getEFT()));
            } else {
                throw new RuntimeException("Error copyCostructor: pdfClass " + pdfClass + " not supported");
            }
        }

    }

    public ProductIstance(String name, ItemGroup itemGroup) {
        super(name, itemGroup);
    }

    public ProductIstance(String name) {
        super(name);
    }

    public ProductIstance(String nameType, Integer quantityProduced, StochasticTime pdf) {
        super(nameType, quantityProduced, pdf);
    }

}
