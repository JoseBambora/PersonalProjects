package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseAutoComplete;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.GameList;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.Cache;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.GameStatus;

import java.util.Map;

public class GameDel implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionGame = new OptionNumber("jogo", "Jogo a remover", true, Number.INTEGER)
                .setAutoComplete(GameList::gameListNotOpened);
        slashCommand.setName("game-del")
                .setDescription("Remover um jogo do calendário")
                .setSendThinking()
                .setEphemeral()
                .addOptions(optionGame)
                .addPermission(Permission.KICK_MEMBERS);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer gameId = (Integer) map.get("jogo");
        Settings.commitTransaction(c -> Game.deleteGame(c, gameId))
                .thenApply(r -> r == 1 ? responseCommand.setTemplate("Success").setVariable("op", "Remover Jogo.") : responseCommand.setTemplate("500"))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
