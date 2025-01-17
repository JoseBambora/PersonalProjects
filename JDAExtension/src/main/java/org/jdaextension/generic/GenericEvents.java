package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseModal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public abstract class GenericEvents {
    private final Logger log = LoggerFactory.getLogger(GenericEvents.class);

    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        log.error("The method onCall(ButtonInteractionEvent event, String id, Response response) is not override.");
        response.setTemplate("500");
    }

    public void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, ResponseModal response) {
        log.error("The method onCall(ModalInteractionEvent event, String id, Response response) is not override.");
        response.setTemplate("500");
    }

    public void onShutDown(ShutdownEvent shutdownEvent) {

    }
}
