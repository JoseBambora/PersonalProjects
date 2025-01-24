package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class OptionString extends Option<OptionString> {
    private BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, String>> autoComplete;

    public OptionString(String name, String description, boolean required) {
        super(name, description, required, OptionType.STRING);
        autoComplete = null;
    }

    public OptionString addChoice(String name, String value) {
        this.addChoiceString(name, value);
        return this;
    }


    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return optionMapping.getAsString();
    }

    @Override
    protected boolean hasAutoComplete() {
        return autoComplete != null;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Map<String, String> choices = this.autoComplete.apply(event, event.getFocusedOption().getValue());
        List<Command.Choice> choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue())).toList();
        event.replyChoices(choicesFinal).queue();
    }

    public OptionString setAutoComplete(BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, String>> autoComplete) {
        this.autoComplete = autoComplete;
        return this;
    }
}
