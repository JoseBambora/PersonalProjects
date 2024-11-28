package org.jdaextension.examples;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.Option;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.reponses.ResponseButtonClick;
import org.jdaextension.reponses.ResponseMessage;

import java.util.Map;
import java.util.stream.IntStream;

public class HelloCommand2 implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        Option option1 = new Option("name","Name to appear in the message",true)
                .setString();
        Option option2 = new Option("number","number of items", true)
                .setInteger();
        return new SlashCommand("ola","ola versão 2")
                .addOption(option1)
                .addOption(option2)
                .addButtonClick("1",this::onButton1)
                .addButtonClick("2",this::onButton2)
                .addButtonClick("3",this::onButton3)
                .addButtonClick("4",this::onButton4);
    }

    @Override
    public ResponseMessage onCall(SlashCommandInteractionEvent event, Map<String, Object> variables) {
        String name = (String) variables.get("name");
        Integer integer = (Integer) variables.get("number");
        return new ResponseMessage("Template1")
                .setVariable("name",name)
                .setVariable("items", IntStream.range(0,integer).boxed().toList());
    }

    public ResponseButtonClick onButton1(ButtonInteractionEvent event) {
        return new ResponseButtonClick("Template3")
                .setVariable("name","Button 1 clicked");
    }

    public ResponseButtonClick onButton2(ButtonInteractionEvent event) {
        return new ResponseButtonClick("Template3")
                .setVariable("name","Button 2 clicked");
    }

    public ResponseButtonClick onButton3(ButtonInteractionEvent event) {
        return new ResponseButtonClick("Template3")
                .setVariable("name","Button 3 clicked");
    }

    public ResponseButtonClick onButton4(ButtonInteractionEvent event) {
        return new ResponseButtonClick("Template3")
                .setVariable("name","Button 4 clicked");
    }
}
