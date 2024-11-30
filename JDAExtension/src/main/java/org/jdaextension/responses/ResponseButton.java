package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Collections;

public class ResponseButton extends Response{
    private final ButtonInteractionEvent event;
    public ResponseButton(ButtonInteractionEvent eventBT) {
        this.event = eventBT;
    }

    @Override
    public void send() {
        String command = event.getButton().getId().split("_")[0];
        this.build(command);
        if(this.buttons.isEmpty()) {
            event.editMessage(message.toString()).setComponents(Collections.emptyList()).queue();
        }
        else {
            event.editMessage(message.toString()).setActionRow(buttons).queue();
        }
    }
}
