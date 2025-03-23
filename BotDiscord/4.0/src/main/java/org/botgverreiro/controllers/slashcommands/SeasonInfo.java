package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.controllers.autocompleters.SeasonList;
import org.botgverreiro.models.Game;
import org.botgverreiro.models.Season;
import org.botgverreiro.models.Settings;
import org.botgverreiro.utils.ExceptionsHandler;

import java.util.Map;

public class SeasonInfo implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionNumber optionNumber = new OptionNumber("temporada","Temporada a consultar", true, Number.INTEGER)
                .setAutoComplete(SeasonList::seasonList);
        slashCommand
                .setName("season-info")
                .setDescription("Consultar dados de uma temporada")
                .setSendThinking()
                .addOption(optionNumber);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        Integer seasonId = (Integer) map.get("temporada");
        Settings.commitTransaction(c -> Game.selectGamesBySeason(c, seasonId))
                .thenApply(l -> {
                    int totalWinners = l.stream().map(Game::getGameWinners).reduce(0, Integer::sum);
                    int totalPrediction = l.stream().map(Game::getGamePredictions).reduce(0, Integer::sum);
                    float percentage = (float) totalWinners * 100/ totalPrediction;
                    int totalGoalsScored = l.stream().map(Game::getGameGoalsScored).reduce(0, Integer::sum);
                    int totalGoalsSuffered = l.stream().map(Game::getGameGoalsSuffered).reduce(0, Integer::sum);
                    return responseCommand.setTemplate("seasons/SeasonInfo")
                            .setVariable("totalWinners",totalWinners)
                            .setVariable("totalPrediction",totalPrediction)
                            .setVariable("percentage",percentage)
                            .setVariable("totalGoalsScored",totalGoalsScored)
                            .setVariable("totalGoalsSuffered",totalGoalsSuffered);
                })
                .thenCompose(_ -> Settings.commitTransaction(c -> Season.selectSeason(c,seasonId)))
                .thenApply(s -> responseCommand.setVariable("season",s.toString()))
                .thenAccept(ResponseCommand::send)
                .exceptionally(ExceptionsHandler::storeException);
    }
}
