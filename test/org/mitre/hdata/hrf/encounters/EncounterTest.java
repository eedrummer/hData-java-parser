/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.encounters;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.projecthdata.hdata.schemas.core._2009._06.CodedValue;
import static org.junit.Assert.*;

/**
 *
 * @author bobd
 */
public class EncounterTest {

    public EncounterTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }



    /**
     * Test of getDocumentName method, of class Encounter.
     */
    @Test
    public void testGetDocumentName() {
        System.out.println("getDocumentName");
        Encounter instance = new Encounter();
        String expResult = "";
        String result = instance.getDocumentName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSectionId method, of class Encounter.
     */
    @Test
    public void testGetSectionId() {
        System.out.println("getSectionId");
        Encounter instance = new Encounter();
        URI expResult = null;
        URI result = instance.getSectionId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of marshall method, of class Encounter.
     */
    @Test
    public void testMarshall() throws Exception {
        System.out.println("marshall");
        Encounter instance = new Encounter();
        CodedValue t = new CodedValue();
        t.setCode("code");
        t.setCodeSystem("system");
        t.setValue("Hey some really cool stuff should end up in here");
        instance.setType(t);
        OutputStream expResult = null;
        ByteArrayOutputStream result = (ByteArrayOutputStream)instance.marshall();

        System.out.println(new String(result.toByteArray()));
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class Encounter.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Object arg0 = null;
        Encounter instance = new Encounter();
        int expResult = 0;
        int result = instance.compareTo(arg0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}