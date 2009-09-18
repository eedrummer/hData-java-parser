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
package org.mitre.hdata.hrf.adversereactions;

import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.mitre.hdata.hrf.SectionDocument;
import org.mitre.hdata.hrf.util.MarshallUtil;
import org.projecthdata.hdata.schemas._2009._06.core.CodedValue;

/**
 *
 * @author GBEUCHELT
 */
public class Allergy extends org.projecthdata.hdata.schemas._2009._06.allergy.Allergy implements SectionDocument {

    public static String TYPEID = "http://projecthdata.org/hdata/schemas/allergy/2009/06";
    private String documentid;

    public Allergy() {

    }

    public Allergy(org.projecthdata.hdata.schemas._2009._06.allergy.Allergy allergy) {
        this.setDate(allergy.getDate());
        this.setDescription(allergy.getDescription());
        this.setInformationSource(allergy.getInformationSource());
        this.setProduct(allergy.getProduct());
        this.setReaction(allergy.getReaction());
        this.setSeverity(allergy.getSeverity());
        this.setType(allergy.getType());
    }

    public void setUtilDate(Date date) {

        try {
            super.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(Allergy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Date getUtilDate() {
        return super.getDate().toGregorianCalendar().getTime();
    }

    public void setProduct(String name, String code, String codesystem) {

        CodedValue p = new CodedValue();

        p.setCode(code);
        p.setCodeSystem(codesystem);
        p.setDisplayName(name);

        //p.setCodedValue(cv);

        super.setProduct(p);
    }

    public void setReaction(String reaction) {
    }

    public void setSeverity(Severity severity) {

        super.setSeverity(severity.toXMLSeverity());
    }

    public void setType(Type type) {

        super.setType(type.toXMLType());
    }

    @Override
    public OutputStream marshall() throws JAXBException {
        return MarshallUtil.marshall(this, org.projecthdata.hdata.schemas._2009._06.allergy.Allergy.class);
    }

    public String getDocumentId() {
        return documentid;
    }

    public void setDocumentId(String documentid) {
        this.documentid = documentid;
    }

    public URI getTypeId() {
        try {
            return new URI(TYPEID);
        } catch (URISyntaxException ex) {
            Logger.getLogger(Allergy.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public enum Severity {

        MILD("mild", "255604002"),
        MILD_TO_MODERATE("mild to moderate", "371923003"),
        MODERATE("moderate", "6736007"),
        MODERATE_TO_SEVERE("moderate to severe", "371924009"),
        SEVERE("severe", "24484000"),
        FATAL("fatal", "399166001");
        org.projecthdata.hdata.schemas._2009._06.allergy.Severity s;

        Severity(String name, String code) {
            s = new org.projecthdata.hdata.schemas._2009._06.allergy.Severity();
            s.setCodeSystem("SNOMED CT");
            s.setCode(code);
            s.setDisplayName(name);
        }

        static Severity fromSeverity(org.projecthdata.hdata.schemas._2009._06.allergy.Severity sev) {

            return null;

        }

        org.projecthdata.hdata.schemas._2009._06.allergy.Severity toXMLSeverity() {
            return s;
        }
    }

    public enum Type {

        ADVERSE_REACTION("propensity to adverse reactions", "420134006"),
        ADVERSE_REACTION_SUBSTANCE("propensity to adverse reactions to substance", "418038007"),
        ADVERSE_REACTION_DRUG("propensity to adverse reactions to drug", "419511003"),
        ADVERSE_REACTION_FOOD("propensity to adverse reactions to food", "418471000"),
        ALLERGY_SUBSTANCE("allergy to substance", "419199007"),
        ALLERGY_DRUG("drug allergy", "416098002"),
        ALLERGY_FOOD("food allergy", "414285001"),
        INTOLERANCE_DRUG("drug intolerance", "59037007"),
        INTOLERANCE_FOOD("food intolerance", "235719002");
        org.projecthdata.hdata.schemas._2009._06.allergy.Type t;

        Type(String name, String code) {
            t = new org.projecthdata.hdata.schemas._2009._06.allergy.Type();
            t.setCodeSystem("SNOMED CT");
            t.setCode(code);
            t.setDisplayName(name);
        }

        org.projecthdata.hdata.schemas._2009._06.allergy.Type toXMLType() {
            return t;
        }
    }
}
