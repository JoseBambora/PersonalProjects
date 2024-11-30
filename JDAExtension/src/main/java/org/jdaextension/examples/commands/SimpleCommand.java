package org.jdaextension.examples.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;

import java.util.Map;

public class SimpleCommand implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        return new SlashCommand("test","test");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        response.setTemplate("Template");
    }
}
