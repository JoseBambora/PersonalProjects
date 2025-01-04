package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.responses.Response;

public interface MessageCommandInterface {
    void configure(MessageCommand messageCommand);

    void onCall(MessageContextInteractionEvent event, Response response);
}
