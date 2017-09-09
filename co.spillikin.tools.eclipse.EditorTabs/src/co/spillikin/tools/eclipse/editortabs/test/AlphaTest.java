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
 * Check to see if the alphabetize function works.
 * 
 * @author chris
 *
 */
public class AlphaTest {

    DataUtil data = null;
    SessionMap sessionMap = null;
    EditorSession session = null;
    List<FileInfo> fileInfoList = new ArrayList<>();

    // Runs before each test.
    @Before
    public void setup() {
        // None of these can fail unless the JVM is out of memory.
        // We simply create spaces in containers.
        data = DataUtil.unitTestInitialize("Test workspace name");
        // Get session [name - EditorSession] map for this workspace.
        sessionMap = data.getSessionMap();
        // Create our mock file list, out of order.
        fileInfoList.add(new FileInfo("c", "cpath", 0, 0, 0, 0, null, true));
        fileInfoList.add(new FileInfo("a", "apath", 0, 0, 0, 0, null, true));
        fileInfoList.add(new FileInfo("b", "bpath", 0, 0, 0, 0, null, true));

    }

    // Test to see if the list gets alphabetized.
    @Test
    public void testAlpha() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Mark to alpha and no snapshot.  Add files that are not in alpha order.
        session.createEditorSessionData(true, false, fileInfoList, "selected thing");
        // Extract and check for order.
        // Get the list.  Should be alphabetical
        List<FileInfo> fl = session.getFileInfoList();
        Assert.assertNotNull(fl);
        Assert.assertEquals(fl.size(), 3);
        Assert.assertEquals("a", fl.get(0).getFileName());
        Assert.assertEquals("b", fl.get(1).getFileName());
        Assert.assertEquals("c", fl.get(2).getFileName());
        // fail("Not yet implemented");
    }

    // Test to see if the list is in it's original order.
    @Test
    public void testOriginalOrder() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Mark to NOT alpha and no snapshot.  Add files that are not in alpha order.
        session.createEditorSessionData(false, false, fileInfoList, "selected thing");
        // Extract and check for order.
        // Get the list.  Should be alphabetical
        List<FileInfo> fl = session.getFileInfoList();
        Assert.assertNotNull(fl);
        Assert.assertEquals(fl.size(), 3);
        Assert.assertEquals("c", fl.get(0).getFileName());
        Assert.assertEquals("a", fl.get(1).getFileName());
        Assert.assertEquals("b", fl.get(2).getFileName());

    }

    // Runs after each test.
    @After
    public void tearDown() {
        // Clear the data.
        DataUtil.unitTestClear();
        data = null;
    }

}
