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

    public static enum XC
    {
        // ONLY Class initialization failure
        INIT_FAILURE,

        // ONLY Singleton class Exceptions
        INSTANCE_ALREADY_EXISTS,
        INSTANCE_DOES_NOT_EXISTS,

        // ONLY FileIO class Exceptions
        FILE_DOES_NOT_EXISTS,

        FILE_WRITE_ERROR,
        FILE_READ_ERROR,
        FILE_CLOSE_ERROR,

        // ONLY New Password dialog exceptions
        MANDATORY_DATA_MISSING,
        PASSWORD_NAME_ALREADY_EXISTS,

        // Exception thrown ONLY on Login stage, indicating, that it could be
        // new user or Password is incorrect.
        USER_UNKNOWN,

        // ONLY for methods, that convert Object to Bytes and Bytes to Object
        OBJECT_SERIALIZATION_FAILED,
        OBJECT_DESERIALIZATION_FAILED,

        // Exception that indicates LOAD failure, but program can continue with
        // DEFAULT presets
        DEFAULT_SETTINGS_USED,

        // Terminator exceptions
        RESTART_FAILED,

        RESTART,
        END,

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
}
