/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The Section may contain both SectionDocuments and other Sections. It is addressable
 * through the HRF.
 * @author GBEUCHELT
 */
public class Section extends org.projecthdata.hdata.schemas._2009._06.core.Section {

    private final HashMap<String, SectionDocument> objects
            = new HashMap<String, SectionDocument>();

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    public Section() {

    }

    public Section(String friendlyName, URI uid, String path) {
       super();
       this.setFriendlyName(friendlyName);
       this.setUriTypeId(uid);
       this.setPath(path);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters/Setters">

    /**
     * Returns a shallow copy of the HashMap containing the SectionDocuments of this Section.
     * @return Copy of the HashMap of all SectionDocuments.
     */
    public HashMap<String, SectionDocument> getDocuments() {
        return (HashMap<String, SectionDocument>) objects.clone();
    }

    public void setFriendlyName(String friendlyName) {
        this.setName(friendlyName);
        
    }

    public String getFriendlyName() {
        return this.getName();
    }

    public SectionDocument addSectionDocument(SectionDocument doc) {
        if (doc.getDocumentId()== null || doc.getDocumentId().equals("")) {
            doc.setDocumentId(UUID.randomUUID().toString());
        }
        return objects.put(doc.getDocumentId(), doc);
        
    }

    public SectionDocument removeSectionDocument(SectionDocument doc) {
         if (objects.containsValue(doc)) {
             return removeSectionDocument(doc.getDocumentId());
         } else {
             return null;
         }
    }

    public SectionDocument removeSectionDocument(String documentid) {
        return objects.remove(documentid);
    }

    /**
     * Convenience method that accepts URIs instead of simple Strings. Use of the
     * String based counterparts is highly discouraged.
     * @param typeid
     */
    public void setUriTypeId(URI typeid) {
        this.setTypeId(typeid.toString());
    }

    @Override
    @Deprecated
    public void setTypeId(String value) {
        super.setTypeId(value);
    }


    /**
     * Convenience method that returns a TypeID for this section in the form of an URI.
     * Use of the String counterparts is highly discouraged.
     * @return
     */
    public URI getUriTypeId() {
        try {
            return new URI(this.getTypeId());
        } catch (URISyntaxException ex) {
            Logger.getLogger(Section.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    @Deprecated
    public String getTypeId() {
        return super.getTypeId();
    }



    /**
     * Sets the path segment for this Section. Note that no namespace deconfliction
     * is provided at this time: Developers must ensure that Sections are uniquely named.
     * Refer to the hData Content Profiles for more information.
     * @param path Path segment without leading slashes ("/"). 
     */
    @Override
    public void setPath(String path) {
        if (path.contains("/")) {
            throw new IllegalArgumentException();
        }
        super.setPath(path);
        
    }

    /**
     * Returns the path segment of this Section.
     * @return
     */
    @Override
    public String getPath() {
        return super.getPath();
    }

    /**
     * This method return a shallow copy of the children of this section. Note
     * that any changes to this shallow copy (like an add() or similar) will not
     * be copied back to this list.
     *
     * @return The shallow copy of the section list.
     */
    public List<Section> getSections() {

        ArrayList<Section> result = new ArrayList<Section>();

        for (org.projecthdata.hdata.schemas._2009._06.core.Section i : super.getSection()) {
            result.add((Section) i);
        }
        
        return result;

    }

    @Override
    @Deprecated
    public List<org.projecthdata.hdata.schemas._2009._06.core.Section> getSection() {
        ArrayList<org.projecthdata.hdata.schemas._2009._06.core.Section> result
                = new ArrayList<org.projecthdata.hdata.schemas._2009._06.core.Section>();

        for(org.projecthdata.hdata.schemas._2009._06.core.Section i : super.getSection()) {
            result.add(i);
        }

        return result;
    }


   /**
    * Adds a new child section to this Section. If a Section with the same path and typeid already
    * exists, a refence to this existing Section is returned.
    * @param section The child Section to be added. Note that this Section must be fully initialized.
    * @return A reference to the Section.
    * @throws SectionPathExistsException This is thrown if a Section with the same Path already exists.
    */
    public Section addSection(Section section) throws SectionPathExistsException{
        if (! super.getSection().contains(section)) {
            //sanity checking - idempotency

            for (org.projecthdata.hdata.schemas._2009._06.core.Section i : super.getSection()) {
                if (i.getPath().equals(section.getPath())) {
                    if (i.getTypeId().equals(section.getUriTypeId().toString())) {
                        return (Section) super.getSection().get(super.getSection().indexOf(section));
                    } else {
                        throw new SectionPathExistsException();
                    }
                }
            }
            super.getSection().add(section);
        }
        return (Section) super.getSection().get(super.getSection().indexOf(section));
    }

    /**
     * Remove a child Section from this Section.
     * @param section A reference to the child Section that needs to be removed.
     * @return True, if the child section was contained in the Section list and sucessfully removed. 
     */
    public boolean removeSection(Section section) {
            if (super.getSection().contains(section)) {
                super.getSection().remove(section);

                return true;
            } else {
                return false;
            }
    }

    // </editor-fold>


}
