/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf;

import java.io.OutputStream;
import javax.xml.bind.JAXBException;

/**
 * Interface for objects that may be contained in a Section.
 * @author GBEUCHELT
 */
public interface SectionDocument {


    /**
     * Method to serialized the object into an OutputStream.
     * @return
     * @throws JAXBException
     */
    public OutputStream marshall() throws JAXBException;

    /**
     * A unique document id, that will be used for URL contruction and as an
     * identifier. To ensure interoperability, this String should not contains white
     * spaces and other characters that can not be represented by simple ASCII characters.
     * @return
     */
    public String getDocumentId();

    /**
     * This sets a unique documentid for a given SectionDocument. Note that this id is
     * not globally unique or even stable across different implementations.
     * @param documentid A unique id for this document
     * 
     */
    public void setDocumentId(String documentid);

}
