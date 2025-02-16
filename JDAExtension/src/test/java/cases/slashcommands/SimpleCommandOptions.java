package cases.slashcommands;

import cases.MyType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.OptionCustom;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseAutoComplete;
import org.jdaextension.responses.ResponseCommand;

import java.util.HashMap;
import java.util.Map;

public class SimpleCommandOptions implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString option1 = new OptionString("word", "word desc", false)
                .addChoice("Option 1", "World")
                .addChoice("Option 2", "Braga");
        OptionCustom option2 = new OptionCustom("coords", "coords desc", true, MyType::new)
                .addChoice("Option 1", "(1,1)")
                .addChoice("Option 2", "(2,2)");
        OptionCustom option3 = new OptionCustom("coords2", "coords no choice", true, MyType::new)
                .setAutoComplete(this::onAutoComplete);
        slashCommand.setName("simpleoptions")
                .setDescription("hello world")
                .addOptions(option1, option2, option3)
                .setEphemeral()
                .setSendThinking();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        String word = (String) variables.get("word");
        MyType myType = (MyType) variables.get("coords");
        MyType myType2 = (MyType) variables.get("coords2");
        response.setTemplate("SimpleCommandOptions")
                .setVariable("word", word)
                .setVariable("coords", myType.toString())
                .setVariable("coords2", myType2.toString())
                .send();
    }


    public void onAutoComplete(CommandAutoCompleteInteractionEvent event, String value, ResponseAutoComplete responseAutoComplete) {
        Map<String, String> map = new HashMap<>();
        Map.of("(1,1)", "(1,1)", "(1,2)", "(2,2)").entrySet()
                .stream()
                .filter(n -> n.getValue().contains(value))
                .forEach(e -> responseAutoComplete.addChoice(new Command.Choice(e.getKey(),e.getValue())));
        responseAutoComplete.send();
    }
}
