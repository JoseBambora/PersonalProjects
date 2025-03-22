package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.ListUtils;

import java.util.Map;

public class GameList implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        slashCommand.setName("game-list")
                .setDescription("Jogos agendados do bot")
                .setEphemeral()
                .setSendThinking();
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Settings.commitTransaction(Game::selectGamesNotOpened)
                .thenApply(g -> responseCommand.setTemplate("games/GamesList").setVariable("games", ListUtils.subList(g, 15)))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
