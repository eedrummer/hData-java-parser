/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.serialization;

import org.mitre.hdata.hrf.HRFException;

/**
 *
 * @author GBEUCHELT
 */
public class HRFSerialializationException extends HRFException {

    /**
     * Creates a new instance of <code>HRFSerialializationException</code> without detail message.
     */
    public HRFSerialializationException() {
    }


    /**
     * Constructs an instance of <code>HRFSerialializationException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public HRFSerialializationException(String msg) {
        super(msg);
    }

    public HRFSerialializationException(Throwable ex) {
        super(ex);
    }
}
