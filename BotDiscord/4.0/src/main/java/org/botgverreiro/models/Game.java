package org.botgverreiro.models;

import jakarta.persistence.Column;
import org.botgverreiro.tables.Games;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletionStage;

public class Game {
    @Column(name = "GAME_ID")
    private int gameId;
    @Column(name = "SEASON_ID")
    private int seasonId;
    @Column(name = "MODE_ID")
    private int modeId;
    @Column(name = "GAME_STATUS")
    private int gameStatus;
    @Column(name = "GAME_FIELD")
    private int gameField;
    @Column(name = "GAME_DAY")
    private String gameDay;
    @Column(name = "OPPONENT_ID")
    private Team gameOpponent;
    @Column(name = "PREDICTIONS")
    private int gamePredictions;
    @Column(name = "WINNERS")
    private int gameWinners;
    @Column(name = "GOALS_SCORED")
    private int gameGoalsScored;
    @Column(name = "GOALS_SUFFERED")
    private int gameGoalsSuffered;

    private static String dateConverter(int month, int day, int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gameDay = LocalDateTime.of(now.getYear(),month,day,hour,minute);
        int year = gameDay.isBefore(now) ? now.getYear() + 1 : now.getYear();
        return LocalDateTime.of(year,month,day,hour,minute).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static CompletionStage<Integer> insertGame(DSLContext context, int season, String mode, int field, int month, int day, int hour, int minute, String opponent) {
        return context
                .insertInto(Games.GAMES)
                .set(Games.GAMES.SEASON_ID,season)
                .set(Games.GAMES.MODE_NAME, mode)
                .set(Games.GAMES.GAME_FIELD, field)
                .set(Games.GAMES.GAME_DAY, dateConverter(month,day,hour,minute))
                .set(Games.GAMES.OPPONENT_ID, opponent)
                .executeAsync();
    }
}
