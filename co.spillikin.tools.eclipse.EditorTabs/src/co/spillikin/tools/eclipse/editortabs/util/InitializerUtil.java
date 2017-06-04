package co.spillikin.tools.eclipse.editortabs.util;

import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
import co.spillikin.tools.eclipse.editortabs.model.EditorSession;

/**
 * Performs the highst level init needed by every handler.
 * Simple use pattern 
 * 
 * @author chris
 *
 */
public class InitializerUtil {

    private static InitializerUtil instance = null;

    /**
     * This is called by all hanlders and runs only once 
     * (unless we are importing new data).
     * It does have a user interface, in that it will post alerts 
     * when things go wrong.  
     * You can access the plugin and data utils and the model from 
     * here once this has run successfully.
     * 
     * @param s
     * @return
     * @throws TabsPluginException
     */
    public static InitializerUtil getInstance(Shell s) throws TabsPluginException {
        if (instance == null) {
            instance = new InitializerUtil(s);
        }
        return instance;
    }

    /**
     * Used when importing new data. 
     * Reread the data model based on an external file, then reinits the plugin.
     * This too may post dialogs if something went wrong.
     * 
     * @param s
     * @param workspaceName
     * @param dataFilePath
     * @param previous data util.
     * @return
     * @throws TabsPluginException
     */
    public static InitializerUtil importData(Shell s, String workspaceName, 
        String dataFilePath, DataUtil oldUtil )
        throws TabsPluginException {

        DataUtil.importData(workspaceName, dataFilePath, oldUtil);
        instance = null;
        return InitializerUtil.getInstance(s);
    }

    private PluginUtil plugin = null;
    private DataUtil fgData = null;

    /**
     * First thing called by every handler.execute(Shell s);
     * Plugin and Data are always initialized if called.  Null not checked.
     * We need this for importData.
     * 
     * Initialize all handlers by calling this contructor and retrieving
     * the needed contexts.   They are guaranteed to not be NULL if
     * an exception is not thrown.
     * If an exception is thrown, alerts have already been posted, 
     * just return from the handler.
     */
    private InitializerUtil(Shell s) throws TabsPluginException {

        // Always open handler calls like this.  Get plugin and data.
        // First try to get our plugin context.
        try {
            plugin = PluginUtil.getInstance();
        } catch (TabsPluginException e) {
            PluginUtil.postFatalAlertStatic(s, e.getMessage());
            throw new TabsPluginException(e.getMessage());
        }
        // Then try to get our data model
        try {
            fgData = DataUtil.getInstance();
        } catch (TabsPluginException e) {
            plugin.postFatalErrorAlert(s, e.getMessage());
            throw new TabsPluginException(e.getMessage());
        }
        // always update the current tab group if there is one.
        EditorSession editorSession = fgData.getSessionMap().getCurrentEditorSession();
        // does nothing if in snapshot mode.
        if (editorSession != null) {
            editorSession.updateFilePathList(plugin.getOpenFileList());
        }
    }

    public PluginUtil getPluginContainer() {
        return plugin;
    }

    public DataUtil getDataContainer() {
        return fgData;
    }

}
