package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.generic.GenericEvents;
import org.jdaextension.generic.MessageEvent;
import org.jdaextension.responses.ResponseMessageReceiver;
import org.jdaextension.responses.ResponseMessageUpdate;

import java.util.*;
import java.util.function.BiFunction;

public class MessageReceiver extends ButtonReceiver {
    private final MessageEvent controller;
    private final List<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> pipelineReceiver;
    private final List<BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean>> pipelineUpdate;
    private final List<BiFunction<MessageDeleteEvent, Map<String, Object>, Boolean>> pipelineDelete;
    private final int id;
    private boolean received;
    private boolean updates;
    private boolean deletes;

    protected MessageReceiver(MessageEvent controller, int id) {
        this.controller = controller;
        this.pipelineReceiver = new ArrayList<>();
        this.pipelineUpdate = new ArrayList<>();
        this.pipelineDelete = new ArrayList<>();
        this.id = id;
        received = true;
        updates = false;
        deletes = false;
    }


    public MessageReceiver addToPipelineReceive(BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> function) {
        pipelineReceiver.add(function);
        return this;
    }

    public MessageReceiver addToPipelineUpdate(BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean> function) {
        pipelineUpdate.add(function);
        return this;
    }


    public MessageReceiver addToPipelineDelete(BiFunction<MessageDeleteEvent, Map<String, Object>, Boolean> function) {
        pipelineDelete.add(function);
        return this;
    }

    private <T> Map<String, Object> getData(List<BiFunction<T, Map<String, Object>, Boolean>> pipeline, T event) {
        Iterator<BiFunction<T, Map<String, Object>, Boolean>> iterator = pipeline.iterator();
        Map<String, Object> data = new HashMap<>();
        boolean b = true;
        while (b && iterator.hasNext())
            b = iterator.next().apply(event, data);
        return b ? data : null;
    }

    protected void messageReceived(MessageReceivedEvent event) {
        if (received) {
            Map<String, Object> data = getData(pipelineReceiver, event);
            if (data != null) {
                ResponseMessageReceiver responseMessage = new ResponseMessageReceiver(event, id);
                controller.onCall(event, data, responseMessage);
            }
        }
    }

    protected void messageUpdated(MessageUpdateEvent event) {
        if (updates) {
            Map<String, Object> data = getData(pipelineUpdate, event);
            if (data != null) {
                ResponseMessageUpdate responseMessage = new ResponseMessageUpdate(event, id);
                controller.onCall(event, data, responseMessage);
            }
        }
    }

    protected void messageDelete(MessageDeleteEvent event) {
        if (deletes) {
            Map<String, Object> data = getData(pipelineDelete, event);
            if (data != null) {
                controller.onCall(event, data);
            }
        }
    }

    @Override
    protected GenericEvents getController() {
        return controller;
    }

    public MessageReceiver setReceived(boolean received) {
        this.received = received;
        return this;
    }

    public MessageReceiver setUpdates(boolean updates) {
        this.updates = updates;
        return this;
    }

    public MessageReceiver setDeletes(boolean deletes) {
        this.deletes = deletes;
        return this;
    }
}
