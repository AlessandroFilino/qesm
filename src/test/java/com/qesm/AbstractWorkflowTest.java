package com.qesm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.qesm.RandomDAGGenerator.PdfType;

public class AbstractWorkflowTest {
    
    private WorkflowType wf1;
    private WorkflowType wf1Refernce;
    private WorkflowType wfNull;
    private WorkflowIstance wi1;

    @BeforeEach
    public void setup(){
        wf1 = new WorkflowType();
        wf1.generateRandomDAG(5, 5, 2, 2, 60, PdfType.UNIFORM);
        wf1Refernce = wf1;

        wi1 = wf1.makeIstance();
    }


    @Test
    void testEquals() {
        // TODO TEST: Implement test
        assertEquals(wf1, wf1Refernce);
        assertNotEquals(wf1, wfNull);
        assertNotEquals(wf1, wi1);
    }

    @Test
    void testGetRootNode(){
        assertNotEquals(wf1.getRootNode(), null);
    }

    @Test
    void testFindProduct() {
        // TODO TEST: Implement test
    }

    @Test
    void testComputeParallelismValue() {
        // TODO TEST: Implement test
        
    }
    
}
