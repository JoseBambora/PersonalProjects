package org.jdaextension.responses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResponseMessageReceiver extends ResponseMessage<ResponseMessageReceiver> {
    private final MessageReceivedEvent eventReceive;

    public ResponseMessageReceiver(MessageReceivedEvent event, int id) {
        super(id);
        this.eventReceive = event;
    }

    @Override
    public void send() {
        this.send(eventReceive.getMessage());
    }
}
