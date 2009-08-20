/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

/**
 *
 * @author GBEUCHELT
 */
public class ExtensionMissingException extends Exception {

    /**
     * Creates a new instance of <code>ExtensionMissingException</code> without detail message.
     */
    public ExtensionMissingException() {
    }


    /**
     * Constructs an instance of <code>ExtensionMissingException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ExtensionMissingException(String msg) {
        super(msg);
    }
}
