package cases.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseButton;
import org.jdaextension.responses.ResponseCommand;

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
                .setVariable("items", IntStream.range(0, integer).boxed().toList());
    }

    public void onButton1(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 1 clicked");
    }

    public void onButton2(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 2 clicked");
    }

    public void onButton3(ButtonInteractionEvent event, ResponseButton response) {
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", "Button 3 clicked");
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
            default -> response.setTemplate("400").setVariable("message", "Button does not exists");
        }
    }
}
