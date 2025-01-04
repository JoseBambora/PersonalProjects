package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.responses.Response;

public abstract class MessageContextEvent extends GenericEvents {
    abstract public void configure(MessageCommand messageCommand);

    abstract public void onCall(MessageContextInteractionEvent event, Response response);
}
