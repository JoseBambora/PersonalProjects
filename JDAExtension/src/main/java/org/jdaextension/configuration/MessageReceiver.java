package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.generic.MessageReceiverEvent;
import org.jdaextension.responses.Response;
import org.jdaextension.responses.ResponseMessage;

import java.util.*;
import java.util.function.BiFunction;

public class MessageReceiver extends ButtonReceiver {
    private final MessageReceiverEvent controller;
    private final List<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> pipelineReceiver;
    private final List<BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean>> pipelineUpdate;
    private final int id;

    protected MessageReceiver(MessageReceiverEvent controller, int id) {
        this.controller = controller;
        this.pipelineReceiver = new ArrayList<>();
        this.pipelineUpdate = new ArrayList<>();
        this.id = id;
    }

    public MessageReceiver addToPipelineReceive(BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> function) {
        pipelineReceiver.add(function);
        return this;
    }

    public MessageReceiver addToPipelineUpdate(BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean> function) {
        pipelineUpdate.add(function);
        return this;
    }


    protected Response messageReceived(MessageReceivedEvent event) {
        Iterator<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> iterator = pipelineReceiver.iterator();
        Map<String, Object> data = new HashMap<>();
        boolean b = true;
        while (b && iterator.hasNext())
            b = iterator.next().apply(event, data);
        if (b) {
            ResponseMessage responseMessage = new ResponseMessage(event, id);
            controller.onCall(event, data, responseMessage);
            return responseMessage;
        } else
            return null;
    }

    protected Response messageReceived(MessageUpdateEvent event) {
        Iterator<BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean>> iterator = pipelineUpdate.iterator();
        Map<String, Object> data = new HashMap<>();
        boolean b = true;
        while (b && iterator.hasNext())
            b = iterator.next().apply(event, data);
        if (b) {
            ResponseMessage responseMessage = new ResponseMessage(event, id);
            controller.onCall(event, data, responseMessage);
            return responseMessage;
        } else
            return null;
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }
}
