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
package org.mitre.hdata.hrf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.mitre.hdata.hrf.util.DeepCopy;
import org.projecthdata.hdata.schemas._2009._06.core.Extension;
import org.projecthdata.hdata.schemas._2009._06.core.Root;
import org.projecthdata.hdata.schemas._2009._06.core.Sections;

/**
 * This class is used to reference the entire HRF document hierarchy and is used
 * to manage HRFs. Use the HRFFactory to obtain a valid instantiation.
 *
 * @author GBEUCHELT
 */
public abstract class HRF {

    /**
     * Version of this HRF implementation
     */
    public static final String HRF_VERSION = "0.1";
    /**
     * Identifier for Mandatory Extensions
     */
    public static final String EXTENSION_REQUIREMENT_MANDATORY = "mandatory";
    /**
     * Identifier for Optional Extensions
     */
    public static final String EXTENSION_REQUIREMENT_OPTIONAL = "optional";
    // <editor-fold defaultstate="collapsed" desc="Private fields">
    // the Root element of the HRF hierarchy
    protected Root root;
    // an internal list of all absolute (!) sections paths
    private ArrayList<String> sectionPaths = new ArrayList<String>();
    private HashMap<String, Extension> extensionMap = new HashMap<String, Extension>();

    // </editor-fold>
    /**
     * This method adds an extension to the root document and indicates its requirement level. 
     * Extensions MUST be added before any Section can be registered with Section.typeid. 
     * 
     * @param typeid A string that can be converted in to a URN that uniquely identified the 
     * type of the documents that are to be added. 
     * @param requirement Indicate the requirement level for parsers.
     * @return null, if no extension was created, or a reference to the Extension that was registered.
     * If the same Extension-identified by the typeid-was registered before, no new Extension will be added.
     */
    public Extension addExtension(String typeid, String requirement) {

        Extension e = null;
        if (requirement.equals(EXTENSION_REQUIREMENT_MANDATORY) || requirement.equals(EXTENSION_REQUIREMENT_OPTIONAL)) {
            if (!extensionMap.containsKey(typeid)) {
                e = new Extension();

                e.setContent(typeid);
                e.setRequirement(requirement);

                extensionMap.put(typeid, e);
                root.getExtensions().getExtension().add(e);
            } else {
                Extension existingExtension = extensionMap.get(typeid);
                if (existingExtension.getRequirement().equals(EXTENSION_REQUIREMENT_OPTIONAL)) {
                    existingExtension.setRequirement(requirement);
                }

                e = existingExtension;
            }
        }

        return e;
    }

    /**
     * Returns an Extension object, identified by its typeid.
     * @param typeid A URN string identifying the Extension
     * @return The registered Extension, or null if no Extension with typeid was registered.
     */
    public Extension getExtension(String typeid) {
        return extensionMap.get(typeid);
    }

    /**
     * Get a list of all registered Extensions
     * @return List of all registered Extensions. 
     */
    public List<Extension> getExtensions() {
        return root.getExtensions().getExtension();
    }

    

    /**
     * Remove a registered Extension.
     * @param extension A reference to the Extension that need to be removed.
     * @return True if the Extension was registered and removed, false otherwise.
     */
    public boolean removeExtension(Extension extension) {
        return root.getExtensions().getExtension().remove(extensionMap.remove(extension.getContent()));

    }

    /**
     * Return a deep copy of the Root document of this HRF.
     * @return A deep copy of the Root document.
     */
    public Root getRoot() {
        return (Root) DeepCopy.copy(root);
    }

    /**
     * Adds a new subsection at parentpath. Note that this operation is idempotent, so adding a
     * section that already exists will have no effect. The secttion identity is determined by its uid
     * and by its full path. Note that for each new Section type - as identified by the id - 
     * a new extension for that id is also addeed. 
     *
     * This method will return null in two cases: either the parentpath does not exist, or there is a section
     * a different uid at parentpath + section.getPath().
     *
     * @param section The Section to be added to the root.
     * @param parentpath
     * @throws Throws a NullPointerException if either argument is null
     * @return Returns the full path to the newly added section. If the section was not added, returns null.
     */
    public String addSection(Section section, String parentpath) throws SectionPathExistsException {

        // the path to the section
        String result = null;

        // sanity checking
        if (section == null || parentpath == null) {
            throw new NullPointerException();
        }


        if (parentpath.equals("/")) {
            // adding a root section
            if (handleAddSection(parentpath, section)) {
                return null;
            }

            if (!root.getSections().getSection().contains(section)) {
                // sanity checking - idempotency
                for (org.projecthdata.hdata.schemas._2009._06.core.Section i : getRootSections()) {
                if (i.getPath().equals(section.getPath())) {
                    if (i.getTypeId().equals(section.getUriTypeId().toString())) {
                        return getRootSections().get(getRootSections().indexOf(section)).getPath();
                    } else {
                        throw new SectionPathExistsException(); 
                    }
                }
            }
                
                
                root.getSections().getSection().add(section);

            }

            result = parentpath + section.getPath();
        } else if (sectionPaths.contains(parentpath) && !parentpath.equals("")) {
            // adding a sub section

            StringTokenizer st = new StringTokenizer(parentpath, "/");
            Section parent = findSection(getRootSections(), "/" + st.nextToken());
            while (st.hasMoreTokens()) {
                parent = findSection(parent.getSections(), "/" + st.nextToken());
            }

            if (handleAddSection(parentpath + "/", section)) {
                return null;
            }

            parent.addSection(section);     // this call is idempotent

            result = parentpath + "/" + section.getPath();


        }
        if (result != null) {
            sectionPaths.add(result);
        }

        return result;


    }

    /**
     * Get a shallow copy of the child sections in the Root document.
     * @return List<Section> containing a shallow copy of the first level sections.
     */
    public List<Section> getRootSections() {

        ArrayList<Section> result = new ArrayList<Section>();
        try {
            root.getSections().getSection();
        } catch (NullPointerException npex) {
            root.setSections(new Sections());
        }

        for (org.projecthdata.hdata.schemas._2009._06.core.Section i : root.getSections().getSection()) {
            result.add((Section) i);
        }
        return result;
    }

    /**
     * This method is used to get a reference to a Section, as identified by its path. Sections can
     * be used to access SectionDocuments.
     * @param path The full path, including all ancestor path segments to the Section
     * @return Returns a reference to the Section.
     */
    public Section getSection(String path) {
        Section result = null;

        if (!sectionPaths.contains(path) || path.equals("/")) {
            return result;
        }

        StringTokenizer st = new StringTokenizer(path, "/");

        String rootSec = st.nextToken();
        for (Section i : getRootSections()) {
            if (i.getPath().equals(rootSec)) {
                result = i;
                break;
            }
        }

        while (st.hasMoreTokens()) {
            String pSegment = st.nextToken();
            for (Section i : result.getSections()) {
                if (i.getPath().equals(pSegment)) {
                    result = i;
                    break;
                }
            }


        }

        return result;
    }


    /**
     * Access to the paths for all registered Sections. These are the full paths, including
     * all path segments for the Sections' ancestors.
     * @return A List of all Section paths.
     */
    public List<String> getSectionPaths() {
        return sectionPaths;
    }

    /**
     * Method to marshall the Root document. Note that no SectionDocuments will be marshalled through
     * this method. For marshalling an entire HRF, use a customer HRF Serializer, such as the
     * HRFFileSystemSerializer.
     * @return An OutputStream containing the marshalled Root document.
     * @throws JAXBException
     */
    public OutputStream marshall() throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JAXBContext ctx = JAXBContext.newInstance(org.projecthdata.hdata.schemas._2009._06.core.Root.class);
        Marshaller m = ctx.createMarshaller();

        m.marshal(root, bos);

        return bos;
    }

    // <editor-fold defaultstate="collapsed" desc="Private helper methods">
    private boolean handleAddSection(String checkpath, Section section) {
        if (sectionPaths.contains(checkpath + section.getPath())) {
            // see if the section already exists
            Section child = findSection(this.getRootSections(), section.getPath());
            if (!child.getTypeId().equals(section.getTypeId())) {
                return true;
            }
            // get rid of the previously registered section - this way we
            // can update sections, and the method is idempotent
            root.getSections().getSection().remove(child);
        }

        boolean sectionInExtensionList = false;
        for (Extension i : root.getExtensions().getExtension()) {
            if (i.getContent().equals(section.getTypeId())) {
                sectionInExtensionList = true;
            }
        }
        if (!sectionInExtensionList) {
            addExtension(section.getTypeId(), EXTENSION_REQUIREMENT_MANDATORY);
        }

        return false;
    }

    private Section findSection(List<Section> sectionlist, String pathsegment) {
        Section result = null;

        for (Section i : sectionlist) {
            if (pathsegment.equals("/" + i.getPath())) {
                result = i;
                break;
            }
        }

        return result;
    }
    // </editor-fold>
}
