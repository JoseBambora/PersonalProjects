package cases.messagecommands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.generic.MessageContextEvent;
import org.jdaextension.responses.ResponseCommand;


public class HelloCommandMessage implements MessageContextEvent {
    @Override
    public void configure(MessageCommand messageCommand) {
        messageCommand.setName("Hello").setEphemeral().setSendThinking();
    }

    @Override
    public void onCall(MessageContextInteractionEvent event, ResponseCommand response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName());
    }
}
