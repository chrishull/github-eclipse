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
package co.spillikin.tools.eclipse.editortabs;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import co.spillikin.tools.eclipse.editortabs.util.DataUtil;
import static co.spillikin.tools.eclipse.editortabs.Constants.FILENAME;

import org.eclipse.core.runtime.Status;

/**
 * The Activator is called when the plugin starts and stops.
 * We do some data setup here.  We do not have a user interface yet, 
 * so any errors that take place here will be posted to the user when 
 * they try to use the plugin.
 * 
 * @author chris
 *
 */
public class Activator implements BundleActivator {

    private static BundleContext context;

    static BundleContext getContext() {
        return context;
    }

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;

        new Status(Status.INFO, "XXXXXXXXXXXXXXXX FILETAB PLUGIN LOG MESSAGE TEST", null);

        // Init our DataContainer.  Let it try to load our XML file.
        String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
        File stateFile = context.getDataFile(FILENAME);
        String path = stateFile.getAbsolutePath();
        DataUtil.initialize(workspacePath, path);

    }

    /**
     * IMPORTANT
     * By the time we get here, the Workspace windoes are all closed.
     * We CAN NOT get them and add them to the current session.  So 
     * we rely on the fact that Eclipse will restart in it's current
     * state.  The ONLY time items are saved to a session is when 
     * any of our handlers are called.  This turns out to work just fine.
     * DO NOT attempt to get a window list here.
     * 
     * @TODO Write a listener for workspace changes.
     */
    public void stop(BundleContext bundleContext) throws Exception {

        DataUtil fgData = DataUtil.getInstance();
        fgData.save();

        Activator.context = null;
    }

}
