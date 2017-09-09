package co.spillikin.tools.eclipse.editortabs.test;

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
 * Test various calculations.  Numbers of files inserted, removed, etc.
 * 
 * @author chris
 *
 */
public class NumbersTest {

    DataUtil data = null;
    SessionMap sessionMap = null;
    EditorSession session = null;
    List<FileInfo> fileInfoList1 = new ArrayList<>();
    List<FileInfo> fileInfoList2 = new ArrayList<>();

    // Runs before each test.
    @Before
    public void setup() {
        // None of these can fail unless the JVM is out of memory.
        // We simply create spaces in containers.
        data = DataUtil.unitTestInitialize("Test workspace name");
        // Get session [name - EditorSession] map for this workspace.
        sessionMap = data.getSessionMap();
        // Create our mock file list, out of order.
        fileInfoList1.add(new FileInfo("c", "cpath", 0, 0, 0, 0, null, true));
        fileInfoList1.add(new FileInfo("a", "apath", 0, 0, 0, 0, null, true));
        fileInfoList1.add(new FileInfo("b", "bpath", 0, 0, 0, 0, null, true));

        fileInfoList2.add(new FileInfo("d", "dpath", 0, 0, 0, 0, null, true));
        fileInfoList2.add(new FileInfo("a", "apath", 0, 0, 0, 0, null, true));
        fileInfoList2.add(new FileInfo("e", "epath", 0, 0, 0, 0, null, true));
        fileInfoList2.add(new FileInfo("f", "fpath", 0, 0, 0, 0, null, true));
        fileInfoList2.add(new FileInfo("g", "gpath", 0, 0, 0, 0, null, true));

    }

    // Test to see if the list gets alphabetized.
    @Test
    public void testNumToAdd() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Add the first set of files
        session.createEditorSessionData(false, false, fileInfoList1, "sel");
        // The second file list should have two new entries
        Assert.assertEquals(4, session.numToBeAdded(fileInfoList2));
    }

    @Test
    public void testNumToDelete() {
        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Add the first set of files
        session.createEditorSessionData(false, false, fileInfoList1, "sel");
        // The second file list should have two new entries
        Assert.assertEquals(2, session.numToBeDeleted(fileInfoList2));
    }
    
    @Test
    public void testEquality() {
        session = sessionMap.switchEditorSession("Session 1");
        session.createEditorSessionData(false, false, fileInfoList1, "sel");
        Assert.assertEquals(false, session.isIdentical(fileInfoList2));
        Assert.assertEquals(true, session.isIdentical(fileInfoList1));
    }
    
    @Test
    public void testEqualityReversed() {
        session = sessionMap.switchEditorSession("Session 1");
        session.createEditorSessionData(false, false, fileInfoList2, "sel");
        Assert.assertEquals(false, session.isIdentical(fileInfoList1));
        Assert.assertEquals(true, session.isIdentical(fileInfoList2));
    }
    
    
    // Runs after each test.
    @After
    public void tearDown() {
        // Clear the data.
        DataUtil.unitTestClear();
        data = null;
    }

}
