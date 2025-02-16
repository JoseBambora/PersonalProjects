package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class OptionString extends Option<OptionString> {

    public OptionString(String name, String description, boolean required) {
        super(name, description, required, OptionType.STRING);
    }

    public OptionString addChoice(String name, String value) {
        this.addChoiceString(name, value);
        return this;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return optionMapping.getAsString();
    }
}
