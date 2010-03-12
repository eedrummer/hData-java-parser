/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.projecthdata.hdata.hrf;

import java.io.OutputStream;
import java.io.Serializable;
import org.projecthdata.hdata.hrf.serialization.HRFSerialializationException;

/**
 *
 * @author GBEUCHELT
 */
public abstract class hDataDocument implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5587270422383125298L;

	public abstract OutputStream marshall() throws HRFSerialializationException;

}
