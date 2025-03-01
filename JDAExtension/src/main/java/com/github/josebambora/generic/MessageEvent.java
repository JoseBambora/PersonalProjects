package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import com.github.josebambora.configuration.MessageReceiver;
import com.github.josebambora.responses.ResponseMessageReceiver;
import com.github.josebambora.responses.ResponseMessageUpdate;
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
