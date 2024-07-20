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

        STPNBlock andBlock = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
        assertFalse(andBlock.addEnablingToken(null));

        assertNull(andBlock.getSimpleElement());

        assertEquals(andBlock.getComposedElements(), new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
    }

    @Test
    void testSeqToString() {
        STPNBlock simpleBlock = new SimpleBlock(new ProductType("p0", 4, null));
        STPNBlock simpleBlock1 = new SimpleBlock(new ProductType("p1", 4, null));
        STPNBlock simpleBlock2 = new SimpleBlock(new ProductType("p2", 4, null));
        STPNBlock seqBlock = new SeqBlock(new ArrayList<STPNBlock>(List.of(simpleBlock, simpleBlock1, simpleBlock2)));

        String expectedString = "SeqBlock: [[p0, p1, p2]]";

        assertEquals(seqBlock.toString(), expectedString);
    }

    @Test
    void testSeqEquals(){
        STPNBlock simpleBlock0 = new SimpleBlock(new ProductType("p0", 4, null));
        STPNBlock simpleBlock1 = new SimpleBlock(new ProductType("p1", 4, null));
        
        STPNBlock seqBlock0 = new SeqBlock(new ArrayList<STPNBlock>(List.of(simpleBlock0, simpleBlock1)));
        STPNBlock seqBlock_0 = new SeqBlock(new ArrayList<STPNBlock>(List.of(simpleBlock0, simpleBlock1)));

        assertEquals(seqBlock0, seqBlock_0);

    }

    @Test 
    void testEquals(){
        STPNBlock simpleBlock = new SimpleBlock(new ProductType("p0", 4, null));
        STPNBlock simpleBlock1 = new SimpleBlock(new ProductType("p1", 4, null));
        STPNBlock andBlock = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock, simpleBlock1)));
        
        STPNBlock andBlock1 = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock, simpleBlock1)));
        assertNotEquals(andBlock, null);
        assertEquals(andBlock, andBlock1);
        assertEquals(andBlock1, andBlock);
        

        STPNBlock simpleBlock2 = new SimpleBlock(new ProductType("p2", 4, null));
        STPNBlock andBlock2 = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock, simpleBlock1, simpleBlock2)));
        assertNotEquals(andBlock2, andBlock1);
        assertNotEquals(andBlock1, andBlock2);
        
    }
    
}
