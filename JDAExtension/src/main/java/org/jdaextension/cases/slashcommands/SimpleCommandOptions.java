package org.jdaextension.cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.OptionCustom;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.Response;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleCommandOptions extends SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString option1 = new OptionString("word", "word desc", false)
                .addChoice("Option 1", "World")
                .addChoice("Option 2", "Braga");
        slashCommand.setName("simpleoptions")
                .setDescription("hello world")
                .addOptions(option1);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        String word = (String) variables.get("word");
        response.setTemplate("SimpleModal").setModal();
    }

    @Override
    public void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, Response response) {
        System.out.println(fields);
        response.setTemplate("SimpleCommandOptions").setVariable("word",fields.get("field1"));
    }
}