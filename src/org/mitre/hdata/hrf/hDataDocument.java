/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

import java.io.OutputStream;
import org.mitre.hdata.hrf.serialization.HRFSerialializationException;

/**
 *
 * @author GBEUCHELT
 */
public abstract class hDataDocument {

    public abstract OutputStream marshall() throws HRFSerialializationException;

}
