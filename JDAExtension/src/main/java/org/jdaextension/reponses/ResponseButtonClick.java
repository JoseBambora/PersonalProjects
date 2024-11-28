package org.jdaextension.reponses;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Collections;

public class ResponseButtonClick extends Response{
    public ResponseButtonClick(String filename) {
        super(filename);
    }

    @Override
    public ResponseButtonClick setVariable(String name, Object value) {
        return (ResponseButtonClick) super.setVariable(name, value);
    }

    public void send(ButtonInteractionEvent event) {
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
