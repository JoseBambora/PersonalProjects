package org.jdaextension.responses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class ResponseMessage extends Response {
    private final MessageReceivedEvent event;
    private final int id;

    public ResponseMessage(MessageReceivedEvent event, int id) {
        this.event = event;
        this.id = id;
    }

    @Override
    public void send() {
        boolean hasFile = this.build("message_" + id);
        this.sendReactions(event.getMessage());
        if (hasFile) {
            MessageCreateAction mca = event.getMessage().reply(this.message.toString());
            mca = !this.embedBuilder.isEmpty() ? mca.setEmbeds(embedBuilder.build()) : mca;
            mca = buttons.isEmpty() ? mca : mca.setActionRow(buttons);
            mca = mca.setFiles(this.files);
            mca.queue();
        }
    }
}
