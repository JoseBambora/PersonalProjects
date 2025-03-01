package cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import com.github.josebambora.responses.ResponseModal;

import java.util.Map;

public class SimpleModal implements SlashEvent {

    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("modal").setDescription("test modal");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, ResponseCommand response) {
        response.setTemplate("SimpleModal").setModal().send();

    }

    @Override
    public void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, ResponseModal response) {
        response.setTemplate("SimpleModalResponse").send();
    }
}
