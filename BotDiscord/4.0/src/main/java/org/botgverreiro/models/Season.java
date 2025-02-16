package org.botgverreiro.models;

import jakarta.persistence.*;
import org.botgverreiro.tables.Seasons;
import org.jooq.DSLContext;
import org.jooq.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class Season {
    private static final Logger log = LoggerFactory.getLogger(Season.class);

    @Column(name = "SEASON_ID")
    private int seasonId;

    public Season() {
        this.seasonId = -1;
    }

    public Season(int seasonId) {
        this.seasonId = seasonId;
    }

    @Override
    public String toString() {
        int y2 = seasonId % 100;
        int y1 = seasonId / 100;
        return y1 + "-" + y2;
    }

    public Season nextSeason() {
        int y2 = (seasonId % 100) + 1;
        int y1 = (seasonId / 100) + 1;
        return new Season(y1+y2);
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    /**
     * Get all the season from the database and stores them into a stream.
     * @return A stream with all the season within the database
     */
    private static CompletionStage<Stream<Season>> getAllSeasonStream(DSLContext context) {
        return context
                .selectFrom(Seasons.SEASONS)
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(r -> r.map(record -> record.into(Season.class)));
    }

    /**
     * Returns a list containing seasons in which season name is similar to the string given.
     * @param season Season name that we want to get the similar.
     * @return A list of similar seasons.
     */
    public static CompletionStage<List<Season>> getSimilarSeasons(DSLContext context,int season) {
        return context
                .selectFrom(Seasons.SEASONS)
                .where(Seasons.SEASONS.SEASON_ID.contains(season))
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(r -> r.map(record -> record.into(Season.class)))
                .thenApply(Stream::toList);
    }

    /**
     * Insert a season into the database.
     * @param season Season to insert.
     * @return 1 if everything went alright, 0 otherwise.
     */
    private static CompletionStage<Integer> insertSeason(DSLContext context,Season season) {
        return context
                .insertInto(Seasons.SEASONS)
                .set(Seasons.SEASONS.SEASON_ID, season.seasonId)
                .executeAsync();
    }

    /**
     * Return a list of all seasons within the database.
     * @return A list containing all the stored seasons.
     */
    public static CompletionStage<List<Season>> getAllSeason(DSLContext context) {
        return getAllSeasonStream(context).thenApply(Stream::toList);
    }

    /**
     * Get the latest season.
     * @return The latest season.
     */
    public static CompletionStage<Season> getLastSeason(DSLContext context) {
        return getAllSeasonStream(context)
                .thenApply(l -> l.sorted((s1,s2) -> s2.seasonId - s1.seasonId).toList())
                .thenApply(l -> l.isEmpty() ? null : l.getFirst());
    }

    /**
     * Adds a new season to the database.
     * @return The new season stored.
     */
    public static CompletionStage<Season> newSeason(DSLContext context) {
        return getLastSeason(context)
                .thenApply(s -> s != null ? s.nextSeason() : new Season(Integer.parseInt(System.getenv("SEASON"))))
                .thenCompose(ns -> Season.insertSeason(context,ns))
                .thenCompose(_ -> getLastSeason(context));
    }

    /**
     * Deletes a specific season from the database.
     * @param season Season Name to delete.
     * @return 1 if season was successfully deleted, 0 otherwise.
     */
    public static CompletionStage<Integer> deleteSeason(DSLContext context,int season) {
        return context
                .deleteFrom(Seasons.SEASONS)
                .where(Seasons.SEASONS.SEASON_ID.eq(season))
                .executeAsync();
    }
}
