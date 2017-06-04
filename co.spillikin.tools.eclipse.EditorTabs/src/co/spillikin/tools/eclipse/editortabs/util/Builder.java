/**
 * Eclipse Editor Sessions plugin.
 * A plugin designed to allow users to save and manage working sets of
 * files in Eclipse.
 * 
 * Written by Christopher Hull
 * May 2017
 * http://www.chrishull.com
 * http://www.spillikinaerospace.com
 */
package co.spillikin.tools.eclipse.editortabs.util;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import co.spillikin.tools.eclipse.editortabs.model.EditorSessionsData;

/**
 * Static utility to marshall and unmarshall the data model
 * between XML and Java objects.
 * 
 * @author chris
 *
 */
public class Builder {

    /**
     * For testing, save to a hardcoded path.
     * @param editorData
     */
    public static void save(EditorSessionsData sessionsData) {
        save(sessionsData, "editordata.xml");
    }

    /**
     * Given our data and an absolute path to an XML file, save.
     * @param sessionsData
     * @param filePath
     */
    public static void save(EditorSessionsData sessionsData, String filePath) {
        try {

            File file = new File(filePath);
            // Make sure the context is the IMPL.
            // JABX doesn't handle interfaces.
            JAXBContext jaxbContext = JAXBContext.newInstance(EditorSessionsData.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(sessionsData, file);

        } catch (JAXBException e) {
            e.printStackTrace();
        }

    }

    /**
     * Retrieve our data from an XML file.
     * @param filePath
     * @return  If null came back something went wrong.
     * Remember, the first time thru there will be no data.
     */
    public static EditorSessionsData load(String filePath) {

        EditorSessionsData sessionsData = null;
        try {

            File file = new File(filePath);
            // Use Impl.  JAXB doesn't handle interfaces.
            JAXBContext jaxbContext = JAXBContext.newInstance(EditorSessionsData.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            sessionsData = (EditorSessionsData) jaxbUnmarshaller.unmarshal(file);

        } catch (JAXBException e) {
            // We land here the first time thru if there is no file.
            // This is ok.
            // System.out.println(e.getMessage());
            // e.printStackTrace();
        } catch (Exception e ) {
            // Something bad happened
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Throwable t) {
            // Something really bad happened
            System.out.println(t.getMessage());
            t.printStackTrace();
            return null;
        }
        return sessionsData;
    }

}
