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
package co.spillikin.tools.eclipse.editortabs.model;

import java.awt.PageAttributes.OriginType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This represents an individual editor session, the lowest level
 * in the hierarchy of elements. It's a set of attributes including
 * a list of files to be managed by this session.
 * 
 * WorkspaceMap contains SessionMapImpl(s) contains EditorSessionImpl(s)
 * This chain is saves and retrieved in a corss version way via JAXB.
 * 
 * I attempted to front this with an interface but JAXB doesn't handle
 * interfaces very well, so hidden methods are private.
 * 
 * If you use interfaces with JAXB you will see something like this...
 * com.sun.xml.internal.bind.v2.runtime.IllegalAnnotationsException: 
 * n counts of IllegalAnnotationExceptions
 * co.spillikin.tools.eclipse.editortabs.model.EditorSession is an interface, 
 * and JAXB can't handle interfaces.
 * I certainly could have isolated JAXB behind another layer but why bother
 * with such a simple data model.
 * 
 * All items here are POJOs.  The handlers and pluginUtil are
 * responsible for translating to and from PDE objects.
 * 
 * @author chris
 *
 */

@XmlRootElement(name = "EditorSession")
public class EditorSession {

    private String label = "unnamed editor session";
    private Boolean isSnapshot = false;
    private Boolean keepAlphabetical = false;
    private List<FileInfo> fileInfoList = new ArrayList<>();

    // Serialization, package, needed by SessionMap
    EditorSession() {
    }

    /**
     * Interface
     * Return the label (not used)
     * @return String
     */
    public String getLabel() {
        return label;
    }

    @XmlAttribute
    private void setLabel(String label) {
        this.label = label;
    }

    /**
     * Interface
     * Return true if this session list is to be sorted in alpahbetical order
     * when tabs are opened.
     * @return Boolean
     */
    public Boolean getKeepAlphabetical() {
        return keepAlphabetical;
    }

    @XmlAttribute
    private void setKeepAlphabetical(Boolean keepAlphabetical) {
        this.keepAlphabetical = keepAlphabetical;
    }

    /**
     * Interface
     * Return true if session in snapshot mode.
     * @return Boolean
     */
    public Boolean getIsSnapshot() {
        return isSnapshot;
    }

    @XmlAttribute
    private void setIsSnapshot(Boolean isSnapshot) {
        this.isSnapshot = isSnapshot;
    }

    /**
     * Interface
     * Retrieve the list of files associated with this session.
     * If keepAlphabetical set, return in alphabetical order
     * based on filename.
     * JABX does not want fileInfoList going thru an array
     * for some reason. Deserialization behaves unpredictably
     * if I use this method as JAXB's getter. But for
     * usability I wanted this to be the public one.
     * 
     * @return List<String>
     */
    public List<FileInfo> getFileInfoList() {

        // We don't want to sort the data in the original list
        // se we'll sort a clone
        // @SuppressWarnings("unchecked")
        // unsafe.  Breaks in the null case.
        // TODO investigate this.
        // ArrayList<FileInfo> copy = (ArrayList<FileInfo>) ((ArrayList<FileInfo>) fileInfoList).clone();

        FileInfo[] fiArray = fileInfoList.toArray(new FileInfo[fileInfoList.size()]);
        if (keepAlphabetical) {
            Arrays.sort(fiArray);
        }
        return Arrays.asList(fiArray);
    }

    /**
     * I might have used this as an interface, but JAXB doesn't
     * seem to want the field (fileInfoList) altered, and that's 
     * exactly what I aim to do when alphabetizing.
     * 
     * @return List<String>
     */
    private List<FileInfo> getFileInfoListJaxb() {
        return fileInfoList;
    }

    @XmlElement(name = "FileInfo")
    private void setFileInfoListJaxb(List<FileInfo> fileInfoList) {
        this.fileInfoList = fileInfoList;
    }

    /**
     * Interface
     * Safe to call everywhere.  Sets a new file path list
     * unless snapshot is true or the list is empty.
     * 
     * @param filePathList
     */
    public void updateFilePathList(List<FileInfo> fileInfoList) {
        if (getIsSnapshot() || fileInfoList.size() == 0) {
            return;
        }
        this.fileInfoList = fileInfoList;
    }

    /**
     * Interface
     * Used after OK clicked in Create dialog.  
     * 
     * This is a brand new session so we just set all data.
     * Called when a new editor session is created.  First space is saved
     * for it in the map, then it's data is filled in.
     * The Create dialog should have been blocked if list is empty.
     * 
     * @param keepAlpha
     * @param isSanpshot
     * @param fileInfoList
     */
    public void createEditorSessionData(Boolean keepAlpha, Boolean isSanpshot,
        List<FileInfo> fileInfoList) {
        this.isSnapshot = isSanpshot;
        this.keepAlphabetical = keepAlpha;
        this.fileInfoList = fileInfoList;
    }

    /**
     * Interface 
     * Used after OK clicked in Update dialog. 
     * 
     * The functionality associated with the Update dialog is probably 
     * the most complicated in the plugin.   If the session is a snapshot,
     * the user can opt to update it to the currently open tab set.  This 
     * is to avoid forcing the user to turn snapshot off to capture
     * the current tab set, and then turning it back on by opening up
     * the Update dialog a second time. 
     * 
     * In the Update portion of the CreateUpdate handler, Update is accomplished by
     * 1: grabbing the file list from the session to be updated.
     * 2: Deleting that session.
     * 3: Creating a new space for a new session with (possibly) a new name.
     *    (basically we create an entirely new session)
     * 4: Calling this method correctly to set the session data.
     * 
     * The rules here are a little more complicated than create. See comments
     * in code and the UpdateTest unit test.
     * 
     * @param keepAlpha - Set if this set is to be opened in alpha order.
     * @param isSanpshot - Set if this set is a snapshot, not to be continuously 
     * updated.
     * @param originalList - This is the list that came from the 
     * original session that we are updating. This list will have data and not 
     * be of zero length.
     * @param newList - This list comes from the currently open set of tabs.
     * This list may be empty if all tabs are closed.
     * @param updateSnapShot - Set to True if user wants to set the
     * snapshot to the current set of tabs. Only pay attention to it if 
     * isSanpshot is also true.
     */
    public void updateEditorSessionData(Boolean keepAlpha, Boolean isSanpshot,
        List<FileInfo> originalList, List<FileInfo> newList, Boolean updateSnapShot) {
        this.isSnapshot = isSanpshot;
        this.keepAlphabetical = keepAlpha;
        // If all the editor windows are closed, set orignalList and exit
        // no matter what.
        // Identical effect as updateFilePathList(...);
        if (newList.size() == 0) {
            this.fileInfoList = originalList;
            return;
        }
        // If we are not a snapshot, always set to the 
        // open (non empty) set of tabs.
        if (!isSanpshot) {
            this.fileInfoList = newList;
            return;
            // if we are a snapshot and we want to be updated, use 
            // (non empty) open set of tabs.
        } else {
            if (updateSnapShot) {
                this.fileInfoList = newList;
                return;
            }
        }
        // We are a snapshot and we will keep our original list, no update.
        this.fileInfoList = originalList;
    }

    /**
     * Interface
     * This is used by the Select dialog.
     * This will update according to pushbutton settings.  
     * @param keepAlpha
     * @param isSanpshot
     */
    public void updateEditorSessionButtons(Boolean keepAlpha, Boolean isSanpshot) {
        this.isSnapshot = isSanpshot;
        this.keepAlphabetical = keepAlpha;
    }

    /**
     * Return the number of items in the new list 
     * the can be found in the existing list.
     * A nice little O n^2 algorithm, but the lists are small.
     * 
     * @param thisList
     * @param newList
     * @return Number of items in the new list that are also in the
     * existing list.
     */
    private Integer numTheSame(List<FileInfo> thisList, List<FileInfo> newList) {

        int numThatMatch = 0;
        for (FileInfo nf : newList) {
            for (FileInfo existing : thisList) {
                if (nf.getFullPath().equals(existing.getFullPath())) {
                    numThatMatch++;
                }
            }
        }
        return numThatMatch;
    }

    /**
     * Are the proposed files the same as the existing files.
     * @param newList
     * @return true if the same.
     */
    public Boolean isIdentical(List<FileInfo> newList) {
        if (fileInfoList.size() != newList.size()) {
            return false;
        } else {
            if (numTheSame(fileInfoList, newList) == fileInfoList.size()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Compute the number of new files that will be added
     * if we replace the list in this group with the 
     * proposed list.
     * 
     * @param newList
     * @return
     */
    public int numToBeAdded(List<FileInfo> newList) {
        return newList.size() - numTheSame(fileInfoList, newList);
    }

    /**
     * Compute the number of new files that will be removed
     * if we replace the list in this group with the 
     * proposed list.
     * 
     * @param newList
     * @return
     */
    public int numToBeDeleted(List<FileInfo> newList) {
        return fileInfoList.size() - numTheSame(fileInfoList, newList);
    }

}
