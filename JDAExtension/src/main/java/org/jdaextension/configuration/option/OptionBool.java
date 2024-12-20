package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class OptionBool extends Option<OptionBool> {
    public OptionBool(String name, String description, boolean required) {
        super(name,description,required, OptionType.BOOLEAN);
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return optionMapping.getAsBoolean();
    }
}
