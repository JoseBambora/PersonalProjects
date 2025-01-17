package org.jdaextension.cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseCommand;
import org.jdaextension.responses.ResponseModal;

import java.util.Map;

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
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        String word = (String) variables.get("word");
        response.setTemplate("SimpleModal").setModal();
    }

    @Override
    public void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, ResponseModal response) {
        System.out.println(fields);
        response.setTemplate("SimpleCommandOptions").setVariable("word", fields.get("field1"));
    }

    @Override
    public void onShutDown(ShutdownEvent shutdownEvent) {
        System.out.println("Shutting down command word");
    }
}