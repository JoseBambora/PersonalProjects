package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Games;
import org.botgverreiro.tables.Predictions;
import org.botgverreiro.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Record4;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Prediction {
    private Game game;
    private User user;
    @Column(name = "GOALS_HOME")
    private int homeGoals;
    @Column(name = "GOALS_AWAY")
    private int awayGoals;

    public Prediction setInfo(Game game, User user) {
        this.game = game;
        this.user = user;
        return this;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    private static List<Record4<Integer,String, Integer, Integer>> combineValues(DSLContext context, String user, List<Integer> games, List<Integer> homeGoals, List<Integer> awayGoals) {
        return IntStream.range(0, games.size())
                .mapToObj(g -> context.newRecord(Predictions.PREDICTIONS.GAME_ID,Predictions.PREDICTIONS.USER_ID, Predictions.PREDICTIONS.GOALS_HOME, Predictions.PREDICTIONS.GOALS_AWAY)
                        .value1(games.get(g))
                        .value2(user)
                        .value3(homeGoals.get(g))
                        .value4(awayGoals.get(g)))
                .toList();
    }

    public static CompletionStage<Integer> insertPredictions(DSLContext context, String user, List<Integer> games, List<Integer> homeGoals, List<Integer> awayGoals) {
        return context
                .insertInto(Predictions.PREDICTIONS)
                .set(combineValues(context,user,games,homeGoals,awayGoals))
                .onDuplicateKeyUpdate()
                .setAllToExcluded()
                .executeAsync();
    }

    public static CompletionStage<List<Prediction>> getPredictionsGame(DSLContext context, int game) {
        return context
                .select()
                .from(Predictions.PREDICTIONS)
                .join(Users.USERS).on(Users.USERS.USER_ID.eq(Predictions.PREDICTIONS.USER_ID))
                .join(Games.GAMES).on(Games.GAMES.GAME_ID.eq(Predictions.PREDICTIONS.GAME_ID))
                .where(Predictions.PREDICTIONS.GAME_ID.eq(game))
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(l -> l.map(r -> r.into(Prediction.class).setInfo(r.into(Game.class),r.into(User.class))))
                .thenApply(Stream::toList);
    }

    public static CompletionStage<Integer> deletePredictionsGame(DSLContext context, int game) {
        return context
                .deleteFrom(Predictions.PREDICTIONS)
                .where(Predictions.PREDICTIONS.GAME_ID.eq(game))
                .executeAsync();
    }
}
