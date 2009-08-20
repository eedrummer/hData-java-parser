/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.advancedirectives;

import java.io.OutputStream;
import javax.xml.bind.JAXBException;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author bobd
 */
public class AdvanceDirective extends org.projecthdata.hdata.schemas._2009._06.advance_directive.AdvanceDirective implements org.mitre.hdata.hrf.SectionDocument {

    private String documentid;

    private static String TYPEID = "http://projecthdata.org/hdata/schemas/advancedirective/2009/06";

    public AdvanceDirective() {
    }

    public AdvanceDirective(org.projecthdata.hdata.schemas._2009._06.advance_directive.AdvanceDirective ad) {
        //TODO copy constructor

    }

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
