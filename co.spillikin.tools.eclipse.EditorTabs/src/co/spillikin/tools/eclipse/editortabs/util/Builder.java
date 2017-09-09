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

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
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
        save(sessionsData, "editordatatest.xml");
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
     * Retrieve our data from an XML file.  This function is called 
     * when the plugin is initializing and does not have a face.  So if we
     * get an exception we store it in the return struct EditorSessionsData.
     * This will be picked up later and displayed to the user when they try to 
     * use the plugin.
     * 
     * @param filePath
     * @return  EditorSessionsData, will not be null.  Check for error
     * status whenever this object is retrieved for use.
     */
    public static EditorSessionsData load(String filePath) {

        EditorSessionsData sessionsData = null;
        try {

            File file = new File(filePath);
            // If the file does not exist, simply return NULL.
            if (!file.exists()) {
                return new EditorSessionsData();
            }

            // Use Impl.  (BTW, JAXB doesn't handle interfaces.)
            JAXBContext jaxbContext = JAXBContext.newInstance(EditorSessionsData.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            sessionsData = (EditorSessionsData) jaxbUnmarshaller.unmarshal(file);

            // If Anything bad happens, catch and stash
        } catch (Throwable t) {
            EditorSessionsData eData = new EditorSessionsData();

            eData.setDataException(new TabsPluginException(t));
            return eData;

        }
        return sessionsData;
    }

}
