package org.jdaextension.cases.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jdaextension.configuration.SlashCommand;
import org.jdaextension.generic.SlashEvent;
import org.jdaextension.responses.Response;

import java.util.Map;

public class SimpleCommand extends SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("off")
                .setDescription("test");
    }

    @Override
    public void onCall(SlashCommandInteractionEvent event, Map<String, Object> variables, Response response) {
        response.setSuccess(() -> event.getJDA().shutdownNow());
    }
}
