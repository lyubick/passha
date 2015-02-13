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
    /**@brief Adds log entry if @a c is not RC_OK adding caller function name.
     * @author curious-odd-man
     * @param c - ReturnCodes value.
     * @return @a c
     */
    public static ReturnCodes check(ReturnCodes c)
    {
        if (!c.equals(ReturnCodes.RC_OK))
        {
            StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
            // The last element of the array represents the bottom of the stack,
            // which is the least recent method invocation in the sequence.
            StackTraceElement e = stacktrace[2];// maybe this number needs to be
                                                // corrected
            String methodName = e.getMethodName();
            Logger.printDebug("Function " + methodName + " returned error code " + c.toString()
                    + "!");
        }

        return c;
    }
}
