package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Collections;

public class ResponseButton extends Response{
    private final ButtonInteractionEvent event;
    public ResponseButton(ButtonInteractionEvent event) {
        this.event = event;
    }

    @Override
    public void send() {
        String command = event.getButton().getId().split("_")[0];
        boolean hasFile = this.build(command);
        this.sendReactions(event.getMessage());
        if(hasFile) {
            if (this.buttons.isEmpty()) {
                event.editMessage(message.toString()).setFiles(this.files).setComponents(Collections.emptyList()).queue();
            } else {
                event.editMessage(message.toString()).setFiles(this.files).setActionRow(buttons).queue();
            }
        }
    }
}
