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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.model.FileInfo;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.FailsafeUtil;
import co.spillikin.tools.eclipse.editortabs.util.PluginUtil;

/**
 * This handler will simply alphabetize the open tabs.
 * Perhaps the only function anyone will ever really use.  :-)
 * I guess if nothing is open it will pop up some obvious dialog.
 * 
 * @author chris
 *
 */
public class AlphaHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {

        // The init util will display needed errors to the user if it fails.
        // Init all handlers like this.
        FailsafeUtil iu = null;
        try {
            iu = FailsafeUtil.getInstance(s);
        } catch (Exception e) {
            return;
        }
        PluginUtil plugin = iu.getPluginContainer();
        DataUtil fgData = iu.getDataContainer();

        // Business logic.  Get, sort, close all, open list.
        // We want a list of fileList, not IFile becasue we want to
        // remember cursor positions.
        String selectedTab = plugin.getSelectedEditor();
        List<FileInfo> fileList = plugin.getOpenFileList();
        FileInfo[] fiArray = fileList.toArray(new FileInfo[fileList.size()]);
        Arrays.sort(fiArray);
        plugin.closeAllEditors();
        for (FileInfo f : fiArray) {
            plugin.openFile(f);
        }
        if ( selectedTab != null ) {
            plugin.selectEditor(selectedTab);
        }
        fgData.save();
    }

}
