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

import co.spillikin.tools.eclipse.editortabs.TabsPluginException;

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
    // @Deprecated  As state is stored within a workspace the map is not needed.
    // private Map<String, SessionMap> workspaceMap = new HashMap<>();
    private String workspaceName = null;

    // Map of session name to session data
    private SessionMap sessionMap = new SessionMap();

    // Error state saved when this is deserialized (or not if an error took place)
    // Retrieved and checked whenever used via UI.
    private transient TabsPluginException dataException = null;

    // Serialization, public, Needed by DataUtil
    public EditorSessionsData() {
    }

    /**
     * Interface
     * @return
     */
    public String getWorkspaceName() {
        return workspaceName;
    }

    /**
     * Interface
     * @param workspaceMap
     */
    @XmlAttribute
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    /**
     * Interface
     * @return
     */
    public SessionMap getSessionMap() {
        return sessionMap;
    }

    /**
     * Interface
     * Set the workspace map.
     * @param sessionMap
     */
    @XmlElement(name="sessionMapContainer")
    public void setSessionMap(SessionMap sessionMap) {
        this.sessionMap = sessionMap;
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

    @SuppressWarnings("unused")
    private String getCompany() {
        return company;
    }

    @XmlAttribute
    private void setCompany(String company) {
        this.company = company;
    }

    @SuppressWarnings("unused")
    private String getAuthor() {
        return author;
    }

    @XmlAttribute
    private void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Checked bu the User Interface whenever this object is
     * accessed.
     * @return
     */
    public TabsPluginException getDataException() {
        return dataException;
    }

    /**
     * Saved off by Load if an error took place.
     * @param e
     */
    public void setDataException(TabsPluginException e) {
        dataException = e;
    }

}
