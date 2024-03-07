package main;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PersonaTest {
    @Test
    public void testGetName() {
        Persona pippo = new Persona("Pippo");
        assertEquals("Pippo", pippo.getName());
    }

    @Test
    public void testSetName() {
        
    }
}

