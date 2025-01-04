package cases.usercommands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.interfaces.UserCommandInterface;
import org.jdaextension.responses.Response;


public class HelloCommandUser implements UserCommandInterface {
    @Override
    public void configure(UserCommand userCommand) {
        userCommand.setName("Hello").setEphemeral().setSendThinking();
    }

    @Override
    public void onCall(UserContextInteractionEvent event, Response response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName());
    }
}
