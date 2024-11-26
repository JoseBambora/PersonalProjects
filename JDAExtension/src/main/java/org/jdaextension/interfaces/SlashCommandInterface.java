package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.reponses.ResponseMessage;

import java.util.Map;

public interface SlashCommandInterface {
    public SlashCommand configure();
    public ResponseMessage execute(SlashCommandInteractionEvent event, Map<String, Object> variables);
}
