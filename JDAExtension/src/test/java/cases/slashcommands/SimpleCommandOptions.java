package cases.slashcommands;

import cases.MyType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.OptionCustom;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseAutoComplete;
import com.github.josebambora.responses.ResponseCommand;

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
