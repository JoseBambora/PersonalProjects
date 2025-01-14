package org.jdaextension.generic;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.responses.Response;

import java.util.Map;


public abstract class MessageReceiverEvent extends GenericEvents {

    abstract public void configure(MessageReceiver messageReceiver);

    abstract public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response);

    abstract public void onCall(MessageUpdateEvent event, Map<String, Object> data, Response response);
}
