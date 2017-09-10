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

import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
import co.spillikin.tools.eclipse.editortabs.model.EditorSession;

/**
 * Makes sure everything is in good working order.
 * If not, an alert is posted and an exception is thrown.
 * Called by everything with a user interface, so not the Activator.
 * The Activator will start things up.  If something went wrong, the 
 * user will find out when the UI calls FailsafeUtil to get needed
 * data.
 * 
 * This is the only legal or permissible way to get state.
 * 
 * Simple use pattern - try (getInstance) catch (exit) all clear.
 * 
 * @author chris
 *
 */
public class FailsafeUtil {

    private static FailsafeUtil instance = null;

    /**
     * Used by all top level handlers.
     * 
     * This is called by all hanlders to acquire the plugin and data 
     * (unless we are importing new data).
     * It does have a user interface, in that it will post alerts 
     * when things go wrong, so don't call this in the activator or
     * when the Eclipse UI state is unknown.
     * 
     * You can access the plugin, data utils and data model from 
     * here once this has run successfully. It also updates 
     * the current session.
     * 
     * TODO Add some unit tests.
     * 
     * @param s Shell
     * @return An instance of FailsafeUtil from which you can call
     *   getPluginContainer() and getDataContainer()
     * @throws TabsPluginException.
     * As the user is alerted, you can choose to bury this exception and 
     * exit immediately.
     */
    public static FailsafeUtil getInstance(Shell s) throws TabsPluginException {
        // Will post alert if error and throw TabsPluginException
        if (instance == null) {
            instance = new FailsafeUtil(s);
        }

        try {
            // always update the current tab group if there is one.
            // This will ignore if in snapshot mode.
            EditorSession editorSession = instance.fgData.getSessionMap().getCurrentEditorSession();
            // does nothing if in snapshot mode.
            if (editorSession != null) {
                editorSession.updateFilePathList(instance.plugin.getOpenFileList(),
                    instance.plugin.getSelectedEditor());
            }
        } catch (Exception e) {
            // Post error.
            PluginUtil.postAndLogException(s, e);
            throw new TabsPluginException(e);
        }

        return instance;
    }

    /**
     * Used when importing new data. 
     * 
     * Reread the data model based on an external file, then reinitializes the plugin.
     * This too may post dialogs if something goes wrong.
     * 
     * What we do here is reinitialize DataUtil (which contains the data model) 
     * with the imported file by calling DataUtil.importData.  This swaps in
     * the new data.  Then we null out instance and reinitialize ourselves by using the 
     * normal handler init above.
     * 
     * @param s Shell
     * @param workspaceName  Path to the current workspace.
     * @param dataFilePath  Path to the file being imported.
     * @param previous data util which contains items that need to be
     * swapped back in after import is done.
     * @return FailsafeUtil
     * @throws TabsPluginException.
     * User is alreted if something goes wrong so this exception can be buried.
     */
    public static FailsafeUtil importData(Shell s, String workspaceName, String dataFilePath,
        DataUtil oldUtil) throws TabsPluginException {

        try {
            DataUtil.importData(workspaceName, dataFilePath, oldUtil);
            instance = null;
        } catch (Exception e) {
            // Post error.
            instance = null;
            PluginUtil.postAndLogException(s, e);
            throw new TabsPluginException(e);
        }

        return FailsafeUtil.getInstance(s);
    }

    private PluginUtil plugin = null;
    private DataUtil fgData = null;

    /**
     * First thing called by every handler.execute(Shell s);
     * Plugin and Data are always initialized if called.  Null not checked.
     * We need this for importData.
     * 
     * TODO
     * Add unit tests for resource keys, possibly more.  Try to make sure
     * the plugin is as close to a can't fail state as possible before
     * giving the all clear.  Some tests will be one time.
     * 
     * Initialize all handlers by calling this constructor and retrieving
     * the needed contexts.   They are guaranteed to not be NULL if
     * an exception is not thrown.
     * If an exception is thrown, alerts have already been posted, 
     * just return from the handler.
     */
    private FailsafeUtil(Shell s) throws TabsPluginException {

        // Always open handler calls like this.  Get plugin and data.
        // First try to get our plugin context.
        // Throwables repackaged as TabsPluginException by both inits.
        try {
            plugin = PluginUtil.getInstance();
        } catch (TabsPluginException e) {
            // Post an error with hard coded strings.  We can't get our resource.
            PluginUtil.postAndLogException(s, e);
            throw new TabsPluginException(e);
        }
        // Then try to get our data model.  Should certainly work but 
        // keep try catch here.
        try {
            fgData = DataUtil.getInstance();
        } catch (TabsPluginException e) {
            PluginUtil.postAndLogException(s, e);
            throw new TabsPluginException(e);
        }

    }

    public PluginUtil getPluginContainer() {
        return plugin;
    }

    public DataUtil getDataContainer() {
        return fgData;
    }

}
