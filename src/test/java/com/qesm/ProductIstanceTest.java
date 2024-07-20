package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExpolynomialTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.AbstractProduct.ItemGroup;

public class ProductIstanceTest {

    @Test
    void testProductIstanceCreationCoverage(){

        ProductIstance productIstance1 = new ProductIstance(new ProductType("p1", 1, new UniformTime(0,2)));
        assertEquals(productIstance1.getPdf().get().getEFT(), BigDecimal.valueOf(0.0));
        assertEquals(productIstance1.getPdf().get().getLFT(), BigDecimal.valueOf(2.0));

        ProductIstance productIstance2 = new ProductIstance(new ProductType("p2", 1, new DeterministicTime(BigDecimal.valueOf(2))));
        assertEquals(productIstance2.getPdf().get().getEFT(), BigDecimal.valueOf(2));
        assertEquals(productIstance2.getPdf().get().getLFT(), BigDecimal.valueOf(2));

        ProductIstance productIstance3 = new ProductIstance(new ProductType("p3", 1, new ErlangTime(1, 2)));
        assertTrue(productIstance3.getPdf().get() instanceof ErlangTime);
        ErlangTime pdf3 = (ErlangTime) productIstance3.getPdf().get();
        assertEquals(pdf3.getK(), 1);
        assertEquals(pdf3.getRate(), 2.0);

        ProductIstance productIstance4 = new ProductIstance(new ProductType("p4", 1, new ExponentialTime(BigDecimal.valueOf(2))));
        assertTrue(productIstance4.getPdf().get() instanceof ExponentialTime);
        ExponentialTime pdf4 = (ExponentialTime) productIstance4.getPdf().get();
        assertEquals(pdf4.getRate(), BigDecimal.valueOf(2));

        assertThrows(RuntimeException.class, () -> {
            new ProductIstance(new ProductType("p5", 1, new ExpolynomialTime()));
        });
        
        ProductIstance productIstance6 = new ProductIstance(new ProductType("p6"));
        assertEquals(productIstance6.getName(), "p6");
        assertEquals(productIstance6.getItemGroup(), ItemGroup.RAW_MATERIAL);

        ProductIstance productIstance7 = new ProductIstance("p7", ItemGroup.RAW_MATERIAL);
        assertEquals(productIstance7.getName(), "p7");
        assertEquals(productIstance7.getItemGroup(), ItemGroup.RAW_MATERIAL);

    }
}
