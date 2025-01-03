package cases.messagecommands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.interfaces.MessageCommandInterface;
import org.jdaextension.responses.Response;


public class HelloCommandMessage implements MessageCommandInterface {
    @Override
    public MessageCommand configure() {
        return new MessageCommand("Hello").setEphemeral().setSendThinking();
    }

    @Override
    public void onCall(MessageContextInteractionEvent event, Response response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName());
    }
}
