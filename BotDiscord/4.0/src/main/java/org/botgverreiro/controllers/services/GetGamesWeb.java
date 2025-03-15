package org.botgverreiro.controllers.services;

import com.github.josebambora.responses.ResponseCommand;
import org.botgverreiro.models.*;
import org.botgverreiro.utils.ExceptionsHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.converter.scalars.ScalarsConverterFactory;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class GetGamesWeb {
    public interface Requests {
        @GET("modalidade/futebol/?eqp=687#calendario")
        Call<String> getNextGames();

        @GET("modalidade/futebol/?eqp=687#resultados")
        Call<String> getResults();
    }
    private Requests requests;

    private GetGamesWeb() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(System.getenv("URL_SITE"))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        requests = retrofit.create(Requests.class);
    }

    private List<String> extractStrings(Elements elements, int removeFstChars) {
        return elements.stream().map(Element::wholeText).map(String::strip).map(s -> s.substring(removeFstChars)).toList();
    }

    public void call() {
        Call<String> html = requests.getResults();
        html.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Elements gamesHTML = Jsoup.parse(response.body()).body().select("#list-calendar").select("div.items");
                    List<String> dates = extractStrings(gamesHTML.select("div.date"),0).stream().map(s -> s.split(" ")).map(s -> s[0] + " " + s[1].toLowerCase() + ".").toList();
                    List<String> times = extractStrings(gamesHTML.select("div.time"),4);
                    List<String> homeTeams = extractStrings(gamesHTML.select("div.home").select("div.name"),0);
                    List<String> teams = extractStrings(gamesHTML.select("div.teams").select("div.name"),0);
                    Mode mode = new Mode("Futebol");
                    List<Team> teamsList = teams.stream().filter(s -> !s.equals("SC Braga")).map(Team::new).toList();
                    List<Integer> fields = homeTeams.stream().map(s -> s.equals("SC Braga")).map(b -> b ? 0 : 1).toList();
                    List<String> getTime = times.stream().map(s -> s.substring(0,5)).toList();
                    Settings.commitTransaction(c -> {
                                Team.insertTeams(c, teamsList);
                                return Season.getLastSeason(c);
                            })
                            .thenApply(s -> {
                                List<Game> games = new ArrayList<>(teamsList.size());
                                for(int i = 0; i < teamsList.size(); i++)
                                    games.add(new Game(s,mode,fields.get(i),dates.get(i),getTime.get(i),teamsList.get(i)));
                                return games;
                            })
                            .thenCompose(games -> Settings.commitTransaction(c -> Game.insertGames(c,games)))
                            .exceptionally(ExceptionsHandler::storeExceptionInt);

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable throwable) {

            }
        });
    }



    private static GetGamesWeb getGamesWeb;
    public static GetGamesWeb getInstance() {
        if(getGamesWeb == null)
            getGamesWeb = new GetGamesWeb();
        return getGamesWeb;
    }
}
