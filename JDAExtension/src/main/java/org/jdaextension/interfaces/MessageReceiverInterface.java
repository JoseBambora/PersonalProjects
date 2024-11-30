package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.responses.Response;

import java.util.List;


public interface MessageReceiverInterface {
    MessageReceiver configure();
    void onCall(MessageReceivedEvent event, List<String> groups, Response response);
}
