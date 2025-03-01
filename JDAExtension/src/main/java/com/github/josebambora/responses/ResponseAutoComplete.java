package com.github.josebambora.responses;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

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

    public ResponseAutoComplete addChoice(List<Command.Choice> choices) {
        this.choices.addAll(choices);
        return this;
    }

    public ResponseAutoComplete addChoice(Command.Choice ...choices) {
        return addChoice(List.of(choices));
    }

    public void send() {
        if(choices.size() > 25)
            event.replyChoices(choices.subList(0,25)).queue();
        else
            event.replyChoices(choices).queue();
    }
}
