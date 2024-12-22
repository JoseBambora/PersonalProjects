package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.MessageEditCallbackAction;

import java.util.Collections;

public class ResponseButton extends Response {
    private final ButtonInteractionEvent event;

    public ResponseButton(ButtonInteractionEvent event) {
        this.event = event;
    }

    private MessageEditCallbackAction handleButtons(MessageEditCallbackAction messageEditRequest) {
        return buttons.isEmpty() ? messageEditRequest.setComponents(Collections.emptyList()) : messageEditRequest.setActionRow(buttons);
    }

    private MessageEditCallbackAction sendMessageEmbed() {
        return setEmbed(event.editMessage(message.toString()));
    }

    @Override
    public void send() {
        String command = event.getButton().getId().split("_")[0];
        boolean hasFile = this.build(command);
        this.sendReactions(event.getMessage());
        if (hasFile) {
            handleButtons(sendMessageEmbed()).setFiles(this.files).queue();
        }
    }
}
