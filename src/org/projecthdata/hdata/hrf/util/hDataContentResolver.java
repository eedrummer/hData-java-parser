/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.projecthdata.hdata.hrf.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Properties;
import org.projecthdata.hdata.hrf.serialization.HRFSerializer;

/**
 *
 * @author GBEUCHELT
 */
public class hDataContentResolver {

    private final HashMap<Class, URI> map;

    public hDataContentResolver(Properties configuration) throws ClassNotFoundException, URISyntaxException {
        map = new HashMap<Class, URI>();

        for (Entry e : configuration.entrySet()) {
            map.put(Class.forName((String) e.getKey()), new URI((String) e.getValue()));
        }
    }

    public URI getNamespace(Class clazz) {
        return map.get(clazz); 
    }

    public void registerExtension(HRFSerializer fss) {
        for (Entry<Class,URI> e : map.entrySet()) {
            fss.registerExtension(e.getValue().toString(), e.getKey());
        }

    }
}
