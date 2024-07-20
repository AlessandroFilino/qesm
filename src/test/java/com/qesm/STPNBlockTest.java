package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

public class STPNBlockTest {


    @Test
    void testSimpleBlock(){ 
        assertThrows(RuntimeException.class, () -> {
            new SimpleBlock(new ProductType("p0"));
        });
        ProductType p0 = new ProductType("p0", 4, null);
        SimpleBlock simpleBlock1 = new SimpleBlock(p0);
        SimpleBlock simpleBlock2 = new SimpleBlock(p0);
        
        assertNull(simpleBlock1.getComposedElements());
        assertEquals(simpleBlock1, simpleBlock1);
        assertNotEquals(simpleBlock1, null);
        assertNotEquals(simpleBlock1, "");
        assertEquals(simpleBlock1, simpleBlock2);

        assertEquals(simpleBlock1.toString(), "p0");

        ProductType p1 = new ProductType("p1");
        ProductType p2 = new ProductType("p2");
        simpleBlock1.addEnablingToken(p1);
        simpleBlock1.addEnablingToken(p2);
        assertEquals(simpleBlock1.getEnablingTokens(), List.of(p1, p2));

        assertNotEquals(simpleBlock1, simpleBlock2);

        assertTrue(simpleBlock1.getUuid() instanceof UUID);
        assertEquals(simpleBlock1.getSimpleElement(), p0);;

        simpleBlock1.getBlockInfo(0);
        
        assertEquals(simpleBlock1.toString(), "p0 enablingTokens: [ p1 p2 ] ");

        ProductType p3 = new ProductType("p3", 4, null);
        SimpleBlock simpleBlock3 = new SimpleBlock(p3);
        assertNotEquals(simpleBlock1, simpleBlock3);
        

        

    }

    // @Test
    // void testAndBlock(){
    //     assertThrows(RuntimeException.class, () -> {
    //         new AndBlock(null);
    //     });

    //     STPNBlock andBlock = new AndBlock(new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
    //     assertFalse(andBlock.addEnablingToken(null));

    //     assertNull(andBlock.getSimpleElement());

    //     assertEquals(andBlock.getComposedElements(), new ArrayList<STPNBlock>(List.of(simpleBlock)));
        
    // }

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
