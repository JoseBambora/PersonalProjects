package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseMessage;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class MessageReceiver extends ButtonBehaviour<MessageReceiver> {
    private final MessageReceiverInterface controller;
    private final List<BiFunction<MessageReceivedEvent,Map<String,Object>, Boolean>> pipeline;
    private final int id;

    protected MessageReceiver(MessageReceiverInterface controller, List<BiFunction<MessageReceivedEvent,Map<String,Object>, Boolean>> pipeline, int id) {
        this.controller = controller;
        this.pipeline = pipeline;
        this.id = id;
    }

    protected Response messageReceived(MessageReceivedEvent event) {
        Iterator<BiFunction<MessageReceivedEvent,Map<String,Object>, Boolean>> iterator = pipeline.iterator();
        Map<String,Object> data = new HashMap<>();
        boolean b = true;
        while (b && iterator.hasNext())
            b = iterator.next().apply(event,data);
        if(b) {
            ResponseMessage responseMessage = new ResponseMessage(event, id);
            controller.onCall(event, data, responseMessage);
            return responseMessage;
        }
        else
            return null;
    }
}
