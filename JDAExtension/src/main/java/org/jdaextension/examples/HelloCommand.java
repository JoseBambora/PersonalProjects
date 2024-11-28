package org.jdaextension.examples;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.Option;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.reponses.ResponseMessage;

import java.util.Map;

public class HelloCommand implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        Option option1 = new Option("word","word desc", false)
                .setString()
                .addChoice("Option 1", "World")
                .addChoice("Option 2", "Braga");
        Option option2 = new Option("coords","coords desc", true)
                .setCustom()
                .addChoice("Option 1", new MyType(1,1))
                .addChoice("Option 2", new MyType(2,2));
        return new SlashCommand("hello", "hello world")
                .addOption(option1)
                .addOption(option2)
                .setSendThinking();
    }

    @Override
    public ResponseMessage onCall(SlashCommandInteractionEvent event, Map<String, Object> variables) {
        String word = (String) variables.get("word");
        MyType myType = (MyType) variables.get("coords");
        System.out.println(word);
        System.out.println(myType);
        return new ResponseMessage("Template2")
                .setVariable("word",word)
                .setVariable("coords", myType.toString());
    }
}
