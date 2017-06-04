package co.spillikin.tools.eclipse.editortabs.spareparts;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.BevelBorder;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

import co.spillikin.tools.eclipse.editortabs.model.EditorSession;
import co.spillikin.tools.eclipse.editortabs.model.SessionMap;

public class UIElements {
    
    // TODO  get rid of me, sample code
    public JPopupMenu popup;

    // Our controls
    private Button alphaToggle;
    private Button snapshotToggle;
    private Label sessionSelectLabel;

    // All group data, read only.
    private SessionMap sessionMap;
    // Button states
    Boolean okPressed = false;
    // I could have just queried the buttons when OK hit, but am not sure
    // those objects exist anymore at that point.
    Boolean keepAlphabetical;
    Boolean isSnapshot;

    // The selected group name from the Combo.
    private String selectedSessionName = null;

    // ==================  sample code ===================

    public void PopupMenuExample() {
        popup = new JPopupMenu();
        ActionListener menuListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.out
                    .println("Popup menu item [" + event.getActionCommand() + "] was pressed.");
            }
        };
        JMenuItem item;
        popup.add(item = new JMenuItem("Left", new ImageIcon("1.gif")));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Center", new ImageIcon("2.gif")));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Right", new ImageIcon("3.gif")));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.add(item = new JMenuItem("Full", new ImageIcon("4.gif")));
        item.setHorizontalTextPosition(JMenuItem.RIGHT);
        item.addActionListener(menuListener);
        popup.addSeparator();
        popup.add(item = new JMenuItem("Settings . . ."));
        item.addActionListener(menuListener);

        popup.setLabel("Justification");
        popup.setBorder(new BevelBorder(BevelBorder.RAISED));
        // popup.addPopupMenuListener(new PopupPrintListener());

        // addMouseListener(new MousePopupListener());
    }

    /**
     * Code to create a right click popup menu in a button.
     * @param container
     */
    private void rightClickPopupMenuButton(Composite container) {

        Button sessionSelectButton;
        sessionSelectButton = new Button(container, SWT.FLAT);
        sessionSelectButton.setText("Right Click to select a group");

        // A list of strings for popup.
        String[] groupList = null;
        Menu popupMenu = new Menu(container);
        for (String name : groupList) {
            MenuItem newItem = new MenuItem(popupMenu, SWT.NONE);
            newItem.setText(name);
            newItem.setData(name);
            addListener(newItem);
        }
        sessionSelectButton.setMenu(popupMenu);
    }

    // This is the listener for the popup menu.
    private void addListener(MenuItem b) {
        b.addSelectionListener(new SelectionListener() {

            public void widgetSelected(SelectionEvent e) {
                Widget w = e.widget;
                // widgetSelected Widget to string MenuItem {Refresh}
                Object data = w.getData();
                // If a new group was selected, update all state
                // and controls in dialog.
                if (data instanceof String) {
                    selectedSessionName = (String) data;
                    // Button is a class member variable
                    // sessionSelectButton.setText(selectedSessionName);
                    EditorSession session = sessionMap.getEditorSession(selectedSessionName);
                    isSnapshot = session.getIsSnapshot();
                    keepAlphabetical = session.getKeepAlphabetical();
                    alphaToggle.setSelection(keepAlphabetical);
                    snapshotToggle.setSelection(isSnapshot);

                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                Widget w = e.widget;
                System.out.println("widgetDefaultSelected Widget to string " + w);

            }
        });
    }

}
