package messages;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.jdaextension.interfaces.MessageReceiverInterface;
import org.jdaextension.responses.Response;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessage implements MessageReceiverInterface {
    @Override
    public List<BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean>> configure() {
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e,v) -> e.getAuthor().getName().equals("teste");
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p2 = (e,v) -> {
            v.put("name", e.getAuthor().getName());
            return true;
        };
        return List.of(p1,p2);
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, Response response) {
        response.setVariables(data).setTemplate("SimpleMessage");
    }
}
