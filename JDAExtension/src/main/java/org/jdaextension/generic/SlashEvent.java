package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.responses.ResponseCommand;

import java.util.Map;

public interface SlashEvent extends GenericEvents {

    void configure(SlashCommand slashCommand);

    void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response);
}
