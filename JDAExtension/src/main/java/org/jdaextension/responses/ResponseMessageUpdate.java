package org.jdaextension.responses;

import net.dv8tion.jda.api.events.message.MessageUpdateEvent;

public class ResponseMessageUpdate extends ResponseMessage<ResponseMessageUpdate> {
    private final MessageUpdateEvent eventUpdate;
    private boolean cleanEmojis;

    public ResponseMessageUpdate(MessageUpdateEvent event, int id) {
        super(id);
        this.eventUpdate = event;
        cleanEmojis = false;
    }

    public ResponseMessageUpdate cleanReactions() {
        cleanEmojis = true;
        return this;
    }

    @Override
    public void send() {
        if (cleanEmojis)
            eventUpdate.getMessage().clearReactions().queue(_ -> this.send(eventUpdate.getMessage()));
        else
            this.send(eventUpdate.getMessage());
    }
}
