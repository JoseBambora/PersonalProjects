package org.jdaextension.examples.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.option.OptionCustom;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.examples.MyType;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class HelloCommand implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        OptionString option1 = new OptionString("word","word desc", false)
                .addChoice("Option 1", "World")
                .addChoice("Option 2", "Braga");
        OptionCustom option2 = new OptionCustom("coords","coords desc", true, MyType::new)
                .addChoice("Option 1", "(1,1)")
                .addChoice("Option 2", "(2,2)");
        OptionCustom option3 = new OptionCustom("coords2","coords no choice", true, MyType::new)
                .setAutoComplete(this::onAutoComplete);
        return new SlashCommand("hello", "hello world")
                .addOptions(option1, option2, option3)
                .setEphemeral()
                .addPermission(Permission.ADMINISTRATOR)
                .setSendThinking();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        String word = (String) variables.get("word");
        MyType myType = (MyType) variables.get("coords");
        MyType myType2 = (MyType) variables.get("coords2");
        System.out.println(word);
        System.out.println(myType);
        System.out.println(myType2);
        response.setTemplate("Template2")
                .setVariable("word",word)
                .setVariable("coords", myType.toString());
    }


    public List<Choice> onAutoComplete(CommandAutoCompleteInteractionEvent event) {
        String value = event.getFocusedOption().getValue();
        return Stream.of(new Choice("(1,1)","(1,1)"), new Choice("(1,2)","(2,2)")).filter(n -> n.getAsString().contains(value)).toList();
    }
}
