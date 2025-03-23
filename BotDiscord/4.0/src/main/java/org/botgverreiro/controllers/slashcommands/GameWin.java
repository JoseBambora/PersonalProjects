package org.botgverreiro.controllers.slashcommands;

import com.github.josebambora.configuration.SlashCommand;
import com.github.josebambora.configuration.option.Number;
import com.github.josebambora.configuration.option.OptionNumber;
import com.github.josebambora.configuration.option.OptionString;
import com.github.josebambora.generic.SlashEvent;
import com.github.josebambora.responses.ResponseCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.botgverreiro.models.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;

public class GameWin implements SlashEvent {
    @Override
    public void configure(SlashCommand slashCommand) {
        OptionString optionMode = new OptionString("modo", "Modalidade do jogo", true);
        Settings.commitTransactionNoResult(c -> Mode.selectAllModesSync(c).forEach(m -> optionMode.addChoice(m.toString(), m.toString())));

        OptionNumber optionGoalsHome = new OptionNumber("goloscasa", "Golos marcados pela equipa visitada",true, Number.INTEGER);
        OptionNumber optionGoalsAway = new OptionNumber("golosfora", "Golos marcados pela equipa visitante",true, Number.INTEGER);

        slashCommand
                .setName("game-win")
                .setDescription("Definir um resultado de um jogo")
                .addOptions(optionMode,optionGoalsHome,optionGoalsAway);
    }

    @Override
    public void onCall(SlashCommandInteractionEvent slashCommandInteractionEvent, Map<String, Object> map, ResponseCommand responseCommand) {
        String mode = (String) map.get("modo");
        Integer homeGoals = (Integer) map.get("goloscasa");
        Integer awayGoals = (Integer) map.get("golosfora");

        CompletionStage<Game> game = Settings.commitTransaction(c -> Game.selectLastGame(c, new Mode(mode)));
        CompletionStage<List<Prediction>> predictions = game.thenCompose(g -> Settings.commitTransaction(c -> Prediction.selectPredictionsGame(c, g.getGameId())));
        CompletionStage<List<User>> winners = predictions.thenApply(predictionsList -> predictionsList.stream().filter(p -> p.isWinner(homeGoals, awayGoals)).map(Prediction::getUser).toList());
        CompletionStage<List<User>> losers = predictions.thenApply(predictionsList -> predictionsList.stream().filter(p -> !p.isWinner(homeGoals, awayGoals)).map(Prediction::getUser).toList());
        CompletionStage<Integer> addPointsWinners = winners.thenCompose(l -> Settings.commitTransaction(c -> User.updatePoints(c, l, true)));
        CompletionStage<Integer> addPointsLosers = losers.thenCompose(l -> Settings.commitTransaction(c -> User.updatePoints(c, l, false)));
        CompletionStage<Integer> deletePredictions = predictions.thenCompose(_ -> game.thenCompose(g -> Settings.commitTransaction(c -> Prediction.deletePredictionsGame(c, g.getGameId()))));
        CompletionStage<Integer> updateGame = game
                .thenApply(g -> g.setGameGoalsScored(g.isHome() ? homeGoals :  awayGoals))
                .thenApply(g -> g.setGameGoalsSuffered(g.isHome() ? awayGoals :  homeGoals))
                .thenCompose(g -> addPointsWinners.thenApply(g::setGameWinners))
                .thenCompose(g -> deletePredictions.thenApply(g::setGamePredictions))
                .thenCompose(g -> Settings.commitTransaction(c -> Game.updateFinishedGame(c, g)));

        responseCommand.setTemplate("games/GameWinners");
        game.thenApply(g -> responseCommand.setVariable("opponent", g.getGameOpponent().getTeamName()));
        addPointsWinners.thenApply(s -> responseCommand.setVariable("winnersSize", s));
        deletePredictions.thenApply(s -> responseCommand.setVariable("predictions", s));
        winners.thenApply(w -> responseCommand.setVariable("winners", w.stream().map(User::getUserId)));

        game.thenCompose(_ -> predictions)
                .thenCompose(_ -> winners)
                .thenCompose(_ -> losers)
                .thenCompose(_ -> addPointsWinners)
                .thenCompose(_ -> addPointsLosers)
                .thenCompose(_ -> deletePredictions)
                .thenCompose(_ -> updateGame)
                .thenAccept(_ -> responseCommand.send());
    }
}
