package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public abstract class ButtonBehaviour<T> {
    private static final Logger log = LoggerFactory.getLogger(ButtonBehaviour.class);
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
            log.error("No Behaviour defined for this button {}. Id available: {}", id, buttonsInteractions.keySet());
            responseButton.setTemplate("500");
        }
        return responseButton;
    }
}
