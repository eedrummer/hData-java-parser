/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.advancedirectives;

import java.io.OutputStream;
import java.net.URI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bobd
 */
public class AdvanceDirectiveTest {

    public AdvanceDirectiveTest() {
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
     * Test of getDocumentName method, of class AdvanceDirective.
     */
    @Test
    public void testGetDocumentName() {
        System.out.println("getDocumentName");
        AdvanceDirective instance = new AdvanceDirective();
        String expResult = "";
        String result = instance.getDocumentName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSectionId method, of class AdvanceDirective.
     */
    @Test
    public void testGetSectionId() {
        System.out.println("getSectionId");
        AdvanceDirective instance = new AdvanceDirective();
        URI expResult = null;
        URI result = instance.getSectionId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of marshall method, of class AdvanceDirective.
     */
    @Test
    public void testMarshall() throws Exception {
        System.out.println("marshall");
        AdvanceDirective instance = new AdvanceDirective();
        OutputStream expResult = null;
        OutputStream result = instance.marshall();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class AdvanceDirective.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Object arg0 = null;
        AdvanceDirective instance = new AdvanceDirective();
        int expResult = 0;
        int result = instance.compareTo(arg0);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}