package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import com.github.josebambora.configuration.MessageCommand;
import com.github.josebambora.responses.ResponseCommand;

public interface MessageContextEvent extends GenericEvents {
    void configure(MessageCommand messageCommand);

    void onCall(MessageContextInteractionEvent event, ResponseCommand response);
}
