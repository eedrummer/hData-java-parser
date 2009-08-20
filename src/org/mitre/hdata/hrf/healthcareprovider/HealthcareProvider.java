/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.healthcareprovider;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author bobd
 */
public class HealthcareProvider extends org.projecthdata.hdata.schemas._2009._06.provider.Provider implements org.mitre.hdata.hrf.SectionDocument {

    public static String TYPEID = "http://projecthdata.org/hdata/schemas/healthcare_provider/2009/06";
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
