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

public class EmptyListTest {

    DataUtil data = null;
    SessionMap sessionMap = null;
    EditorSession session = null;
    List<FileInfo> fileInfoList1 = new ArrayList<>();
    List<FileInfo> emptyList = new ArrayList<>();

    // Runs before each test.
    @Before
    public void setup() {
        // None of these can fail unless the JVM is out of memory.
        // We simply create spaces in containers.
        data = DataUtil.unitTestInitialize("Test workspace name");
        // Get session [name - EditorSession] map for this workspace.
        sessionMap = data.getSessionMap();
        // Create our mock file list, out of order.
        fileInfoList1.add(new FileInfo("a", "apath", 0, 0, 0, 0, null, true));
    }

    // Updating session data should be blocked if the list is empty
    // all tabs are closed.
    @Test
    public void testEmpty() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Add the first set of files, establish the snap shot.
        session.createEditorSessionData(false, false, fileInfoList1, "sel");
        // Update with a new set of files, should not happen.
        session.updateFilePathList(emptyList, "fake current tab");
        // Check to see whose there
        List<FileInfo> fList = session.getFileInfoList();
        // Array length should be 1
        Assert.assertEquals(1,fList.size() );
        Assert.assertEquals("a" ,fList.get(0).getFileName() );
    }
    
    // Runs after each test.
    @After
    public void tearDown() {
        // Clear the data.
        DataUtil.unitTestClear();
        data = null;
    }

}
