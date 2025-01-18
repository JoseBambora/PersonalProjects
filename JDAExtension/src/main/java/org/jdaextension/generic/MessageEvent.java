package org.jdaextension.generic;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.responses.ResponseMessageReceiver;
import org.jdaextension.responses.ResponseMessageUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public interface MessageEvent extends GenericEvents {
    Logger log = LoggerFactory.getLogger(MessageEvent.class);

    void configure(MessageReceiver messageReceiver);

    default void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        log.error("The method onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) is not override.");
        response.setTemplate("500");
    }

    default void onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) {
        log.error("The method onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) is not override.");
        response.setTemplate("500");
    }

    default void onCall(MessageDeleteEvent event, Map<String, Object> data) {
        log.error("The method onCall(MessageDeleteEvent event, Map<String, Object> data) is not override.");
    }
}
