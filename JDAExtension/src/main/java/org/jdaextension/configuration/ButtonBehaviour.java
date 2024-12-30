package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseButton;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ButtonBehaviour<T> {
    private final Map<String, BiConsumer<ButtonInteractionEvent, Response>> buttonsInteractions;

    public ButtonBehaviour() {
        this.buttonsInteractions = new HashMap<>();
    }

    public T addButtonClick(String buttonID, BiConsumer<ButtonInteractionEvent, Response> buttonClickFunction) {
        buttonsInteractions.put(buttonID, buttonClickFunction);
        return (T) this;
    }

    protected Response onButtonClick(ButtonInteractionEvent event, String id) {
        ResponseButton responseButton = new ResponseButton(event);
        if (this.buttonsInteractions.containsKey(id))
            this.buttonsInteractions.get(id).accept(event, responseButton);
        else {
            responseButton.setTemplate("500")
                    .setVariable("message", "No Behaviour defined for this button");
        }
        return responseButton;
    }
}
