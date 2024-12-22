package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.responses.Response;

import java.util.Map;

public interface SlashCommandInterface {
    SlashCommand configure();

    void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response);
}
