package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Modes;
import org.botgverreiro.tables.Seasons;
import org.botgverreiro.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class User {
    @Column(name = "USER_ID")
    private String userId;

    private Season season;
    private Mode mode;

    @Column(name = "POINTS")
    private int userPoints;
    @Column(name = "PREDICTIONS")
    private int userPredictions;


    public User setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public User setSeason(Season season) {
        this.season = season;
        return this;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    private static User fromRecordToUser(Record record) {
        return record.into(User.class)
                .setSeason(record.into(Season.class))
                .setMode(record.into(Mode.class));
    }

    /**
     * Get classification for a specific Season for all Modes.
     *
     * @param context Database context.
     * @param season Season in question.
     * @return Sorted list of users by points.
     */
    public static CompletionStage<List<User>> getClassificationSeason(DSLContext context, int season) {
        return context
                .select()
                .from(Users.USERS)
                .join(Modes.MODES).on(Users.USERS.MODE_NAME.eq(Modes.MODES.MODE_NAME))
                .join(Seasons.SEASONS).on(Users.USERS.SEASON_ID.eq(Seasons.SEASONS.SEASON_ID))
                .where(Users.USERS.SEASON_ID.eq(season))
                .orderBy(Users.USERS.MODE_NAME,Users.USERS.POINTS.desc())
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(r -> r.map(User::fromRecordToUser))
                .thenApply(Stream::toList);

    }

    /**
     * Auxiliary function to update the points, by combining a list of users for the same season and mode.
     *
     * @param context Database context.
     * @param users Users to update points.
     * @param season Season to update points.
     * @param mode The associated mode.
     * @return A list containing the association of users-season-mode.
     */
    private static List<Record3<String, Integer, String>> combineValues(DSLContext context, String userId, List<Game> games) {
        return games.stream()
                .map(g -> context.newRecord(Users.USERS.USER_ID, Users.USERS.SEASON_ID, Users.USERS.MODE_NAME).value1(userId).value2(g.getSeason().getSeasonId()).value3(g.getMode().getModeId()))
                .toList();
    }

    /**
     * Method that updates the classification points.
     *
     * @param context Database context.
     * @param users Users to update points.
     * @param season Season in the context.
     * @param mode Mode in the context.
     * @param points Points to increment.
     * @param predictions Predictions to increment.
     * @return An integer containing the number of affected rows.
     */
    public static CompletionStage<Integer> updatePoints(DSLContext context, List<User> users, boolean areWinners) {
        List<String> userIds = users.stream().map(u -> u.userId).toList();
        return context
                .update(Users.USERS)
                .set(Users.USERS.POINTS, Users.USERS.POINTS.plus(areWinners ? 3 : 1))
                .set(Users.USERS.PREDICTIONS, Users.USERS.PREDICTIONS.plus(1))
                .where(Users.USERS.USER_ID.in(userIds))
                .executeAsync();
    }

    public static CompletionStage<Integer> insertUser(DSLContext context, String userId, List<Game> games) {
        return context
                .insertInto(Users.USERS)
                .set(combineValues(context,userId,games))
                .onConflictDoNothing()
                .executeAsync();
    }

    /**
     * Method that returns user statistics.
     *
     * @param context Database context.
     * @param user User to see their stats.
     * @return User statistics.
     */
    public static CompletionStage<User> getUserStats(DSLContext context, String user) {
        return context
                .select().from(Users.USERS)
                .join(Modes.MODES).on(Users.USERS.MODE_NAME.eq(Modes.MODES.MODE_NAME))
                .join(Seasons.SEASONS).on(Users.USERS.SEASON_ID.eq(Seasons.SEASONS.SEASON_ID))
                .where(Users.USERS.USER_ID.eq(user))
                .fetchAsync()
                .thenApply(List::getFirst)
                .thenApply(User::fromRecordToUser);
    }

    /**
     * Delete information of a user.
     *
     * @param context Database context.
     * @param user User to delete.
     * @return All the affected rows, 1 if success, 0 otherwise.
     */
    public static CompletionStage<Integer> deleteUser(DSLContext context, String user) {
        return context
                .deleteFrom(Users.USERS)
                .where(Users.USERS.USER_ID.eq(user))
                .executeAsync();
    }

    public String getUserId() {
        return userId;
    }
}
