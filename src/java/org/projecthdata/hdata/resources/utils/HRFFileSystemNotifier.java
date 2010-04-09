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

import com.sun.jersey.api.core.ResourceConfig;
import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeFactory;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.resources.HDataResource;

/**
 *
 * @author GBEUCHELT
 */
public class HRFFileSystemNotifier implements UpdateNotifier {

    private final ResourceConfig rc;

    public HRFFileSystemNotifier(ResourceConfig rc) {
        this.rc = rc;
    }


    public void notifyChange(HRF hrf) throws Exception {

        hrf.getRoot().setLastModified(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));

        HDataRestConfig cfg = HDataRestConfig.getInstance(rc);

        FileSystemHelper.deleteDirectory(new File("/tmp/hrf/" + hrf.getRoot().getId()));

        try {
            cfg.getSerializer().serialize(new File("/tmp/hrf/" + hrf.getRoot().getId()), hrf);
        } catch (IOException ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public void deleteHrf(HRF hrf) throws Exception {

        if (! FileSystemHelper.deleteDirectory(new File("/tmp/hrf/" + hrf.getRoot().getId()))) {
            throw new Exception();
        }

    }

   
}
