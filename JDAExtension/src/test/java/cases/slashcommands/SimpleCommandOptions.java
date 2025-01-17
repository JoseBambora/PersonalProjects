package cases.slashcommands;

import cases.MyType;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command.Choice;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.configuration.option.OptionCustom;
import org.jdaextension.configuration.option.OptionString;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.ResponseCommand;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SimpleCommandOptions extends SlashEvent {
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
                .setVariable("coords2", myType2.toString());
    }


    public List<Choice> onAutoComplete(CommandAutoCompleteInteractionEvent event, String value) {
        return Stream.of(new Choice("(1,1)", "(1,1)"), new Choice("(1,2)", "(2,2)")).filter(n -> n.getAsString().contains(value)).toList();
    }
}
