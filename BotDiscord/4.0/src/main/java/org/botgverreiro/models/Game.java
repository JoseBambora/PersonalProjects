package org.botgverreiro.models;

import jakarta.persistence.Column;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.botgverreiro.tables.*;
import org.jooq.DSLContext;
import org.jooq.Record5;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;
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

    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public Game() {}
    public Game(Season season, Mode mode, int field, String day, String time, Team opponent) {
        this.gameField = field;
        this.gameDay = day + " " + time.replace(".",":");
        this.season = season;
        this.gameOpponent = opponent;
        this.mode = mode;
    }

    @Override
    public String toString() {
        String gameStr = gameField == 1 ? gameOpponent.getTeamName() + " x SC Braga" : "SC Braga x " + gameOpponent.getTeamName();
        return gameStr + ", " + gameDay;
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

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(this.gameDay,pattern);
    }

    public LocalDateTime getFinishTime() {
        return getDateTime().plusHours(3);
    }

    public Game setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public int getGameId() {
        return gameId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Game game)) return false;
        return gameId == game.gameId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(gameId);
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
        return LocalDateTime.of(year, month, day, hour, minute).format(pattern);
    }

    private static List<Record5<Integer,String, Integer, String, String>> combineValues(DSLContext context, List<Game> games) {
        return games.stream()
                .map(game -> context.newRecord(Games.GAMES.SEASON_ID, Games.GAMES.MODE_NAME, Games.GAMES.GAME_FIELD, Games.GAMES.GAME_DAY, Games.GAMES.OPPONENT_ID)
                        .value1(game.season.getSeasonId())
                        .value2(game.mode.getModeId())
                        .value3(game.gameField)
                        .value4(game.gameDay)
                        .value5(game.gameOpponent.getTeamName()))
                .toList();
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

    public static CompletionStage<Integer> insertGames(DSLContext context, List<Game> games) {
        return context
                .insertInto(Games.GAMES)
                .set(combineValues(context,games))
                .executeAsync();
    }

    public static CompletionStage<Game> getGame(DSLContext context, int gameId) {
        return context.
                select()
                .from(Games.GAMES)
                .join(Teams.TEAMS).on(Teams.TEAMS.TEAM_NAME.eq(Games.GAMES.OPPONENT_ID))
                .join(Modes.MODES).on(Modes.MODES.MODE_NAME.eq(Games.GAMES.MODE_NAME))
                .join(Seasons.SEASONS).on(Seasons.SEASONS.SEASON_ID.eq(Games.GAMES.SEASON_ID))
                .where(Games.GAMES.GAME_ID.eq(gameId))
                .fetchAsync()
                .thenApply(r -> r.isEmpty() ? null : Wrappers.toGame(r.getFirst()));

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
                .thenApply(l -> l.map(Wrappers::toGame))
                .thenApply(Stream::toList);
    }

    public static CompletionStage<List<Game>> getGamesNotOpened(DSLContext context) {
        return context
                .select()
                .from(Games.GAMES)
                .join(Teams.TEAMS).on(Teams.TEAMS.TEAM_NAME.eq(Games.GAMES.OPPONENT_ID))
                .join(Modes.MODES).on(Modes.MODES.MODE_NAME.eq(Games.GAMES.MODE_NAME))
                .join(Seasons.SEASONS).on(Seasons.SEASONS.SEASON_ID.eq(Games.GAMES.SEASON_ID))
                .where(Games.GAMES.GAME_STATUS.eq(0))
                .fetchAsync()
                .thenApply(Collection::stream)
                .thenApply(l -> l.map(Wrappers::toGame))
                .thenApply(Stream::toList);
    }

    public static CompletionStage<Integer> deleteGame(DSLContext context, int game) {
        return context
                .deleteFrom(Games.GAMES)
                .where(Games.GAMES.GAME_ID.eq(game))
                .executeAsync();
    }

    public static CompletionStage<Integer> closeGames(DSLContext context, List<Game> games) {
        Set<Integer> ids = games.stream().map(g -> g.gameId).collect(Collectors.toSet());
        return context
                .update(Games.GAMES)
                .set(Games.GAMES.GAME_STATUS,1)
                .where(Games.GAMES.GAME_ID.in(ids))
                .executeAsync();
    }

    public String getGameStatus() {
        return gameStatus == 0 ? "✅ por abrir / aberto" : "❌ terminado";
    }

    public String getGameField() {
        return gameField == 1 ? "Fora" : gameField == 0 ? "Casa" : "Neutro";
    }

    public String getGameDay() {
        return gameDay;
    }

    public int getGamePredictions() {
        return gamePredictions;
    }

    public int getGameWinners() {
        return gameWinners;
    }

    public int getGameGoalsScored() {
        return gameGoalsScored;
    }

    public int getGameGoalsSuffered() {
        return gameGoalsSuffered;
    }

    public Team getGameOpponent() {
        return gameOpponent;
    }

    public Season getSeason() {
        return season;
    }

    public Mode getMode() {
        return mode;
    }
}
