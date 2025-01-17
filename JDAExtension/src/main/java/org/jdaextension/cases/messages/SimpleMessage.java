package org.jdaextension.cases.messages;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.generic.MessageReceiverEvent;
import org.jdaextension.responses.ResponseMessageReceiver;
import org.jdaextension.responses.ResponseMessageUpdate;

import java.util.Map;

public class SimpleMessage extends MessageReceiverEvent {
    @Override
    public void configure(MessageReceiver messageReceiver) {

    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        response.addEmoji("✅");
    }

    @Override
    public void onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) {
        response.cleanReactions().addEmoji("U+1F504");
    }
}
