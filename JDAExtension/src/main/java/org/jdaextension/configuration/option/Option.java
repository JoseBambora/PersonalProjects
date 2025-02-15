package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class Option<T> implements Comparable<Option<?>> {
    private final String name;
    private final String description;
    private final boolean required;
    private final OptionType type;
    private final List<Choice> choiceList;

    protected Option(String name, String description, boolean required, OptionType type) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.choiceList = new ArrayList<>();
        this.type = type;
    }

    protected void addChoiceString(String name, String value) {
        this.choiceList.add(new Choice(name, value));
    }

    protected void addChoiceLong(String name, long value) {
        this.choiceList.add(new Choice(name, value));
    }

    protected void addChoiceDouble(String name, double value) {
        this.choiceList.add(new Choice(name, value));
    }

    protected void addChoiceInt(String name, int value) {
        this.choiceList.add(new Choice(name, value));
    }

    public Object parser(SlashCommandInteractionEvent event) {
        OptionMapping optionMapping = event.getOption(name);
        return optionMapping == null ? null : this.parseOption(optionMapping);
    }

    protected abstract Object parseOption(OptionMapping optionMapping);

    protected abstract boolean hasAutoComplete();

    public OptionData buildOption() {
        OptionData optionData = new OptionData(type, name, description, required, hasAutoComplete()).addChoices(this.choiceList);
        // to clear some RAM usage
        choiceList.clear();
        return optionData;
    }

    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }

    public abstract void onAutoComplete(CommandAutoCompleteInteractionEvent event);

    @Override
    public int compareTo(@NotNull Option<?> other) {
        return this.isRequired() ? -1 : other.isRequired() ? 1 : 0;
    }
}
