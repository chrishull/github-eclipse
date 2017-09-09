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
package co.spillikin.tools.eclipse.editortabs.ui;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;

import static co.spillikin.tools.eclipse.editortabs.Constants.*;

import java.util.ResourceBundle;
import java.util.Set;

/**
 * This dialog handles the selection of a set of editor tabs.
 * The user can also change settings made when the set was created
 * from this dialog.
 * 
 * @author chris
 *
 */
public class SelectGroupDialog extends TitleAreaDialog {

    // Our controls
    private Button alphaToggle;
    private Button snapshotToggle;

    // All group data, read only.
    private SessionMap sessionMap;
    // Button states
    Boolean okPressed = false;
    // The selected group name from the Combo.
    private String selectedSessionName = null;
    // The current group name for display in dialog
    private String currentGroupName = null;
    // Alpahbetize on open
    Boolean keepAlphabetical;
    // Update continuously or snapshot.
    Boolean isSnapshot;
    // Resource bundle containing dialog strings
    ResourceBundle resBundle;

    /**
     * Initialize the dialog with default settings.
     * Current session can be null but sessionMap CAN NOT BE EMPTY.
     * @param parentShell
     * @param sessionMap. The current set of sessions and all associated data
     * mapped by name. Contains currenrtSessionName and previousSessionName.
     */
    public SelectGroupDialog(Shell parentShell, ResourceBundle resBundle,
        final SessionMap sessionMap) {
        super(parentShell);

        // The set of all groups to switch between
        // but DONT modify this set here.
        this.sessionMap = sessionMap;
        this.resBundle = resBundle;
        // Allow user to quickly switch to the previous session
        selectedSessionName = sessionMap.getPreviousSessionName();
        if (selectedSessionName == null) {
            selectedSessionName = sessionMap.getCurrentSessionName();
        }
        // If none is selected, grab the first one
        if (selectedSessionName == null) {
            selectedSessionName = sessionMap.getSessionNames().iterator().next();
        }
        // Finally just get the current name
        currentGroupName = sessionMap.getCurrentSessionName();
    }

    /**
     * Let this dialog be resizable.
     */
    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Creates the title and message for our dialog.
     */
    @Override
    public void create() {
        super.create();
        setTitle(resBundle.getString(SELECT_TITLE_KEY));
        setMessage(resBundle.getString(SELECT_DESCRIPTION_KEY), IMessageProvider.INFORMATION);

    }

    /**
     * Creates the rest of the dialog's contents, controls and 
     * instruction text.
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        // Only show the current group name if there is one.
        if (currentGroupName != null) {
            // Description to the left of the currently selected group
            Label lbCurrentGroupLeft = new Label(container, SWT.NONE);
            lbCurrentGroupLeft.setText(resBundle.getString(SELECT_CURRENT_GROUP_DESCRIPTION_KEY));
            // The currently selected group to it's right.
            Label lbCurrentGroupRight = new Label(container, SWT.NONE);
            lbCurrentGroupRight.setText(currentGroupName);
        }

        // The popup menu and it's associate description text
        createGroupsPopup(container);

        // Label to the left of the alpha check box
        Label lbAlpha = new Label(container, SWT.NONE);
        lbAlpha.setText(resBundle.getString(ALPHA_CHECKBOX_DESCRIPTION_KEY));
        // The alpha checkbox to the right.
        alphaToggle = new Button(container, SWT.CHECK);
        alphaToggle.setText(resBundle.getString(ALPHA_CHECKBOX_KEY));
        alphaToggle.addListener(SWT.Selection, event -> onAlphaSelect(alphaToggle));

        // Label to the left of the snapshot check box
        Label lbSnap = new Label(container, SWT.NONE);
        lbSnap.setText(resBundle.getString(SNAPSHOT_CHECKBOX_DESCRIPTION_KEY));
        // the snapshot toggle to the right.
        snapshotToggle = new Button(container, SWT.CHECK);
        snapshotToggle.setText(resBundle.getString(SNAPSHOT_CHECKBOX_KEY));

        snapshotToggle.addListener(SWT.Selection, event -> onSnapshotSelect(snapshotToggle));

        updateSessionDataAndButtonStates();
        return area;
    }

    /**
     * Create our Combo (what the rest of the world calls a popup menu)
     * which allows the user to select different sessions by name.
     * @param container
     */
    private void createGroupsPopup(Composite container) {

        // Left side, Label for drop down
        Label lbtGroupName = new Label(container, SWT.NONE);
        lbtGroupName.setText(resBundle.getString(SELECT_SESSION_NAME_DESCRIPTION_KEY));

        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        // Right side, Combo showing sessions to choose from
        Combo sessionSelectCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);

        sessionSelectCombo.addListener(SWT.Selection,
            event -> onDropdownNameSelected(sessionSelectCombo));
        int i = 0;
        int select = 0;
        Set<String> groupNameList = sessionMap.getSessionNames();
        for (String name : groupNameList) {
            sessionSelectCombo.add(name);
            if (name.equalsIgnoreCase(selectedSessionName)) {
                select = i;
            }
            i++;
        }
        sessionSelectCombo.select(select);

    }

    /**
     * Update button states and set globals based on selectedSessionName.
     * Call at dialog setup or whenever a different session is selected
     * via the Combo.  Be sure to set selectedSessionName first.
     */
    private void updateSessionDataAndButtonStates() {
        EditorSession session = sessionMap.getEditorSession(selectedSessionName);
        if (session != null) {
            keepAlphabetical = session.getKeepAlphabetical();
            alphaToggle.setSelection(keepAlphabetical);
            isSnapshot = session.getIsSnapshot();
            snapshotToggle.setSelection(isSnapshot);
        }
    }

    // When user selects a different name via the combo.
    private void onDropdownNameSelected(Combo combo) {
        selectedSessionName = combo.getText();
        updateSessionDataAndButtonStates();
    }

    // When user pushed the Alpha button, set it's global.
    private void onAlphaSelect(Button cb) {
        keepAlphabetical = cb.getSelection();
    }

    // When user pushed the Snapshot button, set it's global
    private void onSnapshotSelect(Button cb) {
        isSnapshot = cb.getSelection();
    }

    // One of the dialog terminating buttons was hit.  Ok or Cancel.
    @Override
    protected void okPressed() {
        okPressed = true;
        // Do this last if you are still reading controls at this point.
        // This closes the dialog and invalidates the controls
        super.okPressed();
    }

    /**
     * Return the session name that the user selected.
     * @return
     */
    public String getSelectedSessionName() {
        return selectedSessionName;
    }

    /**
     * Return true if the user checked the keepAlpahbetical buttson.
     * @return
     */
    public Boolean getKeepAlphabetical() {
        return keepAlphabetical;
    }

    /**
     * Return true if the user checked the isSnapshot button.
     * @return
     */
    public Boolean getIsSnapshot() {
        return isSnapshot;
    }

}
