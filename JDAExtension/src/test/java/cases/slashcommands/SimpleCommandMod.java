package cases.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseButton;
import com.github.josebambora.responses.ResponseCommand;

import java.util.Map;
import java.util.stream.IntStream;

public class SimpleCommandMod implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString option1 = new OptionString("name", "Name to appear in the message", true);
        OptionNumber option2 = new OptionNumber("number", "number of items", true, Number.INTEGER);
        slashCommand.setName("simplemod")
                .setDescription("ola versão 2")
                .addOption(option1)
                .addOption(option2)
                .addPermission(Permission.KICK_MEMBERS);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        String name = (String) variables.get("name");
        Integer integer = (Integer) variables.get("number");
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", name)
                .setVariable("items", IntStream.range(0, integer).boxed().toList())
                .send();
    }

    public void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 1 clicked")
                .send();
    }

    public void onButton2(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 2 clicked")
                .send();
    }

    public void onButton3(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 3 clicked")
                .send();
    }

    public void onButton4(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 4 clicked");
    }

    @Override
    public void onCall(ButtonInteractionEvent event, String id, ResponseButton response) {
        switch (id) {
            case "1" -> onButton1(event, response);
            case "2" -> onButton2(event, response);
            case "3" -> onButton3(event, response);
            case "4" -> onButton4(event, response);
            default -> response.setTemplate("400").setVariable("message", "Button does not exists").send();
        }
    }
}
