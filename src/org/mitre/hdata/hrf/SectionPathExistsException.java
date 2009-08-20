/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

/**
 *
 * @author GBEUCHELT
 */
public class SectionPathExistsException extends Exception {

    /**
     * Creates a new instance of <code>SectionPathExistsException</code> without detail message.
     */
    public SectionPathExistsException() {
    }


    /**
     * Constructs an instance of <code>SectionPathExistsException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public SectionPathExistsException(String msg) {
        super(msg);
    }
}
