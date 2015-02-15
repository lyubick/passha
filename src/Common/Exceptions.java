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
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private CODES code;

    public static enum CODES
    {
        INIT_FAILURE,

        FILE_NAME_ABSENT,
        FILE_DOES_NOT_EXISTS,

        WRITE_ERROR,

        BLACK_MAGIC
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
