package org.jdaextension.examples.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;

import java.util.Map;

public class HelloMessage implements MessageReceiverInterface {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        messageReceiver.addButtonClick("1", this::onButton1)
                .addToPipeline((e, m) -> {
                    m.put("counter", "5");
                    return true;
                });
    }

    private void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("TemplateEmbed").setVariable("counter", "6");
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response) {
        response.setTemplate("TemplateEmbed").setVariable("counter", data.get("counter"));
        // response.addEmoji(Emoji.fromUnicode("✅"));
    }
}
