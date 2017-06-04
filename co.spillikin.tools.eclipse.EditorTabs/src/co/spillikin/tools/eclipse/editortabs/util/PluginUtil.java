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
package co.spillikin.tools.eclipse.editortabs.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IURIEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;
import co.spillikin.tools.eclipse.editortabs.model.FileInfo;

import static co.spillikin.tools.eclipse.editortabs.Constants.RESOURCE_FILE_NAME;
import static co.spillikin.tools.eclipse.editortabs.Constants.ERROR_FATAL_TITLE_KEY;
import static co.spillikin.tools.eclipse.editortabs.Constants.ERROR_FATAL_MESSAGE_1_KEY;
import static co.spillikin.tools.eclipse.editortabs.Constants.ERROR_FATAL_MESSAGE_2_KEY;

/**
 * PluginUtil.
 * A utility class used to access Eclipse PDE functionality and 
 * access plugin resources.
 * 
 * @author chris
 *
 */
public class PluginUtil {

    // Singleton
    private static PluginUtil instance = null;

    /**
     * Created by the Activator as it is the first part of the plugin to call this.
     * Singleton gets or creates an instance of this utility.
     * If initialization fails an exception is thrown.  This container
     * can not be used but statics can of course.
     * 
     * @return
     * @throws TabsPluginException
     */
    public static PluginUtil getInstance() throws TabsPluginException {
        if (instance == null) {
            instance = new PluginUtil();
        }
        return instance;
    }

    // Bundle to our resources
    private ResourceBundle resBundle = null;
    // Error message if resource key can not be found for instance.
    private String resErrorMessage = null;

    /**
     * THis constructor is called only once.
     * Attempt to load our resources.  if this fails
     * save off the error state and throw an exception.
     * 
     * We save off the error state in case other user interface
     * elements access this utility after initialization has failed.
     */
    private PluginUtil() throws TabsPluginException {
        try {
            resBundle = ResourceBundle.getBundle(RESOURCE_FILE_NAME);
        } catch (Exception e) {
            throw new TabsPluginException(e.getMessage());
        }
    }

    /**
     * Get the resource load error message.
     * Null if ok.
     * @return
     */
    public String getResErrorMessage() {
        return resErrorMessage;
    }

    /**
     * Given an FileInfo, open it up in one of the Eclipse editor windows
     * and attempt to reposition the cursor.
     * @param file
     * @return true if success, false if fail.
     */
    private boolean openFile(FileInfo fileInfo) {

        Path path = new Path(fileInfo.getFullPath());
        // May need to check bounds
        ITextSelection currrentTextSelection = null;
        try {
            currrentTextSelection = getTextSelectionForPath(fileInfo.getFullPath());
        } catch (PartInitException | BadLocationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            return false;
        }

        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);

        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();

        IEditorDescriptor desc = editorRegistry.getDefaultEditor(file.getName());
        try {
            workbenchPage.openEditor(new FileEditorInput(file), desc.getId());
        } catch (PartInitException e) {
            e.printStackTrace();
            return false;
        }

        // Once the file is open, try to reposition the cursor and selected 
        // text where it used to be
        if (!fileInfo.getIsEmpty()) {
            try {
                setTextSelectionForFile(fileInfo);
            } catch (PartInitException | BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * Given a path relative to a Project (IPath), find an IFile and open it.
     * @param FileInfo
     * @return
     */
    public boolean openFileList(List<FileInfo> fileInfoList) {

        boolean retVal = true;
        for (FileInfo fi : fileInfoList) {
            if (!openFile(fi)) {
                retVal = false;
            }
        }
        return retVal;
    }

    /**
     * Get a list of currently open files
     */
    private List<IFile> getOpenFlieList() {

        List<IFile> fileList = new ArrayList<IFile>();
        IEditorReference[] erList = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().getEditorReferences();
        for (IEditorReference er : erList) {
            IEditorInput ei = null;
            try {
                ei = er.getEditorInput();
            } catch (PartInitException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            if (ei != null) {
                IFile file = getFileFromEditorInput(ei);
                fileList.add(file);
            }
        }
        return fileList;
    }

    /**
     * Return an alphabetized list of paths.
     * This may not be very interesting as the intent here is
     * to alphabetize the file names.
     * @return
     */
    public List<FileInfo> getOpenFileList() {
        List<IFile> fileList = getOpenFlieList();
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        for (IFile file : fileList) {
            String fullPath = file.getFullPath().toString();
            String name = file.getName();
            ITextSelection selection = null;
            try {
                selection = getTextSelectionForPath(fullPath);
            } catch (PartInitException | BadLocationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // If we got an error or it came back null, set to 
            // empty so we won't attempt to select.
            if (selection == null) {
                selection = new ITextSelection() {
                    @Override
                    public boolean isEmpty() {
                        return true;
                    }

                    @Override
                    public int getEndLine() {
                        return 0;
                    }

                    @Override
                    public int getLength() {
                        return 0;
                    }

                    @Override
                    public int getOffset() {
                        return 0;
                    }

                    @Override
                    public int getStartLine() {
                        return 0;
                    }

                    @Override
                    public String getText() {
                        return null;
                    }
                };
            }
            FileInfo fi = new FileInfo(name, fullPath, selection.getStartLine(),
                selection.getEndLine(), selection.getOffset(), selection.getLength(),
                selection.getText(), selection.isEmpty());
            fileInfoList.add(fi);
        }
        return fileInfoList;
    }

    /**
     * Close all open tabs.
     */
    public void closeAllEditors() {
        // true means save contents of editors.
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();

        workbenchPage.closeAllEditors(true);

    }

    /**
     * Check to see if any of the editor tabs need to be saved.
     * @return true if closng a tab will destroy user edits.
     */
    public boolean needSaveing() {
        IEditorReference[] erList = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().getEditorReferences();
        for (IEditorReference er : erList) {
            // IEditorInput ei = null;
            if (er.isDirty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the SelectionProvider for the currently OPEN
     * Eclipse file given it's full path.
     * 
     * @param Full path to file (used as a unique ID).
     * 
     * @throws PartInitException
     * @throws BadLocationException
     */
    public ISelectionProvider getSelectionProviderForPath(String fullPath)
        throws PartInitException, BadLocationException {

        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        IEditorReference[] erList = workbenchPage.getEditorReferences();
        // Loop thru open tabs till we find our IFile
        for (IEditorReference editorRef : erList) {
            IEditorInput ei = null;
            ei = editorRef.getEditorInput();
            if (ei != null) {
                IFile file = getFileFromEditorInput(ei);
                // if we found it, get it's ITextSelection
                String p = file.getFullPath().toString();
                if (fullPath.equals(p)) {
                    IEditorPart editorPart = (IEditorPart) editorRef.getPart(false);
                    if (editorPart == null) {
                        return null;
                    }
                    return editorPart.getSite().getSelectionProvider();
                }
            }
        }
        return null;
    }

    /**
     * For a given open file, return it's ITextSelection so we can 
     * save off the selected text ares / cursor location 
     * so we can set it when we reopen.
     * 
     * @param Full path to file (used as a unique ID).
     * 
     * @throws PartInitException
     * @throws BadLocationException
     */
    public ITextSelection getTextSelectionForPath(String fullPath)
        throws PartInitException, BadLocationException {

        ISelectionProvider is = getSelectionProviderForPath(fullPath);
        if (is == null) {
            return null;
        }
        return (ITextSelection) is.getSelection();

    }

    /**
     * For a given open file, return it's ITextSelection so we can 
     * save off the selected text ares / cursor location 
     * so we can set it when we reopen.
     * 
     * @param Full path to file (used as a unique ID).
     * 
     * @throws PartInitException
     * @throws BadLocationException
     */
    public boolean setTextSelectionForFile(FileInfo fi)
        throws PartInitException, BadLocationException {

        String fullPath = fi.getFullPath();
        ISelectionProvider is = getSelectionProviderForPath(fullPath);
        if (is == null) {
            return false;
        }

        ITextSelection itSel = new ITextSelection() {
            @Override
            public int getEndLine() {
                return fi.getEndLine();
            }

            @Override
            public int getStartLine() {
                return fi.getStartLine();
            }

            @Override
            public boolean isEmpty() {
                return fi.getIsEmpty();
            }

            @Override
            public int getLength() {
                return fi.getLength();
            }

            @Override
            public int getOffset() {
                return fi.getOffset();
            }

            @Override
            public String getText() {
                return fi.getText();
            }
        };

        is.setSelection(itSel);
        return true;
    }

    /**
    * Given a resource string, get it's value.  Returns 
    * an error message to the caller in the event the key is incorrect.
    * This should never happen.
     * 
     * @param key
     * @return
     */
    public String getResourceString(String key) {
        String res = "Resource error. No string for key: " + key;
        try {
            res = resBundle.getString(key);
        } catch (Exception e) {
        }
        return res;
    }

    /**
     * Sometimes we need to pass the resource into a dialog so 
     * it can grab it's own strings.
     * 
     * @return
     */
    public ResourceBundle getResourceBundle() {
        return resBundle;
    }

    /**
     * Display a localizable fatal error dialog.
     * @param input
     * @return
     */
    public void postFatalErrorAlert(Shell s, String errorMessage) {
        MessageDialog.openError(s, resBundle.getString(ERROR_FATAL_TITLE_KEY),
            resBundle.getString(ERROR_FATAL_MESSAGE_1_KEY) + " " + errorMessage + " "
                + resBundle.getString(ERROR_FATAL_MESSAGE_2_KEY));
    }

    /**
     * Post a fatal alert dialog in the event resources could not be found.
     * @param s
     * @param errorMessage
     */
    public static void postFatalAlertStatic(Shell s, String errorMessage) {
        MessageDialog.openError(s, "A Fatal Error Has Occurred",
            "Needed resources could not be found.  and " + errorMessage + " Exiting.");
        return;
    }

    public static IFile getFileFromEditorInput(IEditorInput input) {
        if (input == null)
            return null;

        if (input instanceof IFileEditorInput)
            return ((IFileEditorInput) input).getFile();

        IPath path = getPathFromEditorInput(input);
        if (path == null)
            return null;

        return ResourcesPlugin.getWorkspace().getRoot().getFile(path);
    }

    /**
     * ==========================================================
     * SPARE PARTS???
     */

    public static IPath getPathFromEditorInput(IEditorInput input) {

        if (input instanceof ILocationProvider)
            return ((ILocationProvider) input).getPath(input);

        if (input instanceof IURIEditorInput) {
            URI uri = ((IURIEditorInput) input).getURI();
            if (uri != null) {
                IPath path = URIUtil.toPath(uri);
                if (path != null)
                    return path;
            }
        }

        return null;
    }

}
