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
        INIT_FAILURE,

        FILE_NAME_ABSENT,
        FILE_DOES_NOT_EXISTS,

        WRITE_ERROR,
        READ_ERROR,
        CLOSE_ERROR,

        INSTANCE_ALREADY_EXISTS,
        NO_INSTANCE_EXISTS,

        MISSING_MANDATORY_DATA,

        BLACK_MAGIC
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
        Logger.printError("Function " + methodName + "() from " + className + "@" + line + " thrown: " + code.toString() + "!");
        this.code = code;
    }
}
