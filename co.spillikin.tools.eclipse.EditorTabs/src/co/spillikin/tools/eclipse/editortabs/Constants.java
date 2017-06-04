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
package co.spillikin.tools.eclipse.editortabs;

/**
 * Version information and other constants.
 * 
 * @author chris
 *
 */
public class Constants {

    // Information stored in the XML file.
    // Version for backwards comparability
    public static final Integer VERSION_MAJOR = 1;
    public static final Integer VERSION_MINOR = 0;
    public static final String AUTHOR = "Christopher Hull";
    public static final String COMPANY = "Spillikin Aerospace";

    public static final String DIALOG_TITLE = "EditorSessions Plugin";

    // State saved as...
    public static final String FILENAME = "EditorSessionsData.xml";

    // Our properties file
    public static final String RESOURCE_FILE_NAME = "resource.dialogstrings";
    // Resource string keys
    // Create Update dialog Title and Description
    public static final String CREATE_TITLE_KEY = "create_title";
    public static final String CREATE_DESCRIPTION_KEY = "create_description";
    public static final String UPDATE_TITLE_KEY = "update_title";
    public static final String UPDATE_DESCRIPTION_KEY = "update_description";
    // Create Update name descriptions
    public static final String CREATE_SESSION_NAME_DESCRIPTION_KEY = "create_session_name_description";
    public static final String UPDATE_SESSION_NAME_DESCRIPTION_KEY = "update_session_name_description";

    // Checkbox titles.  Descriptions appear to the left of the checkbox
    // and give more detail
    public static final String ALPHA_CHECKBOX_KEY = "alpha_checkbox";
    public static final String ALPHA_CHECKBOX_DESCRIPTION_KEY = "alpha_checkbox_description";
    public static final String SNAPSHOT_CHECKBOX_KEY = "snapshot_checkbox";
    public static final String SNAPSHOT_CHECKBOX_DESCRIPTION_KEY = "snapshot_checkbox_description";
    public static final String SAVEONLY_CHECKBOX_KEY = "saveonly_checkbox";
    public static final String SAVEONLY_CHECKBOX_DESCRIPTION_KEY = "saveonly_checkbox_description";
    // optional snapshot update
    public static final String SSUPDATE_CHECKBOX_KEY = "ssupdate_checkbox";
    public static final String SSUPDATE_CHECKBOX_DESCRIPTION_1_KEY = "ssupdate_checkbox_description_1";
    public static final String SSUPDATE_CHECKBOX_DESCRIPTION_2_KEY = "ssupdate_checkbox_description_2";

    // Replacement text for session name
    public static final String SESSION_NAME_PREPEND_KEY = "session_name_prepend";
    public static final String SESSION_NAME_IF_NULL_FOR_NEW_KEY = "session_name_if_null_for_new";
    public static final String SESSION_NAME_IF_NULL_FOR_DISPLAY_KEY = "session_name_if_null_for_display";

    // Select dialog Title and Description
    public static final String SELECT_TITLE_KEY = "select_title";
    public static final String SELECT_DESCRIPTION_KEY = "select_description";
    public static final String SELECT_SESSION_NAME_DESCRIPTION_KEY = "select_session_name_description";

    // Import and Export Dialogs
    public static final String EXPORT_TITLE_KEY = "export_title";
    public static final String EXPORT_MESSAGE_KEY = "export_message";
    public static final String IMPORT_TITLE_KEY = "import_title";
    public static final String IMPORT_MESSAGE_KEY = "import_message";

    // Info and Error Dialogs

    // Fatal error dialog
    public static final String ERROR_FATAL_TITLE_KEY = "error_fatal_title";
    public static final String ERROR_FATAL_MESSAGE_1_KEY = "error_fatal_message_1";
    public static final String ERROR_FATAL_MESSAGE_2_KEY = "error_fatal_message_2";

    // Into There are no open tabs to save
    public static final String INFO_NO_OPEN_TITLE_KEY = "info_no_open_title";
    public static final String INFO_NO_OPEN_MESSAGE_KEY = "info_no_open_message";

    // Error There is no current session selected
    public static final String ERROR_NO_CURRENT_TITLE_KEY = "error_no_current_title";
    public static final String ERROR_NO_CURRENT_MESSAGE_KEY = "error_no_current_message";

    // Error The session name can not be blank
    public static final String ERROR_BLANK_NAME_TITLE_KEY = "error_blank_name_title";
    public static final String ERROR_BLANK_NAME_MESSAGE_KEY = "error_blank_name_message";

    // Error Name already in use
    public static final String ERROR_DUPLICATE_NAME_TITLE_KEY = "error_duplicate_name_title";
    public static final String ERROR_DUPLICATE_NAME_MESSAGE_KEY = "error_duplicate_name_message";

    // Into New session created
    public static final String INFO_NEW_SESSION_TITLE_KEY = "info_new_session_title";
    public static final String INFO_NEW_SESSION_MESSAGE_KEY = "info_new_session_message";

    // Into Session changed to
    public static final String INFO_CHANGED_SETTINGS_TITLE_KEY = "info_changed_settings_title";
    public static final String INFO_CHANGED_SETTINGS_MESSAGE_KEY = "info_changed_settings_message";

    // Warning No current session
    public static final String WARN_NO_CURRENT_TITLE_KEY = "warn_no_current_title";
    public static final String WARN_NO_CURRENT_MESSAGE_KEY = "warn_no_current_message";

    // Into No session to select
    public static final String INFO_NO_SESSIONS_TITLE_KEY = "info_no_sessions_title";
    public static final String INFO_NO_SESSIONS_MESSAGE_KEY = "info_no_sessions_message";

    // Info Session selected
    public static final String INFO_SELECTED_SESSION_TITLE_KEY = "info_selected_session_title";
    public static final String INFO_SELECTED_SESSION_MESSAGE_KEY = "info_selected_session_message";

    // =======================================
    // Exceptions
    public static final String EX_PLUGIN_CANT_FIND_DATA = "Plugin could not find it's data";
}
