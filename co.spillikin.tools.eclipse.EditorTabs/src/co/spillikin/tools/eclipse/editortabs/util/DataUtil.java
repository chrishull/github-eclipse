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

import java.util.Set;
import java.util.TreeSet;

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
import co.spillikin.tools.eclipse.editortabs.model.EditorSessionsData;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;

import static co.spillikin.tools.eclipse.editortabs.Constants.EX_PLUGIN_CANT_FIND_DATA;

/**
 * DataUtil.
 * A set of utility functions used to access and manipulate the 
 * data model.
 * 
 * @author chris
 *
 */
public class DataUtil {

    // Singleton
    private static DataUtil instance = null;

    /**
     * Called by the Activator during start.
     * 
     * Call this when the plugin starts up to set up our
     * data store (which won't exist the first time the
     * plugin is run.)  The store will not exist when the 
     * user switches workspaces as well.
     * 
     * @param workspaceName from the Activator (full path to worksspace)
     *   for instance, when running from within Eclipse, the secondary Eclipse
     *   workspace is at /Users/chris/projects/runtime-EclipseApplication
     *   This path is not used for file access.  Rather a unique ID for the workspace.
     * @param Full path to our XML file.
     * @return
     */
    public static DataUtil initialize(String workspaceName, String dataFilePath) {
        if (instance == null) {
            instance = new DataUtil(dataFilePath);
            // Set the current workspace name (not used, just to track things)
            instance.editorSessionData.setWorkspaceName(workspaceName);
        }
        return instance;
    }

    /**
     * USED BY the InitializationUtil during data import.
     * No one else should call this.
     * Used to import previously saved data.  Overwrites existing.
     * TODO Consider merging with existing.
     * @param workspaceName
     * @param dataFilePath for the file being imported.
     * @param the original DataUtil reference.
     * @return
     */
    static DataUtil importData(String workspaceName, String dataFilePath, DataUtil oldUtil) {
        instance = new DataUtil(dataFilePath, oldUtil);
        // Set the current workspace name (not used, just to track things)
        instance.editorSessionData.setWorkspaceName(workspaceName);
        return instance;
    }

    /**
     * For Testing Only
     * This is used only be the unit tests. It does not read or write a file.
     * Unit tests run this at setup and then access via getInstance();
     * 
     * I suppose I could have used dependency injection here, but the two code
     * paths are so simple that I think that would have been unnecessarily 
     * complicated in this case. Adding a test access to the data model 
     * is trivial.
     * 
     * @param workspaceName.  Some arbitrary fake workspace name.
     * @return
     */
    public static DataUtil unitTestInitialize(String workspaceName) {
        if (instance == null) {
            instance = new DataUtil(workspaceName);
        }
        return instance;
    }

    /**
     * For testing only.
     * Clears the instance after each test.
     */
    public static void unitTestClear() {
        instance = null;
    }

    /**
     * INITIALIZE must have been called first, done by the Accessor at plugin start.
     * See above.
     * Any part of the plugin can access state information here.
     * If the EditorSessionData layer got an error when trying to deserialize
     * then an exception will be thrown here.
     * 
     * @return DataContainer
     */
    public static DataUtil getInstance() throws TabsPluginException {

        // We should have been initialized by now.  
        // This gets caught by FailsafeUtil and posted as an error dialog.
        if (instance == null) {
            throw new TabsPluginException(EX_PLUGIN_CANT_FIND_DATA);
        }
        // If Accessor got an error when loading, throw
        TabsPluginException ex = instance.editorSessionData.getDataException();
        if (ex != null) {
            throw ex;

        }
        return instance;
    }

    // All of our JAXB marshaled data goes here.
    // This is all the data this container touches.
    private EditorSessionsData editorSessionData = null;

    // Full path to the XML data file to load/save
    private String dataFilePath = null;

    /**
     * Initialize this DataContainer singleton by loading
     * and instantiating the underlying EditorSessionsData.
     * Then set EditorSessionsData to work with whatever the
     * current Workspace is (because state is stored in workspace now).
     * 
     * This is possibly deserialized JAXB.  If no file exists 
     * (will happen the first time) an exception will take place
     * 
     * @param dataFilePath.  Full path.
     * @param importing.  If true we will not overwrite the dataFilePath 
     * in this object.
     */
    private DataUtil( String dataFilePath) {
        this.dataFilePath = dataFilePath;
        editorSessionData = Builder.load(dataFilePath);
        
    }

    /**
     * This is used for Importing data.  It assumes that a DataUtil 
     * was created by the Activator, and the user wished to import
     * a data file.  It will load that file, but then switch
     * back to the file location used to initialize in the first place.
     * @param workspaceName
     * @param dataFilePath
     * @param oldUtil
     */
    private DataUtil(String dataFilePath, DataUtil oldUtil) {
        editorSessionData = Builder.load(dataFilePath);
        this.dataFilePath = oldUtil.dataFilePath;
    }

    /**
     * Save all data associated with this DataContainer.
     */
    public void save() {
        Builder.save(editorSessionData, dataFilePath);
    }

    /**
     * Save all data associated with this DataContainer
     * to a place chosen by the user.
     */
    public void export(String fullPath) {
        Builder.save(editorSessionData, fullPath);
    }

    /**
     * Switch to a group, or create new if does not exist yet.
     * If that group does not yet exist, create space for it.
     * All subsequent adds will be made under that group.
     * 
     * @param group A group name.  May be new.
     */
    public void switchGroup(String sessionName) {
        SessionMap sessionMap = editorSessionData.getSessionMap();
        sessionMap.switchEditorSession(sessionName);
    }

    /**
     * Delete the current group if it exists.
     */
    public void deleteCurrentGroup() {
        SessionMap sessionMap = editorSessionData.getSessionMap();
        sessionMap.deleteCurrentEditorSession();
    }

    /**
     * Get an array of all the group names.
     * These should be sorted alphabetically.
     * @return String[] String array of all tab group names.
     */
    public String[] getGroupNames() {
        SessionMap sessionMap = editorSessionData.getSessionMap();
        Set<String> keys = sessionMap.getSessionNames();
        Set<String> orderedStringSet = new TreeSet<String>();
        for (String key : keys) {
            orderedStringSet.add(key.toString());
        }
        return (String[]) orderedStringSet.toArray(new String[0]);
    }

    /**
     * Get the currently selected tab group, or null
     * if none selected.
     * @return
     */
    public String getCurrentGroup() {
        SessionMap sessionMap = editorSessionData.getSessionMap();
        return sessionMap.getCurrentSessionName();
    }

    /**
     * Get the session map for the workspace we are running with.
     * 
     * @return The current session map for this workspace.
     */
    public SessionMap getSessionMap() {
        return editorSessionData.getSessionMap();
    }
}
