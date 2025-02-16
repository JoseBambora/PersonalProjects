package cases.usercommands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import org.jdaextension.configuration.UserCommand;
import org.jdaextension.generic.UserContextEvent;
import org.jdaextension.responses.ResponseCommand;


public class HelloCommandUser2 implements UserContextEvent {
    @Override
    public void configure(UserCommand userCommand) {
        userCommand.setName("Hello2");
    }

    @Override
    public void onCall(UserContextInteractionEvent event, ResponseCommand response) {
        response.setTemplate("SimpleMessage").setVariable("name", event.getUser().getName()).send();
    }
}
