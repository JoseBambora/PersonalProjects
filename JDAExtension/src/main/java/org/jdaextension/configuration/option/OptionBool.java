package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class OptionBool extends Option<OptionBool> {
    private BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, Boolean>> autoComplete;

    public OptionBool(String name, String description, boolean required) {
        super(name, description, required, OptionType.BOOLEAN);
        autoComplete = null;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return optionMapping.getAsBoolean();
    }

    @Override
    protected boolean hasAutoComplete() {
        return autoComplete != null;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Map<String, Boolean> choices = this.autoComplete.apply(event, event.getFocusedOption().getValue());
        List<Command.Choice> choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue() ? 1 : 0)).toList();
        event.replyChoices(choicesFinal).queue();
    }


    public OptionBool setAutoComplete(BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, Boolean>> autoComplete) {
        this.autoComplete = autoComplete;
        return this;
    }
}
