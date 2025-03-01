package cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseButton;
import com.github.josebambora.responses.ResponseCommand;

import java.util.Map;

public class SimpleCommand implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("simple")
                .setDescription("test");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "1").send();
    }


    public void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "2").send();
    }

    @Override
    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists").send();
        }
    }
}
