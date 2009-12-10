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
package org.projecthdata.hdata.hrf.serialization;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.SyndFeedOutput;
import java.io.ByteArrayInputStream;
import org.projecthdata.hdata.hrf.ExtensionMissingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.projecthdata.hdata.hrf.HRF;
import org.projecthdata.hdata.hrf.HRFFactory;
import org.projecthdata.hdata.hrf.Section;
import org.projecthdata.hdata.hrf.SectionDocument;
import org.projecthdata.hdata.hrf.SectionPathExistsException;
import org.projecthdata.hdata.hrf.hDataDocument;
import org.projecthdata.hdata.hrf.hDataXmlDocument;
import org.projecthdata.hdata.hrf.util.MarshallUtil;
import org.projecthdata.hdata.schemas._2009._06.core.Extensions;
import org.projecthdata.hdata.schemas._2009._06.core.Sections;
import org.projecthdata.hdata.schemas._2009._11.metadata.DocumentMetaData;

/**
 *
 * @author GBEUCHELT
 */
public class HRFFileSystemSerializer implements HRFSerializer {

    HashMap<String, Class> registeredExtensions;
    
    public HRFFileSystemSerializer() {
        registeredExtensions = new HashMap<String, Class>();
    }

    public void serialize(Object o, HRF hrf) throws Exception {
        if (! o.getClass().equals(File.class)) {
            throw new IllegalArgumentException();
        }
        this.serialize((File) o, hrf);
    }

    public void serialize(File location, HRF hrf) throws IOException {
        try {
            if (location.exists()) {
                File[] i = location.listFiles();
                if (i == null || i.length != 0) {
                    throw new IOException();
                }
            } else {
                if (!location.mkdirs()) {
                    throw new IOException();
                }
            }
            File root = new File(location, "root.xml");
            root.createNewFile();
            FileOutputStream fosRoot = new FileOutputStream(root);
            fosRoot.write(((ByteArrayOutputStream) hrf.marshall()).toByteArray());
            fosRoot.close();

            List<Section> rootsections = hrf.getRootSections();
            serializeSection(rootsections, location);


        } catch (JAXBException ex) {
            Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
            IOException th = new IOException();
            th.initCause(ex);
            throw th;
        } catch (HRFSerialializationException ex) {
            Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
            IOException th = new IOException();
            th.initCause(ex);
            throw th;
        }

    }

    public HRF deserialize(Object o) throws Exception {
        if (! o.getClass().equals(File.class)) {
            throw new IllegalArgumentException();
        }
        return this.deserialize((File) o);
    }

    public HRF deserialize(File location) throws IOException, ExtensionMissingException, HRFSerialializationException {

        try {
        HRF hrf = null;

        File[] rootDirContentArray = location.listFiles();

        if (rootDirContentArray == null || rootDirContentArray.length == 0) {
            throw new IOException();
        }

        ArrayList<File> sections = new ArrayList<File>();

        File rootFile = null;
        org.projecthdata.hdata.schemas._2009._06.core.Root rootDoc = null;

        for (int i = 0; i < rootDirContentArray.length; i++) {
            parseDirectories(rootDirContentArray[i], sections);
            if (rootDirContentArray[i].getName().toLowerCase().equals("root.xml")) {
                rootFile = rootDirContentArray[i];
            }
        }

        if (rootFile == null) {
            throw new IOException("Cannot find root.xml document - giving up.");
        }
        try {
            JAXBContext rootCtx = JAXBContext.newInstance(org.projecthdata.hdata.schemas._2009._06.core.Root.class);
            Unmarshaller u = rootCtx.createUnmarshaller();

            rootDoc = (org.projecthdata.hdata.schemas._2009._06.core.Root) u.unmarshal(rootFile);
            org.projecthdata.hdata.schemas._2009._06.core.Root hrfRoot = new org.projecthdata.hdata.schemas._2009._06.core.Root();

            hrfRoot.setCreated(rootDoc.getCreated());
            hrfRoot.setDocumentId(rootDoc.getDocumentId());
            hrfRoot.setLastModified(rootDoc.getLastModified());
            hrfRoot.setVersion(rootDoc.getVersion());
            hrfRoot.setSections(new Sections());
            hrfRoot.setExtensions(new Extensions());

            hrf = (new HRFFactory()).getHRFInstance(hrfRoot);

        } catch (JAXBException ex) {
            Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
            throw new IOException("Cannot deserialize document", ex);
        }

        // now we have a working HRF

        for (org.projecthdata.hdata.schemas._2009._06.core.Extension i : rootDoc.getExtensions().getExtension()) {
            if (!this.registeredExtensions.keySet().contains(i.getContentType())) {
                if (i.getRequirement().equals(HRF.EXTENSION_REQUIREMENT_MANDATORY)) {
                    throw new ExtensionMissingException();
                }
            }

            // needed to populate all internal lists
            hrf.addExtension(i.getContentType(), i.getRequirement());

        }


        for (org.projecthdata.hdata.schemas._2009._06.core.Section i : rootDoc.getSections().getSection()) {

            Section s = convertSectionToConvenienceSection(i);
            hrf.addSection(s, "/");

            decentSection(i, "", hrf);
        }
        // all internal structures in the HRF should now be uptodate

        Unmarshaller mdu = JAXBContext.newInstance(DocumentMetaData.class).createUnmarshaller();

        for (File i : sections) {
            try {
                File[] sectionContent = i.listFiles();

                // read the section meta data
                HashMap<String, DocumentMetaData> dmd = new HashMap<String, DocumentMetaData>();

                SyndFeedInput input = new SyndFeedInput();
                File atomFile = new File(i, "section.xml");
                SyndFeed atomFeed = input.build(atomFile);

                for (Object e : atomFeed.getEntries()) {

                    String md = ((SyndContent) ((SyndEntry) e).getContents().iterator().next()).getValue();
                    ByteArrayInputStream bais = new ByteArrayInputStream(md.getBytes());

                    DocumentMetaData t = (DocumentMetaData) mdu.unmarshal(bais);
                    dmd.put(t.getDocumentId(), t);
                }

                int pos = i.getCanonicalPath().replace("\\", "/").indexOf(location.getCanonicalPath().replace("\\", "/")) + location.getCanonicalPath().replace("\\", "/").length();
                String secName = i.getCanonicalPath().replace("\\", "/").substring(pos);

                Section section = hrf.getSection(secName);

                Class sectionClass = registeredExtensions.get(section.getUriTypeId().toString());

                Unmarshaller secu = null;
                if (sectionClass != null) {
                    secu = JAXBContext.newInstance(sectionClass).createUnmarshaller();
                }
                    for (int j = 0; j < sectionContent.length; j++) {
                        if (!sectionContent[j].isDirectory() &&
                                !sectionContent[j].getName().equals("section.xml")) {


                            // it's not a sub section
                            hDataDocument hdd = null;

                            // docID through the filename - .xml
                            String docName = sectionContent[j].getName().substring(0, sectionContent[j].getName().length()-4);

                            if (dmd.get(docName).getContentType()==null ) {
                                hdd = new hDataXmlDocument(secu.unmarshal(sectionContent[j])); 
                            } else {
                                if (dmd.get(docName).getMediaType().equals("application/xml")) {
                                
                                    Unmarshaller u = JAXBContext.newInstance(registeredExtensions.get(dmd.get(docName).getContentType())).createUnmarshaller();
                                    hdd = new hDataXmlDocument(u.unmarshal(sectionContent[j])); 
                                } else {
                                    //TODO: Binary deserialization
                                }
                            }
                            
                            
                            SectionDocument doc = new SectionDocument(dmd.get(docName), hdd);

                            section.addSectionDocument(doc);

                        }
                    }
                
            } catch (JAXBException ex) {
                Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        return hrf;
        } catch (Exception ex) {
            throw new HRFSerialializationException(ex);
        }


    }

    public void registerExtension(String uri, Class clazz) {
        registeredExtensions.put(uri, clazz);
    }

    public Class resolveExtension(String uri) {
        return registeredExtensions.get(uri);
    }

    // <editor-fold defaultstate="collapsed" desc="private methods">
    private Section convertSectionToConvenienceSection(org.projecthdata.hdata.schemas._2009._06.core.Section i) {
        Section s = new Section();
        // deprecated
        //s.setClazz(i.getClazz());
        s.setName(i.getName());
        s.setTypeId(i.getTypeId());
        s.setPath(i.getPath());
        return s;
    }

    private void decentSection(org.projecthdata.hdata.schemas._2009._06.core.Section section,
            String parentpath,
            HRF hrf) throws SectionPathExistsException {
        for (org.projecthdata.hdata.schemas._2009._06.core.Section i : section.getSection()) {

            Section s = convertSectionToConvenienceSection(i);
            hrf.addSection(s, parentpath + "/" + section.getPath());

            decentSection(i, parentpath + "/" + section.getPath(), hrf);
        }
    }

    private void parseDirectories(File file, ArrayList<File> sections) {
        if (file.isDirectory()) {
            sections.add(file);
            File[] directoryContent = file.listFiles();
            if (directoryContent.length != 0) {
                for (int i = 0; i < directoryContent.length; i++) {
                    parseDirectories(directoryContent[i], sections);
                }
            }
        }
    }

    private void serializeSection(List<Section> sectionsList, File section) throws IOException, HRFSerialializationException {
        for (Section i : sectionsList) {
            File secFile = new File(section, i.getPath());
            secFile.mkdir();

            SyndFeed sectionAtomFeed = new SyndFeedImpl();
            sectionAtomFeed.setFeedType("atom_1.0");

            List<SyndEntry> entries = new ArrayList<SyndEntry>();
            
            for (SectionDocument sd : i.getDocuments().values()) {

                File docFile = new File(secFile, sd.getDocumentId() + ".xml");
                docFile.createNewFile();
                FileOutputStream fosDoc = new FileOutputStream(docFile);
                fosDoc.write(((ByteArrayOutputStream) sd.marshall()).toByteArray());
                fosDoc.close();

                SyndContent content = new SyndContentImpl();
                content.setType("application/xml");
                try {

                    ByteArrayOutputStream os = new ByteArrayOutputStream();

                    JAXBContext ctx = JAXBContext.newInstance(DocumentMetaData.class);
                    Marshaller m = ctx.createMarshaller();
                    m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

                    m.marshal(sd.getDocumentMetaData(), os);

                    content.setValue(os.toString());
                } catch (JAXBException ex) {
                    Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
                    throw new HRFSerialializationException(ex);
                }

                List<SyndContent> contentList = new ArrayList<SyndContent>();
                contentList.add(content);

                SyndEntry entry = new SyndEntryImpl();
                entry.setTitle(sd.getDocumentId());
                entry.setContents(contentList);

                entries.add(entry);

            }

            sectionAtomFeed.setEntries(entries);
            File sectionAtom = new File(secFile, "section.xml");
            sectionAtom.createNewFile();
            SyndFeedOutput atomOut = new SyndFeedOutput();
            Writer atomWriter = new FileWriter(sectionAtom);
            try {
                atomOut.output(sectionAtomFeed, atomWriter);
            } catch (FeedException ex) {
                Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
                throw new IOException(ex); 
            }

            serializeSection(i.getSections(), secFile);

        }
    }
    // </editor-fold>
}
