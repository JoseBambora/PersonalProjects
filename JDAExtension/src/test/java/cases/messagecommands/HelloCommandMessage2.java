package cases.messagecommands;

import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jdaextension.configuration.MessageCommand;
import org.jdaextension.interfaces.MessageCommandInterface;
import org.jdaextension.responses.Response;


public class HelloCommandMessage2 implements MessageCommandInterface {
    @Override
    public void configure(MessageCommand messageCommand) {
        messageCommand.setName("Hello2");
    }

    @Override
    public void onCall(MessageContextInteractionEvent event, Response response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName());
    }
}
