package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.responses.Response;

public interface MessageCommandInterface {
    MessageCommand configure();
    void onCall(MessageContextInteractionEvent event, Response response);
}
