package com.github.josebambora.generic;

import com.github.josebambora.configuration.Pageable;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.responses.ResponseButton;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.util.Map;

public interface SlashEventPageable extends GenericEvents {

    void configure(SlashCommand slashCommand);

    void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response);

    void onCall(ButtonInteractionEvent event, String id, Pageable pageable, ResponseButton response);
}
