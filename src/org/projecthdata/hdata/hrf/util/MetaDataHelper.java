/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.projecthdata.hdata.hrf.util;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.DOMOutputter;
import org.projecthdata.hdata.hrf.HRFException;
import org.projecthdata.hdata.hrf.SectionDocument;
import org.projecthdata.hdata.schemas._2009._11.metadata.DocumentMetaData;
import org.projecthdata.hdata.schemas._2009._11.metadata.LinkInfo;
import org.w3c.dom.Document;

/**
 *
 * @author GBEUCHELT
 */
public class MetaDataHelper {

    private static DefaultJDOMFactory jdf = new DefaultJDOMFactory();
    private static DOMOutputter domOut = new DOMOutputter();
    private static DOMBuilder domBuild = new DOMBuilder();
    private static MetaDataHelper instance = null;
    private final JAXBContext ctx;
    private final Unmarshaller mdu;
    private final Marshaller m;
    private final DocumentBuilder db;

    private MetaDataHelper() throws JAXBException, ParserConfigurationException {
        ctx = JAXBContext.newInstance(DocumentMetaData.class);

        mdu = ctx.createUnmarshaller();

        m = ctx.createMarshaller();
        m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        m.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        db = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    }

    public static DocumentMetaData convertMetaData(Element element) throws HRFException {
        if (!initialize()) {
            throw new HRFException("Cannot create MetaDataHelper instance.");
        }

        DocumentMetaData result = null;

        org.jdom.Document mdJdom = jdf.document(element);
        try {
            try {
                result = (DocumentMetaData) instance.mdu.unmarshal(domOut.output(mdJdom));
            } catch (JDOMException ex) {
                Logger.getLogger(MetaDataHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (JAXBException ex) {
            Logger.getLogger(MetaDataHelper.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }

    public static Element convertMetaData(DocumentMetaData metadata) throws HRFException {
        if (!initialize()) {
            throw new HRFException("Cannot create MetaDataHelper instance.");
        }

        Element result = null;

        Document metadataDom = instance.db.newDocument();
        try {
            instance.m.marshal(metadata, metadataDom);
        } catch (JAXBException ex) {
            Logger.getLogger(MetaDataHelper.class.getName()).log(Level.SEVERE, null, ex);
            throw new HRFException(ex);
        }

        result = domBuild.build(metadataDom.getDocumentElement());

        return result;
    }

    public static SyndFeed createMetadataFeed(Collection<SectionDocument> sectionDocuments) throws HRFException {

        SyndFeed result = new SyndFeedImpl();
        result.setFeedType("atom_1.0");

        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        for (SectionDocument sd : sectionDocuments) {


            SyndEntry entry = new SyndEntryImpl();
            configureFeedEntry(entry, sd.getDocumentMetaData());
            entries.add(entry);
        }

        result.setEntries(entries);

        return result;
    }

    public static HashMap<String, DocumentMetaData> parseFeed(SyndFeed feed) throws HRFException {
        HashMap<String, DocumentMetaData> result = new HashMap<String, DocumentMetaData>();

        for (Object e : feed.getEntries()) {

            DocumentMetaData t = MetaDataHelper.convertMetaData(((List<Element>) ((SyndEntry) e).getForeignMarkup()).get(0));
            result.put(t.getDocumentId(), t);
        }

        return result;

    }

    public static void configureFeedEntry(SyndEntry entry, DocumentMetaData metadata) throws HRFException {
        List<Element> mdList = new ArrayList<Element>();
        try {
            mdList.add(convertMetaData(metadata));
        } catch (HRFException ex) {
            Logger.getLogger(MetaDataHelper.class.getName()).log(Level.SEVERE, null, ex);
            throw new HRFException(ex);
        }

        entry.setTitle(metadata.getDocumentId());
        entry.setForeignMarkup(mdList);

        if (metadata.getLinkedDocuments() != null
                && ! metadata.getLinkedDocuments().getLink().isEmpty()) {
            for (LinkInfo i : metadata.getLinkedDocuments().getLink()) {
                entry.setLink(i.getTarget());
            }
        }

        if (metadata.getRecordDate() != null) {
            entry.setPublishedDate(DateConverter.getUtilsDateFromXMLDate(metadata.getRecordDate().getCreatedDateTime()));
        }

    }

    private static synchronized boolean initialize() {
        if (instance == null) {
            try {
                instance = new MetaDataHelper();
            } catch (Exception ex) {
                Logger.getLogger(MetaDataHelper.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }
        return true;
    }
}
