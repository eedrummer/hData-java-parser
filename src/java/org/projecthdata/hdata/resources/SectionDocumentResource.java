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

import com.sun.jersey.api.core.ResourceConfig;
import org.projecthdata.hdata.resources.utils.HttpNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.Section;
import org.projecthdata.hdata.hrf.SectionDocument;
import org.projecthdata.hdata.resources.utils.HDataRestConfig;
import org.projecthdata.hdata.resources.utils.ModifyHandleHelper;

/**
 *
 * @author GBEUCHELT
 */
public class SectionDocumentResource extends AbstractResource {

    private final String documentid;
    private final Section section;
    private final SectionResource parentResource;
    private final HRF hrf;
    @Context
    private ResourceConfig rc;


    public SectionDocumentResource(HRF hrf, String sectionPath, String documentid, SectionResource parentResource) {

        this.hrf = hrf;
        this.documentid = documentid;
        this.section = hrf.getSection(sectionPath);
        this.parentResource = parentResource;

    }

    @GET
    public Response getDocument() {

        SectionDocument doc = section.getDocuments().get(documentid);

        if (doc == null) {
            throw new HttpNotFoundException("No document with documentid " + documentid +
                    " in this section found.");
        }

        try {
            return Response.ok(((ByteArrayOutputStream) doc.marshall()).toString("UTF-8"), MediaType.APPLICATION_XML_TYPE).build();
        } catch (Exception ex) {
            Logger.getLogger(SectionDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }
    }

    @DELETE
    public Response deleteDocument() {

        if (section.removeSectionDocument(documentid) == null) {
            return Response.serverError().build();
        }
        try {
            notifyChange(hrf);
        } catch (Exception ex) {
            Logger.getLogger(SectionDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();

        }

        return Response.status(204).build();
    }

    @POST
    public Response illegalOperation() {
        return Response.status(405).build();
    }

    @PUT
    @Consumes({MediaType.APPLICATION_XML})
    public Response handlePut(@Context UriInfo uriInfo, InputStream data) {
        Response result = Response.status(Response.Status.BAD_REQUEST).build();

        SectionDocument doc = section.getDocuments().get(documentid);

        if (doc == null) {
            throw new HttpNotFoundException("No document with documentid " + documentid +
                    " in this section found.");
        }


        try {
            result = ModifyHandleHelper.handleDocument(hrf,
                    HDataRestConfig.getInstance(rc).resolveExtension(section.getUriTypeId().toString()),
                    doc.getDocumentMetaData(),  // TODO: this must be updated first: date, identity of caller, etc.
                    data,
                    section,
                    uriInfo,
                    true);
            notifyChange(hrf);
        } catch (Exception ex) {
            Logger.getLogger(SectionDocumentResource.class.getName()).log(Level.SEVERE, null, ex);
            result = Response.serverError().build();
        }
        
        return result;
    }

    @Override
    public void notifyChange(HRF hrf) throws Exception {
        parentResource.notifyChange(hrf);
    }
}
