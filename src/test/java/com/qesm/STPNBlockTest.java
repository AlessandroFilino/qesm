package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class STPNBlockTest {


    @Test
    void testSimpleBlock(){ 
        assertThrows(RuntimeException.class, () -> {
            new SimpleBlock(new ProductType("p0"));
        });

        STPNBlock simpleBlock = new SimpleBlock(new ProductType("p0", 4, null));

        simpleBlock.getBlockInfo(0);
    }

    @Test
    void testAndBlock(){
        assertThrows(RuntimeException.class, () -> {
            new AndBlock(null);
        });

        STPNBlock simpleBlock0 = new SimpleBlock(new ProductType("p0", 4, null));
        STPNBlock simpleBlock1 = new SimpleBlock(new ProductType("p1", 3, null));
        STPNBlock andBlock1 = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock0, simpleBlock1)));
        STPNBlock andBlock1Equals = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock0, simpleBlock1)));
        
        assertFalse(andBlock1.addEnablingToken(null));
        assertNull(andBlock1.getSimpleElement());
        assertEquals(andBlock1.getComposedElements(), new ArrayList<STPNBlock>(List.of(simpleBlock0, simpleBlock1)));
        assertEquals(andBlock1.getBlockInfo(0), "BlockType: AndBlock  p0 tokens:   p1 tokens: ");

        STPNBlock simpleBlock2 = new SimpleBlock(new ProductType("p2", 4, null));
        STPNBlock simpleBlock3 = new SimpleBlock(new ProductType("p3", 4, null));
        STPNBlock andBlock2 = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock2, simpleBlock3)));

        assertEquals(andBlock1, andBlock1Equals);
        assertEquals(andBlock1, andBlock1);
        assertNotEquals(andBlock1, simpleBlock0);
        assertNotEquals(andBlock1, null);
        assertNotEquals(andBlock1, andBlock2);

        
    }

}
