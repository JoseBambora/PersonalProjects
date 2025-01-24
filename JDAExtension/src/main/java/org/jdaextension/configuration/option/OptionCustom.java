package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;


public class OptionCustom extends Option<OptionCustom> {
    private final Function<String, Object> customParser;
    private BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, String>> autoComplete;

    public OptionCustom(String name, String description, boolean required, Function<String, Object> customParser) {
        super(name, description, required, OptionType.STRING);
        this.customParser = customParser;
        autoComplete = null;
    }

    public OptionCustom addChoice(String name, String value) {
        this.addChoiceString(name, value);
        return this;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return customParser.apply(optionMapping.getAsString());
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


    public OptionCustom setAutoComplete(BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, String>> autoComplete) {
        this.autoComplete = autoComplete;
        return this;
    }
}
