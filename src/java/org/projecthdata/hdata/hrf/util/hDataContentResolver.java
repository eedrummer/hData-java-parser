/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.projecthdata.hdata.hrf.util;

import com.google.common.base.Predicate;
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
import javax.xml.bind.annotation.XmlRootElement;
import org.projecthdata.hdata.hrf.serialization.HRFSerializer;
import org.projecthdata.hdata.resources.utils.HDataRestConfig;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

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

    public void scanClassPath(String classpath) {

        Properties props = new java.util.Properties();

        Predicate<String> filter = new FilterBuilder().include(classpath + ".*");

        Reflections ref = new Reflections(new ConfigurationBuilder()
              .filterInputsBy(filter)
              .setScanners(new SubTypesScanner(), new TypeAnnotationsScanner())
              .setUrls(ClasspathHelper.getUrlsForPackagePrefix(classpath)));

        for(Class i : ref.getTypesAnnotatedWith(XmlRootElement.class))  {
            try {
                JAXBContext ctx = JAXBContext.newInstance(i);
                Logger.getLogger(HDataRestConfig.class.getName()).log(Level.INFO, i.getName());
                props.setProperty(i.getName(), ctx.createJAXBIntrospector().getElementName(JAXBIntrospector.getValue(i.newInstance())).getNamespaceURI());

                this.loadExtensionsFromProperties(props);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (URISyntaxException ex) {
                Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(hDataContentResolver.class.getName()).log(Level.SEVERE, null, ex);
            }
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
