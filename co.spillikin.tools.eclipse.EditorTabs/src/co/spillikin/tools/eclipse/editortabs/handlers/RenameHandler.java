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
import org.eclipse.swt.widgets.Shell;

/**
 * Allow the user to update the settings for the current session.
 * Has all the same features more or less as create, and shares the 
 * same user interface.
 * 
 * @author chris
 *
 */
public class RenameHandler {

    @Execute
    public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell s) throws ExecutionException {

        // Use the create update handler to update
        CreateUpdate handler = new CreateUpdate(false);
        handler.execute(s);
    }

}
