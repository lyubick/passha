/**
 *
 */
package Common;

import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public final class Return
{
    /**
     * @brief Return code enumerator must include all possible Return Codes,
     *        errors for all classes
     *
     */
    public static enum RC
    {
        OK,
        NOK,
        ABEND,

        SECURITY_FAILURE,
        SECURITY_BREACH,

        NAME_ALREADY_TAKEN,

        FAIL_TO_LAUNCH,

        NONEXISTING_FUNCTION_CALL,
        FORM_DOES_NOT_EXIST,

    }

    /**
     * @brief Adds log entry if @a c is not RC_OK adding caller function name.
     *
     * @param c
     *            RETURNCODES value.
     * @return @a c
     */
    public static RC check(RC c)
    {
        if (!c.equals(RC.OK))
        {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            // The last element of the array represents the bottom of the stack,
            // which is the least recent method invocation in the sequence.
            StackTraceElement e = stacktrace[2];// maybe this number needs to be
                                                // corrected
            String methodName = e.getMethodName();
            Logger.printError("Function " + methodName + " returned error code " + c.toString()
                    + "!");
        }

        return c;
    }

}
