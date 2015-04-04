/**
 * 
 */
package UI;

/**
 * @author andrejs.lubimovs
 *
 */
import javax.swing.JFrame;

import Common.Exceptions;
import Common.Exceptions.XC;
import Main.Terminator;

import com.melloware.jintellitype.HotkeyListener;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;

public class HotKeyAgent extends JFrame implements HotkeyListener, IntellitypeListener
{
    /**
     * 
     */
    private static final long  serialVersionUID = 1L;
    private static HotKeyAgent self             = null;

    private HotKeyAgent()
    {
        self = this;
    }

    public static void init()
    {
        if (self != null) Terminator.terminate(new Exceptions(XC.INSTANCE_ALREADY_EXISTS));
        self = new HotKeyAgent();
    }

    public static HotKeyAgent getInstance()
    {
        if (self == null) Terminator.terminate(new Exceptions(XC.INIT_FAILURE));
        return self;
    }

    public void register()
    {
        JIntellitype.getInstance();
        JIntellitype.getInstance().registerHotKey(1, JIntellitype.MOD_WIN, (int) 'A');
        JIntellitype.getInstance().addHotKeyListener(self);
        JIntellitype.getInstance().addIntellitypeListener(self);
    }

    public void unregister()
    {
        JIntellitype.getInstance().unregisterHotKey(1);
        JIntellitype.getInstance().cleanUp();
    }

    @Override
    public void onIntellitype(int arg0)
    {
        if (arg0 == 1) Logger.Logger.printDebug("WINDOWS+A hotkey pressed");
    }

    @Override
    public void onHotKey(int arg0)
    {
        if (arg0 == 1) Logger.Logger.printDebug("WINDOWS+A hotkey pressed");
    }
}