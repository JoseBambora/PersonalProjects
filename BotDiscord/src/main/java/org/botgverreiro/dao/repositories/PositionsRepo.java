package org.botgverreiro.dao.repositories;

import org.botgverreiro.dao.utils.Converters;
import org.botgverreiro.model.classes.User;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * Like all the repositories, this one uses <b>transactions</b> to commit database operations and uses SQLITE.
 * This class handles database connections for POSITIONS table. The table contains the following structure:
 * <p>
 * <code>
 * Table POSITIONS {
 * user STRING [primary key]
 * season STRING [primary key]
 * name String
 * points INT
 * predictions INT
 * }
 * </code>
 * </p>
 * This table also serves to save user important information (username + name).
 *
 * @see Converters
 */
public class PositionsRepo {
    private final Table<Record> tableName = table("POSITIONS");
    private final Field<String> seasonColumn = field("season", String.class);
    private final Field<String> nameColumn = field("name", String.class);
    private final Field<String> userColumn = field("user", String.class);
    private final Field<Integer> pointsColumn = field("points", Integer.class);
    private final Field<Integer> predictionsColumn = field("predictions", Integer.class);

    public PositionsRepo(String dbName) {
        createTable(dbName);
    }

    private void createTable(String dbName) {
        try (Connection connection = DriverManager.getConnection(dbName)) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            create.createTableIfNotExists(tableName)
                    .column(seasonColumn, SQLDataType.VARCHAR(50))
                    .column(userColumn, SQLDataType.VARCHAR(50))
                    .column(nameColumn, SQLDataType.VARCHAR(50))
                    .column(pointsColumn, SQLDataType.INTEGER)
                    .column(predictionsColumn, SQLDataType.INTEGER)
                    .primaryKey(userColumn, seasonColumn)
                    .execute();
        } catch (SQLException _) {
        }
    }

    /**
     * This function returns the position of a specific user.
     * More precisely, the user has the same position as the previous one if it has the same number of points, otherwise it has the next position.
     *
     * @param previousPoints Previous user points.
     * @param currentPoints  Current user points.
     * @param index          Previous user position.
     * @return Current user position.
     */
    private int getPosition(AtomicInteger previousPoints, int currentPoints, AtomicInteger index) {
        return previousPoints.getAndSet(currentPoints) == currentPoints ? index.get() : index.incrementAndGet();
    }

    /**
     * Get the classification for a specific season.
     *
     * @param configuration Database configuration.
     * @param season        Season to get the classification.
     * @return The classification.
     */
    public List<User> getPositionsSeason(Configuration configuration, String season) {
        AtomicInteger index = new AtomicInteger(0);
        AtomicInteger previousPoints = new AtomicInteger(-1);
        return configuration.dsl()
                .selectFrom(tableName)
                .where(seasonColumn.eq(season))
                .orderBy(pointsColumn.desc(), predictionsColumn.asc(), nameColumn.asc())
                .fetch()
                .stream()
                .map(r -> Converters.toUser(r, userColumn, nameColumn, predictionsColumn, pointsColumn, getPosition(previousPoints, r.get(pointsColumn), index)))
                .toList();
    }

    /**
     * Similar to <b>noPoints</b> method, but instead of incrementing predictions column, it increments points column.
     * This is important when the command <b>/win</b> is used, in order to increment the points from the winners.
     *
     * @param configuration Database configuration.
     * @param users         Winners users mentions.
     * @param season        Season ID.
     * @param points        Points to increment.
     */
    public void incrementPoints(Configuration configuration, Iterable<String> users, String season, int points) {
        for (String user : users) {
            configuration.dsl().update(tableName)
                    .set(pointsColumn, pointsColumn.plus(points))
                    .set(predictionsColumn, predictionsColumn.plus(1))
                    .where(seasonColumn.eq(season))
                    .and(userColumn.eq(user))
                    .execute();
        }
    }

    /**
     * Get the points of a specific user for a specific season.
     *
     * @param configuration Database configuration.
     * @param user          User mention.
     * @param season        Season ID.
     * @return The number of the user points.
     */
    public int getPointsUser(Configuration configuration, String user, String season) {
        return Optional.ofNullable(
                        configuration.dsl().select(pointsColumn)
                                .from(tableName)
                                .where(seasonColumn.eq(season))
                                .and(userColumn.eq(user))
                                .fetchOne(0, Integer.class))
                .orElse(0);

    }

    /**
     * For the case when a user wants to delete its information.
     *
     * @param configuration Database configuration.
     * @param user          User mention.
     * @return The number of deleted entries (can be more than one, in case of multiple seasons).
     */
    public int deleteUser(Configuration configuration, String user) {
        return configuration.dsl().deleteFrom(tableName)
                .where(userColumn.eq(user))
                .execute();
    }

    /**
     * Inserts a new user if it doesn't exist.
     *
     * @param configuration Database configuration.
     * @param mention       User Discord mention.
     * @param name          Username.
     * @param season        Season ID.
     */
    public void insertUser(Configuration configuration, String mention, String name, String season) {
        configuration.dsl()
                .insertInto(tableName)
                .set(userColumn, mention)
                .set(nameColumn, name)
                .set(seasonColumn, season)
                .set(pointsColumn, 0)
                .set(predictionsColumn, 0)
                .onConflictDoNothing()
                .executeAsync();
    }

}
