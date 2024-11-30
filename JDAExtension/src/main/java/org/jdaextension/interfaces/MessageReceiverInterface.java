package org.jdaextension.interfaces;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.responses.ResponseMessage;

import java.util.List;


public interface MessageReceiverInterface {
    MessageReceiver configure();
    ResponseMessage onCall(MessageReceivedEvent event, List<String> groups);
}
