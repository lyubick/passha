package org.kgbt.passha.desktop.autotype;

import org.kgbt.passha.core.logger.Logger;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyMapper {
    private static Map<Character, Integer> aKeyMap = null;
    private static List<Character> aShiftRequired = null;
    private static Robot aRobot = null;
    private static KeyMapper This = null;

    private static final int PRE_ALT_TAB_DELAY = 100;
    private static final int POST_ALT_TAB_DELAY = 100;
    private static final int TYPE_IN_DELAY = 50;

    private KeyMapper() {
        try {
            aRobot = new Robot();
        } catch (AWTException e) {
            Logger.printFatal("Failed to instantiate key-presser robot...");
        }

        aKeyMap = new HashMap<>();
        aShiftRequired = new ArrayList<>();

        for (char x = 'A'; x <= 'Z'; ++x) {
            aKeyMap.put(x, (int) x);
            aKeyMap.put((char) (x + 32), (int) x);
            aShiftRequired.add(x);
        }

        for (char x = '0'; x <= '9'; ++x)
            aKeyMap.put(x, (int) x);

        aKeyMap.put('`', KeyEvent.VK_BACK_QUOTE);
        aKeyMap.put('~', KeyEvent.VK_BACK_QUOTE);
        aShiftRequired.add('~');

        aKeyMap.put(' ', KeyEvent.VK_SPACE);

        aKeyMap.put('!', KeyEvent.VK_1);
        aShiftRequired.add('!');

        aKeyMap.put('@', KeyEvent.VK_2);
        aShiftRequired.add('@');

        aKeyMap.put('#', KeyEvent.VK_3);
        aShiftRequired.add('#');

        aKeyMap.put('$', KeyEvent.VK_4);
        aShiftRequired.add('$');

        aKeyMap.put('%', KeyEvent.VK_5);
        aShiftRequired.add('%');

        aKeyMap.put('^', KeyEvent.VK_6);
        aShiftRequired.add('^');

        aKeyMap.put('&', KeyEvent.VK_7);
        aShiftRequired.add('&');

        aKeyMap.put('*', KeyEvent.VK_8);
        aShiftRequired.add('*');

        aKeyMap.put('(', KeyEvent.VK_9);
        aShiftRequired.add('(');

        aKeyMap.put(')', KeyEvent.VK_0);
        aShiftRequired.add(')');

        aKeyMap.put('-', KeyEvent.VK_MINUS);
        aKeyMap.put('_', KeyEvent.VK_MINUS);
        aShiftRequired.add('_');

        aKeyMap.put('=', KeyEvent.VK_EQUALS);
        aKeyMap.put('+', KeyEvent.VK_EQUALS);
        aShiftRequired.add('+');

        aKeyMap.put('[', KeyEvent.VK_OPEN_BRACKET);
        aKeyMap.put('{', KeyEvent.VK_OPEN_BRACKET);
        aShiftRequired.add('{');

        aKeyMap.put(']', KeyEvent.VK_CLOSE_BRACKET);
        aKeyMap.put('}', KeyEvent.VK_CLOSE_BRACKET);
        aShiftRequired.add('}');

        aKeyMap.put('\\', KeyEvent.VK_BACK_SLASH);
        aKeyMap.put('|', KeyEvent.VK_BACK_SLASH);
        aShiftRequired.add('|');


        aKeyMap.put(';', KeyEvent.VK_SEMICOLON);
        aKeyMap.put(':', KeyEvent.VK_SEMICOLON);
        aShiftRequired.add(':');

        aKeyMap.put('\'', KeyEvent.VK_QUOTE);
        aKeyMap.put('"', KeyEvent.VK_QUOTE);
        aShiftRequired.add('"');

        aKeyMap.put(',', KeyEvent.VK_COMMA);
        aKeyMap.put('<', KeyEvent.VK_COMMA);
        aShiftRequired.add('<');

        aKeyMap.put('.', KeyEvent.VK_PERIOD);
        aKeyMap.put('>', KeyEvent.VK_PERIOD);
        aShiftRequired.add('>');

        aKeyMap.put('/', KeyEvent.VK_SLASH);
        aKeyMap.put('?', KeyEvent.VK_SLASH);
        aShiftRequired.add('?');
    }

    public static void typeString(String input, boolean altTab) {
        if (This == null)
            This = new KeyMapper();

        Logger.printDebug("Password to auto-type: " + input);

        // FIXME: More cool solution for scenario:
        //  1. In GUI User presses Ctrl+V
        //  2. Alt+Tab performed and auto-type occurs
        //  3. If User still keeps pressed buttons it interfere with auto-type
        //  Solution: Just release this combination
        aRobot.keyRelease(KeyEvent.VK_V);
        aRobot.keyRelease(KeyEvent.VK_CONTROL);

        if (altTab) {
            aRobot.delay(PRE_ALT_TAB_DELAY);
            aRobot.keyPress(KeyEvent.VK_ALT);
            aRobot.keyPress(KeyEvent.VK_TAB);
            aRobot.keyRelease(KeyEvent.VK_TAB);
            aRobot.keyRelease(KeyEvent.VK_ALT);
            aRobot.delay(POST_ALT_TAB_DELAY);
        }

        // Start auto-type
        for (int i = 0; i < input.length(); ++i) {
            char passwordChar = input.charAt(i);

            Logger.printTrace("Character to type: " + passwordChar);

            if (aShiftRequired.contains(passwordChar)) aRobot.keyPress(KeyEvent.VK_SHIFT);
            aRobot.keyPress(aKeyMap.get(passwordChar));
            aRobot.delay(TYPE_IN_DELAY);
            aRobot.keyRelease(aKeyMap.get(passwordChar));
            if (aShiftRequired.contains(passwordChar)) aRobot.keyRelease(KeyEvent.VK_SHIFT);
        }
    }
}
