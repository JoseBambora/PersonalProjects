package org.botgverreiro.dao.repositories;

import org.botgverreiro.dao.utils.Converters;
import org.botgverreiro.model.classes.Prediction;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

/**
 * Like all the repositories, this one uses <b>transactions</b> to commit database operations and uses SQLITE.
 * This class handles database connections for PREDICTIONS table. The table contains the following structure:
 * <p>
 * <code>
 * Table PREDICTIONS {
 * user STRING [primary key]
 * gameday INT [primary key]
 * home INT
 * away INT
 * }
 * </code>
 * </p>
 *
 * @see Converters
 */

public class PredictionsRepo {
    private final Table<Record> tableName = table("PREDICTIONS");
    private final Field<String> userColumn = field("user", String.class);
    private final Field<Integer> gameDayColumn = field("gameday", Integer.class);
    private final Field<Integer> homeColumn = field("home", Integer.class);
    private final Field<Integer> awayColumn = field("away", Integer.class);

    public PredictionsRepo(String dbName) {
        createTable(dbName);
    }

    private void createTable(String dbName) {
        try (Connection connection = DriverManager.getConnection(dbName)) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            create.createTableIfNotExists(tableName)
                    .column(userColumn, SQLDataType.VARCHAR(50))
                    .column(gameDayColumn, SQLDataType.INTEGER)
                    .column(homeColumn, SQLDataType.INTEGER)
                    .column(awayColumn, SQLDataType.INTEGER)
                    .primaryKey(userColumn, gameDayColumn)
                    .execute();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Counts the entries of a prediction. Useful to check if a user made a new prediction or update the last one.
     *
     * @param configuration Database configuration.
     * @param prediction    User prediction.
     * @return The number of user predictions (0 or 1).
     */
    private int countEntries(Configuration configuration, Prediction prediction) {
        return Optional.ofNullable(
                        configuration.dsl()
                                .selectCount()
                                .from(tableName)
                                .where(userColumn.eq(prediction.user()), gameDayColumn.eq(prediction.gameDay()))
                                .fetchOne(0, Integer.class))
                .orElse(0);
    }

    /**
     * Insert or updated a prediction of a user.
     *
     * @param configuration Database configuration.
     * @param prediction    User prediction.
     * @return 0 if the user never made a prediction, 1 if the user overrides his / her previous prediction.
     */
    public int insertOrUpdatePrediction(Configuration configuration, Prediction prediction) {
        int count = countEntries(configuration, prediction);
        configuration.dsl()
                .insertInto(tableName)
                .columns(userColumn, homeColumn, awayColumn, gameDayColumn)
                .values(prediction.user(), prediction.homeGoals(), prediction.awayGoals(), prediction.gameDay())
                .onDuplicateKeyUpdate()
                .set(awayColumn, prediction.awayGoals())
                .set(homeColumn, prediction.homeGoals())
                .returning(homeColumn)
                .execute();
        return count;
    }

    /**
     * Get all prediction for a gameday.
     *
     * @param configuration Database configuration.
     * @param gameDay       Game day.
     * @return List of predictions.
     */
    public List<Prediction> getPredictions(Configuration configuration, int gameDay) {
        return configuration.dsl()
                .select()
                .from(tableName)
                .where(gameDayColumn.eq(gameDay))
                .fetch()
                .stream()
                .map(r -> Converters.toPredictions(r, userColumn, gameDayColumn, homeColumn, awayColumn))
                .toList();
    }

    /**
     * Clean predictions for a game day.
     *
     * @param configuration Database configuration.
     * @param gameDay       Game day.
     * @return The number of deleted predictions.
     */
    public int deletePredictions(Configuration configuration, int gameDay) {
        return configuration.dsl()
                .deleteFrom(tableName)
                .where(gameDayColumn.eq(gameDay))
                .execute();
    }
}
