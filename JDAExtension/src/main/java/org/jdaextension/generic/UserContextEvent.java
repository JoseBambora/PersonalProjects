package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.responses.ResponseCommand;

public interface UserContextEvent extends GenericEvents {
    void configure(UserCommand userCommand);

    void onCall(UserContextInteractionEvent event, ResponseCommand response);
}
