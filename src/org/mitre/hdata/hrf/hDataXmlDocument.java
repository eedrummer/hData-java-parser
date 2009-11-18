/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.mitre.hdata.hrf.serialization.HRFSerialializationException;
import org.mitre.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author GBEUCHELT
 */
public abstract class hDataXmlDocument extends hDataDocument {

    @Override
    public OutputStream marshall() throws HRFSerialializationException {

        OutputStream result;
        try {
            result = MarshallUtil.marshall(this, this.getClass());
        } catch (JAXBException ex) {
            try {
                result = MarshallUtil.marshall(this, this.getClass().getSuperclass());
            } catch (JAXBException ex1) {
                Logger.getLogger(hDataXmlDocument.class.getName()).log(Level.SEVERE, null, ex1);
                throw new HRFSerialializationException(ex1);
            }
        }
        return result;
    }
}
