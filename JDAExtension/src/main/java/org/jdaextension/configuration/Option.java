package org.jdaextension.configuration;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jdaextension.interfaces.CustomType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Option {
    private final String name;
    private final String description;
    private final boolean required;
    private OptionType type;
    private Map<Integer,CustomType> choicesCustom;
    private final List<Choice> choiceList;

    public Option(String name, String description, boolean required) {
        this.name = name;
        this.description = description;
        this.required = required;
        this.choicesCustom = null;
        this.type = OptionType.STRING;
        this.choiceList = new ArrayList<>();
    }

    public Option setString() {
        type = OptionType.STRING;
        return this;
    }
    public Option setInteger() {
        type = OptionType.INTEGER;
        return this;
    }
    public Option setNumber() {
        type = OptionType.NUMBER;
        return this;
    }
    public Option setBoolean()  {
        type = OptionType.BOOLEAN;
        return this;
    }

    public Option setCustom() {
        type = OptionType.INTEGER;
        choicesCustom = new HashMap<>();
        return this;
    }

    public Option addChoice(String name, String value) {
        this.choiceList.add(new Choice(name,value));
        return this;
    }

    public Option addChoice(String name, long value) {
        this.choiceList.add(new Choice(name,value));
        return this;
    }

    public Option addChoice(String name, double value) {
        this.choiceList.add(new Choice(name,value));
        return this;
    }

    public Option addChoice(String name, CustomType customType) {
        this.choiceList.add(new Choice(name,choicesCustom.size()));
        choicesCustom.put(choicesCustom.size(),customType.clone());
        return this;
    }

    public Object parser(SlashCommandInteractionEvent event) {
        OptionMapping optionMapping = event.getOption(name);
        if(optionMapping == null)
            return null;
        else if (choicesCustom != null)
            return choicesCustom.get(optionMapping.getAsInt()).clone();
        else if (type == OptionType.INTEGER)
            return optionMapping.getAsInt();
        else if (type == OptionType.BOOLEAN)
            return optionMapping.getAsBoolean();
        else if (type == OptionType.NUMBER)
            return optionMapping.getAsDouble();
        else
            return optionMapping.getAsString();
    }

    public OptionData generateOptionData() {
        return new OptionData(type,name,description,required).addChoices(this.choiceList);
    }


    public String getName() {
        return name;
    }

    public boolean isRequired() {
        return required;
    }
}
