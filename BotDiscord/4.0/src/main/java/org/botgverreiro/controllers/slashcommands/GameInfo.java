package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.GameList;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;

import java.util.Map;

public class GameInfo implements SlashEvent {

    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionNumber = new OptionNumber("jogo", "Jogo a consultar", true, Number.INTEGER)
                .setAutoComplete(GameList::gameList);
        slashCommand
                .setName("game-info")
                .setDescription("Visualizar detalhes especificos de um certo jogo")
                .addOption(optionNumber);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer gameId = (Integer) map.get("jogo");
        Settings.commitTransaction(c -> Game.selectGame(c, gameId))
                .thenApply(g -> g.isPresent() ?
                        responseCommand.setVariable("gameExists", true)
                                .setVariable("opponent", g.get().getGameOpponent().getTeamName())
                                .setVariable("dateTime", g.get().getGameDay())
                                .setVariable("status", g.get().getGameStatus())
                                .setVariable("mode", g.get().getMode().toString())
                                .setVariable("field", g.get().getGameField())
                                .setVariable("scored", g.get().getGameGoalsScored())
                                .setVariable("suffered", g.get().getGameGoalsSuffered())
                                .setVariable("predictions", g.get().getGamePredictions())
                                .setVariable("correct", g.get().getGameWinners())
                        : responseCommand.setVariable("gameExists", false))
                .thenApply(r -> r.setTemplate("games/GameInfo"))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
