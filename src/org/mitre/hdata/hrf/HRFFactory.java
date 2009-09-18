/*
 *
 *
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
package org.mitre.hdata.hrf;


import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.projecthdata.hdata.schemas._2009._06.core.Extensions;
import org.projecthdata.hdata.schemas._2009._06.core.Root;
import org.projecthdata.hdata.schemas._2009._06.core.Sections;



/**
 * Factory class for HRF.
 *
 *
 * @author GBEUCHELT
 */
public class HRFFactory {


    /**
     * Obtain a new HRF instance. All necessay fields in the Root document will be initialized,
     * including the generation of a new Documentid.
     * @return Return a new HRF Instance.
     */
    public HRF getHRFInstance() {
        return new HRFImpl();
    }


    /**
     * Instantiate a HRF instance based on an existing Root document. This method is useful
     * for reconstructing HRF object from their reseptive serialized versions.
     * @param root The fully instantiated Root object. Note that this method does not check if all
     * fields are initialized (such as Sections, Extensions, etc.).
     * @return The HRF instance. 
     */
    public HRF getHRFInstance(Root root) {
        return new HRFImpl(root);
    }

    private class HRFImpl extends HRF {
        protected HRFImpl(Root root) {
            this.root = root;
        }

        protected HRFImpl() {
            super();
            try {
                root = new Root();
                root.setCreated(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
                root.setDocumentId(UUID.randomUUID().toString());
                root.setLastModified(root.getCreated());
                root.setVersion(HRF.HRF_VERSION);
                root.setSections(new Sections());
                root.setExtensions(new Extensions());

            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(HRFFactory.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }
}
