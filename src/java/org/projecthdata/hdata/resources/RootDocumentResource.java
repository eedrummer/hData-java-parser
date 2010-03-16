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
package org.projecthdata.hdata.resources;

import java.io.ByteArrayOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.util.MarshallUtil;

/**
 *
 * @author GBEUCHELT
 */
public class RootDocumentResource extends AbstractResource {

    private final HRF hrf;
    public RootDocumentResource(HRF hrf) {
        this.hrf = hrf;
    }

    @GET
    @Produces("application/xml")
    public Response getDocument() {


        try {
            return Response.ok(((ByteArrayOutputStream) MarshallUtil.marshall(hrf.getRoot(), org.projecthdata.hdata.schemas._2009._06.core.Root.class)).toString("UTF-8"), MediaType.APPLICATION_XML_TYPE).build();
        } catch (Exception ex) {
            Logger.getLogger(SectionDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }
    }


    @POST
    public Response invalidPost() {
        return invalidOperation();
    }

    @PUT
    public Response invalidPut() {
        return invalidOperation(); 
    }

    @DELETE
    public Response invalidDelete() {
        return invalidOperation();
    }

    private Response invalidOperation() {
        return Response.status(405).build(); 
    }

    @Override
    public void notifyChange(HRF hrf) {
        throw new UnsupportedOperationException("Not supported.");
    }


}
