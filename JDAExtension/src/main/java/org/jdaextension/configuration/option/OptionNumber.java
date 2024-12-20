package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class OptionNumber extends Option<OptionNumber>{
    public OptionNumber(String name, String description, boolean required) {
        super(name, description, required, OptionType.NUMBER);
    }

    public OptionNumber addChoice(String name, long value) {
        this.addChoiceLong(name,value);
        return this;
    }

    public OptionNumber addChoice(String name, double value) {
        this.addChoiceDouble(name,value);
        return this;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return optionMapping.getAsDouble();
    }
}
