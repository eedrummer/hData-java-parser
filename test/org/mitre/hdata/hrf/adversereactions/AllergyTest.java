/*
 *
 *
 *    Copyright 2009 The MITRE Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mitre.hdata.hrf.adversereactions;

import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.projecthdata.hdata.schemas.allergy._2009._06.Severity;
import org.projecthdata.hdata.schemas.allergy._2009._06.Type;

/**
 *
 * @author bobd
 */
public class AllergyTest {

    public AllergyTest() {
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
     * Test of setUtilDate method, of class Allergy.
     */
    @Test
    public void testSetUtilDate() {
        System.out.println("setUtilDate");
        Date date = null;
        Allergy instance = new Allergy();
        instance.setUtilDate(date);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getUtilDate method, of class Allergy.
     */
    @Test
    public void testGetUtilDate() {
        System.out.println("getUtilDate");
        Allergy instance = new Allergy();
        Date expResult = null;
        Date result = instance.getUtilDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setProduct method, of class Allergy.
     */
    @Test
    public void testSetProduct() {
        System.out.println("setProduct");
        String name = "";
        String code = "";
        String codesystem = "";
        Allergy instance = new Allergy();
        instance.setProduct(name, code, codesystem);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setSeverity method, of class Allergy.
     */
    @Test
    public void testSetSeverity() {
        System.out.println("setSeverity");
        Severity severity = null;
        Allergy instance = new Allergy();
        instance.setSeverity(severity);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setType method, of class Allergy.
     */
    @Test
    public void testSetType() {
        System.out.println("setType");
        Type type = null;
        Allergy instance = new Allergy();
        instance.setType(type);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of marshall method, of class Allergy.
     */
    @Test
    public void testMarshall() throws Exception {
        System.out.println("marshall");
        Allergy instance = new Allergy();
        OutputStream expResult = null;
        OutputStream result = instance.marshall();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDocumentName method, of class Allergy.
     */
    @Test
    public void testGetDocumentName() {
        System.out.println("getDocumentName");
        Allergy instance = new Allergy();
        String expResult = "";
        String result = instance.getDocumentName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of compareTo method, of class Allergy.
     */
    @Test
    public void testCompareTo() {
        System.out.println("compareTo");
        Object o = null;
        Allergy instance = new Allergy();
        int expResult = 0;
        int result = instance.compareTo(o);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getSectionId method, of class Allergy.
     */
    @Test
    public void testGetSectionId() {
        System.out.println("getSectionId");
        Allergy instance = new Allergy();
        URI expResult = null;
        URI result = instance.getSectionId();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}