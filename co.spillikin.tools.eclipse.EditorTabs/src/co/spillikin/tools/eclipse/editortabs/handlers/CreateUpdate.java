/**
 * Eclipse Editor Sessions manager feature plugin.
 * A plugin designed to allow users to save, restore and manage 
 * working and reference sets of files in the Eclipse IDE.
 * 
 * Written by Christopher Hull - 2017
 * http://www.chrishull.com
 * http://www.spillikinaerospace.com
 * chrishull42@gmail.com
 */
package co.spillikin.tools.eclipse.editortabs.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.FileInfo;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;
import co.spillikin.tools.eclipse.editortabs.ui.CreateUpdateDialog;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.FailsafeUtil;
import co.spillikin.tools.eclipse.editortabs.util.PluginUtil;

import static co.spillikin.tools.eclipse.editortabs.Constants.*;

/**
 * Create new and update existing are so similar that they share this code
 * Create takes defaults from the existing session and makes a new 
 * dialog allowing the user to make a "copy of" session by default.
 * Update does the same, except it only modified the current session.
 * 
 * I could have made this a static, but I might want to keep some state
 * around in the future.
 * 
 * @author chris
 *
 */
public class CreateUpdate {

    private Boolean create;

    /**
     * Create an instance of this utility.
     * @param create  Set to true to make a new session, false
     * to simply modify the current one if there is a current one.
     */
    public CreateUpdate(Boolean create) {
        this.create = create;
    }

    /**
     * Intended to be called by the CreateHandler or UpdateHandler's
     * execute method.  
     * 
     * @param s
     * @throws ExecutionException
     */
    public void execute(Shell s) throws ExecutionException {

        // The init util will display needed errors to the user if it fails.
        // Init all handlers like this.
        FailsafeUtil iu = null;
        try {
            iu = FailsafeUtil.getInstance(s);
        } catch (Exception e) {
            throw new ExecutionException(e.getMessage());
        }
        PluginUtil plugin = iu.getPluginContainer();
        DataUtil pluginData = iu.getDataContainer();

        // Get the list of currently open tabs.
        List<FileInfo> newList = plugin.getOpenFileList();

        // Business logic for this handler.
        // If create and no open tabs, alert and exit.
        if (create && (newList.size() == 0)) {
            MessageDialog.openInformation(s, plugin.getResourceString(INFO_NO_OPEN_TITLE_KEY),
                plugin.getResourceString(INFO_NO_OPEN_MESSAGE_KEY));
            return;
        }
        // Create a new or Update an existing session.
        // First, get the current session if it exists.
        SessionMap sessionMap = pluginData.getSessionMap();
        EditorSession session = sessionMap.getCurrentEditorSession();
        String currentSessionName = sessionMap.getCurrentSessionName();

        // If creating we need to show the list of open tabs as a popup
        // else we show number of tabs witin session being updated.
        List<String> tabNames = new ArrayList<>();

        if (create) {
            for (FileInfo fi : newList) {
                tabNames.add(fi.getFileName());
            }
        } else {
            for (FileInfo fi : session.getFileInfoList()) {
                tabNames.add(fi.getFileName());
            }

        }

        // Determine how to put up our dialog.
        CreateUpdateDialog dialog = null;
        ResourceBundle bundle = plugin.getResourceBundle();
        // If the name is not null, create dialog with default settings
        // that are a copy of the current session.
        if (currentSessionName != null) {

            // session will likewise not be null.
            // Create a new copy, or Update an existing session.
            dialog = new CreateUpdateDialog(s, bundle, tabNames, create, currentSessionName,
                session.getIsSnapshot(), session.getKeepAlphabetical(),
                session.numToBeAdded(newList), session.numToBeDeleted(newList),
                session.isIdentical(newList));
            // otherwise new session is not a copy.
        } else {
            // If we are updating and there is no currently selected session
            // then alert and exit
            if (create == false) {
                MessageDialog.openError(s, plugin.getResourceString(ERROR_NO_CURRENT_TITLE_KEY),
                    plugin.getResourceString(ERROR_NO_CURRENT_MESSAGE_KEY));
                return;
            }
            // Create a new session. There is no default so not copying.
            dialog = new CreateUpdateDialog(s, plugin.getResourceBundle(), tabNames);
        }
        dialog.create();

        // Process dialog results.
        // If Ok pushed, fill in data from dialog.
        if (dialog.open() == Window.OK) {
            // If we are creating a new group
            if (create) {
                String newSessionName = dialog.getSessionName();
                // User provided an empty string.
                if (newSessionName.length() == 0) {
                    MessageDialog.openError(s, plugin.getResourceString(ERROR_BLANK_NAME_TITLE_KEY),
                        plugin.getResourceString(ERROR_BLANK_NAME_MESSAGE_KEY));
                    return;
                }

                // If this new session name is in use then error and return.
                if (sessionMap.isNameInUse(newSessionName)) {
                    MessageDialog.openError(s,
                        plugin.getResourceString(ERROR_DUPLICATE_NAME_TITLE_KEY),
                        plugin.getResourceString(ERROR_DUPLICATE_NAME_MESSAGE_KEY));
                    return;
                }
                // Create a new group
                // Create the new group space and fill in it's data.
                sessionMap.switchEditorSession(newSessionName);
                session = sessionMap.getCurrentEditorSession();
                session.createEditorSessionData(dialog.getKeepAlphabetical(),
                    dialog.getIsSnapshot(), plugin.getOpenFileList(), plugin.getSelectedEditor());

                // If we want to save only, then switch back to previous session.
                if (dialog.getSaveonly()) {
                    sessionMap.switchEditorSession(currentSessionName);
                    // we want to switch to the new group remember previous group.
                } else {
                    sessionMap.setPreviousSessionName(currentSessionName);
                }

                // Display the new session name dialog.
                // It could be NULL if we are not switching to a new session.
                newSessionName = sessionMap.getCurrentSessionName();
                if (newSessionName == null) {
                    newSessionName = bundle.getString(SESSION_NAME_IF_NULL_FOR_DISPLAY_KEY);
                }
                MessageDialog.openInformation(s,
                    plugin.getResourceString(INFO_NEW_SESSION_TITLE_KEY),
                    plugin.getResourceString(INFO_NEW_SESSION_MESSAGE_KEY) + newSessionName);

                // If we are modifying the current session
                // We actually delete the current one and replace it.
            } else {
                String replacementSessionName = dialog.getSessionName();
                // User provided an empty string.
                if (replacementSessionName.length() == 0) {
                    MessageDialog.openError(s, plugin.getResourceString(ERROR_BLANK_NAME_TITLE_KEY),
                        plugin.getResourceString(ERROR_BLANK_NAME_MESSAGE_KEY));
                    return;
                }

                // If this replacementSessionName session name is in use 
                // then error and return.
                // If we didn't change the name then DO NOT run this check.
                if (!currentSessionName.equals(replacementSessionName)) {
                    if (sessionMap.isNameInUse(replacementSessionName)) {
                        MessageDialog.openError(s,
                            plugin.getResourceString(ERROR_DUPLICATE_NAME_TITLE_KEY),
                            plugin.getResourceString(ERROR_DUPLICATE_NAME_MESSAGE_KEY));
                        return;
                    }
                }
                // To "update" we actually delete and recreate
                // First, save off the file list from the current group
                session = sessionMap.getCurrentEditorSession();
                List<FileInfo> originalFiles = session.getFileInfoList();
                // Delete the current group, session is now orphaned.
                sessionMap.deleteCurrentEditorSession();
                // Create a new group with the new name and fill with data.
                sessionMap.switchEditorSession(replacementSessionName);
                session = sessionMap.getCurrentEditorSession();
                // Finally Update.  See method and unit test for details.
                session.updateEditorSessionData(dialog.getKeepAlphabetical(),
                    dialog.getIsSnapshot(), originalFiles, newList, dialog.getUpdateSnapshot(),
                    plugin.getSelectedEditor());

                // If the old group name is the same as previous group then
                // set previous group to this new name
                String prevName = sessionMap.getPreviousSessionName();
                if (prevName != null) {
                    if (currentSessionName.equals(prevName)) {
                        sessionMap.setPreviousSessionName(replacementSessionName);
                    }
                }

                // Show settings changed.
                MessageDialog.openInformation(s,
                    plugin.getResourceString(INFO_CHANGED_SETTINGS_TITLE_KEY),
                    plugin.getResourceString(INFO_CHANGED_SETTINGS_MESSAGE_KEY)
                        + replacementSessionName);

            }
        }

        // save to disk.
        pluginData.save();
    }

}
