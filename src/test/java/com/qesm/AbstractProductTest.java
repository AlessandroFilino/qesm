package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Optional;

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
    private ProductIstance prodIstanceRaw1;

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
        prodIstanceRaw1 = new ProductIstance(prodTypeRaw1);
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
        assertNotEquals(prodTypeRaw1, nullProductType);
        assertNotEquals(prodTypeRaw1, prodTypeRaw2);
        assertNotEquals(prodTypeRaw1, prodIstanceRaw1);
        assertEquals(prodTypeRaw1, prodTypeRaw1);
        assertEquals(prodTypeRaw1, prodTypeRaw1Reference);

        assertNotEquals(prodIstanceRaw1, nullProductType);
    }

    @Test
    void testEqualsAttributes() {
        assertNotEquals(prodTypeRaw1.getName(), prodTypeRaw2.getName());
        assertNotEquals(prodTypeRaw1.getItemGroup(), prodTypeProcessed1.getItemGroup());
        assertNotEquals(prodTypeProcessed1, prodTypeProcessed1DifferentQuantity);

        assertNotEquals(prodTypeProcessedUniform01, prodTypeProcessedUniform15);
        assertNotEquals(prodTypeProcessedUniform15, prodTypeProcessedUniform16);
        assertNotEquals(prodTypeProcessedErlang01, prodTypeProcessedErlang15);
        assertNotEquals(prodTypeProcessedErlang15, prodTypeProcessedErlang16);
        assertNotEquals(prodTypeProcessedExponential1, prodTypeProcessedExponential5);
        assertNotEquals(prodTypeProcessedDeterministic1, prodTypeProcessedDeterministic5);

        assertNotEquals(prodTypeProcessedUniform01, prodTypeProcessedErlang01);
        assertNotEquals(prodTypeProcessedErlang01, prodTypeProcessedExponential1);
        assertNotEquals(prodTypeProcessedExponential1, prodTypeProcessedDeterministic1);
        assertNotEquals(prodTypeProcessedDeterministic1, prodTypeProcessedUniform01);

        assertEquals(prodTypeProcessedUniform01, prodTypeProcessedUniform01Equal);
        assertEquals(prodTypeProcessedErlang01, prodTypeProcessedErlang01Equal);
        assertEquals(prodTypeProcessedExponential1, prodTypeProcessedExponential1Equal);
        assertEquals(prodTypeProcessedDeterministic1, prodTypeProcessedDeterministic1Equal);

        assertNotEquals(prodTypeProcessedUniform01, prodTypeProcessedExpolynomial01);
    }

    @Test
    void testGetWithItemGroupCheck() {
        assertTrue(StochasticTime.class.isAssignableFrom(prodTypeProcessedUniform01.getPdf().getClass()));
        assertEquals(prodTypeRaw1.getPdf(), Optional.empty());
    }
}

// public Optional<StochasticTime> getPdf() {
// return getWithItemGroupCheck(pdf);
// }
// protected <T> Optional<T> getWithItemGroupCheck(T valueToReturn){
// if(itemGroup == ItemGroup.PROCESSED){
// return Optional.of(valueToReturn);
// }
// else{
// return Optional.empty();
// }
// }
