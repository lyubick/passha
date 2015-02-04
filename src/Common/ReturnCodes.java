/**
 *
 */
package Common;


/// improvement:
/// i propose to convert ReturnCodes to class and make member method "ReturnCode rc(ReturnCode c);"
/// and if c is error code write log to console/file if applicable
/// this way if we will instead of writing "return ReturnCodes.RC_NOK" - return ReturnCodes.rc(ReturnCodes.RC_NOK);
/// we can get notified of any errors
/// also we can think of other implementations of similar functionality, like return new ReturnCode(RC_NOK);

/**
 * @author lyubick
 *
 */

public enum ReturnCodes {
	RC_OK,
	RC_NOK,
	RC_SECURITY_FAILURE,
	RC_SECURITY_BREACH
}
