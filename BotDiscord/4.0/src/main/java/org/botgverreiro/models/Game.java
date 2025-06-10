package org.botgverreiro.models;

import jakarta.persistence.Column;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.botgverreiro.tables.Games;
import org.botgverreiro.tables.Modes;
import org.botgverreiro.tables.Seasons;
import org.botgverreiro.tables.Teams;
import org.botgverreiro.utils.GameStatus;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Record5;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class Game {
    private static final DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    @Column(name = "GAME_ID")
    private int gameId;
    // 0 -> not opened
    // 1 -> opened
    // 2 -> closed
    // 3 -> finished
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

    public Game() {
    }

    public Game(Season season, Mode mode, int field, String day, String time, Team opponent) {
        this.gameField = field;
        this.gameDay = day + " " + time.replace(".", ":");
        this.season = season;
        this.gameOpponent = opponent;
        this.mode = mode;
    }

    public Game(Season season, Mode mode, int field, int month, int day, int hour, int minute, Team opponent) {
        this.gameField = field;
        this.gameDay = dateConverter(month, day, hour, minute);
        this.season = season;
        this.gameOpponent = opponent;
        this.mode = mode;
    }

    /*
     * ===================
     * Repository Methods
     * ===================
     */

    /**
     * Method that converts date values to a string. This method is important since it defines year value.
     * @param month Month value.
     * @param day Day value.
     * @param hour Hour value.
     * @param minute Minute value.
     * @return A string following Game.pattern.
     */
    private static String dateConverter(int month, int day, int hour, int minute) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime gameDay = LocalDateTime.of(now.getYear(), month, day, hour, minute);
        int year = gameDay.isBefore(now) ? now.getYear() + 1 : now.getYear();
        return LocalDateTime.of(year, month, day, hour, minute).format(pattern);
    }

    /**
     * Auxiliary function that combines a list of games into a single query to insert them.
     * @param context Database context.
     * @param games Games to add.
     * @return A list of records containing games main information.
     */
    private static List<Record5<Integer, String, Integer, String, String>> combineValues(DSLContext context, List<Game> games) {
        return games.stream()
                .map(game -> context.newRecord(Games.GAMES.SEASON_ID, Games.GAMES.MODE_NAME, Games.GAMES.GAME_FIELD, Games.GAMES.GAME_DAY, Games.GAMES.OPPONENT_ID)
                        .value1(game.season.getSeasonId())
                        .value2(game.mode.getModeId())
                        .value3(game.gameField)
                        .value4(game.gameDay)
                        .value5(game.gameOpponent.getTeamName()))
                .toList();
    }

    /**
     * A select query for GAMES table that is the same across multiple querys.
     * @param context Database context.
     * @return The query with the proper joins.
     */
    private static SelectOnConditionStep<Record> selectQuery(DSLContext context) {
        return context.
                select()
                .from(Games.GAMES)
                .join(Teams.TEAMS).on(Teams.TEAMS.TEAM_NAME.eq(Games.GAMES.OPPONENT_ID))
                .join(Modes.MODES).on(Modes.MODES.MODE_NAME.eq(Games.GAMES.MODE_NAME))
                .join(Seasons.SEASONS).on(Seasons.SEASONS.SEASON_ID.eq(Games.GAMES.SEASON_ID));
    }

    /* =================== Inserts =================== */

    /**
     * Inserts a single game into GAMES table.
     * @param context Database context.
     * @param game Game to insert.
     * @return 1 if sucess, 0 otherwise.
     */
    public static CompletionStage<Integer> insertGame(DSLContext context, Game game) {
        return insertGames(context, Collections.singletonList(game));
    }

    /**
     * Inserts multiple game at once at GAMES table.
     * @param context Database context.
     * @param games Games to insert.
     * @return The number of games inserted.
     */
    public static CompletionStage<Integer> insertGames(DSLContext context, List<Game> games) {
        return context
                .insertInto(Games.GAMES)
                .set(combineValues(context, games))
                .onConflict(Games.GAMES.GAME_STATUS, Games.GAMES.OPPONENT_ID, Games.GAMES.SEASON_ID, Games.GAMES.MODE_NAME, Games.GAMES.GAME_FIELD)
                .doUpdate()
                .set(Games.GAMES.GAME_DAY, DSL.excluded(Games.GAMES.GAME_DAY))
                .executeAsync();
    }
    /* =================== Updates =================== */

    /**
     * Updates the game status to 1 when games are being closed.
     * @param context Database context.
     * @param games Games to change the status.
     * @return The number of affected rows.
     */
    public static CompletionStage<Integer> updateStatus(DSLContext context, List<Game> games, int gameStatus) {
        Set<Integer> ids = games.stream().map(g -> g.gameId).collect(Collectors.toSet());
        return context
                .update(Games.GAMES)
                .set(Games.GAMES.GAME_STATUS, gameStatus)
                .where(Games.GAMES.GAME_ID.in(ids))
                .executeAsync();
    }

    /**
     * Updates the statics of a game after it finishes. Basically sets the number of scored and suffered goals,
     * the number of predictions and the number of winners.
     * @param context Database context.
     * @param game Game object updated.
     * @return The number of affected rows.
     */
    public static CompletionStage<Integer> updateFinishedGame(DSLContext context, Game game) {
        return context
                .update(Games.GAMES)
                .set(Games.GAMES.GOALS_SCORED, game.gameGoalsScored)
                .set(Games.GAMES.GOALS_SUFFERED, game.gameGoalsSuffered)
                .set(Games.GAMES.PREDICTIONS, game.gamePredictions)
                .set(Games.GAMES.WINNERS, game.gameWinners)
                .set(Games.GAMES.GAME_STATUS, GameStatus.FINISHED.getStatus())
                .where(Games.GAMES.GAME_ID.eq(game.gameId))
                .executeAsync();
    }

    /* =================== Selects =================== */

    /**
     * Gets a game with a specific id.
     * @param context Database context.
     * @param gameId Game id.
     * @return Game object.
     */
    public static CompletionStage<Game> selectGame(DSLContext context, int gameId) {
        return selectQuery(context)
                .where(Games.GAMES.GAME_ID.eq(gameId))
                .limit(1)
                .fetchAsync()
                .thenApply(r -> r.isEmpty() ? null : Wrappers.toGame(r.getFirst()));

    }

    /**
     * Get the latest closed game of a specific mode.
     * @param context Database context.
     * @param mode Mode to filter games.
     * @return The latest closed game.
     */
    public static CompletionStage<Game> selectLastGame(DSLContext context, Mode mode) {
        return selectQuery(context)
                .where(Games.GAMES.GAME_STATUS.eq(GameStatus.CLOSE.getStatus()), Games.GAMES.MODE_NAME.eq(mode.getModeId()))
                .limit(1)
                .fetchAsync()
                .thenApply(r -> r.isEmpty() ? null : Wrappers.toGame(r.getFirst()));
    }

    /**
     * Get games with a specific opponent and status.
     * @param context Database context.
     * @param opponent Opponent to filter games.
     * @param gameStatus Status to filter games.
     * @return A list of games objects after filter.
     */
    public static CompletionStage<List<Game>> selectGames(DSLContext context, String opponent, int gameStatus) {
        return selectQuery(context)
                .where(Games.GAMES.GAME_STATUS.eq(gameStatus), Games.GAMES.OPPONENT_ID.contains(opponent))
                .fetchAsync()
                .thenApply(l -> Wrappers.converter(l, Wrappers::toGame));
    }

    /**
     * Get games with a specific opponent and status.
     * @param context Database context.
     * @param opponent Opponent to filter games.
     * @return A list of games objects after filter.
     */
    public static CompletionStage<List<Game>> selectGames(DSLContext context, String opponent) {
        return selectQuery(context)
                .where(Games.GAMES.OPPONENT_ID.contains(opponent))
                .fetchAsync()
                .thenApply(l -> Wrappers.converter(l, Wrappers::toGame));
    }

    /**
     * Get the list of games that are not opened yet.
     * @param context Database context.
     * @return A list of games objects that are not opened.
     */
    public static CompletionStage<List<Game>> selectGamesByStatus(DSLContext context, int gameStatus) {
        return selectQuery(context)
                .where(Games.GAMES.GAME_STATUS.eq(gameStatus))
                .orderBy(Games.GAMES.GAME_ID.desc())
                .fetchAsync()
                .thenApply(l -> Wrappers.converter(l, Wrappers::toGame));
    }

    /**
     * Selects the games from one season.
     * @param context Database context.
     * @param season Season id.
     * @return A list of games of specified season.
     */
    public static CompletionStage<List<Game>> selectGamesBySeason(DSLContext context, int season) {
        return selectQuery(context)
                .where(Games.GAMES.SEASON_ID.eq(season))
                .fetchAsync()
                .thenApply(l -> Wrappers.converter(l, Wrappers::toGame));
    }

    /* =================== Deletes =================== */

    /**
     * Deletes a specified game.
     * @param context Database context.
     * @param game Game id.
     * @return Number of deleted rows.
     */
    public static CompletionStage<Integer> deleteGame(DSLContext context, int game) {
        return context
                .deleteFrom(Games.GAMES)
                .where(Games.GAMES.GAME_ID.eq(game))
                .executeAsync();
    }

    @Override
    public String toString() {
        String gameStr = gameField == 1 ? gameOpponent.getTeamName() + " x SC Braga" : "SC Braga x " + gameOpponent.getTeamName();
        return gameStr + ", " + gameDay;
    }

    public Command.Choice toChoice() {
        return new Command.Choice(this.toString(), gameId);
    }

    public LocalDateTime getDateTime() {
        return LocalDateTime.parse(this.gameDay, pattern);
    }

    public LocalDateTime getStartTime() {
        return getDateTime();
    }

    public LocalDateTime getFinishTime() {
        return getDateTime().plusHours(3);
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

    public Game setGamePredictions(int gamePredictions) {
        this.gamePredictions = gamePredictions;
        return this;
    }

    public int getGameWinners() {
        return gameWinners;
    }

    public Game setGameWinners(int gameWinners) {
        this.gameWinners = gameWinners;
        return this;
    }

    public int getGameGoalsScored() {
        return gameGoalsScored;
    }

    public Game setGameGoalsScored(int gameGoalsScored) {
        this.gameGoalsScored = gameGoalsScored;
        return this;
    }

    public int getGameGoalsSuffered() {
        return gameGoalsSuffered;
    }

    public Game setGameGoalsSuffered(int gameGoalsSuffered) {
        this.gameGoalsSuffered = gameGoalsSuffered;
        return this;
    }

    public Team getGameOpponent() {
        return gameOpponent;
    }

    public Game setGameOpponent(Team team) {
        this.gameOpponent = team;
        return this;
    }

    public Season getSeason() {
        return season;
    }

    public Game setSeason(Season season) {
        this.season = season;
        return this;
    }

    public Mode getMode() {
        return mode;
    }

    public Game setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public boolean isHome() {
        return gameField != 1;
    }
}
