
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

import java.io.File;
import java.util.ResourceBundle;
import static co.spillikin.tools.eclipse.editortabs.Constants.EXPORT_MESSAGE_KEY;
import static co.spillikin.tools.eclipse.editortabs.Constants.EXPORT_TITLE_KEY;
import static co.spillikin.tools.eclipse.editortabs.Constants.FILENAME;
import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.FailsafeUtil;

/**
 * Export function
 *
 */
public class ExportHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {

        // The init util will display needed errors to the user if it fails.
        // Init all handlers like this.
        FailsafeUtil iu = null;
        try {
            iu = FailsafeUtil.getInstance(s);
        } catch (Exception e) {
            throw new ExecutionException(e.getMessage());
        }
        ResourceBundle bundle = iu.getPluginContainer().getResourceBundle();
        DataUtil fgData = iu.getDataContainer();

        DirectoryDialog fileDialog = new DirectoryDialog(s);
        fileDialog.setText(bundle.getString(EXPORT_TITLE_KEY));
        fileDialog.setMessage(bundle.getString(EXPORT_MESSAGE_KEY));
        String path = fileDialog.open();
        if (path != null) {
            String fullPath = path + File.separator + FILENAME;
            System.out.println(fullPath);
            fgData.export(fullPath);
        }
        

    }

}
