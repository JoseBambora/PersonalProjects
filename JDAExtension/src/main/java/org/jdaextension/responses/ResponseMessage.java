package org.jdaextension.responses;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class ResponseMessage extends Response{
    private final MessageReceivedEvent event;
    private final int id;
    public ResponseMessage(MessageReceivedEvent event, int id) {
        this.event = event;
        this.id = id;
    }

    private MessageCreateAction sendMessageEmbed() {
        return setEmbed(event.getMessage().reply(this.message.toString()));
    }

    private MessageCreateAction setButtons(MessageCreateAction messageCreateAction) {
        return buttons.isEmpty() ? messageCreateAction : messageCreateAction.setActionRow(buttons);
    }
    @Override
    public void send() {
        boolean hasFile = this.build("message" + id);
        this.sendReactions(event.getMessage());
        if(hasFile) {
            setButtons(sendMessageEmbed()).setFiles(this.files).queue();
        }
    }
}
