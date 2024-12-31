package slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.Number;
import org.jdaextension.configuration.option.OptionNumber;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.interfaces.SlashCommandInterface;
import org.jdaextension.responses.Response;

import java.util.Map;
import java.util.stream.IntStream;

public class SimpleCommandMod implements SlashCommandInterface {
    @Override
    public SlashCommand configure() {
        OptionString option1 = new OptionString("name", "Name to appear in the message", true);
        OptionNumber option2 = new OptionNumber("number", "number of items", true, Number.INTEGER);
        return new SlashCommand("simplemod", "ola versão 2")
                .addOption(option1)
                .addOption(option2)
                .addButtonClick("1", this::onButton1)
                .addButtonClick("2", this::onButton2)
                .addButtonClick("3", this::onButton3)
                .addButtonClick("4", this::onButton4)
                .addPermission(Permission.KICK_MEMBERS);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        String name = (String) variables.get("name");
        Integer integer = (Integer) variables.get("number");
        response.setTemplate("SimpleCommandMod")
                .setVariable("name", name)
                .setVariable("items", IntStream.range(0, integer).boxed().toList());
    }

    public void onButton1(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Button 1 clicked");
    }

    public void onButton2(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Button 2 clicked");
    }

    public void onButton3(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Button 3 clicked");
    }

    public void onButton4(ButtonInteractionEvent event, Response response) {
        response.setTemplate("Template3")
                .setVariable("name", "Button 4 clicked");
    }
}
