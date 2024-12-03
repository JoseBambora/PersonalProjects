package org.jdaextension.responses;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ResponseMessage extends Response{
    private final MessageReceivedEvent event;
    private final int id;
    public ResponseMessage(MessageReceivedEvent event, int id) {
        this.event = event;
        this.id = id;
    }

    @Override
    public void send() {
        boolean hasFile = this.build("message" + id);
        this.sendReactions(event.getMessage());
        if(hasFile) {
            if(this.buttons.isEmpty()) {
                event.getMessage().reply(this.message.toString()).setFiles(this.files).queue();
            }
            else {
                event.getMessage().reply(this.message.toString()).setFiles(this.files).setActionRow(this.buttons).queue();
            }
        }
    }
}
