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

import org.projecthdata.hdata.resources.utils.ModifyHandleHelper;
import org.projecthdata.hdata.resources.utils.HDataRestConfig;
import org.projecthdata.hdata.resources.utils.HttpNotFoundException;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.Section;
import org.projecthdata.hdata.hrf.ExtensionMissingException;
import org.projecthdata.hdata.hrf.serialization.HRFSerialializationException;
import org.projecthdata.hdata.resources.utils.FileSystemHelper;
import org.projecthdata.hdata.schemas._2009._06.core.Root;

/**
 *
 * @author GBEUCHELT
 */
@Path("/hDataRecord/")
public class HDataResource extends AbstractResource {

    private final HDataRestConfig cfg;

    public HDataResource(@PathParam("id") String id, @Context ResourceConfig rc) throws Exception {

        cfg = HDataRestConfig.getInstance(rc);



        //TODO: need to register all extensions here
//        cfg.registerExtension(Patient.CONTENT_TYPE, Patient.class);
//        cfg.registerExtension(Allergy.CONTENT_TYPE, Allergy.class);


    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_OCTET_STREAM})
    @Path("{id}")
    public Response getHrf(@PathParam("id") String id,
            @DefaultValue("") @QueryParam("type") String type,
            @Context HttpHeaders hh,
            @Context UriInfo uriInfo)
            throws JAXBException, Exception {

        List<String> transferEncodings = hh.getRequestHeader("TE");
        HRF hrf = getHrfById(id);

        if (hrf == null) {
            return Response.status(404).build();
        }

        if (type.equals("deflate") || (transferEncodings != null && transferEncodings.contains("deflate"))) {
            File tempDir = new File("tmp/HRF");
            FileSystemHelper.deleteDirectory(tempDir);
            if (tempDir.mkdirs()) {
                try {
                    cfg.getSerializer().serialize(tempDir, hrf);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    zipDir(tempDir, new ZipOutputStream(baos));

                    return Response.ok(new ByteArrayStreamingOutput(baos), MediaType.APPLICATION_OCTET_STREAM_TYPE).build();
                } catch (IOException ex) {
                    Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            // should be a HTTP 500
            return Response.serverError().build();

        } else if (type.equals("sections")) {
            SyndFeed result = new SyndFeedImpl();
            result.setFeedType("atom_1.0");
            List<SyndEntry> entries = new ArrayList<SyndEntry>();

            result.setTitle("Subsection feed for HRF " + hrf.getRoot().getId());
            result.setLink(uriInfo.getAbsolutePath().toString());


            for (Section i : hrf.getRootSections()) {
                SyndContent content = new SyndContentImpl();

                content.setValue("Section for " + i.getFriendlyName());
                content.setType("text/plain");
                List<SyndContent> contentList = new ArrayList<SyndContent>();
                contentList.add(content);

                SyndEntry entry = new SyndEntryImpl();
                entry.setLink(uriInfo.getAbsolutePath().toString() + i.getPath());
                entry.setTitle(i.getFriendlyName());
                entry.setContents(contentList);

                entries.add(entry);

            }
            result.setEntries(entries);

            return Response.ok(result.createWireFeed(), MediaType.APPLICATION_ATOM_XML).build();

        } else {
            return getRoot(id, uriInfo);

        }
    }

    @PUT
    public Response illegalOperation(@PathParam("id") String id) {
        return Response.status(405).build();
    }

    @POST
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML})
    public Response handlePost(@PathParam("id") String id,
            @QueryParam("type") String type,
            @QueryParam("requirement") String requirement,
            @QueryParam("typeId") String typeId,
            @QueryParam("path") String path,
            @QueryParam("name") String name,
            @Context UriInfo uriInfo) {
        Response result = Response.status(Response.Status.BAD_REQUEST).build();

        HRF hrf = getHrfById(id);

        if (type == null) {
            return result;
        }
        URI typeIdUri;
        try {
            typeIdUri = URI.create(typeId);
        } catch (IllegalArgumentException e) {
            return result;
        } catch (NullPointerException e) {
            return result;
        }

        if (type.equals("section")) {
            result = ModifyHandleHelper.handleSection(hrf, path, name, typeId, typeIdUri, uriInfo, "/");
            try {
                notifyChange(hrf);
            } catch (Exception ex) {
                Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.serverError().build();
            }

        } else if (type.equals("extension")) {

            result = ModifyHandleHelper.handleExtension(hrf, typeId, uriInfo);
            try {
                notifyChange(hrf);
            } catch (Exception ex) {
                Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
                return Response.serverError().build();

            }

        }
        return result;

    }

    @DELETE
    @Path("{id}")
    public Response deleteHrf(@PathParam("id") String id) {
        HRF hrf = getHrfById(id);

        if (hrf == null) {
            return Response.status(404).build();
        }
        try {
            cfg.getNotifier().deleteHrf(hrf);
        } catch (Exception ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }

        return Response.status(204).build();

    }

    @Path("{id}/{sectionid}")
    public AbstractResource getSectionResource(@PathParam("id") String id,
            @PathParam("sectionid") String sectionid) {

        HRF hrf = getHrfById(id);


        if (sectionid.equals("root.xml")) {
            return new RootDocumentResource(hrf);
        } else {
            for (Section i : hrf.getRootSections()) {
                if (i.getPath().equals(sectionid)) {
                    return new SectionResource(hrf, "/" + sectionid);
                }
            }

            throw new HttpNotFoundException("There is no section with path /" + sectionid);
        }
    }

    @Override
    public void notifyChange(HRF hrf) throws Exception {
        cfg.getNotifier().notifyChange(hrf);
    }

    // <editor-fold defaultstate="collapsed" desc="Private Helper Methods">
    private Response getRoot(@PathParam("id") String id,
            @Context UriInfo uriInfo) throws JAXBException {

        try {
            HRF hrf = getHrfById(id);
            JAXBContext ctx = JAXBContext.newInstance(Root.class);
            Marshaller m = ctx.createMarshaller();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            m.marshal(hrf.getRoot(), baos);
            return Response.ok(baos.toString("UTF-8"), MediaType.APPLICATION_XML_TYPE).build();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.serverError().build();
        }

    }

    private HRF getHrfById(String id) {

        HRF result = null;
        try {

            try {
                result = cfg.getSerializer().deserialize(new File("/tmp/hrf/" + id));
            } catch (HRFSerialializationException ex) {
                Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
            }


        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (ExtensionMissingException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);

        }
        return result;
    }

    private void zipDir(File zipdir, ZipOutputStream zos) throws FileNotFoundException {
        try {
            //get a listing of the directory content
            String[] dirList = zipdir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            //loop through dirList, and zip the files
            for (int i = 0; i < dirList.length; i++) {
                File f = new File(zipdir, dirList[i]);
                if (f.isDirectory()) {
                    //if the File object is a directory, call this
                    //function again to add its content recursively
                    zipDir(f, zos);
                    //loop again
                    continue;
                }
                //if we reached here, the File object f was not  a directory
                //create a FileInputStream on top of f
                FileInputStream fis = new FileInputStream(f);

                ZipEntry anEntry = new ZipEntry(f.getPath());
                //place the zip entry in the ZipOutputStream object
                zos.putNextEntry(anEntry);
                //now write the content of the file to the ZipOutputStream
                while ((bytesIn = fis.read(readBuffer)) != -1) {
                    zos.write(readBuffer, 0, bytesIn);
                }
                //close the Stream
                fis.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ByteArrayStreamingOutput implements StreamingOutput {

        ByteArrayOutputStream baos;

        public ByteArrayStreamingOutput(ByteArrayOutputStream baos) {
            this.baos = baos;
        }

        public void write(OutputStream output) throws IOException, WebApplicationException {
            output.write(baos.toByteArray());
        }
    }
    // </editor-fold>
}
