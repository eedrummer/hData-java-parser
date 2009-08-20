/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author GBEUCHELT
 */
public class DeepCopy {

    /**
     * A simple Object copy based on the ObjectStreams. Note that object must 
     * implement the Serializable interface. 
     * @param orig
     * @return a deep copy of the original object. 
     */
    public static Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos =
                    new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Retrieve an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in =
                    new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }
}
