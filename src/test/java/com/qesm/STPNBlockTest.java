package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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

        STPNBlock andBlock = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
        assertFalse(andBlock.addEnablingToken(null));

        assertNull(andBlock.getSimpleElement());

        assertEquals(andBlock.getComposedElements(), new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
    }

}
