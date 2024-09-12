package com.qesm.workflow;

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

import com.qesm.workflow.AbstractProduct.ItemGroup;

public class AbstractProductTest {

    private ProductTemplate prodTemplateRaw1;
    private ProductTemplate prodTemplateRaw2;
    private ProductTemplate prodTemplateProcessed1;
    private ProductTemplate prodTemplateProcessed1DifferentQuantity;
    private ProductTemplate prodTemplateRaw1Reference;
    private ProductTemplate nullProductTemplate;
    private ProductInstance prodInstanceRaw1;

    private UniformTime uniformTime01;
    private UniformTime uniformTime01Equal;
    private UniformTime uniformTime15;
    private UniformTime uniformTime16;
    private ProductTemplate prodTemplateProcessedUniform01;
    private ProductTemplate prodTemplateProcessedUniform01Equal;
    private ProductTemplate prodTemplateProcessedUniform15;
    private ProductTemplate prodTemplateProcessedUniform16;

    private ErlangTime erlangTime01;
    private ErlangTime erlangTime01Equal;
    private ErlangTime erlangTime15;
    private ErlangTime erlangTime16;
    private ProductTemplate prodTemplateProcessedErlang01;
    private ProductTemplate prodTemplateProcessedErlang01Equal;
    private ProductTemplate prodTemplateProcessedErlang15;
    private ProductTemplate prodTemplateProcessedErlang16;

    private ExponentialTime exponentialTime1;
    private ExponentialTime exponentialTime1Equal;
    private ExponentialTime exponentialTime5;
    private ProductTemplate prodTemplateProcessedExponential1;
    private ProductTemplate prodTemplateProcessedExponential1Equal;
    private ProductTemplate prodTemplateProcessedExponential5;

    private DeterministicTime deterministicTime1;
    private DeterministicTime deterministicTime1Equal;
    private DeterministicTime deterministicTime5;
    private ProductTemplate prodTemplateProcessedDeterministic1;
    private ProductTemplate prodTemplateProcessedDeterministic1Equal;
    private ProductTemplate prodTemplateProcessedDeterministic5;

    private ExpolynomialTime expolynomialTime01;
    private ProductTemplate prodTemplateProcessedExpolynomial01;

    @BeforeEach
    public void setup() {
        prodTemplateRaw1 = new ProductTemplate("p1", ItemGroup.RAW_MATERIAL);
        prodTemplateRaw2 = new ProductTemplate("p2", ItemGroup.RAW_MATERIAL);
        prodTemplateProcessed1 = new ProductTemplate("p1", ItemGroup.PROCESSED);
        prodTemplateProcessed1DifferentQuantity = new ProductTemplate("p1", ItemGroup.PROCESSED);
        prodTemplateProcessed1DifferentQuantity.setQuantityProduced(10);
        prodInstanceRaw1 = new ProductInstance(prodTemplateRaw1);
        prodTemplateRaw1Reference = prodTemplateRaw1;

        uniformTime01 = new UniformTime(0, 1);
        uniformTime01Equal = new UniformTime(0, 1);
        uniformTime15 = new UniformTime(1, 5);
        uniformTime16 = new UniformTime(1, 6);
        prodTemplateProcessedUniform01 = new ProductTemplate("p1", 1, uniformTime01);
        prodTemplateProcessedUniform01Equal = new ProductTemplate("p1", 1, uniformTime01Equal);
        prodTemplateProcessedUniform15 = new ProductTemplate("p1", 1, uniformTime15);
        prodTemplateProcessedUniform16 = new ProductTemplate("p1", 1, uniformTime16);

        erlangTime01 = new ErlangTime(0, 1);
        erlangTime01Equal = new ErlangTime(0, 1);
        erlangTime15 = new ErlangTime(1, 5);
        erlangTime16 = new ErlangTime(1, 6);
        prodTemplateProcessedErlang01 = new ProductTemplate("p1", 1, erlangTime01);
        prodTemplateProcessedErlang01Equal = new ProductTemplate("p1", 1, erlangTime01Equal);
        prodTemplateProcessedErlang15 = new ProductTemplate("p1", 1, erlangTime15);
        prodTemplateProcessedErlang16 = new ProductTemplate("p1", 1, erlangTime16);

        exponentialTime1 = new ExponentialTime(BigDecimal.valueOf(1));
        exponentialTime1Equal = new ExponentialTime(BigDecimal.valueOf(1));
        exponentialTime5 = new ExponentialTime(BigDecimal.valueOf(5));
        prodTemplateProcessedExponential1 = new ProductTemplate("p1", 1, exponentialTime1);
        prodTemplateProcessedExponential1Equal = new ProductTemplate("p1", 1, exponentialTime1Equal);
        prodTemplateProcessedExponential5 = new ProductTemplate("p1", 1, exponentialTime5);

        deterministicTime1 = new DeterministicTime(BigDecimal.valueOf(1));
        deterministicTime1Equal = new DeterministicTime(BigDecimal.valueOf(1));
        deterministicTime5 = new DeterministicTime(BigDecimal.valueOf(5));
        prodTemplateProcessedDeterministic1 = new ProductTemplate("p1", 1, deterministicTime1);
        prodTemplateProcessedDeterministic1Equal = new ProductTemplate("p1", 1, deterministicTime1Equal);
        prodTemplateProcessedDeterministic5 = new ProductTemplate("p1", 1, deterministicTime5);

        expolynomialTime01 = new ExpolynomialTime(BigDecimal.valueOf(0), BigDecimal.valueOf(1), "---");
        prodTemplateProcessedExpolynomial01 = new ProductTemplate("p1", 1, expolynomialTime01);

    }

    @Test
    void testEquals() {
        assertFalse(prodTemplateRaw1.equalsAttributes(nullProductTemplate));
        assertFalse(prodTemplateRaw1.equalsAttributes(prodTemplateRaw2));
        assertNotEquals(prodTemplateRaw1, prodInstanceRaw1);
        assertEquals(prodTemplateRaw1, prodTemplateRaw1);
        assertEquals(prodTemplateRaw1, prodTemplateRaw1Reference);
        assertNotEquals(prodInstanceRaw1, nullProductTemplate);
    }

    @Test
    void testEqualsAttributes() {
        assertNotEquals(prodTemplateRaw1.getName(), prodTemplateRaw2.getName());
        assertNotEquals(prodTemplateRaw1.getItemGroup(), prodTemplateProcessed1.getItemGroup());
        assertFalse(prodTemplateProcessed1.equalsAttributes(prodTemplateProcessed1DifferentQuantity));
        assertFalse(prodTemplateProcessedUniform01.equalsAttributes(prodTemplateProcessedUniform15));
        assertFalse(prodTemplateProcessedUniform15.equalsAttributes(prodTemplateProcessedUniform16));
        assertFalse(prodTemplateProcessedErlang01.equalsAttributes(prodTemplateProcessedErlang15));
        assertFalse(prodTemplateProcessedErlang15.equalsAttributes(prodTemplateProcessedErlang16));
        assertFalse(prodTemplateProcessedExponential1.equalsAttributes(prodTemplateProcessedExponential5));
        assertFalse(prodTemplateProcessedDeterministic1.equalsAttributes(prodTemplateProcessedDeterministic5));

        assertFalse(prodTemplateProcessedUniform01.equalsAttributes(prodTemplateProcessedErlang01));
        assertFalse(prodTemplateProcessedErlang01.equalsAttributes(prodTemplateProcessedExponential1));
        assertFalse(prodTemplateProcessedExponential1.equalsAttributes(prodTemplateProcessedDeterministic1));
        assertFalse(prodTemplateProcessedDeterministic1.equalsAttributes(prodTemplateProcessedUniform01));

        assertTrue(prodTemplateProcessedUniform01.equalsAttributes(prodTemplateProcessedUniform01Equal));
        assertTrue(prodTemplateProcessedErlang01.equalsAttributes(prodTemplateProcessedErlang01Equal));
        assertTrue(prodTemplateProcessedExponential1.equalsAttributes(prodTemplateProcessedExponential1Equal));
        assertTrue(prodTemplateProcessedDeterministic1.equalsAttributes(prodTemplateProcessedDeterministic1Equal));

        assertFalse(prodTemplateProcessedUniform01.equalsAttributes(prodTemplateProcessedExpolynomial01));
    }

    @Test
    void testGetWithItemGroupCheck() {
        assertTrue(StochasticTime.class.isAssignableFrom(prodTemplateProcessedUniform01.getPdf().getClass()));
        assertEquals(prodTemplateRaw1.getPdf(), null);
    }
}
