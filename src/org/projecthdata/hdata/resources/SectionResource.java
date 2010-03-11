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
import com.sun.jersey.multipart.BodyPartEntity;
import com.sun.jersey.multipart.MultiPart;
import org.projecthdata.hdata.hrf.HRFException;
import org.projecthdata.hdata.resources.utils.ModifyHandleHelper;
import org.projecthdata.hdata.resources.utils.HDataRestConfig;
import org.projecthdata.hdata.resources.utils.HttpNotFoundException;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBException;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.Section;
import org.projecthdata.hdata.hrf.SectionDocument;
import org.projecthdata.hdata.hrf.util.MetaDataHelper;
import org.projecthdata.hdata.schemas._2009._11.metadata.DocumentMetaData;

/**
 *
 * @author GBEUCHELT
 */
public class SectionResource extends AbstractResource {

    private final HRF hrf;
    private final String sectionPath;
    private final Section section;
    private final AbstractResource parentResource;
    @Context
    private ResourceConfig rc;

    public SectionResource(HRF hrf, String sectionPath, AbstractResource parentResource) {
        this.hrf = hrf;
        this.sectionPath = sectionPath;
        this.parentResource = parentResource;

        section = hrf.getSection(sectionPath);
    }

    @GET
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Response getSectionResource(
            @DefaultValue("documents") @QueryParam("type") String type,
            @Context UriInfo uriInfo) throws JAXBException, UnsupportedEncodingException, HRFException {

        SyndFeed result = new SyndFeedImpl();
        result.setFeedType("atom_1.0");
        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        if (type.equals("documents")) {

            result.setTitle("Document feed for " + section.getFriendlyName());
            result.setLink(uriInfo.getAbsolutePath().toString());

            for (SectionDocument i : section.getDocuments().values()) {

                SyndEntry entry = new SyndEntryImpl();
                MetaDataHelper.configureFeedEntry(entry, i.getDocumentMetaData());
                entry.setLink(uriInfo.getAbsolutePathBuilder().path(i.getDocumentId() + ".xml").build().toString());

                entries.add(entry);
            }

            result.setEntries(entries);

        } else if (type.equals("sections")) {
            result.setTitle("Subsection feed for " + section.getFriendlyName());
            result.setLink(uriInfo.getAbsolutePath().toString());


            for (Section i : section.getSections()) {
                SyndContent content = new SyndContentImpl();

                content.setValue("Section for " + i.getFriendlyName());
                content.setType("text/plain");
                List<SyndContent> contentList = new ArrayList<SyndContent>();
                contentList.add(content);

                SyndEntry entry = new SyndEntryImpl();
                entry.setLink(uriInfo.getAbsolutePathBuilder().path(i.getPath()).build().toString());
                entry.setTitle(i.getFriendlyName());
                entry.setContents(contentList);

                entries.add(entry);

            }
            result.setEntries(entries);

        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(result.createWireFeed(), MediaType.APPLICATION_ATOM_XML).build();

    }

    @PUT
    public Response illegalOperation(@PathParam("id") String id) {
        return Response.status(405).build();
    }

    @POST
    @Consumes({"multipart/mixed"})
    public Response handlePost(
            @QueryParam("type") String type,
            @QueryParam("typeId") String typeId,
            @QueryParam("path") String path,
            @QueryParam("name") String name,
            @Context UriInfo uriInfo,
            MultiPart multipart) {
        Response result = Response.status(Response.Status.BAD_REQUEST).build();

        if (type == null) {
            return result;
        }

        if (type.equals("section")) {

            URI typeIdUri;
            try {
                typeIdUri = URI.create(typeId);
            } catch (IllegalArgumentException e) {
                return result;
            } catch (NullPointerException e) {
                return result;
            }

            result = ModifyHandleHelper.handleSection(hrf, path, name, typeId, typeIdUri, uriInfo, sectionPath);


        } else if (type.equals("document")) {

            String sectionTypeId = section.getUriTypeId().toString();

            try {

                DocumentMetaData metadata = multipart.getBodyParts().get(0).getEntityAs(DocumentMetaData.class);
                BodyPartEntity bpe = (BodyPartEntity) multipart.getBodyParts().get(1).getEntity();

                result = ModifyHandleHelper.handleDocument(hrf,
                        HDataRestConfig.getInstance(rc).resolveExtension(sectionTypeId),
                        metadata,
                        bpe.getInputStream(),
                        section,
                        uriInfo,
                        false);

            } catch (Exception ex) {
                Logger.getLogger(SectionResource.class.getName()).log(Level.SEVERE, null, ex);
                result = Response.serverError().build();
            }

        }
        try {
            notifyChange(hrf);
        } catch (Exception ex) {
            Logger.getLogger(SectionResource.class.getName()).log(Level.SEVERE, null, ex);

            return Response.serverError().build();
        }
        return result;

    }

    @DELETE
    public Response handleDelete() {

        String pPath = sectionPath.substring(0, sectionPath.lastIndexOf("/"));
        Section pSection = hrf.getSection(pPath);

        if (!pSection.removeSection(section)) {
            return Response.serverError().build();
        }
        try {
            notifyChange(hrf);
        } catch (Exception ex) {
            Logger.getLogger(SectionResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }

        return Response.status(204).build();
    }

    @Path("{abstractid}")
    public AbstractResource getAbstractResource(
            @PathParam("abstractid") String abstractid) {

        if (!abstractid.endsWith(".xml")) {
            for (Section i : section.getSections()) {
                if (i.getPath().equals(abstractid)) {
                    return new SectionResource(hrf, sectionPath + "/" + abstractid, this);
                }
            }
            throw new HttpNotFoundException("There is no child section with path /" + sectionPath + "/" + abstractid);
        } else {

            String documentid = abstractid.substring(0, abstractid.length() - 4);
            return new SectionDocumentResource(hrf, sectionPath, documentid, this);
        }
    }

    @Override
    public void notifyChange(HRF hrf) throws Exception {
        HDataRestConfig.getInstance(rc).getNotifier().notifyChange(hrf);
    }
}
