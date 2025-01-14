package cases.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.generic.MessageReceiverEvent;
import org.jdaextension.responses.Response;

import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessage extends MessageReceiverEvent {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e, v) -> e.getAuthor().getName().equals("teste");
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p2 = (e, v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        messageReceiver.addToPipelineReceive(p1).addToPipelineReceive(p2);
    }

    private void onButton1(ButtonInteractionEvent event, Response response) {
        response.setVariable("name", "Button Clicked").setTemplate("SimpleMessage");
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response) {
        response.setVariables(data).setTemplate("SimpleMessage");
    }

    @Override
    public void onCall(MessageUpdateEvent event, Map<String, Object> data, Response response) {

    }


    @Override
    public void onCall(ButtonInteractionEvent event, String id, Response response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }

}
