package org.jdaextension.responses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class ResponseMessage extends Response {
    private final MessageReceivedEvent eventReceive;
    private final MessageUpdateEvent eventUpdate;
    private final int id;

    public ResponseMessage(MessageReceivedEvent event, int id) {
        this.eventReceive = event;
        this.eventUpdate = null;
        this.id = id;
    }

    public ResponseMessage(MessageUpdateEvent event, int id) {
        this.eventReceive = null;
        this.eventUpdate = event;
        this.id = id;
    }

    private void send(MessageCreateAction mca) {
        mca = !this.embedBuilder.isEmpty() ? mca.setEmbeds(embedBuilder.build()) : mca;
        mca = buttons.isEmpty() ? mca : mca.setActionRow(buttons);
        mca = mca.setFiles(this.files);
        mca.queue();
    }

    @Override
    public void send() {
        boolean hasFile = this.build("message_" + id);
        if (eventReceive != null) {
            this.sendReactions(eventReceive.getMessage());
            if (hasFile) {
                MessageCreateAction mca = eventReceive.getMessage().reply(this.message.toString());
                send(mca);
            }
        } else {
            this.sendReactions(eventUpdate.getMessage());
            if (hasFile) {
                MessageCreateAction mca = eventUpdate.getMessage().reply(this.message.toString());
                send(mca);
            }
        }
    }
}
