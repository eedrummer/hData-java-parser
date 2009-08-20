/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.results;

import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author bobd
 */
public class Result extends org.projecthdata.hdata.schemas._2009._06.result.Result implements org.mitre.hdata.hrf.SectionDocument {

    public static String TYPEID = "http://projecthdata.org/hdata/schemas/results/2009/06";

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
