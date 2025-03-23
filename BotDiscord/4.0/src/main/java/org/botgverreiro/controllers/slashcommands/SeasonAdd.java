package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Season;
import org.botgverreiro.models.Settings;

import java.util.Map;

public class SeasonAdd implements SlashEvent {

    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand
                .setName("season-add")
                .setDescription("Adicionar uma nova temporada")
                .setSendThinking()
                .setEphemeral();

    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Settings.commitTransaction(Season::insertNewSeason)
                .thenApply(n -> n == 1 ? responseCommand.setTemplate("seasons/SeasonAdd") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send);
    }
}
