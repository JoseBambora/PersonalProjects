package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class OptionNumber extends Option<OptionNumber> {
    private final Number type;
    private BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, Double>> autoComplete;

    public OptionNumber(String name, String description, boolean required, Number type) {
        super(name, description, required, type == Number.INTEGER ? OptionType.INTEGER : OptionType.NUMBER);
        this.type = type;
        autoComplete = null;
    }

    public OptionNumber addChoice(String name, long value) {
        this.addChoiceLong(name, value);
        return this;
    }

    public OptionNumber addChoice(String name, double value) {
        this.addChoiceDouble(name, value);
        return this;
    }

    public OptionNumber addChoice(String name, int value) {
        this.addChoiceInt(name, value);
        return this;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return switch (type) {
            case LONG -> optionMapping.getAsLong();
            case FLOAT -> ((Double) optionMapping.getAsDouble()).floatValue();
            case INTEGER -> optionMapping.getAsInt();
            default -> optionMapping.getAsDouble();
        };
    }

    @Override
    protected boolean hasAutoComplete() {
        return autoComplete != null;
    }

    @Override
    public void onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        Map<String, Double> choices = this.autoComplete.apply(event, event.getFocusedOption().getValue());
        List<Command.Choice> choicesFinal;
        if (type == Number.LONG)
            choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue().longValue())).toList();
        else if (type == Number.INTEGER)
            choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue().intValue())).toList();
        else if (type == Number.FLOAT)
            choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue().floatValue())).toList();
        else
            choicesFinal = choices.entrySet().stream().map(o -> new Command.Choice(o.getKey(), o.getValue())).toList();
        event.replyChoices(choicesFinal).queue();
    }

    public OptionNumber setAutoComplete(BiFunction<CommandAutoCompleteInteractionEvent, String, Map<String, Double>> autoComplete) {
        this.autoComplete = autoComplete;
        return this;
    }
}
