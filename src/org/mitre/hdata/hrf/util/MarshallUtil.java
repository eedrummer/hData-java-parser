/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author bobd
 */
public class MarshallUtil {

    public static OutputStream marshall(Object obj, Class klass) throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JAXBContext ctx = JAXBContext.newInstance(klass);
        Marshaller m = ctx.createMarshaller();

        m.marshal(obj, bos);

        return bos;
    }

}
