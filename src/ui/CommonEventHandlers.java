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
                if ("0123456789".contains(keyEvent.getCharacter()))
                    ef.setText(keyEvent.getCharacter());
                keyEvent.consume();
            }
        };
    }
}
