/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

/**
 *
 * @author GBEUCHELT
 */
public class HRFException extends Exception {

    /**
     * Creates a new instance of <code>HRFException</code> without detail message.
     */
    public HRFException() {
    }


    /**
     * Constructs an instance of <code>HRFException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public HRFException(String msg) {
        super(msg);
    }

    public HRFException(Throwable ex) {
        super(ex); 
    }
}
