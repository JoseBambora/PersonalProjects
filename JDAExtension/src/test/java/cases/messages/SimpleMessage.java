package cases.messages;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.configuration.MessageReceiver;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessage implements MessageReceiverInterface {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e, v) -> e.getAuthor().getName().equals("teste");
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p2 = (e, v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        messageReceiver.addToPipeline(p1).addToPipeline(p2).addButtonClick("1", this::button1);
    }

    private void button1(ButtonInteractionEvent event, Response response) {
        response.setVariable("name","Button Clicked").setTemplate("SimpleMessage");

    }
    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response) {
        response.setVariables(data).setTemplate("SimpleMessage");
    }
}
