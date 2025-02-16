package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Modes;
import org.botgverreiro.tables.Seasons;
import org.botgverreiro.tables.Users;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record3;
import org.jooq.impl.DSL;
import org.jooq.impl.QOM;

import java.util.ArrayList;
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
    public static CompletionStage<List<User>> getClassificationSeason(DSLContext context,int season)  {
        return context
                .select()
                .from(Users.USERS)
                .join(Modes.MODES).on(Users.USERS.MODE_NAME.eq(Modes.MODES.MODE_NAME))
                .join(Seasons.SEASONS).on(Users.USERS.SEASON_ID.eq(Seasons.SEASONS.SEASON_ID))
                .where(Users.USERS.SEASON_ID.eq(season))
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(r -> r.map(u -> u.into(User.class).setSeason(u.into(Season.class)).setMode(u.into(Mode.class))))
                .thenApply(Stream::toList);

    }

    private static List<Record3<String,Integer,String>> combineValues(DSLContext context, Collection<String> users, int season, String mode) {
        return users.stream()
                .map(n -> context.newRecord(Users.USERS.USER_ID,Users.USERS.SEASON_ID, Users.USERS.MODE_NAME).value1(n).value2(season).value3(mode))
                .toList();
    }

    public static CompletionStage<Integer> updatePoints(DSLContext context, Collection<String> users, int season, String mode, int points, int predictions) {
        return context
                .insertInto(Users.USERS, Users.USERS.USER_ID,Users.USERS.SEASON_ID, Users.USERS.MODE_NAME)
                .valuesOfRecords(combineValues(context,users,season,mode))
                .onDuplicateKeyUpdate()
                .set(Users.USERS.POINTS, Users.USERS.POINTS.plus(points))
                .set(Users.USERS.PREDICTIONS, Users.USERS.PREDICTIONS.plus(predictions))
                .executeAsync();
    }

    public static CompletionStage<List<User>> getUserStats(DSLContext context, String user) {
        return context
                .selectFrom(Users.USERS)
                .where(Users.USERS.USER_ID.eq(user))
                .fetchAsync()
                .thenApply(r -> r.into(User.class));
    }

    public static CompletionStage<Integer> deleteUser(DSLContext context,String user) {
        return context
                .deleteFrom(Users.USERS)
                .where(Users.USERS.USER_ID.eq(user))
                .executeAsync();
    }
}
