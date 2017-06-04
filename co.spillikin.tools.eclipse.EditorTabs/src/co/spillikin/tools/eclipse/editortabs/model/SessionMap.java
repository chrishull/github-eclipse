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
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the second layer, just above the sessions themselves.
 * It maps a given name to an EditorSession.
 * 
 * WorkspaceMap contains SessionMapImpl(s) contains EditorSession(s)
 * This chain is saves and retrieved in a corss version way via JAXB.
 * 
 * @author chris
 *
 */
@XmlRootElement(name = "SessionMap")
public class SessionMap {

    // The current session by name
    private String currentSessionName = null;
    // The previously current session name
    private String previousSessionName = null;

    // Map of session names to sessions
    private Map<String, EditorSession> sessionMap = new HashMap<>();

    /**
     * Interface
     * Map of session names to data.  Guaranteed to not be null.
     * @return Map<String, EditorSession>
     */
    public Map<String, EditorSession> getSessionMap() {
        return sessionMap;
    }

    /**
     * Interface
     * @param sessionMap
     */
    @XmlElement
    public void setSessionMap(Map<String, EditorSession> sessionMap) {
        this.sessionMap = sessionMap;
    }

    /**
     * Interface
     * @return
     */
    public String getCurrentSessionName() {
        return currentSessionName;
    }

    /**
     * Interface
     * @param currentSessionName
     */
    @XmlAttribute
    public void setCurrentSessionName(String currentSessionName) {
        this.currentSessionName = currentSessionName;
    }

    /**
     * Interface
     * @return
     */
    public String getPreviousSessionName() {
        return previousSessionName;
    }

    /**
     * Interface
     * @param previousSessionName
     */
    @XmlAttribute
    public void setPreviousSessionName(String previousSessionName) {
        this.previousSessionName = previousSessionName;
    }

    /**
     * Interface
     * Given the name of an editor session, get the session
     * itself.  Also set the current session name.
     * This will create a new space for a session name that does
     * not exist.
     * If sessionName is null, the current session is set, but no
     * new session is created (no such thing as a null keyed session).
     * 
     * @param sessionName. May be null.
     * @return The associated EditorSession.  May be newly created.
     * null if null was passed in.
     */
    public EditorSession switchEditorSession(String sessionName) {
        currentSessionName = sessionName;
        if (sessionName == null) {
            return null;
        }
        if (sessionMap.get(sessionName) == null) {
            sessionMap.put(sessionName, new EditorSession());
        }
        return sessionMap.get(sessionName);
    }

    /**
     * Interface
     * Get an editor session by name without switching.  
     * Will return NULL if the nameed session does not exist.
     * @param sessionName
     * @return EditorSession, or null if null was passed in or session
     * does not exist.
     */
    public EditorSession getEditorSession(String sessionName) {
        if (sessionName == null) {
            return null;
        }
        return sessionMap.get(sessionName);
    }

    /**
     * Interface
     * Return the current EditorSession.  If current is null
     * then return null.
     * @return
     */
    public EditorSession getCurrentEditorSession() {
        if (currentSessionName == null) {
            return null;
        }
        return sessionMap.get(currentSessionName);
    }

    /**
     * Interface
     * Delete the current editor session from the map.
     * The current editor session is the one specified in
     * switchEditorSession.  If current is null then do 
     * nothing.  If we delete, then set current to null.
     */
    public void deleteCurrentEditorSession() {
        if (currentSessionName == null) {
            return;
        }
        deleteEditorSession(currentSessionName);
        currentSessionName = null;
    }

    /**
     * Interface
     * Remove the given editor session.
     * @param workspace
     * @return
     */
    public void deleteEditorSession(String sessionName) {
        sessionMap.remove(sessionName);
    }

    /**
     * Interface
     * Return a list of session names for display in dropdown 
     * menus and the like.
     * @return
     */
    public Set<String> getSessionNames() {
        return sessionMap.keySet();
    }

    /**
     * Interface
     * Check to see if a given session name is already being used.
     * @param someName
     * @return
     */
    public Boolean isNameInUse(String someName) {
        Set<String> existingNames = getSessionNames();
        for (String name : existingNames) {
            if (someName.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }
}
