package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class OptionNumber extends Option<OptionNumber> {
    private final Number type;
    public OptionNumber(String name, String description, boolean required, Number type) {
        super(name, description, required, OptionType.NUMBER);
        this.type = type;
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
}
