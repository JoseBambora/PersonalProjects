package com.github.josebambora.configuration;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.josebambora.generic.GenericEvents;
import com.github.josebambora.responses.ResponseButton;

public abstract class ButtonReceiver {
    abstract protected GenericEvents getController();

    public ResponseButton onButtonClicked(ButtonInteractionEvent event, String id) {
        ResponseButton responseButton = new ResponseButton(event);
        getController().onCall(event, id, responseButton);
        return responseButton;
    }
}
