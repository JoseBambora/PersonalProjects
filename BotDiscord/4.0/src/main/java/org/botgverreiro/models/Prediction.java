package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Games;
import org.botgverreiro.tables.Predictions;
import org.botgverreiro.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Record4;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.IntStream;

public class Prediction {
    private Game game;
    private User user;
    @Column(name = "GOALS_HOME")
    private int homeGoals;
    @Column(name = "GOALS_AWAY")
    private int awayGoals;

    private static List<Record4<Integer, String, Integer, Integer>> combineValues(DSLContext context, String user, List<Game> games, List<Integer> homeGoals, List<Integer> awayGoals) {
        return IntStream.range(0, games.size())
                .mapToObj(g -> context.newRecord(Predictions.PREDICTIONS.GAME_ID, Predictions.PREDICTIONS.USER_ID, Predictions.PREDICTIONS.GOALS_HOME, Predictions.PREDICTIONS.GOALS_AWAY)
                        .value1(games.get(g).getGameId())
                        .value2(user)
                        .value3(homeGoals.get(g))
                        .value4(awayGoals.get(g)))
                .toList();
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    /* =================== Inserts =================== */

    /**
     * Inserts predictions realized by a user.
     * @param context Database context.
     * @param user User ID.
     * @param games List of games to add predictions.
     * @param homeGoals Home goals user predictions.
     * @param awayGoals Away goals user predictions.
     * @return Number of inserted rows.
     */
    public static CompletionStage<Integer> insertPredictions(DSLContext context, String user, List<Game> games, List<Integer> homeGoals, List<Integer> awayGoals) {
        return context
                .insertInto(Predictions.PREDICTIONS)
                .set(combineValues(context, user, games, homeGoals, awayGoals))
                .onDuplicateKeyUpdate()
                .setAllToExcluded()
                .executeAsync();
    }

    /* =================== Updates =================== */

    /* =================== Selects =================== */

    /**
     * Gets the predictions for one game.
     * @param context Database context.
     * @param game Game to get predictions.
     * @return A list with all the prediction for a specified game.
     */
    public static CompletionStage<List<Prediction>> selectPredictionsGame(DSLContext context, int game) {
        return context
                .select()
                .from(Predictions.PREDICTIONS)
                .join(Users.USERS).on(Users.USERS.USER_ID.eq(Predictions.PREDICTIONS.USER_ID))
                .join(Games.GAMES).on(Games.GAMES.GAME_ID.eq(Predictions.PREDICTIONS.GAME_ID))
                .where(Predictions.PREDICTIONS.GAME_ID.eq(game))
                .fetchAsync()
                .thenApply(l -> Wrappers.converter(l, Wrappers::toPrediction));
    }

    /* =================== Deletes =================== */

    /**
     * Deletes all the predictions for one specific game.
     * @param context Database context.
     * @param game Game ID to delete predictions.
     * @return Number of deleted rows.
     */
    public static CompletionStage<Integer> deletePredictionsGame(DSLContext context, int game) {
        return context
                .deleteFrom(Predictions.PREDICTIONS)
                .where(Predictions.PREDICTIONS.GAME_ID.eq(game))
                .executeAsync();
    }


    public Prediction setInfo(Game game, User user) {
        this.game = game;
        this.user = user;
        return this;
    }

    public boolean isWinner(int homeGoals, int awayGoals) {
        return this.homeGoals == homeGoals && this.awayGoals == awayGoals;
    }

    public Game getGame() {
        return game;
    }

    public User getUser() {
        return user;
    }
}
