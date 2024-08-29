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

public class ProductInstanceTest {

    @Test
    void testProductInstanceCreationCoverage() {

        ProductInstance productInstance1 = new ProductInstance(new ProductTemplate("p1", 1, new UniformTime(0, 2)));
        assertEquals(productInstance1.getPdf().getEFT(), BigDecimal.valueOf(0.0));
        assertEquals(productInstance1.getPdf().getLFT(), BigDecimal.valueOf(2.0));

        ProductInstance productInstance2 = new ProductInstance(
                new ProductTemplate("p2", 1, new DeterministicTime(BigDecimal.valueOf(2))));
        assertEquals(productInstance2.getPdf().getEFT(), BigDecimal.valueOf(2));
        assertEquals(productInstance2.getPdf().getLFT(), BigDecimal.valueOf(2));

        ProductInstance productInstance3 = new ProductInstance(new ProductTemplate("p3", 1, new ErlangTime(1, 2)));
        assertTrue(productInstance3.getPdf() instanceof ErlangTime);
        ErlangTime pdf3 = (ErlangTime) productInstance3.getPdf();
        assertEquals(pdf3.getK(), 1);
        assertEquals(pdf3.getRate(), 2.0);

        ProductInstance productInstance4 = new ProductInstance(
                new ProductTemplate("p4", 1, new ExponentialTime(BigDecimal.valueOf(2))));
        assertTrue(productInstance4.getPdf() instanceof ExponentialTime);
        ExponentialTime pdf4 = (ExponentialTime) productInstance4.getPdf();
        assertEquals(pdf4.getRate(), BigDecimal.valueOf(2));

        assertThrows(RuntimeException.class, () -> {
            new ProductInstance(new ProductTemplate("p5", 1, new ExpolynomialTime()));
        });

        ProductInstance productInstance6 = new ProductInstance(new ProductTemplate("p6"));
        assertEquals(productInstance6.getName(), "p6");
        assertEquals(productInstance6.getItemGroup(), ItemGroup.RAW_MATERIAL);

        ProductInstance productInstance7 = new ProductInstance("p7", ItemGroup.RAW_MATERIAL);
        assertEquals(productInstance7.getName(), "p7");
        assertEquals(productInstance7.getItemGroup(), ItemGroup.RAW_MATERIAL);

        ProductInstance productInstance8 = new ProductInstance("p8");
        assertEquals(productInstance8.getName(), "p8");

        ProductInstance productInstance9 = new ProductInstance("p9", 1, new UniformTime(0, 2));
        assertEquals(productInstance9.getName(), "p9");
        assertEquals(productInstance9.getQuantityProduced(), 1);
        assertEquals(productInstance9.getPdf().getEFT(), BigDecimal.valueOf(0.0));
        assertEquals(productInstance9.getPdf().getLFT(), BigDecimal.valueOf(2.0));
    }
}
