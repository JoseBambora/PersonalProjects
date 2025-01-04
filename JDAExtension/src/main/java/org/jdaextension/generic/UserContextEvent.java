package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.responses.Response;

public abstract class UserContextEvent extends GenericEvents {
    abstract public void configure(UserCommand userCommand);

    abstract public void onCall(UserContextInteractionEvent event, Response response);
}
