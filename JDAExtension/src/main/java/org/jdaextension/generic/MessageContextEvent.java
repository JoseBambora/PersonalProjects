package org.jdaextension.generic;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.responses.ResponseCommand;

public interface MessageContextEvent extends GenericEvents {
    void configure(MessageCommand messageCommand);

    void onCall(MessageContextInteractionEvent event, ResponseCommand response);
}
