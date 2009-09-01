/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.serialization;

import org.mitre.hdata.hrf.HRF;

/**
 *
 * @author GBEUCHELT
 */
public interface HRFSerializer {

    HRF deserialize(Object o) throws Exception;

    void serialize(Object o, HRF hrf) throws Exception;
    
    void registerExtension(String uri, Class clazz);

    public Class resolveExtension(String uri);

    

}
