/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.mitre.hdata.hrf.serialization;

import org.mitre.hdata.hrf.ExtensionMissingException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.mitre.hdata.hrf.HRF;
import org.mitre.hdata.hrf.HRFException;
import org.mitre.hdata.hrf.HRFFactory;
import org.mitre.hdata.hrf.Section;
import org.mitre.hdata.hrf.SectionDocument;
import org.mitre.hdata.hrf.SectionPathExistsException;
import org.projecthdata.hdata.schemas._2009._06.core.Extensions;
import org.projecthdata.hdata.schemas._2009._06.core.Sections;

/**
 *
 * @author GBEUCHELT
 */
public class HRFFileSystemSerializer {

    HashMap<String, Class> registeredExtensions;
    private static final HRFFileSystemSerializer instance = new HRFFileSystemSerializer();

    private HRFFileSystemSerializer() {
        registeredExtensions = new HashMap<String, Class>();
    }

    public static HRFFileSystemSerializer getInstance() {
        return instance;
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
        }

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
            if (!this.registeredExtensions.keySet().contains(i.getContent())) {
                if (i.getRequirement().equals(HRF.EXTENSION_REQUIREMENT_MANDATORY)) {
                    throw new ExtensionMissingException();
                }
            }

            // needed to populate all internal lists
            hrf.addExtension(i.getContent(), i.getRequirement());

        }


        for (org.projecthdata.hdata.schemas._2009._06.core.Section i : rootDoc.getSections().getSection()) {

            Section s = convertSectionToConvenienceSection(i);
            hrf.addSection(s, "/");

            decentSection(i, "", hrf);
        }

        // all internal structures in the HRF should now be uptodate

        
        for (File i : sections) {
            try {
                File[] sectionContent = i.listFiles();

                int pos = i.getCanonicalPath().replace("\\", "/").indexOf(location.getCanonicalPath().replace("\\", "/")) + location.getCanonicalPath().replace("\\", "/").length();
                String secName = i.getCanonicalPath().replace("\\", "/").substring(pos);

                Section section = hrf.getSection(secName);

                Class sectionClass = registeredExtensions.get(section.getUriTypeId().toString());
                if (sectionClass != null) {
                    JAXBContext ctx = JAXBContext.newInstance(sectionClass.getSuperclass());

                    for (int j = 0; j < sectionContent.length; j++) {
                        if (!sectionContent[j].isDirectory()) {

                            // it's not a sub section
                            
                            Unmarshaller u = ctx.createUnmarshaller();
                            
                            Object o = u.unmarshal(sectionContent[j]);
                            SectionDocument doc = null;


                            Class[] params = {sectionClass.getSuperclass()};
                            try {
                                Constructor<?> constructor = sectionClass.getConstructor(params);
                                doc = (SectionDocument) constructor.newInstance(o);

                            } catch (Exception ex) {
                                Logger.getLogger(HRFFileSystemSerializer.class.getName()).log(Level.SEVERE, null, ex);
                                // better find a more suitable exception
                                throw new ExtensionMissingException();
                            }

                            String rawid = sectionContent[j].getName();

                            doc.setDocumentId(rawid.substring(0, rawid.length() -4)); 

                            section.addSectionDocument(doc);

                        }
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

    private void serializeSection(List<Section> sectionsList, File section) throws IOException, JAXBException {
        for (Section i : sectionsList) {
            File secFile = new File(section, i.getPath());
            secFile.mkdir();
            for (SectionDocument sd : i.getDocuments().values()) {
                File docFile = new File(secFile, sd.getDocumentId() + ".xml");
                docFile.createNewFile();
                FileOutputStream fosDoc = new FileOutputStream(docFile);
                fosDoc.write(((ByteArrayOutputStream) sd.marshall()).toByteArray());
                fosDoc.close();
            }

            serializeSection(i.getSections(), secFile);

        }
    }
    // </editor-fold>
}
