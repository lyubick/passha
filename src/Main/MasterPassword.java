/**
 *
 */
package Main;

import SHA.SHA;

/**
 * @author curious-odd-man
 *
 */
public final class MasterPassword
{
    private static byte[] masterPasswordHash;
    private static SHA sha;

    public MasterPassword(String pwd)
    {
        sha = new SHA();
        masterPasswordHash = sha.getBytesSHA512(pwd.getBytes());
    }

    public static byte[] getHash(int count)
    {
        byte[] tmp = masterPasswordHash.clone();

        while(count-- > 0)
        {
            tmp = sha.getBytesSHA512(tmp);
        }

        return tmp;
    }
}
