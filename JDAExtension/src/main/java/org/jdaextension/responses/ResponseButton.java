package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.Collections;

public class ResponseButton extends Response {
    private final ButtonInteractionEvent event;

    public ResponseButton(ButtonInteractionEvent event) {
        this.event = event;
    }

    @Override
    public void send() {
        String []split = event.getButton().getId().split("_");
        String command = split[0] + "_" + split[1];
        boolean hasFile = this.build(command);
        this.sendReactions(event.getMessage());
        if (hasFile) {
            MessageEditCallbackAction meca = event.editMessage(message.toString());
            meca = !this.embedBuilder.isEmpty() ? meca.setEmbeds(embedBuilder.build()) : meca;
            meca = buttons.isEmpty() ? meca.setComponents(Collections.emptyList()) : meca.setActionRow(buttons);
            meca = meca.setFiles(this.files);
            meca.queue();
        }
    }
}
