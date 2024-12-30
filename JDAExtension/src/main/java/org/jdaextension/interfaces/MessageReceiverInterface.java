package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.responses.Response;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;


public interface MessageReceiverInterface {
    List<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> configure();

    void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response);
}
