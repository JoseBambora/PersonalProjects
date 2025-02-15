package org.botgverreiro.controllers.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseCommand;

import java.util.Map;

public class Help implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("help")
                .setDescription("Mostrar comandos disponiveis")
                .setEphemeral();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        responseCommand.setTemplate("Help");
    }
}
