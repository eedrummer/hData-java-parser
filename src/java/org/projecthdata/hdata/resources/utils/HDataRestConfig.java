/*
 *    Copyright 2009 The MITRE Corporation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.projecthdata.hdata.resources.utils;

import com.google.common.base.Predicate;
import com.sun.jersey.api.core.ResourceConfig;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.annotation.XmlRootElement;
import org.projecthdata.hdata.hrf.serialization.HRFFileSystemSerializer;
import org.projecthdata.hdata.hrf.serialization.HRFSerializer;
import org.projecthdata.hdata.hrf.util.hDataContentResolver;
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
public class HDataRestConfig {

    private static HDataRestConfig instance;
    private final HRFSerializer serializer;
    private final UpdateNotifier notifier;

    @SuppressWarnings("unchecked")
	private HDataRestConfig(ResourceConfig rc) throws Exception {

        //TODO: the invocation of provided serializers and notifiers should be much more flexible

        String serializerClassName = (String) rc.getProperty("org.projecthdata.hdata.resources.serializer");
        if (serializerClassName == null) {
            serializer = new HRFFileSystemSerializer();
        } else {
            serializer = (HRFSerializer) java.lang.Class.forName(serializerClassName).newInstance();
        }

        String notifierClassName = (String) rc.getProperty("org.projecthdata.hdata.resources.notifier");
        if (notifierClassName == null) {
            notifier = new HRFFileSystemNotifier(rc);
        } else {
            Class[] types = {ResourceConfig.class};
            Object[] params = {rc};

            Constructor ctr = java.lang.Class.forName(notifierClassName).getConstructor(types);
            notifier = (UpdateNotifier) ctr.newInstance(params);

        }

        hDataContentResolver cr = new hDataContentResolver();

        cr.scanClassPath("org.projecthdata.hdata.schemas");

        cr.registerExtension(serializer);

    }

    public static synchronized HDataRestConfig getInstance(ResourceConfig rc) throws Exception {
        if (instance == null) {
            try {
                instance = new HDataRestConfig(rc);
            } catch (Exception ex) {
                if (instance == null) {
                    throw new Exception("Error initializing configuration object.", ex);
                }
            }
        }

        return instance;

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public HRFSerializer getSerializer() {
        return serializer;
    }

    public UpdateNotifier getNotifier() {
        return notifier;
    }

    @SuppressWarnings("unchecked")
	public void registerExtension(String uri, Class clazz) {
        getSerializer().registerExtension(uri, clazz);
    }

    @SuppressWarnings("unchecked")
	public Class resolveExtension(String uri) {

        return getSerializer().resolveExtension(uri);
    }
}
