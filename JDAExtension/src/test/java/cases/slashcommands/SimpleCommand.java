package cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.Response;

import java.util.Map;

public class SimpleCommand extends SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("simple")
                .setDescription("test");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "1");
    }


    public void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "2");
    }

    @Override
    public void onCall(ButtonInteractionEvent event, String id, Response response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }
}
