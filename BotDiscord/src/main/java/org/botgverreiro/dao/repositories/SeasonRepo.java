package org.botgverreiro.dao.repositories;

import org.botgverreiro.dao.utils.Converters;
import org.botgverreiro.model.classes.Season;
import org.jooq.Record;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;


/**
 * Like all the repositories, this one uses <b>transactions</b> to commit database operations and uses SQLITE.
 * This class handles database connections for SEASON table. The table contains the following structure:
 * <p>
 * <code>
 * Table SEASON {
 * season STRING [primary key]
 * games INT
 * scored INT
 * conceded INT
 * total_prediction INT
 * correct_prediction INT
 * total_win INT
 * total_draw INT
 * total_lose INT
 * }
 * </code>
 * </p>
 *
 * @see Converters
 */
public class SeasonRepo {
    private final Table<Record> tableName = table("SEASONS");
    private final Field<String> seasonColumn = field("season", String.class);
    private final Field<Integer> gamesColumn = field("games", Integer.class);
    private final Field<Integer> scoredColumn = field("scored", Integer.class);
    private final Field<Integer> concededColumn = field("conceded", Integer.class);
    private final Field<Integer> totalPredictionColumn = field("total_prediction", Integer.class);
    private final Field<Integer> correctPredictionColumn = field("correct_prediction", Integer.class);
    private final Field<Integer> totalWinColumn = field("total_win", Integer.class);
    private final Field<Integer> totalDrawColumn = field("total_draw", Integer.class);
    private final Field<Integer> totalLoseColumn = field("total_lose", Integer.class);

    public SeasonRepo(String dbName) {
        createTable(dbName);
    }

    private void createTable(String dbName) {
        try (Connection connection = DriverManager.getConnection(dbName)) {
            DSLContext create = DSL.using(connection, SQLDialect.SQLITE);
            create.createTableIfNotExists(tableName)
                    .column(seasonColumn, SQLDataType.VARCHAR(50))
                    .column(gamesColumn, SQLDataType.INTEGER)
                    .column(scoredColumn, SQLDataType.INTEGER)
                    .column(concededColumn, SQLDataType.INTEGER)
                    .column(totalPredictionColumn, SQLDataType.INTEGER)
                    .column(correctPredictionColumn, SQLDataType.INTEGER)
                    .column(totalWinColumn, SQLDataType.INTEGER)
                    .column(totalLoseColumn, SQLDataType.INTEGER)
                    .column(totalDrawColumn, SQLDataType.INTEGER)
                    .primaryKey(seasonColumn)
                    .execute();
        } catch (SQLException ignored) {
        }
    }

    /**
     * Inserts a new season.
     *
     * @param configuration Database configuration.
     * @param season        Season to add.
     */
    public void insertSeason(Configuration configuration, Season season) {
        configuration.dsl()
                .insertInto(tableName)
                .set(seasonColumn, season.getSeason())
                .set(gamesColumn, season.getGames())
                .set(scoredColumn, season.getScored())
                .set(concededColumn, season.getConceded())
                .set(totalPredictionColumn, season.getTotalPredictions())
                .set(correctPredictionColumn, season.getCorrectPredictions())
                .set(totalWinColumn, season.getTotalWins())
                .set(totalDrawColumn, season.getTotalDraws())
                .set(totalLoseColumn, season.getTotalLoses())
                .onConflictDoNothing()
                .execute();
    }

    /**
     * Gets the latest season.
     *
     * @param configuration Database configuration.
     * @return Latest Season.
     */
    public Season getLastSeason(Configuration configuration) {
        Record record = configuration.dsl()
                .select()
                .from(tableName)
                .orderBy(seasonColumn.desc())
                .limit(1)
                .fetchOne();
        return Converters.toSeason(record, seasonColumn, gamesColumn, scoredColumn, concededColumn, totalPredictionColumn, correctPredictionColumn, totalWinColumn, totalDrawColumn, totalLoseColumn);
    }

    /**
     * Gets a specific season.
     *
     * @param configuration Database configuration.
     * @param season        Season ID.
     * @return Season that has the id season.
     */
    public Season getSeason(Configuration configuration, String season) {
        Record record = configuration.dsl()
                .select()
                .from(tableName)
                .where(seasonColumn.eq(season))
                .fetchOne();
        return Converters.toSeason(record, seasonColumn, gamesColumn, scoredColumn, concededColumn, totalPredictionColumn, correctPredictionColumn, totalWinColumn, totalDrawColumn, totalLoseColumn);
    }

    /**
     * Updates the statistics of a season.
     *
     * @param configuration Database configuration.
     * @param season        Season ID.
     */
    public void updateSeason(Configuration configuration, Season season) {
        configuration.dsl()
                .update(tableName)
                .set(totalPredictionColumn, season.getTotalPredictions())
                .set(correctPredictionColumn, season.getCorrectPredictions())
                .set(scoredColumn, season.getScored())
                .set(concededColumn, season.getConceded())
                .set(gamesColumn, season.getGames())
                .set(totalWinColumn, season.getTotalWins())
                .set(totalDrawColumn, season.getTotalDraws())
                .set(totalLoseColumn, season.getTotalLoses())
                .where(seasonColumn.eq(season.getSeason()))
                .execute();
    }

    /**
     * Deletes a season.
     *
     * @param configuration Database configuration.
     * @param season        Season ID.
     * @return The number of deleted seasons (0 or 1).
     */
    public int deleteSeason(Configuration configuration, String season) {
        return configuration.dsl()
                .deleteFrom(tableName)
                .where(seasonColumn.eq(season))
                .execute();
    }
}
