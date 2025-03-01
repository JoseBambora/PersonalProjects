package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.josebambora.responses.ResponseButton;
import com.github.josebambora.responses.ResponseModal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface GenericEvents extends ShutdownInterface {
    Logger log = LoggerFactory.getLogger(GenericEvents.class);

    default void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        log.error("The method onCall(ButtonInteractionEvent event, String id, Response response) is not override.");
        response.setTemplate("500");
    }

    default void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, ResponseModal response) {
        log.error("The method onCall(ModalInteractionEvent event, String id, Response response) is not override.");
        response.setTemplate("500");
    }
}
