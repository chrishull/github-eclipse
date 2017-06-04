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

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.InitializerUtil;
import co.spillikin.tools.eclipse.editortabs.util.PluginUtil;

/**
 * Post a dialog allowing the user to delete the current group.
 * @author chris
 *
 * Note.  In the E3 world, Handlers used to look like this...
 * MyHandler extends AbstractHandler
 * @override
 * public Object execute(ExecutionEvent event) throws ExecutionException {
 *
 */
public class DeleteHandler {

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

        // Business logic.  Allow user to delete current group.
        // Get name of current group (or null).
        String currentSession = fgData.getCurrentGroup();

        // If there is no current name, alert and exit.
        if (currentSession == null) {
            MessageDialog.openInformation(s, "No Currently Active Session",
                "You must first select a session in order to delete it.");
            return;
        }

        // create a dialog with ok and cancel buttons and a question icon
        boolean result = MessageDialog.openConfirm(s, "Delete Current Session",
            "You are about to delete the " + " session: " + currentSession
                + "  Are you sure you want to do this?");

        if (result) {
            fgData.deleteCurrentGroup();
            MessageDialog.openInformation(s, "Session Deleted",
                "The session has been deleted. The set of editor tabs has no " + 
            "session name. If you want this session saved go to Create Session.");
        }

        fgData.save();
    }

}
