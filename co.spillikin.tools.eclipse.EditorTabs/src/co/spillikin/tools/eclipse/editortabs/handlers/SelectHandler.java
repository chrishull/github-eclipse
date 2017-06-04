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
package co.spillikin.tools.eclipse.editortabs.handlers;

import java.util.List;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.FileInfo;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;
import co.spillikin.tools.eclipse.editortabs.ui.SelectGroupDialog;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.InitializerUtil;
import co.spillikin.tools.eclipse.editortabs.util.PluginUtil;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import static co.spillikin.tools.eclipse.editortabs.Constants.*;

/**
 * The user has selected the SelectGroup popup menu item.  
 * Bring up a dialog and allow the user to select a different 
 * tab group.
 * @author chris
 *
 */
public class SelectHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {

        // The init util will display needed errors to the user if it fails.
        // Init all handlers like this.
        InitializerUtil iu = null;
        try {
            iu = InitializerUtil.getInstance(s);
        } catch (Exception e) {
            return;
        }
        PluginUtil plugin = iu.getPluginContainer();
        DataUtil fgData = iu.getDataContainer();

        // Business logic.  Allow user to select a new group from drop down.
        String[] groupList = fgData.getGroupNames();
        // If there are no groups yet, alert and exit.
        if (groupList.length == 0) {
            MessageDialog.openInformation(s, plugin.getResourceString(INFO_NO_SESSIONS_TITLE_KEY),
                plugin.getResourceString(INFO_NO_SESSIONS_MESSAGE_KEY));
            return;
        }

        // If there is no current group warn the user that tabs are about to be lost.
        if (fgData.getCurrentGroup() == null) {

            boolean result = MessageDialog.openConfirm(s,
                plugin.getResourceString(WARN_NO_CURRENT_TITLE_KEY),
                plugin.getResourceString(WARN_NO_CURRENT_MESSAGE_KEY));

            if (!result) {
                return;
            }
        }
        // Create dialog.
        // The dialog needs the entire session map so it can update
        // the other associated settings (checkboxes) and show current states.
        // The dialog will NOT modify the session map
        SelectGroupDialog dialog = new SelectGroupDialog(s, plugin.getResourceBundle(),
            fgData.getSessionMap());
        dialog.create();
        // Open will return with OK pressed or not.
        // If the user clecked OK then process
        // Switch to the new session and update it's state with 
        // other choices made by the user.
        if (dialog.open() == Window.OK) {
            // Close all the tabs
            plugin.closeAllEditors();
            String selectedName = dialog.getSelectedSessionName();
            // Get the session map
            SessionMap sessionMap = fgData.getSessionMap();
            // Set previous to current
            sessionMap.setPreviousSessionName(sessionMap.getCurrentSessionName());
            // Switch to the new editor session
            sessionMap.switchEditorSession(selectedName);
            // Get it from the map and set it's data.  See javadoc for details.
            EditorSession session = sessionMap.getCurrentEditorSession();
            session.updateEditorSessionButtons( dialog.getKeepAlphabetical(),  
                dialog.getIsSnapshot());
            // Open all the files associated with the session.
            plugin.openFileList( session.getFileInfoList() );
            // Finally show the new session dialog.
            MessageDialog.openInformation(s,
                plugin.getResourceString(INFO_SELECTED_SESSION_TITLE_KEY),
                plugin.getResourceString(INFO_SELECTED_SESSION_MESSAGE_KEY) + selectedName);
        }

        // Save data model to disk.
        fgData.save();
    }

}
