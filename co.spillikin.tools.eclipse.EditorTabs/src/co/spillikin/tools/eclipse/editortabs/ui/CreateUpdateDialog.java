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

import java.util.ResourceBundle;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import static co.spillikin.tools.eclipse.editortabs.Constants.*;

public class CreateUpdateDialog extends TitleAreaDialog {

    // Creating or Updating
    private Boolean create;

    // Text control area where the user will enter the new session name.
    private Text newSessionNameControl;
    // The default name used when the dialog is first opened.
    private String currentSessionName;
    // The resulting new session name
    private String newSessionName;
    // Did the user press the OK button, if not cancel.
    private Boolean okPressed = false;
    // Toggle for alphabetical
    private Boolean keepAlphabetical = false;
    // Toggle for isSnapshot
    private Boolean isSnapshot = false;
    // Checkbox to switch to this group.
    // This is not saved in the data model but is used by the create handler.
    private Boolean saveonly = false;
    // Update snapshot toggle result
    private Boolean updateSnapshot = false;
    // This will be true only if Update and if there is a difference between
    // existing files and open tabs.
    private Boolean showUpdateSnapshotControl = false;
    // If ss is to be updated, give use some info
    private int numToAdd = 0;
    private int numToDelete = 0;
    // ResourceBundle with associated Strings for the dialog.
    private ResourceBundle resBundle;

    /**
     * Create a new Create New Session (save as) dialog where
     * the defaults come from the current session. 
     * OR update an existing session's information.
     * 
     * @param parentShell
     * @param currentSessionName, may be null.
     * @param isSnapshot
     * @param keepAlphabetical
     */
    public CreateUpdateDialog(Shell parentShell, ResourceBundle resBundle, Boolean create,
        String currentSessionName, Boolean isSnapshot, Boolean keepAlphabetical, int numToAdd,
        int numToDelete, Boolean doNotShowUpdateSnapshotControl) {
        super(parentShell);
        this.create = create;
        this.currentSessionName = currentSessionName;
        this.isSnapshot = isSnapshot;
        this.keepAlphabetical = keepAlphabetical;
        this.resBundle = resBundle;
        // updateSnapshot options, only if updating.
        if (create == false) {
            // look closely Chris, there's a ! here.
            this.showUpdateSnapshotControl = !doNotShowUpdateSnapshotControl;
            this.numToAdd = numToAdd;
            this.numToDelete = numToDelete;
        } else {
            this.showUpdateSnapshotControl = false;
        }

    }

    /**
     * If there is no current session name to default from, use this
     * constructor.
     * We are by definition nut updating, so create forced to true.
     * @param parentShell
     * @param resBundle
     */
    public CreateUpdateDialog(Shell parentShell, ResourceBundle resBundle) {
        // Set "the same" to true to prevent the additional checkbox from 
        // appearing.
        this(parentShell, resBundle, true, null, false, false, 0, 0, true);

    }

    @Override
    public void create() {
        super.create();

        // If we are creating a new editor session
        if (create) {
            setTitle(resBundle.getString(CREATE_TITLE_KEY));
            setMessage(resBundle.getString(CREATE_DESCRIPTION_KEY), IMessageProvider.INFORMATION);
            // If we are updating an existing session.
        } else {
            setTitle(resBundle.getString(UPDATE_TITLE_KEY));
            setMessage(resBundle.getString(UPDATE_DESCRIPTION_KEY), IMessageProvider.INFORMATION);
        }
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);

        createDialogControls(container);

        return area;
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private void createDialogControls(Composite container) {

        // Label to the left of the name to be filled in
        Label lbtGroupName = new Label(container, SWT.NONE);
        if (create) {
            lbtGroupName.setText(resBundle.getString(CREATE_SESSION_NAME_DESCRIPTION_KEY));
        } else {
            lbtGroupName.setText(resBundle.getString(UPDATE_SESSION_NAME_DESCRIPTION_KEY));
        }
        GridData dataFirstName = new GridData();
        dataFirstName.grabExcessHorizontalSpace = true;
        dataFirstName.horizontalAlignment = GridData.FILL;

        // Name to the right of description
        newSessionNameControl = new Text(container, SWT.BORDER);
        newSessionNameControl.setLayoutData(dataFirstName);
        // Prefill with "new session" (if null) or "copy of " current session.
        String sn = currentSessionName;
        // If update, we should never have been called with a null current session.
        // If create, prefill with either "new session" or "copy of foo session"
        if (create) {
            if (sn == null) {
                sn = resBundle.getString(SESSION_NAME_IF_NULL_FOR_NEW_KEY);
            } else {
                sn = resBundle.getString(SESSION_NAME_PREPEND_KEY) + sn;
            }
        }
        newSessionNameControl.setText(sn);

        // Label to the left of the alpha check box
        Label lbAlpha = new Label(container, SWT.NONE);
        lbAlpha.setText(resBundle.getString(ALPHA_CHECKBOX_DESCRIPTION_KEY));

        // Alpha button to the right
        Button alphaToggle = new Button(container, SWT.CHECK);
        alphaToggle.setText(resBundle.getString(ALPHA_CHECKBOX_KEY));
        alphaToggle.setSelection(keepAlphabetical);
        alphaToggle.addListener(SWT.Selection, event -> onAlphaSelect(alphaToggle));

        // Label to the left of the snapshot check box
        Label lbSnap = new Label(container, SWT.NONE);
        lbSnap.setText(resBundle.getString(SNAPSHOT_CHECKBOX_DESCRIPTION_KEY));

        // Checkbox to the right
        Button snapshotToggle = new Button(container, SWT.CHECK);
        snapshotToggle.setText(resBundle.getString(SNAPSHOT_CHECKBOX_KEY));
        snapshotToggle.setSelection(isSnapshot);
        snapshotToggle.addListener(SWT.Selection, event -> onSnapshotSelect(snapshotToggle));

        // Save only checkbox if in create mode.
        if (create) {
            // Label to the left of the lbSaveonly check box
            Label lbSaveonly = new Label(container, SWT.NONE);
            lbSaveonly.setText(resBundle.getString(SAVEONLY_CHECKBOX_DESCRIPTION_KEY) );

            // Checkbox to the right
            Button saveonlyToggle = new Button(container, SWT.CHECK);
            // The button label needs the name of the current session  too
            // "stay with <no current session>   or  stay with some session name.
            sn = currentSessionName;
            if (sn == null) {
                sn = resBundle.getString(SESSION_NAME_IF_NULL_FOR_DISPLAY_KEY);
            }
            sn = resBundle.getString(SAVEONLY_CHECKBOX_KEY) + sn;
            saveonlyToggle.setText(sn);
            saveonlyToggle.setSelection(saveonly);
            saveonlyToggle.addListener(SWT.Selection, event -> onSaveonlySelect(saveonlyToggle));
        }
        
        // Update snapshot option
        // This will only be true if 
        // 1: we are in update mode and
        // 2: The open tabs are different from the snapshotted ones.
        if (showUpdateSnapshotControl ) {
            // Label to the left of the lbUpdateSs check box
            Label lbUpdateSs = new Label(container, SWT.NONE);
            lbUpdateSs.setText(numToAdd + " " + resBundle.getString(SSUPDATE_CHECKBOX_DESCRIPTION_1_KEY) + 
                "\n" + 
                numToDelete +  " " + resBundle.getString(SSUPDATE_CHECKBOX_DESCRIPTION_2_KEY)  );

            // Checkbox to the right
            Button ssupdateToggle = new Button(container, SWT.CHECK);
            ssupdateToggle.setText(resBundle.getString(SSUPDATE_CHECKBOX_KEY));
            ssupdateToggle.setSelection(saveonly);
            ssupdateToggle.addListener(SWT.Selection, event -> onUpdateSsSelect(ssupdateToggle));
        }

    }

    // When the user clicks the alpha checkbox update.
    private void onAlphaSelect(Button cb) {
        keepAlphabetical = cb.getSelection();
    }

    // When the user clicks the snapshot checkbox update.
    private void onSnapshotSelect(Button cb) {
        isSnapshot = cb.getSelection();
    }

    // When the user clicks the saveonly checkbox update.
    private void onSaveonlySelect(Button cb) {
        saveonly = cb.getSelection();
    }

    // When the user clicks the update snapshot checkbox update.
    private void onUpdateSsSelect(Button cb) {
        updateSnapshot = cb.getSelection();
    }
    
    /**
     * Must copy out text from control as it will go away.
     */
    @Override
    protected void okPressed() {
        okPressed = true;
        newSessionName = newSessionNameControl.getText();
        // Do this last.  It closes the dialog and invalidates controls.
        super.okPressed();
    }

    public Boolean getOkPressed() {
        return okPressed;
    }

    /**
     * Get the new session name the user typed in.
     * @return
     */
    public String getSessionName() {
        return newSessionName;
    }

    /**
     * Return true if the user checked the snapshot checkbox.
     * @return
     */
    public Boolean getIsSnapshot() {
        return isSnapshot;
    }

    /**
     * Return true if the user checked the alpha checkbox.
     * @return
     */
    public Boolean getKeepAlphabetical() {
        return keepAlphabetical;
    }

    /**
     * Return true if the caller is to save a new session
     * only but not switch to it.
     * @return
     */
    public Boolean getSaveonly() {
        return this.saveonly;
    }
    
    /**
     * Return true if we are to set the current 
     * fille tab set to this session.
     * @return
     */
    public Boolean getUpdateSnapshot() {
        return updateSnapshot;
    }
}
