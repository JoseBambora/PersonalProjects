package slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;

import java.util.Map;

public class SimpleCommand implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        return new SlashCommand("simple", "test")
                .addButtonClick("1", this::onButton1);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "1");
    }


    public void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("SimpleCommand").setVariable("counter", "2");
    }
}
