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
 * This test makes sure session.updateFilePathList(...) behaves
 * correctly.  This is simple.  updateFilePathList must block
 * replacement if session is in snapshot mode or if the
 * list is empty.
 * 
 * updateFilePathList is called all the time, whenever one
 * of the handlers is used.
 * 
 * @author chris
 *
 */
public class SnapshotTest {

    DataUtil data = null;
    SessionMap sessionMap = null;
    EditorSession session = null;
    List<FileInfo> fileInfoList1 = new ArrayList<>();
    List<FileInfo> fileInfoList2 = new ArrayList<>();
    List<FileInfo> fileInfoList3 = new ArrayList<>();

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

        fileInfoList2.add(new FileInfo("no1", "no1path", 0, 0, 0, 0, null, true));
        fileInfoList2.add(new FileInfo("no2", "no2path", 0, 0, 0, 0, null, true));
        fileInfoList3.add(new FileInfo("yes1", "yes1path", 0, 0, 0, 0, null, true));
        fileInfoList3.add(new FileInfo("yes2", "yes2path", 0, 0, 0, 0, null, true));

    }

    // Updating session data should be blocked if in ss mode.
    // We also rely on alpha here
    @Test
    public void testSnapshotOn() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Add the first set of files, establish the snap shot.
        session.createEditorSessionData(true, true, fileInfoList1);
        // Update with a new set of files, should not happen.
        session.updateFilePathList(fileInfoList2);
        // Check to see whose there
        List<FileInfo> fList = session.getFileInfoList();
        // Array length should be 3
        Assert.assertEquals(3,fList.size() );
        // And because we set alpha true, we can test this...
        Assert.assertEquals("a" ,fList.get(0).getFileName() );
        Assert.assertEquals("b" ,fList.get(1).getFileName() );
        Assert.assertEquals("c" ,fList.get(2).getFileName() );
    }

    // Updating session file lists should be allowed if ss is off.
    // We also rely on alpha here
    @Test
    public void testSnapshotOff() {

        // Create a new session and make it the current one.
        session = sessionMap.switchEditorSession("Session 1");
        // Add the first set of files, establish the snap shot.
        session.createEditorSessionData(true, false, fileInfoList1);
        // Update with a new set of files.  Remember, this is a replace.
        session.updateFilePathList(fileInfoList3);
        // Check to see whose there
        List<FileInfo> fList = session.getFileInfoList();
        // Array length should be 3
        Assert.assertEquals(2,fList.size() );
        // And because we set alpha true, we can test this...
        Assert.assertEquals("yes1" ,fList.get(0).getFileName() );
        Assert.assertEquals("yes2" ,fList.get(1).getFileName() );
    }
    
    // Runs after each test.
    @After
    public void tearDown() {
        // Clear the data.
        DataUtil.unitTestClear();
        data = null;
    }
}
