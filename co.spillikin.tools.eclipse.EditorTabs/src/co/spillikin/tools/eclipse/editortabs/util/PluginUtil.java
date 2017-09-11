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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
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
import org.eclipse.core.runtime.Platform;

import java.io.File;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

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
            // Catch anything and repackage.  Used by FailsafeUtil
        } catch (Throwable e) {
            throw new TabsPluginException(e);
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
     * Given a full path String, open a file in an editor tab.
     * This handles files in the workspace and outside of it as well.  But 
     * does not deal with external editors. You're on your own there.
     * 
     * @param pathStr Full path to file to open.
     * @return True if file successfully opened. Exceptions are buried.
     */
    public boolean openFile(String pathStr) {

        Path path = new Path(pathStr);
        IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        IEditorRegistry editorRegistry = PlatformUI.getWorkbench().getEditorRegistry();
        IEditorDescriptor desc = editorRegistry.getDefaultEditor(file.getName());

        // Open this way if the path exists in the workspace (IFile is a real IFile)
        if (file.exists()) {
            try {
                // workspace we get a CoreException (Can not determine URI) if doesn't exist.
                // This will not throw for some reason if the file is outside the 
                // workspace. You will get en empty editor tab with an error message.
                // This is why we check first.
                FileEditorInput fei = new FileEditorInput(file);
                workbenchPage.openEditor(fei, desc.getId());
            } catch (PartInitException e) {
                return false;
            }
            // Open this way if the file is outside the workspace.
        } else {
            File fileToOpen = new File(pathStr);
            if (fileToOpen.exists() && fileToOpen.isFile()) {
                IFileStore fileStore = EFS.getLocalFileSystem().getStore(fileToOpen.toURI());
                try {
                    IDE.openEditorOnFileStore(workbenchPage, fileStore);
                } catch (PartInitException e2) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * Given an FileInfo, open it up in one of the Eclipse editor windows
     * and attempt to reposition the cursor.
     * @param file
     * @return true if success, false if fail.
     */
    public boolean openFile(FileInfo fileInfo) {

        if (!openFile(fileInfo.getFullPath())) {
            return false;
        }

        // Once the file is open, try to reposition the cursor and selected 
        // text where it used to be
        if (!fileInfo.getIsEmpty()) {
            try {
                setTextSelectionForFile(fileInfo);
            } catch (PartInitException | BadLocationException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Open all the FileInfo in a list in the Eclipse editor window.
     * Select the given current editor (or don't if null)
     * 
     * @param FileInfo
     * @return
     */
    public boolean openFileList(List<FileInfo> fileInfoList, String filePath) {

        boolean retVal = true;
        for (FileInfo fi : fileInfoList) {
            if (!openFile(fi)) {
                retVal = false;
            }
        }
        selectEditor(filePath);
        return retVal;
    }

    /**
     * Get a list of currently open editor files.
     * Returns a "native" Eclipse PDE list of IFile.
     * 
     * @return List<IFile>
     */
    public List<IFile> getOpenEclipseEditorFileList() {

        List<IFile> fileList = new ArrayList<IFile>();
        IEditorReference[] erList = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage().getEditorReferences();
        for (IEditorReference er : erList) {
            IEditorInput ei = null;
            try {
                ei = er.getEditorInput();
            } catch (PartInitException e1) {
                // nothing to do
            }
            if (ei != null) {
                IFile file = getFileFromEditorInput(ei);
                if (file != null) {
                    fileList.add(file);
                }
            }
        }
        return fileList;
    }

    /**
     * Return the open set of files as a list of FileInfo, complete
     * with cursor position information.
     * 
     * @return List<FileInfo>
     */
    public List<FileInfo> getOpenFileList() {
        List<IFile> fileList = getOpenEclipseEditorFileList();
        List<FileInfo> fileInfoList = new ArrayList<FileInfo>();
        for (IFile file : fileList) {
            FileInfo fi = iFileToFileInfo(file);
            fileInfoList.add(fi);
        }
        return fileInfoList;
    }

    /**
     * Get the currently selected editor as a full path.
     * May return null if all editors are closed.
     */
    public String getSelectedEditor() {
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        IEditorPart p = workbenchPage.getActiveEditor();
        // If no editors are opened p will be null.
        if (p == null) {
            return null;
        }
        IEditorInput ei = p.getEditorInput();
        IFile file = getFileFromEditorInput(ei);
        if (file == null) {
            return null;
        }
        return file.getFullPath().toString();
    }

    /**
     * Select the given editor tab as idendified by it's full path.
     * Must be open.  May not work but fails gracefully.
     * 
     * @param filePath  Unique ID for editor.
     */
    public void selectEditor(String filePath) {
        if (filePath == null) {
            return;
        }
        IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getActivePage();
        IEditorReference[] erList = workbenchPage.getEditorReferences();
        // Loop thru editor references and try to match path.
        for (IEditorReference er : erList) {
            IEditorInput ei = null;
            try {
                ei = er.getEditorInput();
            } catch (PartInitException e1) {
                // nothing to do
                return;
            }
            if (ei != null) {
                IFile file = getFileFromEditorInput(ei);
                if (file == null) {
                    continue;
                }
                String path = file.getFullPath().toString();
                if (path.equals(filePath)) {
                    // Returns the editor referenced by this object. Returns null 
                    // if the editor was not instantiated or it failed to be restored. 
                    // Tries to restore the editor if restore is true.
                    IEditorPart ep = er.getEditor(false);
                    if (ep == null) {
                        return;
                    }
                    workbenchPage.activate(ep);
                    // redundant?
                    // ep.setFocus();

                    return;
                }
            }
        }
    }

    /**
     * Convert an Eclipse IFile to a FileInfo object.
     * 
     * @param file
     * @return
     */
    private FileInfo iFileToFileInfo(IFile file) {

        String fullPath = file.getFullPath().toString();
        String name = file.getName();
        ITextSelection selection = null;
        try {
            selection = getTextSelectionForPath(fullPath);
        } catch (PartInitException | BadLocationException e) {
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
        return new FileInfo(name, fullPath, selection.getStartLine(), selection.getEndLine(),
            selection.getOffset(), selection.getLength(), selection.getText(), selection.isEmpty());

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
                if (file == null) {
                    continue;
                }
                // if we found it, get it's ITextSelection
                String p = file.getFullPath().toString();
                if (fullPath.equals(p)) {
                    // Don't assume we get a IEditorPart
                    Object ep = editorRef.getPart(false);
                    if (ep instanceof IEditorPart) {
                        IEditorPart editorPart = (IEditorPart) ep;
                        return editorPart.getSite().getSelectionProvider();
                    }
                    return null;
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
     * @param Full path to file.  Used to get the SelectionProvider
     * and from there the ITextSelection for that full path.
     * 
     * @return ITextSelection.  If not null this info will be 
     * moved to the associated FileInfo.
     * 
     * @throws PartInitException
     * @throws BadLocationException
     */
    public ITextSelection getTextSelectionForPath(String fullPath)
        throws PartInitException, BadLocationException {

        ISelectionProvider is = getSelectionProviderForPath(fullPath);

        if (is != null) {
            // This can come back as TreeSelection, causing a classCastException
            // This caused an intermittent mystery error for a while.
            Object selection = is.getSelection();
            if (selection instanceof ITextSelection) {
                return (ITextSelection) selection;
            }
        }
        return null;

    }

    /**
     * After opening a file in an editor, position the cursor and
     * selection area based on data found in FileInfo.
     * We convert a FileInfo back to an ITextSelection and set that 
     * selection based on the full path found in the FileInfo.
     * 
     * @param fi  FileInfo associated with an already opened file.
     * @return False if for some reason we couldn't get the selection
     * provider.  Harmless. We just won't set the cursor in that case.
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
        MessageDialog.openError(s, "A Fatal Error Has Occurred", errorMessage);
        return;
    }

    /**
     * Utility displays alrrt showing all info associated with the given exception and 
     * logs it to the platform log.  The location of the log is
     * also displayed in the alert bos.
     * @param s
     * @param e
     */
    public static void postAndLogException(Shell s, Exception e) {

        // Convert the exception's message and stack trace into a String
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        StringBuffer stackTraceSb = new StringBuffer(sw.toString());
        if (stackTraceSb.length() > 1024) {
            stackTraceSb.setLength(1024);
            stackTraceSb.append("(trimmed)...");
        }
        String exMessage = "Exception type: " + e.getClass().toString() + "\n" + "Message: "
            + e.getMessage() + "\n" + "Stacktrace:\n " + stackTraceSb.toString();

        new Status(Status.ERROR, "co.spillikin.editortabs", exMessage);
        exMessage = exMessage + "\n\n" + "Logfile location: "
            + Platform.getLogFileLocation().toString() + "\n"
            + "Please report this error to: chrishull42@gmail.com";
        postFatalAlertStatic(s, exMessage);

    }

    /**
     * Given an IEditorInput, return the associated IFile.
     * NOTE.  This WILL return NULL if the IEditorInput is a document
     * opened with, say, the WebBrowser.
     * 
     * @param input
     * @return
     */
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

    private static IPath getPathFromEditorInput(IEditorInput input) {

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
