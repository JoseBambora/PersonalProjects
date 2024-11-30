package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.responses.ResponseMessage;

import java.util.Map;

public interface SlashCommandInterface {
    SlashCommand configure();
    ResponseMessage onCall(SlashCommandInteractionEvent event, Map<String, Object> variables);
}
