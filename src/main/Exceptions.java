/**
 *
 */
package main;

/**
 * @author lyubick
 *
 */
public final class Exceptions extends Throwable
{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private XC                code;

    private String            text             = null;

    public static enum XC
    {
        // ONLY Class initialization failure
        INIT_FAILURE,

        // ONLY Singleton class Exceptions
        INSTANCE_ALREADY_EXISTS,
        INSTANCE_DOES_NOT_EXISTS,

        // ONLY FileIO class Exceptions
        FILE_DOES_NOT_EXISTS,
        DIR_DOES_NOT_EXIST,

        FILE_CREATE_ERROR,
        FILE_WRITE_ERROR,
        FILE_READ_ERROR,
        FILE_CLOSE_ERROR,

        DIR_CREATE_ERROR,

        // ONLY Button handlers
        HANDLER_DOES_NOT_EXIST,

        // ONLY New Password dialog exceptions
        MANDATORY_DATA_MISSING,
        PASSWORD_NAME_ALREADY_EXISTS,
        PASSWORD_SHORTCUT_ALREADY_IN_USE, // this error code should contain
                                          // conflicting password name in text;

        // Exception thrown ONLY on Login stage, indicating, that it could be
        // new user or Password is incorrect.
        USER_UNKNOWN,

        // Autologin exceptions
        UNABLE_TO_GET_PHYSICAL_ADDR,
        UNABLE_TO_GET_USERNAME,

        // main exception (thrown by regedit()), converted by caller to
        // specific.
        UNABLE_TO_EDIT_REGISTRY,
        UNABLE_TO_ADD_ENTRY,
        UNABLE_TO_DELETE_ENTRY,
        UNABLE_TO_RETRIEVE_ENTRY,

        // ONLY for methods, that convert Object to Bytes and Bytes to Object
        OBJECT_SERIALIZATION_FAILED,
        OBJECT_DESERIALIZATION_FAILED,

        // Exception that indicates LOAD failure, but program can continue with
        // DEFAULT presets
        DEFAULT_SETTINGS_USED,

        // Terminator exceptions
        RESTART_FAILED,

        RESTART,
        UPDATE,
        END,

        // Only Shortcuts form
        NO_SHORTCUTS_EXISTS,

        // General, can be used if error reasons are unknown
        ERROR
    }

    public XC getCode()
    {
        return code;
    }

    public void setCode(XC code)
    {
        this.code = code;
    }

    public Exceptions(XC code)
    {
        this.code = code;
    }

    public Exceptions setText(String text)
    {
        this.text = text;
        return this;
    }

    public String getText()
    {
        if (text != null) return text;
        return "";
    }
}
