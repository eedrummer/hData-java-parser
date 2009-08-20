/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.mitre.hdata.hrf.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author bobd
 */
public class DateConverter {


    public static Date getUtilsDateFromXMLDate(XMLGregorianCalendar cal ){
         return  cal.toGregorianCalendar().getTime();
    }

    public static XMLGregorianCalendar getXLMDateFromUtilsDate(Date date) throws DatatypeConfigurationException{
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

}
