/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package hdatacore;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.mitre.hdata.hrf.HRF;
import org.mitre.hdata.hrf.HRFFactory;
import org.mitre.hdata.hrf.patientinformation.Patient;
import org.mitre.hdata.hrf.adversereactions.Allergy;
import org.mitre.hdata.hrf.ExtensionMissingException;
import org.mitre.hdata.hrf.serialization.HRFFileSystemSerializer;
import org.mitre.hdata.hrf.serialization.HRFSerialializationException;

/**
 *
 * @author GBEUCHELT
 */
public class Deserialize {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here


        try {
            HRF hrf = null;
            HRFFileSystemSerializer des = HRFFileSystemSerializer.getInstance();

            des.registerExtension(Patient.TYPEID, Patient.class);
            des.registerExtension("urn:empty", null);
            des.registerExtension(Allergy.TYPEID, Allergy.class);
            try {
                hrf = des.deserialize(new File("/tmp/hrf/patient"));
            } catch (HRFSerialializationException ex) {
                Logger.getLogger(Deserialize.class.getName()).log(Level.SEVERE, null, ex);
            }

            System.out.println();
        } catch (IOException ex) {
            Logger.getLogger(Deserialize.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExtensionMissingException ex) {
            Logger.getLogger(Deserialize.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
