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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import org.projecthdata.hdata.hrf.serialization.HRFSerializer;

/**
 *
 * @author GBEUCHELT
 */
public class hDataContentResolver {

    @SuppressWarnings("unchecked")
	private final HashMap<Class, URI> map;

    @SuppressWarnings("unchecked")
	public hDataContentResolver() {
        map = new HashMap<Class, URI>();
    }

    @SuppressWarnings("unchecked")
	public URI getNamespace(Class clazz) {
        return map.get(clazz); 
    }

    @SuppressWarnings("unchecked")
	public void registerExtension(HRFSerializer fss) {
        for (Entry<Class,URI> e : map.entrySet()) {
            fss.registerExtension(e.getValue().toString(), e.getKey());
        }

    }

    public void loadExtensionsFromProperties(Properties configuration) throws ClassNotFoundException, URISyntaxException {
        for (Entry e : configuration.entrySet()) {
            map.put(Class.forName((String) e.getKey()), new URI((String) e.getValue()));
        }
    }


    public static URI resolveClassToUri(Object o) {
        URI result = null;
        try {
            JAXBIntrospector i = JAXBContext.newInstance(o.getClass()).createJAXBIntrospector();
            Object ob = JAXBIntrospector.getValue(o);

            result = new URI(i.getElementName(ob).getNamespaceURI());

        } catch (URISyntaxException ex) {
            Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JAXBException ex) {
            Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
