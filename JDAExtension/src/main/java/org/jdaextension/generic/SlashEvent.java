package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.responses.Response;

import java.util.Map;

public abstract class SlashEvent extends GenericEvents {

    abstract public void configure(SlashCommand slashCommand);

    abstract public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response);
}
