package cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseCommand;

import java.util.Map;

public class SimpleCommand implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("simple")
                .setDescription("test");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "1");
    }


    public void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "2");
    }

    @Override
    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }
}
