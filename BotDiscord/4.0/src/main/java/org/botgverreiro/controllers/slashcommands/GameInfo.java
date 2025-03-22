package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseAutoComplete;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.Cache;
import org.botgverreiro.utils.ExceptionsHandler;

import java.util.Map;

public class GameInfo implements SlashEvent {

    private final Cache<String, Game> cacheGames;

    public GameInfo() {
        cacheGames = new Cache<>(s -> Settings.commitTransaction(c -> Game.getGames(c, s)));
    }

    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionNumber = new OptionNumber("jogo","Jogo a consultar",true, Number.INTEGER)
                .setAutoComplete(this::gameList);
        slashCommand
                .setName("game-inf")
                .setSendThinking()
                .setEphemeral()
                .setDescription("Visualizar detalhes especificos de um certo jogo")
                .addOption(optionNumber);
    }

    private void gameList(CommandAutoCompleteInteractionEvent event, String input, ResponseAutoComplete responseAutoComplete) {
        cacheGames.get(input)
                .thenAccept(l -> l.forEach(g -> responseAutoComplete.addChoice(g.toChoice())))
                .thenAccept(_ -> responseAutoComplete.send());
    }
    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer gameId = (Integer) map.get("jogo");
        Settings.commitTransaction(c -> Game.getGame(c,gameId))
                .thenApply(g -> g != null ?
                        responseCommand.setVariable("gameExists", true)
                                .setVariable("opponent",g.getGameOpponent().getTeamName())
                                .setVariable("dateTime",g.getGameDay())
                                .setVariable("status", g.getGameStatus())
                                .setVariable("mode", g.getMode().toString())
                                .setVariable("field",g.getGameField())
                                .setVariable("scored", g.getGameGoalsScored())
                                .setVariable("suffered", g.getGameGoalsSuffered())
                                .setVariable("predictions", g.getGamePredictions())
                                .setVariable("correct",g.getGameWinners())
                        : responseCommand.setVariable("gameExists", false))
                .thenApply(r -> r.setTemplate("games/GameInfo"))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
