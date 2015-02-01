/**
 *
 */
package Common;

/**
 * @author lyubick
 *
 */
public final class Exceptions extends Throwable
{
    private CODES code;

    public static enum CODES
    {
        INIT_FAILURE
    }

    public CODES getCode()
    {
        return code;
    }

    public void setCode(CODES code)
    {
        this.code = code;
    }

    public Exceptions(CODES code)
    {
        this.code = code;
    }
}
