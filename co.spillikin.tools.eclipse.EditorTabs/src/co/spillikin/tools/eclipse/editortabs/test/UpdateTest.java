package co.spillikin.tools.eclipse.editortabs.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.FileInfo;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;

/**
 * This test is to see if the update mechanism works correctly.  It's 
 * somewhat complicated.
 * This is used when the user click OK in the Update dialog.  It is not
 * the same as the updateFilePathList(...) method which is intended
 * to monitor changes to the tab set and record them.
 * 
 * The functionality associated with the Update dialog is probably 
 * the most complicated in the app.   If the session is a snap shot,
 * the user can opt to update it to the currently open tab set.  This 
 * is to avoid forcing the user to turn snapshot off to capture
 * the current tab set, and then turning it back on by opening up
 * the Update dialog a second time.
 * 
 * @see EditorSession.updateEditorSessionData(...);
 * 
 * @author chris
 *
 */
public class UpdateTest {

    DataUtil data = null;
    SessionMap sessionMap = null;
    EditorSession session = null;
    List<FileInfo> originalList = new ArrayList<>();
    List<FileInfo> openTabsList = new ArrayList<>();
    List<FileInfo> openTabsListEmpty = new ArrayList<>();

    // Runs before each test.
    @Before
    public void setup() {
        // None of these can fail unless the JVM is out of memory.
        // We simply create spaces in containers.
        data = DataUtil.unitTestInitialize("Test workspace name");
        // Get session [name - EditorSession] map for this workspace.
        sessionMap = data.getSessionMap();
        // Create our mock file list, out of order.
        originalList.add(new FileInfo("b", "cpath", 0, 0, 0, 0, null, true));
        originalList.add(new FileInfo("a", "apath", 0, 0, 0, 0, null, true));
        originalList.add(new FileInfo("b", "bpath", 0, 0, 0, 0, null, true));

        openTabsList.add(new FileInfo("open1", "u1path", 0, 0, 0, 0, null, true));
        openTabsList.add(new FileInfo("open2", "u2path", 0, 0, 0, 0, null, true));

    }

    // See if we're setup correctly
    @Test
    public void testTheTest() {
        Assert.assertTrue(isOriginal(originalList));
        Assert.assertTrue(isOpenTabs(openTabsList));
    }

    /**
     * Not a snapshot.
     * Updating with noAlpha, noSs, and noUpdateSs
     */
    @Test
    public void testSnapshotOff() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Continuous update, no snapshot.
        session.createEditorSessionData(false, false, originalList, "sel");
        // Get the original list back
        List<FileInfo> listInSession = session.getFileInfoList();
        // Simulate the Update dialog
        // alpha, ss, orig, openTabs, updateIfSs
        session.updateEditorSessionData(false, false,
            listInSession, openTabsList, false, "selected session");
        // We should have the new set
        Assert.assertTrue(isOpenTabs(openTabsList));
    }

    /**
     * Not a snapshot.  Passing empty new list.
     * Updating with noAlpha, noSs, and UpdateSs just for yucks.
     * Should keep original list.
     */
    @Test
    public void testSnapshotOffEmptyOpenTabs() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Continuous update, no snapshot.
        session.createEditorSessionData(false, false, originalList, "sel");
        // Get the original list back
        List<FileInfo> listInSession = session.getFileInfoList();
        // Simulate the Update dialog
        // alpha, ss, orig, openTabs, updateIfSs
        session.updateEditorSessionData(false, false,
            listInSession, openTabsListEmpty, true, "selected session");
        // We should still have the original list
        Assert.assertTrue(isOriginal());
    }
    
    /**
     * This is a snapshot and we want to update it with 
     * new tabs.
     * Updating with noAlpha, Ss, and UpdateSs
     */
    @Test
    public void testSnapshotOnUpdate() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // noAlpha, yes Ss.
        session.createEditorSessionData(false, true, originalList, "sel");
        // Get the original list back
        List<FileInfo> listInSession = session.getFileInfoList();
        // Simulate the Update dialog
        // alpha, stail a ss, orig, openTabs, updateIfSs
        session.updateEditorSessionData(false, true,
            listInSession, openTabsList, true, "selected session");
        // We should have the new set.
        Assert.assertTrue(isOpenTabs());
    }
    
    /**
     * This is a snapshot and we want to update it with 
     * new tabs... but they are empty so we should keep the old
     * ones.
     * Updating with noAlpha, Ss, and UpdateSs
     */
    @Test
    public void testSnapshotUpdateEmptyOpenTabs() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // noAlpha, yes Ss.
        session.createEditorSessionData(false, true, originalList, "sel");
        // Get the original list back
        List<FileInfo> listInSession = session.getFileInfoList();
        // Simulate the Update dialog
        // alpha, stail a ss, orig, openTabs, updateIfSs
        session.updateEditorSessionData(false, true,
            listInSession, openTabsListEmpty, true , "selected session");
        // We should have the new set.
        Assert.assertTrue(isOriginal());
    }
    
    /**
     * This is a snapshot and we DO NOT want to update it with 
     * new tabs.
     * Updating with noAlpha, Ss, and UpdateSs
     */
    @Test
    public void testSnapshotOnDoNotUpdate() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // noAlpha, yes Ss.
        session.createEditorSessionData(false, true, originalList, "sel");
        // Get the original list back
        List<FileInfo> listInSession = session.getFileInfoList();
        // Simulate the Update dialog
        // alpha, stail a ss, orig, openTabs, updateIfSs
        session.updateEditorSessionData(false, true,
            listInSession, openTabsList, false, "selected session");
        // We should have the old set.
        Assert.assertTrue(isOriginal());
    }
    
    /*
    Check for original
    This test is robust in case I add some kind of 
    merging functionality later.
    */
    Boolean isOriginal() {
        return isOriginal(session.getFileInfoList());

    }

    Boolean isOriginal(List<FileInfo> l) {
        int size = originalList.size();
        if (l.size() != size) {
            return false;
        }
        int match = 0;
        for (FileInfo fi : l) {
            if (fi.getFileName().equals("a") || fi.getFileName().equals("b")
                || fi.getFileName().equals("c")) {
                match++;
            }
        }
        return match == size ? true : false;
    }

    /*
    Check for original
    This test is robust in case I add some kind of 
    merging functionality later.
    */
    Boolean isOpenTabs() {
        return isOpenTabs(session.getFileInfoList());

    }
    
    Boolean isOpenTabs(List<FileInfo> l) {
        int size = openTabsList.size();
        if (l.size() != size) {
            return false;
        }
        int match = 0;
        for (FileInfo fi : l) {
            if (fi.getFileName().equals("open1") || fi.getFileName().equals("open2")) {
                match++;
            }
        }
        return match == size ? true : false;
    }

    // Runs after each test.
    @After
    public void tearDown() {
        // Clear the data.
        DataUtil.unitTestClear();
        data = null;
    }

}
