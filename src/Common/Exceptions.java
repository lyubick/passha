/**
 *
 */
package Common;

import Logger.Logger;

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

        THE_END,
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
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        // The last element of the array represents the bottom of the stack,
        // which is the least recent method invocation in the sequence.
        StackTraceElement e = stacktrace[2];// maybe this number needs to be
                                            // corrected
        String methodName = e.getMethodName();
        String className = e.getClassName();
        int line = e.getLineNumber();
        Logger.printError("Function " + methodName + "() from " + className + "@" + line
                + " thrown: " + code.toString() + "!");
        this.code = code;
    }
}
