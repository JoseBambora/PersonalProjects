package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.responses.Response;

import java.util.Map;


public interface MessageReceiverInterface {
    void configure(MessageReceiver messageReceiver);

    void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response);
}
