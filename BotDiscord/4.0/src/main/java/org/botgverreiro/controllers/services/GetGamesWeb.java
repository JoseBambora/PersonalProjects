package org.botgverreiro.controllers.services;

import org.botgverreiro.models.*;
import org.botgverreiro.utils.ExceptionsHandler;
import org.botgverreiro.utils.RequestsClass;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

import static org.botgverreiro.utils.ListUtils.extractStrings;

public class GetGamesWeb {

    private static GetGamesWeb getGamesWeb;

    private GetGamesWeb() {
    }

    public static GetGamesWeb getInstance() {
        if (getGamesWeb == null)
            getGamesWeb = new GetGamesWeb();
        return getGamesWeb;
    }

    public void call() {
        Call<String> html = RequestsClass.getRequests().getNextGames();
        html.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Elements gamesHTML = Jsoup.parse(response.body()).body().select("#list-calendar").select("div.items");
                    Mode mode = new Mode("Futebol");
                    // get strings html
                    List<String> dates = extractStrings(gamesHTML.select("div.date"), 0).stream().map(s -> s.split(" ")).map(s -> s[0] + " " + s[1].toLowerCase() + ".").toList();
                    List<String> times = extractStrings(gamesHTML.select("div.time"), 4);
                    List<String> homeTeams = extractStrings(gamesHTML.select("div.home").select("div.name"), 0);
                    List<String> teams = extractStrings(gamesHTML.select("div.teams").select("div.name"), 0);
                    // get crucial data
                    List<Team> teamsList = teams.stream().filter(s -> !s.equals("SC Braga")).map(Team::new).toList();
                    List<Integer> fields = homeTeams.stream().map(s -> s.equals("SC Braga")).map(b -> b ? 0 : 1).toList();
                    List<String> getTime = times.stream().map(s -> s.substring(0, 5)).toList();
                    Settings.commitTransaction(c -> {
                                Team.insertTeams(c, teamsList);
                                return Season.selectLastSeason(c);
                            })
                            .thenApply(s -> {
                                List<Game> games = new ArrayList<>(teamsList.size());
                                for (int i = 0; i < teamsList.size(); i++)
                                    games.add(new Game(s, mode, fields.get(i), dates.get(i), getTime.get(i), teamsList.get(i)));
                                return games;
                            })
                            .thenCompose(games -> Settings.commitTransaction(c -> Game.insertGames(c, games)))
                            .exceptionally(ExceptionsHandler::storeExceptionInt);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {
                ExceptionsHandler.storeException(throwable);
            }
        });
    }
}
