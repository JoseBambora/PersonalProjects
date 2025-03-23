package org.botgverreiro.controllers.services;

import com.github.josebambora.generic.OnReadyEvent;
import com.github.josebambora.responses.ResponseTextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import org.botgverreiro.models.*;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.RequestsClass;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletionStage;

import static org.botgverreiro.utils.ListUtils.extractStrings;

public class GetResultsWeb implements OnReadyEvent {
    private static GetResultsWeb getResultsWeb;
    private TextChannel textChannel;

    private GetResultsWeb() {
    }

    public static GetResultsWeb getInstance() {
        if (getResultsWeb == null)
            getResultsWeb = new GetResultsWeb();
        return getResultsWeb;
    }

    public void call() {
        Call<String> html = RequestsClass.getRequests().getResults();
        html.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Elements gamesHTML = Jsoup.parse(response.body()).body().select("#list-results").select("div.items");
                    Mode mode = new Mode("Futebol");
                    // get strings html
                    List<String> awayTeams = extractStrings(gamesHTML.select("div.away").select("div.name"), 0);
                    List<String> results = extractStrings(gamesHTML.select("div.teams__result"), 0);
                    // get crucial data
                    String awayTeam = awayTeams.getFirst();
                    List<Integer> result = Arrays.stream(results.getFirst().split(" - ")).map(Integer::parseInt).toList();

                    int homeGoals = result.getFirst();
                    int awayGoals = result.getLast();

                    CompletionStage<Game> game = Settings.commitTransaction(c -> Game.selectLastGame(c, mode));
                    CompletionStage<List<Prediction>> predictions = game.thenCompose(g -> Settings.commitTransaction(c -> Prediction.selectPredictionsGame(c, g.getGameId())));
                    CompletionStage<List<User>> winners = predictions.thenApply(predictionsList -> predictionsList.stream().filter(p -> p.isWinner(homeGoals, awayGoals)).map(Prediction::getUser).toList());
                    CompletionStage<List<User>> losers = predictions.thenApply(predictionsList -> predictionsList.stream().filter(p -> !p.isWinner(homeGoals, awayGoals)).map(Prediction::getUser).toList());
                    CompletionStage<Integer> addPointsWinners = winners.thenCompose(l -> Settings.commitTransaction(c -> User.updatePoints(c, l, true)));
                    CompletionStage<Integer> addPointsLosers = losers.thenCompose(l -> Settings.commitTransaction(c -> User.updatePoints(c, l, false)));
                    CompletionStage<Integer> deletePredictions = predictions.thenCompose(_ -> game.thenCompose(g -> Settings.commitTransaction(c -> Prediction.deletePredictionsGame(c, g.getGameId()))));
                    CompletionStage<Integer> updateGame = game
                            .thenApply(g -> g.setGameGoalsScored(g.isHome() ? homeGoals : awayGoals))
                            .thenApply(g -> g.setGameGoalsSuffered(g.isHome() ? awayGoals : homeGoals))
                            .thenCompose(g -> addPointsWinners.thenApply(g::setGameWinners))
                            .thenCompose(g -> deletePredictions.thenApply(g::setGamePredictions))
                            .thenCompose(g -> Settings.commitTransaction(c -> Game.updateFinishedGame(c, g)));

                    ResponseTextChannel responseTextChannel = new ResponseTextChannel(textChannel);
                    responseTextChannel.setTemplate("games/GameWinners");
                    game.thenApply(g -> responseTextChannel.setVariable("opponent", g.getGameOpponent().getTeamName()));
                    addPointsWinners.thenApply(s -> responseTextChannel.setVariable("winnersSize", s));
                    deletePredictions.thenApply(s -> responseTextChannel.setVariable("predictions", s));
                    winners.thenApply(w -> responseTextChannel.setVariable("winners", w.stream().map(User::getUserId)));

                    game.thenCompose(_ -> predictions)
                            .thenCompose(_ -> winners)
                            .thenCompose(_ -> losers)
                            .thenCompose(_ -> addPointsWinners)
                            .thenCompose(_ -> addPointsLosers)
                            .thenCompose(_ -> deletePredictions)
                            .thenCompose(_ -> updateGame)
                            .thenAccept(_ -> responseTextChannel.send());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                ExceptionsHandler.storeException(throwable);
            }
        });
    }

    @Override
    public void onCall(ReadyEvent readyEvent) {
        textChannel = readyEvent.getJDA().getTextChannelById(System.getenv("CHANNEL_PREDICTIONS"));
    }
}
