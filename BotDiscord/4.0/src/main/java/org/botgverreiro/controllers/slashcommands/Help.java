package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

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
        responseCommand.setTemplate("Help").send();
    }
}
