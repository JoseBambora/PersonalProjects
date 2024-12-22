package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.responses.Response;

public interface UserCommandInterface {
    UserCommand configure();

    void onCall(UserContextInteractionEvent event, Response response);
}
