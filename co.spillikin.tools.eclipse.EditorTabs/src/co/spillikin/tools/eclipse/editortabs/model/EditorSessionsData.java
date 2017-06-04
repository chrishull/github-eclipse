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

import java.util.HashMap;
import java.util.Map;
import static co.spillikin.tools.eclipse.editortabs.Constants.VERSION_MAJOR;
import static co.spillikin.tools.eclipse.editortabs.Constants.VERSION_MINOR;

import static co.spillikin.tools.eclipse.editortabs.Constants.AUTHOR;
import static co.spillikin.tools.eclipse.editortabs.Constants.COMPANY;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the top layer.  It maps a Workspace to a 
 * group of sessions in SessionMap
 * 
 * WorkspaceMap contains SessionMap(s) contains EditorSession(s)
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

@XmlRootElement(name = "EditorSessionsData")
public class EditorSessionsData {

    // Versioning and such
    private Integer versionMajor = VERSION_MAJOR;
    private Integer versionMinor = VERSION_MINOR;
    private String company = COMPANY;
    private String author = AUTHOR;

    // Map of workspaces to map of session names to sessions
    private Map<String, SessionMap> workspaceMap = new HashMap<>();

    // Serialization, public, Needed by DataUtil
    public EditorSessionsData() {
    }

    private Map<String, SessionMap> getWorkspaceMap() {
        return workspaceMap;
    }

    /**
     * Interface
     * Set the workspace map.
     * @param workspaceMap
     */
    @XmlElement
    public void setWorkspaceMap(Map<String, SessionMap> workspaceMap) {
        this.workspaceMap = workspaceMap;
    }

    /**
     * Interface
     * Get the major version of this data
     * @return Integer
     */
    public Integer getVersionMajor() {
        return versionMajor;
    }

    @XmlAttribute
    private void setVersionMajor(Integer versionMajor) {
        this.versionMajor = versionMajor;
    }

    /**
     * Interface
     * The the minor version of this data.
     * @return Integer
     */
    public Integer getVersionMinor() {
        return versionMinor;
    }

    @XmlAttribute
    private void setVersionMinor(Integer versionMinor) {
        this.versionMinor = versionMinor;
    }

    private String getCompany() {
        return company;
    }

    @XmlAttribute
    private void setCompany(String company) {
        this.company = company;
    }

    private String getAuthor() {
        return author;
    }

    @XmlAttribute
    private void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Interface
     * Given a workspace, return the associated session map.
     * If none exists, create a new one and return it.
     * 
     * @param workspace name
     * @return associated set of editor sessinos, mapped by name.
     */
    public SessionMap getSessionMap(String workspace) {
        if (workspaceMap.get(workspace) == null) {
            workspaceMap.put(workspace, new SessionMap());
        }
        return workspaceMap.get(workspace);
    }

    /**
     * Interface
     * Remove the session map for the given workspace.
     * @param workspace
     * @return
     */
    public void deleteSessionMap(String workspace) {
        workspaceMap.remove(workspace);
    }
}
