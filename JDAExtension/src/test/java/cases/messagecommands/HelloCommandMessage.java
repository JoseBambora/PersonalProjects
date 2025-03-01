package cases.messagecommands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import com.github.josebambora.configuration.MessageCommand;
import com.github.josebambora.generic.MessageContextEvent;
import com.github.josebambora.responses.ResponseCommand;


public class HelloCommandMessage implements MessageContextEvent {
    @Override
    public void configure(MessageCommand messageCommand) {
        messageCommand.setName("Hello").setEphemeral().setSendThinking();
    }

    @Override
    public void onCall(MessageContextInteractionEvent event, ResponseCommand response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName()).send();
    }
}
