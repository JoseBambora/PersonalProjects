package org.jdaextension.examples;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.ResponseMessage;

import java.util.List;

public class HelloMessage implements MessageReceiverInterface {
    @Override
    public MessageReceiver configure() {
        return new MessageReceiver()
                .setRegex("(\\d+) *[x\\-] *(\\d+)", (m) -> String.format("%s, mensagem no formato errado.",m.getAuthor().getAsMention()))
                .catchPatternMultipleTimes();
    }

    @Override
    public ResponseMessage onCall(MessageReceivedEvent event, List<String> groups) {
        System.out.println(groups);
        return new ResponseMessage("Template");
    }
}
