package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.oristool.eulero.modeling.stochastictime.DeterministicTime;
import org.oristool.eulero.modeling.stochastictime.ErlangTime;
import org.oristool.eulero.modeling.stochastictime.ExpolynomialTime;
import org.oristool.eulero.modeling.stochastictime.ExponentialTime;
import org.oristool.eulero.modeling.stochastictime.StochasticTime;
import org.oristool.eulero.modeling.stochastictime.UniformTime;

import com.qesm.AbstractProduct.ItemGroup;

public class AbstractProductTest {

    private ProductType prodTypeRaw1;
    private ProductType prodTypeRaw2;
    private ProductType prodTypeProcessed1;
    private ProductType prodTypeProcessed1DifferentQuantity;
    private ProductType prodTypeRaw1Reference;
    private ProductType nullProductType;
    private ProductInstance prodInstanceRaw1;

    private UniformTime uniformTime01;
    private UniformTime uniformTime01Equal;
    private UniformTime uniformTime15;
    private UniformTime uniformTime16;
    private ProductType prodTypeProcessedUniform01;
    private ProductType prodTypeProcessedUniform01Equal;
    private ProductType prodTypeProcessedUniform15;
    private ProductType prodTypeProcessedUniform16;

    private ErlangTime erlangTime01;
    private ErlangTime erlangTime01Equal;
    private ErlangTime erlangTime15;
    private ErlangTime erlangTime16;
    private ProductType prodTypeProcessedErlang01;
    private ProductType prodTypeProcessedErlang01Equal;
    private ProductType prodTypeProcessedErlang15;
    private ProductType prodTypeProcessedErlang16;

    private ExponentialTime exponentialTime1;
    private ExponentialTime exponentialTime1Equal;
    private ExponentialTime exponentialTime5;
    private ProductType prodTypeProcessedExponential1;
    private ProductType prodTypeProcessedExponential1Equal;
    private ProductType prodTypeProcessedExponential5;

    private DeterministicTime deterministicTime1;
    private DeterministicTime deterministicTime1Equal;
    private DeterministicTime deterministicTime5;
    private ProductType prodTypeProcessedDeterministic1;
    private ProductType prodTypeProcessedDeterministic1Equal;
    private ProductType prodTypeProcessedDeterministic5;

    private ExpolynomialTime expolynomialTime01;
    private ProductType prodTypeProcessedExpolynomial01;

    @BeforeEach
    public void setup() {
        prodTypeRaw1 = new ProductType("p1", ItemGroup.RAW_MATERIAL);
        prodTypeRaw2 = new ProductType("p2", ItemGroup.RAW_MATERIAL);
        prodTypeProcessed1 = new ProductType("p1", ItemGroup.PROCESSED);
        prodTypeProcessed1DifferentQuantity = new ProductType("p1", ItemGroup.PROCESSED);
        prodTypeProcessed1DifferentQuantity.setQuantityProduced(10);
        prodInstanceRaw1 = new ProductInstance(prodTypeRaw1);
        prodTypeRaw1Reference = prodTypeRaw1;

        uniformTime01 = new UniformTime(0, 1);
        uniformTime01Equal = new UniformTime(0, 1);
        uniformTime15 = new UniformTime(1, 5);
        uniformTime16 = new UniformTime(1, 6);
        prodTypeProcessedUniform01 = new ProductType("p1", 1, uniformTime01);
        prodTypeProcessedUniform01Equal = new ProductType("p1", 1, uniformTime01Equal);
        prodTypeProcessedUniform15 = new ProductType("p1", 1, uniformTime15);
        prodTypeProcessedUniform16 = new ProductType("p1", 1, uniformTime16);

        erlangTime01 = new ErlangTime(0, 1);
        erlangTime01Equal = new ErlangTime(0, 1);
        erlangTime15 = new ErlangTime(1, 5);
        erlangTime16 = new ErlangTime(1, 6);
        prodTypeProcessedErlang01 = new ProductType("p1", 1, erlangTime01);
        prodTypeProcessedErlang01Equal = new ProductType("p1", 1, erlangTime01Equal);
        prodTypeProcessedErlang15 = new ProductType("p1", 1, erlangTime15);
        prodTypeProcessedErlang16 = new ProductType("p1", 1, erlangTime16);

        exponentialTime1 = new ExponentialTime(BigDecimal.valueOf(1));
        exponentialTime1Equal = new ExponentialTime(BigDecimal.valueOf(1));
        exponentialTime5 = new ExponentialTime(BigDecimal.valueOf(5));
        prodTypeProcessedExponential1 = new ProductType("p1", 1, exponentialTime1);
        prodTypeProcessedExponential1Equal = new ProductType("p1", 1, exponentialTime1Equal);
        prodTypeProcessedExponential5 = new ProductType("p1", 1, exponentialTime5);

        deterministicTime1 = new DeterministicTime(BigDecimal.valueOf(1));
        deterministicTime1Equal = new DeterministicTime(BigDecimal.valueOf(1));
        deterministicTime5 = new DeterministicTime(BigDecimal.valueOf(5));
        prodTypeProcessedDeterministic1 = new ProductType("p1", 1, deterministicTime1);
        prodTypeProcessedDeterministic1Equal = new ProductType("p1", 1, deterministicTime1Equal);
        prodTypeProcessedDeterministic5 = new ProductType("p1", 1, deterministicTime5);

        expolynomialTime01 = new ExpolynomialTime(BigDecimal.valueOf(0), BigDecimal.valueOf(1), "---");
        prodTypeProcessedExpolynomial01 = new ProductType("p1", 1, expolynomialTime01);

    }

    @Test
    void testEquals() {
        assertFalse(prodTypeRaw1.equalsAttributes(nullProductType));
        assertFalse(prodTypeRaw1.equalsAttributes(prodTypeRaw2));
        assertNotEquals(prodTypeRaw1, prodInstanceRaw1);
        assertEquals(prodTypeRaw1, prodTypeRaw1);
        assertEquals(prodTypeRaw1, prodTypeRaw1Reference);
        assertNotEquals(prodInstanceRaw1, nullProductType);
    }

    @Test
    void testEqualsAttributes() {
        assertNotEquals(prodTypeRaw1.getName(), prodTypeRaw2.getName());
        assertNotEquals(prodTypeRaw1.getItemGroup(), prodTypeProcessed1.getItemGroup());
        assertFalse(prodTypeProcessed1.equalsAttributes(prodTypeProcessed1DifferentQuantity));
        assertFalse(prodTypeProcessedUniform01.equalsAttributes(prodTypeProcessedUniform15));
        assertFalse(prodTypeProcessedUniform15.equalsAttributes(prodTypeProcessedUniform16));
        assertFalse(prodTypeProcessedErlang01.equalsAttributes(prodTypeProcessedErlang15));
        assertFalse(prodTypeProcessedErlang15.equalsAttributes(prodTypeProcessedErlang16));
        assertFalse(prodTypeProcessedExponential1.equalsAttributes(prodTypeProcessedExponential5));
        assertFalse(prodTypeProcessedDeterministic1.equalsAttributes(prodTypeProcessedDeterministic5));

        assertFalse(prodTypeProcessedUniform01.equalsAttributes(prodTypeProcessedErlang01));
        assertFalse(prodTypeProcessedErlang01.equalsAttributes(prodTypeProcessedExponential1));
        assertFalse(prodTypeProcessedExponential1.equalsAttributes(prodTypeProcessedDeterministic1));
        assertFalse(prodTypeProcessedDeterministic1.equalsAttributes(prodTypeProcessedUniform01));

        assertTrue(prodTypeProcessedUniform01.equalsAttributes(prodTypeProcessedUniform01Equal));
        assertTrue(prodTypeProcessedErlang01.equalsAttributes(prodTypeProcessedErlang01Equal));
        assertTrue(prodTypeProcessedExponential1.equalsAttributes(prodTypeProcessedExponential1Equal));
        assertTrue(prodTypeProcessedDeterministic1.equalsAttributes(prodTypeProcessedDeterministic1Equal));

        assertFalse(prodTypeProcessedUniform01.equalsAttributes(prodTypeProcessedExpolynomial01));
    }

    @Test
    void testGetWithItemGroupCheck() {
        assertTrue(StochasticTime.class.isAssignableFrom(prodTypeProcessedUniform01.getPdf().getClass()));
        assertEquals(prodTypeRaw1.getPdf(), null);
    }
}
