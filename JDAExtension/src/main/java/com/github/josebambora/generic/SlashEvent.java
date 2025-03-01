package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.responses.ResponseCommand;

import java.util.Map;

public interface SlashEvent extends GenericEvents {

    void configure(SlashCommand slashCommand);

    void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response);
}
