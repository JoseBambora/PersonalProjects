package com.github.josebambora.generic;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import com.github.josebambora.configuration.UserCommand;
import com.github.josebambora.responses.ResponseCommand;

public interface UserContextEvent extends GenericEvents {
    void configure(UserCommand userCommand);

    void onCall(UserContextInteractionEvent event, ResponseCommand response);
}
