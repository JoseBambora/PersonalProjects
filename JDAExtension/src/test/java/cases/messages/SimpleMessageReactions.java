package cases.messages;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import com.github.josebambora.configuration.MessageReceiver;
import com.github.josebambora.generic.MessageEvent;
import com.github.josebambora.responses.ResponseMessageReceiver;
import com.github.josebambora.responses.ResponseMessageUpdate;

import java.util.Map;
import java.util.function.BiFunction;

public class SimpleMessageReactions implements MessageEvent {
    @Override
    public void configure(MessageReceiver messageReceiver) {
        BiFunction<MessageReceivedEvent, Map<String, Object>, Boolean> p1 = (e, v) -> e.getAuthor().getName().equals("teste3");
        messageReceiver.setUpdates(false).setDeletes(true).addToPipelineReceive(p1);
    }

    @Override
    public void onCall(MessageReceivedEvent event, Map<String, Object> data, ResponseMessageReceiver response) {
        response.addEmoji("✅").send();
    }

    @Override
    public void onCall(MessageUpdateEvent event, Map<String, Object> data, ResponseMessageUpdate response) {
        response.cleanReactions().addEmoji("U+1F504").send();
    }
}
