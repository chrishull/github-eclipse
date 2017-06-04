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

import javax.inject.Named;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;
import co.spillikin.tools.eclipse.editortabs.ui.CreateUpdateDialog;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import co.spillikin.tools.eclipse.editortabs.util.PluginUtil;

/**
 * CreateHandler handles the creation of a new editor session.
 * We put up a dialog and ask the user to give a name
 * to the currently open set of tabs in the Eclipse editor window.
 * Changes to these tabs are tracked once a new name has been given.
 * 
 * @author chris
 *
 */
public class CreateHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {
        // Use the create update handler to create
        CreateUpdate handler = new CreateUpdate(true);
        handler.execute(s);
    }

}
