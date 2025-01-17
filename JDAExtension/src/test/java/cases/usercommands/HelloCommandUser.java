package cases.usercommands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.generic.UserContextEvent;
import org.jdaextension.responses.ResponseCommand;


public class HelloCommandUser extends UserContextEvent {
    @Override
    public void configure(UserCommand userCommand) {
        userCommand.setName("Hello").setEphemeral().setSendThinking();
    }

    @Override
    public void onCall(UserContextInteractionEvent event, ResponseCommand response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName());
    }
}
