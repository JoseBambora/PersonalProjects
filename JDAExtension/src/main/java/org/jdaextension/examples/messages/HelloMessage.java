package org.jdaextension.examples.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;

import java.util.List;

public class HelloMessage implements MessageReceiverInterface {
    @Override
    public MessageReceiver configure() {
        return new MessageReceiver()
                .setRegex("(\\d+) *[x\\-] *(\\d+)", (m) -> String.format("%s, mensagem no formato errado.", m.getAuthor().getAsMention()))
                .catchPatternMultipleTimes()
                .addButtonClick("1", this::onButton1);
    }

    private void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Message");
    }

    @Override
    public void onCall(MessageReceivedEvent event, List<String> groups, Response response) {
        System.out.println(groups);
        response.setTemplate("TemplateEmbed").setVariable("counter", "2");
        // response.addEmoji(Emoji.fromUnicode("✅"));
    }
}
