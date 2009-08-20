/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.encounters;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.mitre.hdata.hrf.SectionDocument;
import org.mitre.hdata.hrf.util.DateConverter.*;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author bobd
 */
public class Encounter extends org.projecthdata.hdata.schemas._2009._06.encounter.Encounter implements SectionDocument {

    public static String TYPEID = "http://projecthdata.org/hdata/schemas/encounters/2009/06";
    private String documentid;


    @Override
    public String getDocumentId() {
        return documentid;
    }

    @Override
    public void setDocumentId(String documentid) {
        this.documentid = documentid; 
    }



    @Override
    public OutputStream marshall() throws JAXBException {
        return MarshallUtil.marshall(this, super.getClass());
    }

}
