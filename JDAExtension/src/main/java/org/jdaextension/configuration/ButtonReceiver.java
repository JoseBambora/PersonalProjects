package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseButton;

public abstract class ButtonReceiver {
    abstract protected GenericEvents getController();

    public Response onButtonClicked(ButtonInteractionEvent event, String id) {
        ResponseButton responseButton = new ResponseButton(event);
        getController().onCall(event, id, responseButton);
        return responseButton;
    }

}
