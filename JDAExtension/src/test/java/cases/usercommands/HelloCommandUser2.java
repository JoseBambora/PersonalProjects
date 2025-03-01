package cases.usercommands;

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import com.github.josebambora.configuration.UserCommand;
import com.github.josebambora.generic.UserContextEvent;
import com.github.josebambora.responses.ResponseCommand;


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
