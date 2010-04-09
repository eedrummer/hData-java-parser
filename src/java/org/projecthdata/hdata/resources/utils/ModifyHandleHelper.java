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

import java.io.InputStream;
import org.projecthdata.hdata.resources.*;
import java.net.URI;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.Section;
import org.projecthdata.hdata.hrf.SectionDocument;
import org.projecthdata.hdata.hrf.SectionPathExistsException;
import org.projecthdata.hdata.hrf.hDataXmlDocument;
import org.projecthdata.hdata.schemas._2009._06.core.Extension;
import org.projecthdata.hdata.schemas._2009._11.metadata.DocumentMetaData;

/**
 *
 * @author GBEUCHELT
 */
public abstract class ModifyHandleHelper {

    public static Response handleSection(HRF hrf, String path, String name, String typeId, URI typeIdUri, UriInfo uriInfo, String sectionPath) {

        Response result = null;

        if (path == null || name == null) {
            return result;
        }

        boolean extensionOk = false;
        for (Extension e : hrf.getExtensions()) {
            if (e.getContentType().equals(typeId)) {
                extensionOk = true;
                break;
            }
        }
        if (!extensionOk) {
            return Response.status(Response.Status.NOT_ACCEPTABLE).build();
        }


        Section parentSection = null;
        if (sectionPath.equals("/")) {
            for (Section s : hrf.getRootSections()) {
                if (s.getPath().equals(path)) {
                    return Response.status(Response.Status.CONFLICT).build();
                }
            }
        } else {
            parentSection = hrf.getSection(sectionPath);
            for (Section s : parentSection.getSections()) {
                if (s.getPath().equals(path)) {
                    return Response.status(Response.Status.CONFLICT).build();
                }
            }
        }
        try {
            Section s = new Section(name, typeIdUri, path);

            if (sectionPath.equals("/")) {
                hrf.addSection(s, "/");
            } else {
                parentSection.addSection(s);
            }
            result = Response.created(uriInfo.getAbsolutePathBuilder().path(path).build()).build();
        } catch (SectionPathExistsException ex) {
            Logger.getLogger(HDataResource.class.getName()).log(Level.SEVERE, null, ex);
            return Response.status(Response.Status.CONFLICT).build();
        }

        return result;
    }

    public static Response handleExtension(HRF hrf, String extensionId, UriInfo uriInfo) {

        Response result = null;
        
        Extension regExt = null;
        for (Extension e : hrf.getExtensions()) {
            if (e.getContent().equals(extensionId)) {
                regExt = e;
                break;
            }
        }
        

        hrf.addExtension(extensionId, regExt.getContentType());
        result = Response.created(uriInfo.getAbsolutePathBuilder().path("root.xml").build()).build();

        return result;

    }

    @SuppressWarnings("unchecked")
	public static Response handleDocument(HRF hrf, Class clazz, DocumentMetaData metadata, InputStream data, Section section, UriInfo uriInfo, boolean update) {
        Response result = Response.status(Response.Status.BAD_REQUEST).build();

        if (metadata.getMediaType().equals(MediaType.APPLICATION_XML)) {
            try {
                if (clazz == null) {
                    return result;
                }

                JAXBContext ctx = JAXBContext.newInstance(clazz);
                Unmarshaller u = ctx.createUnmarshaller();
                Object o = u.unmarshal(data);
                SectionDocument doc = new SectionDocument(metadata, new hDataXmlDocument(o));

                if (update) {
                    PathSegment ps = null;
                    Iterator<PathSegment> i = uriInfo.getPathSegments().iterator();
                    while (i.hasNext()) {
                        ps = i.next();
                    }
                    String docId = ps.getPath().substring(0, ps.getPath().length() - 4);

                    doc.setDocumentId(docId);
                    section.removeSectionDocument(doc.getDocumentId());

                    result = Response.ok().build();

                } else {
                    doc.setDocumentId(UUID.randomUUID().toString());

                    result = Response.created(uriInfo.getAbsolutePathBuilder().path(doc.getDocumentId()).build()).build();
                }

                section.addSectionDocument(doc);

            } catch (Exception ex) {
                Logger.getLogger(ModifyHandleHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // binary object not supported yet
            result = Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).build();
        }
        return result;

    }
}

