package cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.Response;

import java.util.Map;

public class SimpleModal extends SlashEvent {

    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("modal").setDescription("test modal");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        response.setTemplate("SimpleModal").setModal();

    }

    @Override
    public void onCall(ModalInteractionEvent event, String id, Map<String, String> fields, Response response) {
        response.setTemplate("SimpleModalResponse");
    }
}
