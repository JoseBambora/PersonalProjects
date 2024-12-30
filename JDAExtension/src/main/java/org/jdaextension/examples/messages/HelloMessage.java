package org.jdaextension.examples.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class HelloMessage implements MessageReceiverInterface {
    @Override
    public List<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> configure() {
        return List.of(
                (e, m) -> {
                    m.put("counter", "5");
                    return true;
                }
        );
    }

    private void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Message");
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response) {
        response.setTemplate("TemplateEmbed").setVariable("counter", data.get("counter"));
        // response.addEmoji(Emoji.fromUnicode("✅"));
    }
}
