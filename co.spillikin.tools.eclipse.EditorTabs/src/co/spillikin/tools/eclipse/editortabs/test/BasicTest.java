package co.spillikin.tools.eclipse.editortabs.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.FileInfo;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;
import co.spillikin.tools.eclipse.editortabs.util.DataUtil;

/**
 * Tests some basics. Initial states, etc.
 * @author chris
 *
 */
public class BasicTest {

    // Check the initial state of a session map and session.
    @Test
    public void testIsNull() {

        DataUtil data = DataUtil.unitTestInitialize("Test workspace name");
        SessionMap sessionMap = data.getSessionMap();

        // There should be nothing to get. Both name and session are null.
        String shouldBeNull = sessionMap.getCurrentSessionName();
        EditorSession shouldAlsoBeNull = sessionMap.getCurrentEditorSession();
        Assert.assertNull(shouldBeNull);
        Assert.assertNull(shouldAlsoBeNull);

        // Getting by name should also return null.
        shouldAlsoBeNull = sessionMap.getEditorSession("newSession");
        Assert.assertNull(shouldAlsoBeNull);

        // Now create a session with nothing in it.
        // Both current name and the session itself should be not null.
        sessionMap.switchEditorSession("newSession");
        String shouldBeNewSession = sessionMap.getCurrentSessionName();
        Assert.assertEquals(shouldBeNewSession, "newSession");
        EditorSession shouldBeASession = sessionMap.getCurrentEditorSession();
        Assert.assertNotNull(shouldBeASession);

        // Now look inside that session. Should be an empty list but not null.
        List<FileInfo> shouldBeEmpty = shouldBeASession.getFileInfoList();
        Assert.assertNotNull(shouldBeEmpty);

        // Finally check the length
        Assert.assertEquals(0, shouldBeEmpty.size());
    }

}
