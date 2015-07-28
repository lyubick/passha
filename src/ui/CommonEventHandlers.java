package ui;

import ui.elements.EntryField;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public final class CommonEventHandlers
{
    public static EventHandler<KeyEvent> getShortcutTFFiler(EntryField ef)
    {
        return new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent keyEvent)
            {
                if (Character.isLetterOrDigit(keyEvent.getCharacter().charAt(0)))
                {
                    ef.setText(keyEvent.getCharacter());
                }

                keyEvent.consume();
            }
        };
    }
}
