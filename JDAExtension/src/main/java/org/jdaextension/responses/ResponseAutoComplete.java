package org.jdaextension.responses;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jdaextension.configuration.option.Number;

import java.util.ArrayList;
import java.util.List;

public class ResponseAutoComplete{
    private final List<Command.Choice> choices;
    private final CommandAutoCompleteInteractionEvent event;

    public ResponseAutoComplete(CommandAutoCompleteInteractionEvent event) {
        this.event = event;
        this.choices = new ArrayList<>();
    }

    public ResponseAutoComplete addChoice(Command.Choice choice) {
        choices.add(choice);
        return this;
    }

    public void send() {
        event.replyChoices(choices).queue();
    }
}
