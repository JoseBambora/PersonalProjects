package cases.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.generic.MessageEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseMessageReceiver;
import org.jdaextension.responses.ResponseMessageUpdate;

import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessage implements MessageEvent {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e, v) -> e.getAuthor().getName().equals("teste");
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p2 = (e, v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean> p1u = (e, v) -> e.getAuthor().getName().equals("teste");
        BiFunction<MessageUpdateEvent, Map<String, Object>, Boolean> p2u = (e, v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        messageReceiver.addToPipelineReceive(p1).addToPipelineReceive(p2).addToPipelineUpdate(p1u).addToPipelineUpdate(p2u).setUpdates(true);
    }

    private void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setVariable("name", "Button Clicked").setTemplate("SimpleMessage");
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        response.setVariables(data).setTemplate("SimpleMessage");
    }

    @Override
    public void onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) {
        response.setVariables(data).setTemplate("SimpleMessage");
    }


    @Override
    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }

}
