/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.projecthdata.hdata.hrf;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.projecthdata.hdata.hrf.serialization.HRFSerialializationException;
import org.projecthdata.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author GBEUCHELT
 */
public class hDataXmlDocument extends hDataDocument {

    Object document;

    public hDataXmlDocument(Object document) {
        this.document = document;
    }

    @Override
    public OutputStream marshall() throws HRFSerialializationException {

        OutputStream result;
        try {
            result = MarshallUtil.marshall(document, document.getClass());
        } catch (JAXBException ex) {
                Logger.getLogger(hDataXmlDocument.class.getName()).log(Level.SEVERE, null, ex);
                throw new HRFSerialializationException(ex);
            }
        return result;
    }
}
