/**
 *
 */
package Common;

import Logger.Logger;

/**
 * @author curious-odd-man
 *
 */
public final class RC
{
    /**
     * @brief Return code enumerator must include all possible Return Codes,
     *        errors for all classes
     *
     */
    public static enum RETURNCODES
    {
        RC_OK,
        RC_NOK,

        RC_SECURITY_FAILURE,
        RC_SECURITY_BREACH,

        FAIL_TO_LAUNCH
    }

    /**
     * @brief Adds log entry if @a c is not RC_OK adding caller function name.
     *
     * @param c
     *            RETURNCODES value.
     * @return @a c
     */
    public static RETURNCODES check(RETURNCODES c)
    {
        if (!c.equals(RETURNCODES.RC_OK))
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
