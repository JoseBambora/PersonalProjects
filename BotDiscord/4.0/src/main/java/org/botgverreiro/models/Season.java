package org.botgverreiro.models;

import jakarta.persistence.Column;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.botgverreiro.tables.Seasons;
import org.jooq.DSLContext;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class Season {

    @Column(name = "SEASON_ID")
    private int seasonId;

    public Season() {
        this.seasonId = -1;
    }

    public Season(int seasonId) {
        this.seasonId = seasonId;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    /**
     * Get all the season from the database and stores them into a stream.
     *
     * @param context Database context.
     * @return A stream with all the season within the database.
     */
    private static CompletionStage<Stream<Season>> selectAllSeasonStream(DSLContext context) {
        return context
                .selectFrom(Seasons.SEASONS)
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(r -> r.map(record -> record.into(Season.class)));
    }

    /**
     * Insert a season into the database.
     *
     * @param context Database context.
     * @param season  Season to insert.
     * @return 1 if everything went alright, 0 otherwise.
     */
    private static CompletionStage<Integer> insertSeason(DSLContext context, Season season) {
        return context
                .insertInto(Seasons.SEASONS)
                .set(Seasons.SEASONS.SEASON_ID, season.seasonId)
                .executeAsync();
    }

    /* =================== Inserts =================== */

    /**
     * Adds a new season to the database.
     *
     * @param context Database context.
     * @return The number of inserted rows.
     */
    public static CompletionStage<Integer> insertNewSeason(DSLContext context) {
        return selectLastSeason(context)
                .thenApply(s -> s != null ? s.nextSeason() : new Season(Integer.parseInt(System.getenv("SEASON"))))
                .thenCompose(ns -> Season.insertSeason(context, ns));
    }
    /* =================== Updates =================== */

    /* =================== Selects =================== */

    /**
     * Returns a list containing seasons in which season name is similar to the string given.
     *
     * @param context Database context.
     * @param season  Season name that we want to get the similar.
     * @return A list of similar seasons.
     */
    public static CompletionStage<List<Season>> selectSimilarSeasons(DSLContext context, int season) {
        return context
                .selectFrom(Seasons.SEASONS)
                .where(Seasons.SEASONS.SEASON_ID.contains(season))
                .fetchAsync()
                .thenApply(r -> r.into(Season.class));
    }

    /**
     * Return a list of all seasons within the database.
     *
     * @param context Database context.
     * @return A list containing all the stored seasons.
     */
    public static CompletionStage<List<Season>> selectAllSeasons(DSLContext context) {
        return selectAllSeasonStream(context).thenApply(Stream::toList);
    }



    /**
     * Get the latest season.
     *
     * @param context Database context.
     * @return The latest season.
     */
    public static CompletionStage<Season> selectLastSeason(DSLContext context) {
        return selectAllSeasonStream(context)
                .thenApply(l -> l.sorted((s1, s2) -> s2.seasonId - s1.seasonId).toList())
                .thenApply(l -> l.isEmpty() ? null : l.getFirst());
    }

    public static CompletionStage<Season> selectSeason(DSLContext context, int seasonId) {
        return context
                .selectFrom(Seasons.SEASONS)
                .where(Seasons.SEASONS.SEASON_ID.eq(seasonId))
                .fetchAsync()
                .thenApply(r -> r.isEmpty() ? null : r.getFirst().into(Season.class));
    }

    /* =================== Deletes =================== */

    /**
     * Deletes a specific season from the database.
     *
     * @param context Database context.
     * @param season  Season Name to delete.
     * @return 1 if season was successfully deleted, 0 otherwise.
     */
    public static CompletionStage<Integer> deleteSeason(DSLContext context, int season) {
        return context
                .deleteFrom(Seasons.SEASONS)
                .where(Seasons.SEASONS.SEASON_ID.eq(season))
                .executeAsync();
    }

    @Override
    public String toString() {
        int y2 = seasonId % 100;
        int y1 = seasonId / 100;
        return y1 + "-" + y2;
    }

    public Command.Choice toChoice() {
        return new Command.Choice(String.valueOf(this), this.seasonId);
    }

    public Season nextSeason() {
        return new Season(seasonId + 101);
    }

    public int getSeasonId() {
        return seasonId;
    }
}
