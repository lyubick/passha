/**
 *
 */
package Main;

/**
 * @author curious-odd-man
 *
 */
public class PasswordsCollection
{
    SpecialPassword passwords[];

    public void generateSpecialPassword(String name, String comment, String hotKey, MasterPassword mpwd)
    {

    }

    private void addSpecialPassword(SpecialPassword password)
    {
    }

    public SpecialPassword getSpecialPassword(int idx)
    {
        if (idx > 0 && idx < passwords.length)
        {
            return passwords[idx];
        }

        return null;
    }

}
