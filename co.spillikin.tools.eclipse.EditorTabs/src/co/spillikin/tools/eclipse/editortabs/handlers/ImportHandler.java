
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

import static co.spillikin.tools.eclipse.editortabs.Constants.IMPORT_TITLE_KEY;
import java.util.ResourceBundle;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.FailsafeUtil;

/**
 * Export function
 *
 */
public class ImportHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {

        // The init util will display needed errors to the user if it fails.
        // Init all handlers like this. DataModel and Plugin guaranteed to be 
        // valid if this passes.
        FailsafeUtil iu = null;
        try {
            iu = FailsafeUtil.getInstance(s);
        } catch (Exception e) {
            throw new ExecutionException(e.getMessage());
        }
        ResourceBundle bundle = iu.getPluginContainer().getResourceBundle();

        FileDialog fileDialog = new FileDialog(s);
        fileDialog.setText(bundle.getString(IMPORT_TITLE_KEY));
        String fullPath = fileDialog.open();
        if (fullPath != null) {
            // Reinitialize our DataContainer.  Let it try to load our XML file.
            String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation()
                .toString();
            // Like above, if this fails alerts will be posted to the user.
            try {
                iu = FailsafeUtil.importData(s, workspacePath, fullPath,
                    iu.getDataContainer());
            } catch (TabsPluginException e) {
                return;
            }

        }
        // save state just imported.
        iu.getDataContainer().save();
    }

}
