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
package org.mitre.hdata.hrf.patientinformation;

import org.mitre.hdata.hrf.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.projecthdata.hdata.schemas._2009._06.core.Address;
import org.projecthdata.hdata.schemas._2009._06.core.Name;
import org.projecthdata.hdata.schemas._2009._06.core.Telecom;


/**
 *
 * @author GBEUCHELT
 */
public class Patient extends org.projecthdata.hdata.schemas._2009._06.patient_information.Patient implements SectionDocument {

    
    public static final String TYPEID = "http://projecthdata.org/hdata/schemas/core/2009/09/patientinformation";
    private String documentid;

    public Patient() {
        super.name = new Name();
    }

    public Patient(org.projecthdata.hdata.schemas._2009._06.patient_information.Patient patient) {
        this.setId(patient.getId());
        this.setBirthPlace(patient.getBirthPlace());
        this.setBirthtime(patient.getBirthtime());
        this.setDescription(patient.getDescription());
        gender = patient.getGender(); 
        this.setGuardian(patient.getGuardian());
        this.setInformationSource(patient.getInformationSource());
        this.getLanguage().addAll(patient.getLanguage());
        this.getAddress().addAll(patient.getAddress());
        this.getTelecom().addAll(patient.getTelecom());
        this.setMaritalStatus(patient.getMaritalStatus());
        this.setName(patient.getName());
        this.getRace().addAll(patient.getRace());
        this.setDescription(patient.getDescription());
        this.setInformationSource(patient.getInformationSource());
        this.setGuardian(patient.getGuardian());

    }
   

    public void setAdministrativeGender(Gender gender) {
       super.setGender(gender.toXmlGender());
    }

    public Gender getAdministrativeGender() {
        return Gender.fromGender(super.getGender());
    }


    

    
    @Override
    public void setDocumentId(String documentid) {
        this.documentid = documentid;
    }

    @Override
    public List<Address> getAddress() {
        if (super.getAddress() == null) {
            super.address = new ArrayList<Address>();
        }

        return super.getAddress();
    }

    @Override
    public List<Telecom> getTelecom() {
        if (super.getTelecom() == null) {
            super.telecom = new ArrayList<Telecom>();
        }
        return super.getTelecom();
    }

  

    // </editor-fold>
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Patient)) {
            return false;
        }
        return ((Patient) obj).hashCode() == this.hashCode();

    }


    // <editor-fold defaultstate="collapsed" desc="Implementation of Interface methods">

    public int compareTo(Object o) {
        if (this.hashCode() == ((Patient) o).hashCode()) {
            return 0;
        } else {

            return this.getDocumentId().compareTo(((Patient)o).getDocumentId());
        }
    }

    public OutputStream marshall() throws JAXBException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        JAXBContext ctx = JAXBContext.newInstance(org.projecthdata.hdata.schemas._2009._06.patient_information.Patient.class);
        Marshaller m = ctx.createMarshaller();
        m.marshal(this, bos);

        return bos;
    }

    public String getDocumentId() {
        return documentid; 
    }
    // </editor-fold>

    public enum Gender {
        MALE ("M", "male"),
        FEMALE ("F", "female"),
        UNSPECIFIED ("UN", "unspecified");

        org.projecthdata.hdata.schemas._2009._06.patient_information.Gender g;

        Gender (String gender, String description) {
            g = new org.projecthdata.hdata.schemas._2009._06.patient_information.Gender();
            g.setCodeSystem("HL7");
            g.setCode(gender);
            g.setDisplayName(description);
        }

        static Gender fromGender(org.projecthdata.hdata.schemas._2009._06.patient_information.Gender gender) {
            if (gender.getCode().toUpperCase().equals("M")) {
                return Gender.MALE;
            } else if (gender.getCode().toUpperCase().equals("F")) {
                return Gender.FEMALE;
            } else if (gender.getCode().toUpperCase().equals("UN")) {
                return Gender.UNSPECIFIED;
            } else {
                throw new IllegalArgumentException();
            }
        }

        org.projecthdata.hdata.schemas._2009._06.patient_information.Gender toXmlGender() {
            return g;
        }

    }
}
