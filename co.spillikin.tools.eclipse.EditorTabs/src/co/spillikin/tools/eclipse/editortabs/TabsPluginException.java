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

/**
 * Using this in case we want to specialize this exception someday.
 * @author chris
 *
 */
public class TabsPluginException extends Exception {

    private static final long serialVersionUID = 1L;

    public TabsPluginException() {
        super();
    }

    public TabsPluginException(String message) {
        super(message);
    }

    public TabsPluginException(Throwable t) {
        super(t);
    }

}
