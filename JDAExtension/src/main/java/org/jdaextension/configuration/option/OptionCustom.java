package org.jdaextension.configuration.option;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.function.Function;


public class OptionCustom extends Option<OptionCustom>{
    private Function<String,Object> customParser;
    public OptionCustom(String name, String description, boolean required, Function<String, Object> customParser) {
       super(name,description,required,OptionType.STRING);
        this.customParser = customParser;
    }
    public OptionCustom addChoice(String name, String value) {
        this.addChoiceString(name,value);
        return this;
    }

    @Override
    protected Object parseOption(OptionMapping optionMapping) {
        return customParser.apply(optionMapping.getAsString());
    }
}
