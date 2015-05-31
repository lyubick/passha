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

        WRITE_ERROR,
        READ_ERROR,
        CLOSE_ERROR,

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

        // TODO
        END,
        END_DISCARD
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
