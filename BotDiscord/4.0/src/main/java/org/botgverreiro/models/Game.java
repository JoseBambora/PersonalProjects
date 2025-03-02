package org.botgverreiro.models;

import jakarta.persistence.Column;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.botgverreiro.tables.Games;
import org.botgverreiro.tables.Modes;
import org.botgverreiro.tables.Seasons;
import org.botgverreiro.tables.Teams;
import org.jooq.DSLContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Stream;

public class Game {
    @Column(name = "GAME_ID")
    private int gameId;
    @Column(name = "GAME_STATUS")
    private int gameStatus;
    @Column(name = "GAME_FIELD")
    private int gameField;
    @Column(name = "GAME_DAY")
    private String gameDay;
    @Column(name = "PREDICTIONS")
    private int gamePredictions;
    @Column(name = "WINNERS")
    private int gameWinners;
    @Column(name = "GOALS_SCORED")
    private int gameGoalsScored;
    @Column(name = "GOALS_SUFFERED")
    private int gameGoalsSuffered;

    private Team gameOpponent;
    private Season season;
    private Mode mode;


    @Override
    public String toString() {
        String modeName = this.mode.toString();
        String gameStr = gameField == 1 ? gameOpponent.getTeamName() + "x SC Braga" : "SC Braga x " + gameOpponent.getTeamName();
        return gameStr + "(" + modeName + ")";
    }

    public Command.Choice toChoice() {
        return new Command.Choice(this.toString(), gameId);
    }

    public Game setGameOpponent(Team team) {
        this.gameOpponent = team;
        return this;
    }

    public Game setSeason(Season season) {
        this.season = season;
        return this;
    }

    public Game setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */
    private static String dateConverter(int month, int day, int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gameDay = LocalDateTime.of(now.getYear(), month, day, hour, minute);
        int year = gameDay.isBefore(now) ? now.getYear() + 1 : now.getYear();
        return LocalDateTime.of(year, month, day, hour, minute).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public static CompletionStage<Integer> insertGame(DSLContext context, int season, String mode, int field, int month, int day, int hour, int minute, String opponent) {
        return context
                .insertInto(Games.GAMES)
                .set(Games.GAMES.SEASON_ID, season)
                .set(Games.GAMES.MODE_NAME, mode)
                .set(Games.GAMES.GAME_FIELD, field)
                .set(Games.GAMES.GAME_DAY, dateConverter(month, day, hour, minute))
                .set(Games.GAMES.OPPONENT_ID, opponent)
                .executeAsync();
    }

    public static CompletionStage<List<Game>> getGames(DSLContext context, String opponent) {
        return context
                .select()
                .from(Games.GAMES)
                .join(Teams.TEAMS).on(Teams.TEAMS.TEAM_NAME.eq(Games.GAMES.OPPONENT_ID))
                .join(Modes.MODES).on(Modes.MODES.MODE_NAME.eq(Games.GAMES.MODE_NAME))
                .join(Seasons.SEASONS).on(Seasons.SEASONS.SEASON_ID.eq(Games.GAMES.SEASON_ID))
                .where(Games.GAMES.GAME_STATUS.eq(0), Games.GAMES.OPPONENT_ID.contains(opponent))
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(l -> l.map(g -> g.into(Game.class).setGameOpponent(g.into(Team.class)).setMode(g.into(Mode.class)).setSeason(g.into(Season.class))))
                .thenApply(Stream::toList);
    }

    public static CompletionStage<Integer> deleteGame(DSLContext context, int game) {
        return context
                .deleteFrom(Games.GAMES)
                .where(Games.GAMES.GAME_ID.eq(game))
                .executeAsync();
    }
}
