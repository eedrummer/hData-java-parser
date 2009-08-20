/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.medication;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author bobd
 */
public class Medication implements org.mitre.hdata.hrf.SectionDocument {

    public static String TYPEID = "http://projecthdata.org/hdata/schemas/medications/2009/06";

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
